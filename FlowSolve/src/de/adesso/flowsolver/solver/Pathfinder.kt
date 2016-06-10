package de.adesso.flowsolver.solver

import de.adesso.flowsolver.solver.model.Grid
import de.adesso.flowsolver.solver.model.Node
import de.adesso.flowsolver.solver.model.Path
import de.adesso.flowsolver.solver.model.x
import de.adesso.flowsolver.solver.model.y
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import java.util.LinkedList

fun distance(node1: Node, node2: Node) = Math.abs(node1.x - node2.x) + Math.abs(node1.y - node2.y)
fun distance(node1: Byte, node2: Byte) = Math.abs(node1.x - node2.x) + Math.abs(node1.y - node2.y)

fun shortestPath(grid: Grid, start: Node, end: Node): Path {
    val parents = HashMap<Node, Node>()
    val queue = LinkedList<Node>()
    val closed = mutableSetOf<Node>()
    queue.add(start)
    closed.add(start)
    
    while (!queue.isEmpty()) {
        val current = queue.pop()
        
        if (current == end) {
            var parent = current
            val path = Path(parents.size + 1, parent.compressed())
            
            while (parent in parents) {
                parent = parents[parent]
                path.add(parent.compressed())
            }
            
            return path
        }
        
        for (d in 0..3) {
            var x = current.x
            var y = current.y
            
            when (d) {
                0 -> y--
                1 -> x++
                2 -> y++
                3 -> x--
            }
            
            if (!grid.valid(x, y)) continue
            
            val node = grid[x, y]
            
            if (node.color != 0 && node.color != start.color) continue
            
            if (node in closed) continue
            closed.add(node)
            
            parents.put(node, current)
            queue.add(node)
        }
    }
    
    throw IllegalArgumentException("no path found")
}

fun pathExists(grid: Grid, start: Node, end: Node): Boolean {
    val stack = LinkedList<Node>()
    val closed = HashSet<Node>()
    stack.add(start)
    
    while (!stack.isEmpty()) {
        val current = stack.removeLast()
        if (!closed.add(current)) continue
        
        if (current == end) return true
        
        val x = current.x
        val y = current.y
        
        var dx = end.x - x
        var dy = end.y - y
        dx = if (dx > 0) 1 else if (dx < 0) -1 else 0
        dy = if (dy > 0) 1 else if (dy < 0) -1 else 0
        
        if (grid.valid(x, y - dy) && (grid[x, y - dy].color == 0 || grid[x, y - dy].color == end.color)) stack.add(grid[x, y - dy])
        if (grid.valid(x - dx, y) && (grid[x - dx, y].color == 0 || grid[x - dx, y].color == end.color)) stack.add(grid[x - dx, y])
        if (grid.valid(x, y + dy) && (grid[x, y + dy].color == 0 || grid[x, y + dy].color == end.color)) stack.add(grid[x, y + dy])
        if (grid.valid(x + dx, y) && (grid[x + dx, y].color == 0 || grid[x + dx, y].color == end.color)) stack.add(grid[x + dx, y])
    }
    
    return false
}

var foundPaths = 0

fun allPaths(start: Node,
             grid: Grid,
             end: Node,
             pairs: Map<Int, Pair<Path, Path>>,
             maxLength: Int,
             depth: Int = 0,
             path: Path = Path(maxLength),
             solutions: MutableList<Path> = ArrayList<Path>()): List<Path> {
    val findingData = FindingData(grid, end, pairs, maxLength, depth, path, solutions)
    
    return allPaths(start, findingData)
}

private data class FindingData(val grid: Grid,
                               val end: Node,
                               val pairs: Map<Int, Pair<Path, Path>>,
                               val maxLength: Int,
                               var depth: Int = 0,
                               val path: Path = Path(maxLength),
                               val solutions: MutableList<Path> = ArrayList<Path>())

private fun allPaths(current: Node, findingData: FindingData): List<Path> {
    val (grid, end, pairs, maxLength, depth, path, solutions) = findingData
    
    if (depth + distance(current, end) >= maxLength) return solutions
    
    if (current == end) {
        if (++foundPaths % 100000 == 0) println("foundPaths = $foundPaths")
        solutions.add(path.copy())
        return solutions
    }
    
    if (isCutoff(grid, path, pairs, end.color)) {
        return solutions
    }
    
    processNeighbors(current, findingData)
    
    return solutions
}

private fun processNeighbors(current: Node, findingData: FindingData) {
    val x = current.x
    val y = current.y
    processNeighbor(x.toInt(), y - 1, findingData)
    processNeighbor(x + 1, y.toInt(), findingData)
    processNeighbor(x.toInt(), y + 1, findingData)
    processNeighbor(x - 1, y.toInt(), findingData)
}

private fun processNeighbor(x: Int, y: Int, findingData: FindingData) {
    val (grid, end, pairs) = findingData
    if (!grid.valid(x, y)) return
    
    var count = 0
    
    val endPath = pairs[end.color]!!.second
    
    if (grid.valid(x, y - 1) && grid[x, y - 1].color == end.color && grid[x, y - 1] !in endPath) count++
    if (grid.valid(x + 1, y) && grid[x + 1, y].color == end.color && grid[x + 1, y] !in endPath) count++
    if (grid.valid(x, y + 1) && grid[x, y + 1].color == end.color && grid[x, y + 1] !in endPath) count++
    if (grid.valid(x - 1, y) && grid[x - 1, y].color == end.color && grid[x - 1, y] !in endPath) count++
    
    if (count >= 2) return
    
    val node = grid[x, y]
    if (node.color != 0 && node != end) return
    
    setCallReset(node, findingData)
}

private fun setCallReset(node: Node, findingData: FindingData) {
    val (grid, end, pairs, maxLength, depth, path) = findingData
    
    val previousColor = node.color
    path.add(node.compressed())
    node.color = end.color
    
    // TODO: Check for empty spaces
    // TODO: filter by filling/splitting
    // TODO: Apply B paths to A after allPaths
    // TODO: Sort by size before filtering
    // TODO: Use previously built data (cache?)
    //    if (depth == 8) {
    //        for (color in 1..start.color - 1) {
    //            if (pathsMap.safeIntersectsAll(path, color)) {
    //                path.remove()
    //                node.color = previousColor
    //                return
    //            }
    //        }
    //    }
    
    findingData.depth++
    allPaths(node, findingData)
    findingData.depth--
    path.remove()
    node.color = previousColor
}