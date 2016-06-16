package de.adesso.flowsolver.solver

import de.adesso.flowsolver.solver.model.Grid
import de.adesso.flowsolver.solver.model.Node
import de.adesso.flowsolver.solver.model.Path
import de.adesso.flowsolver.solver.model.x
import de.adesso.flowsolver.solver.model.y
import java.util.ArrayList
import java.util.LinkedList

/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 06.05.2016
 */

fun BooleanArray.add(node: Byte): Boolean {
    val node = node.toUInt()
    val ret = !this[node]
    this[node] = true
    return ret
}

fun Byte.toUInt() = toInt() + 127

fun BooleanArray.remove(node: Byte): Boolean {
    val node = node.toUInt()
    val ret = this[node]
    this[node] = false
    return ret
}

operator fun BooleanArray.contains(node: Byte) = this[node.toUInt()]
fun BooleanArray.addAll(elements: Collection<Byte>) = elements.fold(initial = false) { current, node -> add(node) || current }
fun BooleanArray.removeAll(elements: Collection<Byte>) = elements.fold(initial = false) { current, node -> remove(node) || current }

fun neighbor(grid: Grid, x: Int, y: Int, opened: MutableList<Node>, closed: BooleanArray, result: MutableList<Node>): Int {
    if (grid.valid(x, y)) {
        val neighbor = grid[x, y]
        
        if (!closed.add(neighbor.compressed())) return neighbor.color
        
        if (neighbor.color == 0) opened.add(neighbor)
        else if (neighbor.color > 0) result.add(neighbor)
        return neighbor.color
    }
    
    return -1
}


fun isCutoff(grid: Grid, by: Path, colors: Map<Int, Pair<Path, Path>>, pathColor: Int): Boolean {
    if (by.size == 0) return false
    if (checkCutoff(grid, by, colors, pathColor, false) > 0)
        return true
    val bottleNecks = identifyBottlenecks(by, grid)
    bottleNecks.filter { distance(it.compressed(), by.last()) <= 4 }.forEach { bottleNeck ->
        bottleNeck.color = -1
        val cutoffColors = checkCutoff(grid, by, colors, pathColor, true)
        bottleNeck.color = 0
        if (cutoffColors > 1) return true
    }
    
    return false
}

private fun checkCutoff(grid: Grid, by: Path, colors: Map<Int, Pair<Path, Path>>, pathColor: Int, withBottlenecks: Boolean): Int {
//    if (preCheckByNeighbors(by, grid)) return false
    
    if (by.size >= 2) {
        val previous = by.nodes[by.size - 2]
        val end = colors[pathColor]!!.second.last()
        if (distance(previous, end) == 1) return colors.size
    }
    
    val closed = BooleanArray(256)
    closed.addAll(by.nodes().dropLast(1).map { grid[it.x, it.y].compressed() })
    
    val nodePairs = mutableSetOf<Int>()
    
    for (node in grid.nodes.asSequence().filter { it.color == 0 }) {
        if (node.compressed() in closed) continue
        
        val opened = LinkedList<Node>()
        val results = mutableListOf<Node>()
        opened.add(node)
        closed.add(node.compressed())
        stack@ while (!opened.isEmpty()) {
            val current = opened.pop()
            
            val color1 = neighbor(grid, current.x, current.y - 1, opened, closed, results)
            val color2 = neighbor(grid, current.x + 1, current.y, opened, closed, results)
            val color3 = neighbor(grid, current.x, current.y + 1, opened, closed, results)
            val color4 = neighbor(grid, current.x - 1, current.y, opened, closed, results)
            
            if (!withBottlenecks && containsIllegalPattern(color1, color2, color3, color4, colors, current, grid)) return colors.size
        }
        
        closed.removeAll(results.map { it.compressed() })
        
        val resultColors = results.map { it.color }.toMutableList()
        resultColors.distinct().forEach { color -> resultColors.remove(color) }
        
        // TODO: If only one group direct connection must fill all fields
        // TODO: Illegal pattern checking near end
        // TODO: Bottleneck finding next to wall
        
        if (!withBottlenecks && resultColors.isEmpty()) return colors.size
        
        nodePairs.addAll(resultColors)
    }
    
    val cutOffColors = colors.keys.toMutableList()
    cutOffColors.remove(pathColor)
    nodePairs.remove(pathColor)
    cutOffColors.removeAll(nodePairs)
    
    for (color in cutOffColors) {
        val pair = colors[color]!!
        
        if (pathExists(grid, pair.first.lastNode(color), pair.second.lastNode(color))) nodePairs.add(color)
    }
    
    return colors.size - nodePairs.size - 1
}

private fun containsIllegalPattern(color1: Int, color2: Int, color3: Int, color4: Int, colors: Map<Int, Pair<Path, Path>>, current: Node, grid: Grid): Boolean {
    /* Disallowing the following
                x1x
                1.1
             */
    val color = if (color1 > 0) color1 else if (color2 > 0) color2 else if (color3 > 0) color3 else return false
    var count = 0
    val start = colors[color]?.first
    val end = colors[color]?.second
    if (color1 == color) count++ // && grid[current.x, current.y - 1] !in end!!
    if (color2 == color) count++ // && grid[current.x + 1, current.y] !in end!!
    if (color3 == color) count++ // && grid[current.x, current.y + 1] !in end!!
    if (color4 == color) count++ // && grid[current.x - 1, current.y] !in end!!
    
    if (count == 3) return true
    
    count = 0
    if (color1 == color && grid[current.x, current.y - 1] !in end!!) count++
    if (color2 == color && grid[current.x + 1, current.y] !in end!!) count++
    if (color3 == color && grid[current.x, current.y + 1] !in end!!) count++
    if (color4 == color && grid[current.x - 1, current.y] !in end!!) count++
    var wallCount = 0
    if (color1 == -1) wallCount++
    if (color2 == -1) wallCount++
    if (color3 == -1) wallCount++
    if (color4 == -1) wallCount++
    
    if (count == 2) {
        /* Disallowing the following
                    x11x
                    1..1
                */
        if (wallCount >= 1) return true
        val (dx, dy) = when {
            color1 == color2 && color1 == color -> 1 to 1
            color2 == color3 && color2 == color -> 1 to -1
            color3 == color4 && color3 == color -> -1 to -1
            color4 == color1 && color4 == color -> -1 to 1
            else -> return false
        }
        val color5 = grid[current.x + dx, current.y + dy].color
        val color6 =
                if (grid.valid(current.x, current.y + 2 * dy))
                    grid[current.x, current.y + 2 * dy].color
                else color
        val color7 = grid[current.x, current.y + dy].color
        if (color5 == color && color6 == color && color7 == 0)
            return true
        
        val color8 = grid[current.x - dx, current.y - dy].color
        val color9 =
                if (grid.valid(current.x - 2 * dx, current.y))
                    grid[current.x - 2 * dx, current.y].color
                else color
        val color10 = grid[current.x - dx, current.y].color
        if (color8 == color && color9 == color && color10 == 0)
            return true
    }
    
    if (count == 1 && wallCount == 2) {
        val dx = if (current.x == 0) 1 else -1
        val dy = if (current.y == 0) 1 else -1
        
        if (grid[current.x + dx, current.y + dy].color == color) {
            if (grid[current.x + dx, current.y].color == color) return true
            if (grid[current.x, current.y + dy].color == color) return true
        }
    }
    
    return false
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
                if (!grid.valid(x + dx, y + dy))
                    return false
                if (grid[x + dx, y + dy].color != 0)
                    return false
                
            }
        }
        return true
    }
    
    return false
}

val bottleNeckCache = ArrayList<Node>();
val bottleNeckPatterns = arrayOf(12, 17, 24, 25, 28, 29, 34, 36, 38, 44, 48, 52, 56, 57, 60, 61, 65, 66, 67, 68, 70, 71, 97, 98, 99, 100, 102, 103, 136, 137, 145, 152, 153, 156, 157, 184, 185, 188, 189, 193, 194, 195, 198, 199, 226, 227, 230, 231)

fun cacheBottlenecks(grid: Grid) {
    /*
    * BottleNeck patterns are generated as follows:
    * 1 2 3
    * 4 . 5
    * 6 7 8
    * 
    * The number is the bit position
    * */
    grid.nodes.filter { n -> n.color == 0 }.forEach { node ->
        if (bottleNeckPatterns.contains(generatePattern(grid, node)) && bottleNeckCache.none { distance(it, node) == 1 })
            bottleNeckCache.add(node)
    }
    
}

private fun identifyBottlenecks(by: Path, grid: Grid): List<Node> {
    val bottleNecks = ArrayList<Node>()
    for (dx in -1..1) {
        for (dy in -1..1) {
            if (dx == 0 && dy == 0) continue
            val node = grid[by.last().x + dx, by.last().y + dy, -1]
            if (node.color == 0 && bottleNeckPatterns.contains(generatePattern(grid, node)) && bottleNecks.none { distance(it, node) == 1 })
                bottleNecks.add(node)
        }
    }
    return bottleNeckCache + bottleNecks
}

private fun generatePattern(grid: Grid, node: Node): Int {
    var pattern = 0
    var index = 0
    
    for (dx in -1..1) {
        for (dy in -1..1) {
            if (dx == 0 && dy == 0) continue
            pattern = pattern or ((if (grid[node.x + dx, node.y + dy, 1].color > 0) 1 else 0) shl index)
            index++
        }
    }
    return pattern
}