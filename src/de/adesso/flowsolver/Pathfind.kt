package de.adesso.flowsolver

import java.util.*

/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 28.04.2016
 */
data class Path(val nodes: MutableList<Node>) : MutableList<Node> by nodes {
    constructor(vararg nodes: Node) : this(LinkedList<Node>(nodes.toList()))
    
    override fun toString() = "Path(color = " + nodes[0].color + ", path = [" +
            nodes.joinToString(separator = ", ") { "(${it.x}, ${it.y})" } + "]"
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
            val path = Path()
            var parent = current
            
            path.add(parent)
            while (parent in parents) {
                parent = parents[parent]
                path.add(parent)
            }
            
            return Path(path.reversed().toMutableList())
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
            
            val node = grid[x][y]
            
            if (node.color != 0 && node.color != start.color) continue
            
            if (node in closed) continue
            closed.add(node)
            
            parents.put(node, current)
            queue.add(node)
        }
    }
    
    throw IllegalArgumentException("no path found")
}

fun allPaths(grid: Grid, start: Node, end: Node, maxLength: Int = 0, depth: Int = 0): List<Path> {
    val solutions = LinkedList<Path>()
    
    if(depth + distance(start, end) > maxLength) return solutions
    
    if (start == end) {
        solutions.add(Path(end))
        return solutions
    }
    
    for ((x, y) in neighbors(start.x, start.y)) {
        if (x < 0 || y < 0 || x >= grid.w || y >= grid.h) continue
        
        val node = grid[x][y]
        if (node.color != 0 && node != end) continue
        
        val previousColor = node.color
        node.color = start.color
        val paths = allPaths(grid, node, end, maxLength, depth + 1)
        paths.forEach { path -> path.add(0, start) }
        solutions.addAll(paths)
        node.color = previousColor
    }
    
    return solutions
}

private fun neighbors(x: Int, y: Int): List<Pair<Int, Int>> {
    return listOf(x to y-1, x+1 to y, x to y+1, x-1 to y)
}