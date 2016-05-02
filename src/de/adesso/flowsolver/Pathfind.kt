package de.adesso.flowsolver

import java.util.HashMap
import java.util.LinkedList


/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 28.04.2016
 */
class Path(size: Int, node: Byte) {
    val nodes = ByteArray(size)
    var pos = 0
    
    val size: Int
        get() = pos
    
    init {
        add(node)
    }
    
    fun add(node: Byte) {
        nodes[pos++] = node
    }
    
    override fun toString() = "Path(path = [" +
            nodes.joinToString(separator = ", ") { "(${x(it)}, ${y(it)})" } + "]"
}

fun distance(node1: Node, node2: Node) = Math.abs(node1.x - node2.x) + Math.abs(node1.y - node2.y)

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
            
            if (x < 0 || y < 0 || x >= grid.w || y >= grid.h) continue
            
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

var foundPaths = 0;

fun allPaths(grid: Grid, start: Node, end: Node, maxLength: Int = 0, depth: Int = 0): List<Path> {
    val solutions = LinkedList<Path>()
    
    if (depth + distance(start, end) >= maxLength) return solutions
    
    if (start == end) {
        if (++foundPaths % 100000 == 0) println("foundPaths = $foundPaths")
        solutions.add(Path(maxLength, end.compressed()))
        return solutions
    }
    
    processNeighbors(depth, end, grid, maxLength, solutions, start)
    
    return solutions
}

private fun processNeighbors(depth: Int, end: Node, grid: Grid, maxLength: Int, solutions: LinkedList<Path>, start: Node) {
    val x = start.x
    val y = start.y
    processNeighbor(depth, end, grid, maxLength, solutions, start, x.toInt(), y - 1)
    processNeighbor(depth, end, grid, maxLength, solutions, start, x + 1, y.toInt())
    processNeighbor(depth, end, grid, maxLength, solutions, start, x.toInt(), y + 1)
    processNeighbor(depth, end, grid, maxLength, solutions, start, x - 1, y.toInt())
}

private fun processNeighbor(depth: Int, end: Node, grid: Grid, maxLength: Int, solutions: LinkedList<Path>, start: Node, x: Int, y: Int) {
    if (x < 0 || y < 0 || x >= grid.w || y >= grid.h) return
    
    val node = grid[x, y]
    if (node.color != 0 && node != end) return
    
    setCallReset(depth, end, grid, maxLength, node, solutions, start)
}

private fun setCallReset(depth: Int, end: Node, grid: Grid, maxLength: Int, node: Node, solutions: LinkedList<Path>, start: Node) {
    val previousColor = node.color
    node.color = start.color
    recursiveCall(depth, end, grid, maxLength, node, solutions, start)
    node.color = previousColor
}

private fun recursiveCall(depth: Int, end: Node, grid: Grid, maxLength: Int, node: Node, solutions: LinkedList<Path>, start: Node) {
    val paths = callRecursion(depth, end, grid, maxLength, node)
    addCurrentFirst(paths, start)
    addToSolutions(paths, solutions)
}

private fun callRecursion(depth: Int, end: Node, grid: Grid, maxLength: Int, node: Node) = allPaths(grid, node, end, maxLength, depth + 1)

private fun addCurrentFirst(paths: List<Path>, start: Node) {
    for (path in paths) path.add(start.compressed())
}

private fun addToSolutions(paths: List<Path>, solutions: LinkedList<Path>) {
    solutions.addAll(paths)
}

private fun neighbors(x: Int, y: Int): List<Pair<Int, Int>> {
    return listOf(x to y - 1, x + 1 to y, x to y + 1, x - 1 to y)
}