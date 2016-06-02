package de.adesso.flowsolver.solver

import de.adesso.flowsolver.solver.model.Grid
import de.adesso.flowsolver.solver.model.Path
import de.adesso.flowsolver.solver.model.PathsData
import de.adesso.flowsolver.solver.model.x
import de.adesso.flowsolver.solver.model.y
import java.util.HashMap
import java.util.LinkedList
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 13.05.2016
 */
fun preFilter(coloredPaths: HashMap<Int, MutableList<Path>>, pathsData: PathsData) {
    // TODO: Don't prefilter with unchanged colors
    println("pre filtering paths")
    println("time = " + measureTimeMillis {
        val changedColors = ConcurrentHashMap(coloredPaths)
        
        do {
            val executor = Executors.newFixedThreadPool(changedColors.size)
            
            val checkedColors = HashMap(changedColors)
            changedColors.clear()
            
            for ((color, paths) in coloredPaths) {
                executor.execute {
                    for (otherColor in checkedColors.keys) {
                        val startSize = paths.size
                        
                        if (preFilter(coloredPaths, pathsData, color, otherColor))
                            changedColors.put(color, paths)
                        
                        if (startSize != paths.size)
                            println("color $color: " + startSize + " -> " + paths.size)
                    }
                }
            }
            
            executor.shutdown()
            executor.awaitTermination(365, TimeUnit.DAYS)
        } while (!changedColors.isEmpty())
    } + " ms\n")
}

fun preFilter(coloredPaths: HashMap<Int, MutableList<Path>>, pathsData: PathsData, color: Int, otherColor: Int): Boolean {
    if (color == otherColor) return false
    val paths = coloredPaths[color]!!
    
    if (paths.isEmpty()) return false
    
    var changed = false
    
    paths.retainAll { path ->
        if (pathsData.intersectsAll(path, otherColor)) {
            pathsData.remove(color, path)
            changed = true
            return@retainAll false
        }
        
        return@retainAll true
    }
    
    return changed
}

fun fullFilter(grid: Grid, coloredPaths: HashMap<Int, MutableList<Path>>): List<Map<Int, Path>> {
    var fullFilter: List<Map<Int, Path>> = mutableListOf()
    println("full filtering paths")
    println("time = " + measureTimeMillis {
        fullFilter = fullFilter(grid, coloredPaths, 1, Array(coloredPaths.size) { null }, LinkedList())
    } + " ms\n")
    return fullFilter
}

private fun fullFilter(grid: Grid, coloredPaths: HashMap<Int, MutableList<Path>>, color: Int, pathList: Array<Path?>, solutions: LinkedList<Map<Int, Path>>): List<Map<Int, Path>> {
    if (color > coloredPaths.size) {
        if (grid.nodes.none { it.color == 0 })
            solutions.add(pathList.mapIndexed { i, path -> i + 1 to path!! }.toMap())
        return solutions
    }
    
    val paths = coloredPaths[color]!!
    
    for (path in paths) {
        if (!tryAddPath(grid, path, color)) continue
        pathList[color - 1] = path
//        val subSolutions = 
        fullFilter(grid, coloredPaths, color + 1, pathList, solutions)
//        subSolutions.forEach { it.put(color, path) }
//        solutions.addAll(subSolutions)
        removePath(grid, path, color)
    }
    
    return solutions
}

fun tryAddPath(grid: Grid, path: Path, color: Int): Boolean {
    for (node in path) {
        val current = grid[node.x, node.y]
        if (current.color == 0) current.color = color
        else if (current.color == color) continue
        else {
            removePath(grid, path, color)
            return false
        }
    }
    
    return true
}

fun removePath(grid: Grid, path: Path, color: Int) {
    for (node in path) {
        val current = grid[node.x, node.y]
        if (current.color == color)
            current.color = 0
    }
}