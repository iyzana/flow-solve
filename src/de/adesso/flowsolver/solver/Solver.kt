package de.adesso.flowsolver.solver

import de.adesso.flowsolver.forEach
import de.adesso.flowsolver.mapValues
import de.adesso.flowsolver.solver.model.Grid
import de.adesso.flowsolver.solver.model.Path
import de.adesso.flowsolver.solver.model.PathsData
import java.util.HashMap
import java.util.LinkedList
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

fun solve(grid: Grid): Map<Int, Path> {
    val points = grid.nodes.filter { it.color != 0 }
    val pairs = fillGrid(grid, points)
    
    require((1..(pairs.keys.max() ?: 1)).toSet() == pairs.keys) { "The level is missing flows or has incomplete flows" }
    
    val shortestPaths = shortestPaths(grid, pairs)
    val pathSum = grid.w * grid.h - shortestPaths.values.sumBy { it - 2 } - pairs.values.sumBy { it.first.size + it.second.size }
    val maxLengths = pairs.keys.associate { it to pathSum + shortestPaths[it]!! }
    
    val coloredPaths = HashMap<Int, MutableList<Path>>()
    val pathsData = PathsData(pairs.keys, grid)
    
    buildAllPaths(coloredPaths, grid, pairs, pathsData, maxLengths)
    preFilter(coloredPaths, pathsData)
    val solutions = fullFilter(grid, coloredPaths)
    
    if (solutions.isEmpty())
        throw IllegalArgumentException("grid is not solvable")
    
    val completeSolutions = joinPaths(solutions, pairs)
    return completeSolutions[0]
}

fun verboseSolve(grid: Grid): Map<Int, Path> {
    val w = grid.w
    val h = grid.h
    println("solving $w x $h grid")
    
    val points = grid.nodes.filter { it.color != 0 }
    println("found ${points.size / 2} flows\n")
    
    println("input grid")
    grid.print()
    val pairs = fillGrid(grid, points)
    println("filled grid")
    grid.print()
    
    require((1..(pairs.keys.max() ?: 1)).toSet() == pairs.keys) { "The level is missing flows or has incomplete flows" }
    
    val shortestPaths = shortestPaths(grid, pairs)
    val pathSum = w * h - shortestPaths.values.sumBy { it - 2 } - pairs.values.sumBy { it.first.size + it.second.size }
    
    val coloredPaths = HashMap<Int, MutableList<Path>>()
    val pathsData = PathsData(pairs.keys, grid)
    val maxLengths = pairs.keys.associate { it to pathSum + shortestPaths[it]!! }
    
    buildAllPaths(coloredPaths, grid, pairs, pathsData, maxLengths)
    preFilter(coloredPaths, pathsData)
    val solutions = fullFilter(grid, coloredPaths)
    
    if (solutions.isEmpty())
        throw IllegalArgumentException("grid is not solvable")
    
    println("solved grid")
    solutions[0].forEach { key, value -> grid.writePath(value, key) }
    grid.print()
    
    val completeSolutions = joinPaths(solutions, pairs)
    println()
    
    completeSolutions.forEachIndexed { index, completeSolution ->
        println("solution $index")
        completeSolution.forEach { key, value ->
            println("color $key $value")
        }
        println()
    }
    println()
    println("====================")
    println()
    
    return completeSolutions[0]
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
                
                val paths = allPaths(start, grid.copy(), end, pairs, maxLength).toMutableList()
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
    return pairs.mapValues { color, pair ->
        val start = pair.first.lastNode(color)
        val end = pair.second.lastNode(color)
        
        shortestPath(grid, start, end).size
    }
}

private fun joinPaths(solutions: List<Map<Int, Path>>, pairs: Map<Int, Pair<Path, Path>>): List<Map<Int, Path>> {
    println("appending pre and postfix")
    
    return solutions.map { solution ->
        solution.mapValues { color, path ->
            val pre = pairs[color]!!.first
            val post = pairs[color]!!.second
            val size = pre.size + path.size + post.size - 1
            
            val completePath = Path(size)
            pre.nodes().forEach { completePath.add(it) }
            path.nodes().forEach { completePath.add(it) }
            post.nodes().dropLast(1).reversed().forEach { completePath.add(it) }
            completePath
        }
    }
}
