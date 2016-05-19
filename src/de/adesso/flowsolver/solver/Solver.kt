package de.adesso.flowsolver.solver

import de.adesso.flowsolver.solver.model.Grid
import de.adesso.flowsolver.solver.model.Node
import de.adesso.flowsolver.solver.model.Path
import de.adesso.flowsolver.solver.model.PathsData
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

fun solve(grid: Grid) {
    val w = grid.w
    val h = grid.h

    println("solving $w x $h grid")

    val points = extractPairs(grid).values.flatMap { listOf(it.first, it.second) }
    println("found ${points.size / 2} flows\n")
    
    println("input grid")
    grid.print()
    val newPoints = fillGrid(grid, points)
    println("filled grid")
    grid.print()

    val pairs = newPoints.mapValues { e -> e.value[0] to e.value[1] }
    require(1 in pairs.keys) { "The level does not contain first flow" }
    require((pairs.keys - 1).all { it - 1 in pairs }) { "The level is missing flows or has incomplete flows" }
    //    +grid
    //    val pairs = extractPairs(grid).mapValues { Path(1, it.value.first.compressed()) to Path(1, it.value.second.compressed()) }

    val shortestPaths = pairs.mapValues { entry ->
        val color = entry.key
        val pair = entry.value
        val start = pair.first.lastNode(color)
        val end = pair.second.lastNode(color)

        shortestPath(grid, start, end).size
    }
    val pathSum = w * h - shortestPaths.values.sum()

    val coloredPaths = HashMap<Int, MutableList<Path>>()

    // color -> x -> y -> List<Path>
    val pathsData = PathsData(pairs.keys, grid)

    val executor = Executors.newFixedThreadPool(pairs.size)

    println("building all paths")
    println("time = " + measureTimeMillis {
        for ((color, pair) in pairs.entries) {
            executor.execute {
                val maxLength = pathSum + shortestPaths[color]!!
                val start = pair.first.lastNode(color)
                val end = pair.second.lastNode(color)

                val paths = allPaths(grid.copy(), start, end, maxLength, pairs).toMutableList()
                println("color $color: ${paths.size} paths")
                
                if(paths.toSet().size != paths.size)
                    throw IllegalStateException("lol holz")

                // TODO: If one path write to grid

                synchronized(coloredPaths) { coloredPaths.put(color, paths) }
                pathsData.add(color, paths)

//                paths.maxBy { it.size }?.let {
//                    val gridCopy = grid.copy()
//                    tryAddPath(gridCopy, it, color)
//                    gridCopy.print()
//                }

                // TODO: Sort by probability
                val otherColors = synchronized(coloredPaths) { LinkedList(coloredPaths.keys) }

                for (otherColor in otherColors) {
                    synchronized(color) { preFilter(coloredPaths, pathsData, color, otherColor) }
//                    println("filtered $color: ${paths.size} paths")
                    synchronized(otherColor) { preFilter(coloredPaths, pathsData, otherColor, color) }
//                    println("filtered $otherColor: ${coloredPaths[otherColor]!!.size} paths")
                }
            }
        }

        executor.shutdown()
        executor.awaitTermination(365, TimeUnit.DAYS)
    } + " ms\n")
    
    println("preprefiltered paths")
    for((color, paths) in coloredPaths)
        println("color $color: ${paths.size} paths")
    println()
    //    pathsData.createStatisticalData()

    preFilter(coloredPaths, pathsData)

    val solutions = fullFilter(grid, coloredPaths)

    if(solutions.isEmpty()) {
        println("grid not solved")
        return
    }
    println("solved grid")
    solutions[0].forEachIndexed { index, path ->
        path.forEach { node ->
            grid[Node.x(node), Node.y(node)].color = index + 1
        }
    }
    grid.print()

    val completeSolutions = joinPaths(solutions, pairs)
    println()

    completeSolutions.forEachIndexed { index, completeSolution ->
        println("solution $index")
        completeSolution.forEachIndexed { index2, path ->
            println("color ${index2 + 1} $path")
        }
        println()
    }
    println()
    println("====================")
    println()
}

private fun extractPairs(grid: Grid): Map<Int, Pair<Node, Node>> {
    val pairs = HashMap<Int, Pair<Node, Node>>()

    (1..20).forEach { color ->
        var first: Node? = null

        (0..grid.w - 1).forEach w@ { x ->
            (0..grid.h - 1).forEach { y ->
                val node = grid[x, y]
                if (node.color == color) {
                    if (first == null) first = node
                    else {
                        pairs.put(color, first!! to node)
                        return@w
                    }
                }
            }
        }
    }

    return pairs
}

private fun joinPaths(solutions: List<List<Path>>, pairs: Map<Int, Pair<Path, Path>>): List<List<Path>> {
    println("appending pre and postfix")

    return solutions.map { solution ->
        solution.mapIndexed { index, path ->
            val pre = pairs[index + 1]!!.first
            val post = pairs[index + 1]!!.second
            val size = pre.size + path.size + post.size - 1

            val complete = Path(size)
            pre.nodes().forEach { complete.add(it) }
            path.nodes().forEach { complete.add(it) }
            post.nodes().dropLast(1).reversed().forEach { complete.add(it) }
            return@mapIndexed complete
        }
    }
}
