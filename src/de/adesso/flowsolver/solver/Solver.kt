package de.adesso.flowsolver.solver

import de.adesso.flowsolver.solver.model.Grid
import de.adesso.flowsolver.solver.model.Node
import de.adesso.flowsolver.solver.model.Path
import de.adesso.flowsolver.solver.model.PathsData
import java.util.HashMap
import java.util.LinkedList
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

fun solve(grid: Grid) : Map<Int, Path> {
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
    require((1..(pairs.keys.max() ?: 1)).toSet() == pairs.keys) { "The level is missing flows or has incomplete flows" }
    
    val shortestPaths = shortestPaths(grid, pairs)
    val pathSum = w * h - shortestPaths.values.sumBy { it - 2 } - pairs.values.sumBy { it.first.size + it.second.size }
    
    val coloredPaths = HashMap<Int, MutableList<Path>>()
    val pathsData = PathsData(pairs.keys, grid)
    val maxLengths = pairs.mapValues { pathSum + shortestPaths[it.key]!! }
    
    buildAllPaths(coloredPaths, grid, pairs, pathsData, maxLengths)
    preFilter(coloredPaths, pathsData)
    val solutions = fullFilter(grid, coloredPaths)
    
    if (solutions.isEmpty())
        throw IllegalArgumentException("grid is not solvable")
    
    println("solved grid")
    solutions[0].forEachIndexed { index, path -> grid.writePath(path, index + 1) }
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

private fun buildAllPaths(coloredPaths: HashMap<Int, MutableList<Path>>, grid: Grid, pairs: Map<Int, Pair<Path, Path>>, pathsData: PathsData, maxLengths: Map<Int, Int>) {
    val executor = Executors.newFixedThreadPool(pairs.size)
    
    println("building all paths")
    println("time = " + measureTimeMillis {
        for ((color, pair) in pairs.entries) {
            executor.execute {
                val maxLength = maxLengths[color]!!
                val start = pair.first.lastNode(color)
                val end = pair.second.lastNode(color)
                
                val paths = allPaths(grid.copy(), start, end, maxLength, pairs).toMutableList()
                println("color $color: ${paths.size} paths")
                
                pathsData.add(color, paths)
                
                // TODO: Sort by probability
                val otherColors = synchronized(coloredPaths) {
                    coloredPaths.put(color, paths)
                    LinkedList(coloredPaths.keys)
                } - color
                
                for (otherColor in otherColors) {
                    synchronized(color) { preFilter(coloredPaths, pathsData, color, otherColor) }
                    synchronized(otherColor) { preFilter(coloredPaths, pathsData, otherColor, color) }
                }
                println("filtered $color: ${paths.size} paths")
            }
        }
        
        executor.shutdown()
        executor.awaitTermination(365, TimeUnit.DAYS)
    } + " ms\n")
    
    println("preprefiltered paths")
    for ((color, paths) in coloredPaths)
        println("color $color: ${paths.size} paths")
    println()
}

private fun shortestPaths(grid: Grid, pairs: Map<Int, Pair<Path, Path>>): Map<Int, Int> {
    return pairs.mapValues { entry ->
        val color = entry.key
        val pair = entry.value
        val start = pair.first.lastNode(color)
        val end = pair.second.lastNode(color)
        
        shortestPath(grid, start, end).size
    }
    return completeSolutions.withIndex().groupBy({ it.index + 1 }, {it.value[0]}).mapValues { it.value[0] }
}

private fun extractPairs(grid: Grid): Map<Int, Pair<Node, Node>> {
    val nodes = grid.nodes.filter { it.color != 0 }
    val grouped = nodes.groupBy { it.color }
    
    require(grouped.all { it.value.size == 2 }) { "Invalid grid" }
    
    return grouped.mapValues { it.value[0] to it.value[1] }
}

private fun oldExtractPairs(grid: Grid): Map<Int, Pair<Node, Node>> {
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
