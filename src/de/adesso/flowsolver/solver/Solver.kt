package de.adesso.flowsolver.solver

import de.adesso.flowsolver.solver.model.Grid
import de.adesso.flowsolver.solver.model.Node
import de.adesso.flowsolver.solver.model.Path
import de.adesso.flowsolver.solver.model.PathsData
import java.util.*
import kotlin.system.measureTimeMillis

fun solve(grid: Grid) {
    val w = grid.w
    val h = grid.h

    println("solving $w x $h grid\n")

    val points = extractPairs(grid).values.flatMap { listOf(it.first, it.second) }
    grid.print()
    val newPoints = fillGrid(grid, points)
    grid.print()

    val pairs = newPoints.mapValues { e -> e.value[0] to e.value[1] }
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

    println("building all paths")
    println("time = " + measureTimeMillis {
        for ((color, pair) in pairs.entries) {
            print("color $color: ")

            val maxLength = pathSum + shortestPaths[color]!!

            val start = pair.first.lastNode(color)
            val end = pair.second.lastNode(color)

            val paths = allPaths(grid, start, end, pathsData, maxLength, pairs).toMutableList()
            println("${paths.size} paths")
            //            if(paths.size < 20) {
            //                paths.forEach { path ->
            //                    val copy = grid.copy()
            //                    
            //                    path.forEach { node ->
            //                        copy[Node.x(node), Node.y(node)].color = color
            //                    }
            //                    
            //                    +copy
            //                }
            //                println()
            //                println()
            //            }
            coloredPaths.put(color, paths)

            pathsData.add(color, paths)
        }
    } + " ms\n")

    //    pathsData.createStatisticalData()

    preFilter(coloredPaths, pathsData)

    val solutions = fullFilter(grid, coloredPaths)

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
}

private fun extractPairs(grid: Grid): Map<Int, Pair<Node, Node>> {
    val pairs = HashMap<Int, Pair<Node, Node>>()
    colors@ for (color in 1..20) {
        var first: Node? = null

        for (x in 0..grid.w - 1) {
            for (y in 0..grid.h - 1) {
                val node = grid[x, y]
                if (node.color == color) {
                    if (first == null) first = node
                    else {
                        pairs.put(color, first to node)
                        continue@colors
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
