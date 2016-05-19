package de.adesso.flowsolver.solver

import de.adesso.flowsolver.solver.model.Grid
import de.adesso.flowsolver.solver.model.Node
import de.adesso.flowsolver.solver.model.Path
import de.adesso.flowsolver.solver.model.PathsData
import java.util.*
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
        do {
            var changed = false

            val executor = Executors.newFixedThreadPool(coloredPaths.size)

            for ((color, paths) in coloredPaths) {
//                executor.execute {
                    val startSize = paths.size

                    for (otherColor in coloredPaths.keys)
                        if (preFilter(coloredPaths, pathsData, color, otherColor))
                            changed = true

                    if (startSize != paths.size)
                        println("color $color: " + startSize + " -> " + paths.size)
//                }
            }
            
            executor.shutdown()
            executor.awaitTermination(365, TimeUnit.DAYS)
        } while (changed)
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

fun fullFilter(grid: Grid, coloredPaths: HashMap<Int, MutableList<Path>>): MutableList<MutableList<Path>> {
    var fullFilter: MutableList<MutableList<Path>> = mutableListOf()
    println("full filtering paths")
    println("time = " + measureTimeMillis {
        fullFilter = fullFilter(grid, coloredPaths, 1)
    } + " ms\n")
    return fullFilter
}

private fun fullFilter(grid: Grid, coloredPaths: HashMap<Int, MutableList<Path>>, color: Int): MutableList<MutableList<Path>> {
    if (color > coloredPaths.size) {
        for (x in 0..grid.w - 1) {
            for (y in 0..grid.h - 1) {
                if (grid[x, y].color == 0) return mutableListOf()
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