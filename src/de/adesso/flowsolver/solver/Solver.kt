package de.adesso.flowsolver.solver

import de.adesso.flowsolver.solver.model.Grid
import de.adesso.flowsolver.solver.model.Node
import de.adesso.flowsolver.solver.model.Path
import de.adesso.flowsolver.solver.model.PathsData
import java.util.HashMap
import kotlin.system.measureTimeMillis

/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 28.04.2016
 */
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

fun solve(grid: Grid) {
    val w = grid.w
    val h = grid.h
    
    val points = extractPairs(grid).values.flatMap { listOf(it.first, it.second) }
    val newPoints = fillGrid(+grid, points)
    +grid
    
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
    
    println("timePaths = " + measureTimeMillis {
        for ((color, pair) in pairs.entries) {
            print("color $color")
            
            val maxLength = pathSum + shortestPaths[color]!!
            print(" maxLength $maxLength ")
            
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
            
            //            for (color2 in 1..color - 1) {
            //                print("filtering $color2 before ${coloredPaths[color2]!!.size} ")
            //                coloredPaths[color2]!!.retainAll { path ->
            //                    if (pathsData.intersectsAll(path, color)) {
            //                        pathsData.remove(color, path)
            //                        return@retainAll false
            //                    }
            //
            //                    return@retainAll true
            //                }
            //                println("after ${coloredPaths[color2]!!.size}")
            //            }
        }
    } + " ms")
    
    //    pathsData.createStatisticalData()
    
    println()
    println("filtering paths...")
    
    do {
        var changed = false
        
        val sizeSorted = coloredPaths.toList().sortedBy { it.second.size }
        for ((color, paths) in sizeSorted) {
            //            print("color $color start " + paths.size)
            val sizeSorted2 = sizeSorted.toList().sortedBy { it.second.size }
            for ((otherColor, otherPaths) in sizeSorted2) {
                if (color == otherColor) continue
                if (paths.isEmpty()) {
                    print('#')
                    continue
                }
                print("color $color with $otherColor start " + paths.size)
                paths.retainAll { path ->
                    if (pathsData.intersectsAll(path, otherColor)) {
                        pathsData.remove(color, path)
                        changed = true
                        return@retainAll false
                    }
                    
                    return@retainAll true
                }
                println(" end " + paths.size)
            }
            //            println(" end " + paths.size) 
        } // Path(path = [(Node(x=1, y=2, color=0)), (Node(x=1, y=3, color=0)), (Node(x=2, y=3, color=0)), (Node(x=3, y=3, color=0)), (Node(x=4, y=3, color=0)), (Node(x=4, y=4, color=0))]
        println()
        println()
        println()
    } while (changed)
    
    
    
    println("filling grid")
    
    coloredPaths.forEach { color, paths ->
        paths.single().forEach { node ->
            grid[Node.x(node), Node.y(node)].color = color
        }
    }
    
    +grid
    
    println("appending pre and postfix...")
    
    val completePaths = coloredPaths.mapValues { e ->
        val color = e.key
        val paths = e.value
        
        paths.map { path ->
            val pre = pairs[color]!!.first
            val post = pairs[color]!!.second
            val size = path.size + pre.size + post.size - 2
            
            val complete = Path(size)
            post.nodes().dropLast(1).forEach { complete.add(it) }
            path.nodes().forEach { complete.add(it) }
            pre.nodes().dropLast(1).reversed().forEach { complete.add(it) }
            complete
        }
    }
    
    println()
    println("filtered paths")
    completePaths.forEach { entry ->
        val color = entry.key
        val paths = entry.value
        
        println("color $color")
        paths.forEach { println(it) }
    }
}


