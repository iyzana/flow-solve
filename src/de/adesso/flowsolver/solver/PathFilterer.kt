package de.adesso.flowsolver.solver

import de.adesso.flowsolver.solver.model.Grid
import de.adesso.flowsolver.solver.model.Node
import de.adesso.flowsolver.solver.model.Path
import de.adesso.flowsolver.solver.model.PathsData
import java.util.HashMap
import java.util.LinkedList

/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 13.05.2016
 */
fun preFilter(coloredPaths: HashMap<Int, MutableList<Path>>, pathsData: PathsData) {
    println("prefiltering paths...")
    
    do {
        var changed = false
        
        val sizeSorted = coloredPaths.toList().sortedBy { it.second.size }
        for ((color, paths) in sizeSorted) {
            val sizeSorted2 = sizeSorted.toList().sortedBy { it.second.size }
            
            val startSize = paths.size
            for ((otherColor, otherPaths) in sizeSorted2) {
                if (color == otherColor) continue
                if (paths.isEmpty()) continue
                
                //                print("color $color with $otherColor start " + paths.size)
                paths.retainAll { path ->
                    if (pathsData.intersectsAll(path, otherColor)) {
                        pathsData.remove(color, path)
                        changed = true
                        return@retainAll false
                    }
                    
                    return@retainAll true
                }
                //                println(" end " + paths.size)
            }
            
            if (startSize != paths.size)
                println("color $color: " + startSize + " -> " + paths.size)
        }
        println()
    } while (changed)
}

fun fullFilter(grid: Grid, coloredPaths: HashMap<Int, MutableList<Path>>, color: Int = 1): MutableList<MutableList<Path>> {
    if (color !in coloredPaths) {
        for (x in 0..grid.w - 1) {
            for (y in 0..grid.h - 1) {
                if(grid[x, y].color == 0) return mutableListOf()
            }
        }
        return mutableListOf(LinkedList())
    }
    
    val paths = coloredPaths[color]!!
    
    val solutions = LinkedList<MutableList<Path>>()
    
    for (path in paths) {
        if (!tryAddPath(grid, path, color)) continue
        val subSolutions = fullFilter(grid, coloredPaths, color + 1)
        subSolutions.forEach { it.add(0, path) }
        solutions.addAll(subSolutions)
        removePath(grid, path, color)
    }
    
    return solutions
}

fun tryAddPath(grid: Grid, path: Path, color: Int): Boolean {
    for (node in path) {
        val current = grid[Node.x(node), Node.y(node)]
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
        val current = grid[Node.x(node), Node.y(node)]
        if (current.color == color)
            current.color = 0
    }
}