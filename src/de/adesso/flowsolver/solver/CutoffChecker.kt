package de.adesso.flowsolver.solver

import de.adesso.flowsolver.solver.model.Grid
import de.adesso.flowsolver.solver.model.Node
import de.adesso.flowsolver.solver.model.Path
import de.adesso.flowsolver.solver.model.x
import de.adesso.flowsolver.solver.model.y
import java.util.HashSet
import java.util.LinkedList

/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 06.05.2016
 */
fun neighbor(grid: Grid, x: Int, y: Int, opened: MutableList<Node>, closed: MutableSet<Node>, result: MutableList<Node>): Int {
    if (valid(grid, x, y)) {
        val neighbor = grid[x, y]
        if (!closed.add(neighbor)) return neighbor.color
        
        if (neighbor.color == 0) opened.add(neighbor)
        else if (neighbor.color > 0) result.add(neighbor)
        return neighbor.color
    }
    
    return -1
}

// TODO: Check by filling bottleneck temporarily
fun isCutoff(grid: Grid, by: Path, colors: Map<Int, Pair<Path, Path>>, pathColor: Int): Boolean {
//    if (preCheckByNeighbors(by, grid)) return false
    val nodes = by.nodes()
    
    if (nodes.size >= 2) {
        val previous = Node(nodes[nodes.lastIndex - 1])
        
        val end = colors[pathColor]!!.second.lastNode(pathColor)
        if (distance(previous, end) == 1) return false
    }
    
    val closed = HashSet<Node>(grid.w * grid.h)
    closed.addAll(by.nodes().dropLast(1).map { grid[it.x, it.y] })
    
    val nodePairs = mutableSetOf<Int>()
    
    for (node in grid.nodes) {
        if (node in closed) continue
        if (node.color != 0) continue
        
        val opened = LinkedList<Node>()
        val results = mutableListOf<Node>()
        opened.add(node)
        closed.add(node)
        stack@ while (!opened.isEmpty()) {
            val current = opened.pop()
            
            val color1 = neighbor(grid, current.x, current.y - 1, opened, closed, results)
            val color2 = neighbor(grid, current.x + 1, current.y, opened, closed, results)
            val color3 = neighbor(grid, current.x, current.y + 1, opened, closed, results)
            val color4 = neighbor(grid, current.x - 1, current.y, opened, closed, results)
            
            /* Disallowing the following
                x1x
                1.1
             */
            val color = if (color1 > 0) color1 else if (color2 > 0) color2 else continue
            var count = 0
            var wallCount = 0
            val start = colors[color]?.first
            val end = colors[color]?.second
            if (color1 == color && grid[current.x, current.y - 1] !in end!!) count++
            if (color2 == color && grid[current.x + 1, current.y] !in end!!) count++
            if (color3 == color && grid[current.x, current.y + 1] !in end!!) count++
            if (color4 == color && grid[current.x - 1, current.y] !in end!!) count++
            if (color1 == -1) wallCount++
            if (color2 == -1) wallCount++
            if (color3 == -1) wallCount++
            if (color4 == -1) wallCount++
            
            if (count == 3) return true
            if (count == 2) {
                if (wallCount >= 1) return true
                val (dx, dy) = when {
                    color1 == color2 && color1 == color -> 1 to 1
                    color2 == color3 && color2 == color -> 1 to -1
                    color3 == color4 && color3 == color -> -1 to -1
                    color4 == color1 && color4 == color -> -1 to 1
                    else -> continue@stack
                }
                val color5 = grid[current.x + dx, current.y + dy].color
                val color6 =
                        if (valid(grid, current.x, current.y + 2 * dy))
                            grid[current.x, current.y + 2 * dy].color
                        else color
                val color7 = grid[current.x, current.y + dy].color
                if (color5 == color && color6 == color && color7 == 0)
                    return true
                
                val color8 = grid[current.x - dx, current.y - dy].color
                val color9 =
                        if (valid(grid, current.x - 2 * dx, current.y))
                            grid[current.x - 2 * dx, current.y].color
                        else color
                val color10 = grid[current.x - dx, current.y].color
                if (color8 == color && color9 == color && color10 == 0)
                    return true
            }
        }
        
        closed.removeAll(results)
        
        val resultColors = results.map { it.color }.toMutableList()
        resultColors.distinct().forEach { color -> resultColors.remove(color) }
        
        if (resultColors.isEmpty()) return true
        
        nodePairs.addAll(resultColors)
        
        if (closed.size == grid.w * grid.h)
            break
    }
    
    val cutOffColors = colors.keys.toMutableList()
    cutOffColors.remove(pathColor)
    cutOffColors.removeAll(nodePairs)
    
    for (color in cutOffColors) {
        val pair = colors[color]!!
        
        if (pathExists(grid, pair.first.lastNode(color), pair.second.lastNode(color))) nodePairs.add(color)
        else return true
    }
    
    return !nodePairs.containsAll(colors.keys - pathColor)
}

/**
 * Check if any blocked field is nearby the last path segment
 */
private fun preCheckByNeighbors(by: Path, grid: Grid): Boolean {
    val nodes = by.nodes()
    
    if (nodes.size >= 2) {
        val lastNode = nodes.last()
        val previous = nodes[nodes.lastIndex - 1]
        val fromX = lastNode.x - previous.x
        val fromY = lastNode.y - previous.y
        
        val x = lastNode.x
        val y = lastNode.y
        loop@ for (dx in Math.max(-1, -1 + fromX)..Math.min(1, 1 + fromX)) {
            for (dy in Math.max(-1, -1 + fromY)..Math.min(1, 1 + fromY)) {
                if (dx == 0 && dy == 0) continue
                if (!valid(grid, x + dx, y + dy))
                    return false
                if (grid[x + dx, y + dy].color != 0)
                    return false
                
            }
        }
        return true
    }
    
    return false
}