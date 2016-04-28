package de.adesso.flowsolver

import java.util.HashMap
import java.util.LinkedList

/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 28.04.2016
 */
data class Path(val nodes: MutableList<Node>) : MutableList<Node> by nodes {
    constructor(vararg node: Node) : this(node.toMutableList())
    
    override fun toString() = "Path(color = " + nodes[0].color + ", path = [" +
            nodes.joinToString(separator = ", ") { "(${it.x}, ${it.y})" } + "]"
}

fun distance(node1: Node, node2: Node) = Math.abs(node1.x - node2.x) + Math.abs(node1.y - node2.y)

fun shortestPath(grid: Grid, start: Node, end: Node): Path {
    val parents = HashMap<Node, Node>()
    val queue = LinkedList<Node>()
    val closed = mutableSetOf<Node>()
    queue.add(start)
    
    while (!queue.isEmpty()) {
        println(queue.size)
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

fun allPaths(grid: Grid, start: Node, end: Node): List<Path> {
    val solutions = LinkedList<Path>()
    
    if (start == end) return mutableListOf(Path(end))
    
    for (d in 0..3) {
        var x = start.x
        var y = start.y
        
        when (d) {
            0 -> y--
            1 -> x++
            2 -> y++
            3 -> x--
        }
        
        if (x < 0 || y < 0 || x >= grid.w || y >= grid.h) continue
        
        val node = grid[x][y]
        if (node.color != 0 && node != end) continue
        
        node.color = start.color
        val paths = allPaths(grid, node, end)
        paths.forEach { path -> path.add(0, start) }
        solutions.addAll(paths)
        node.color = 0
    }
    
    return solutions
}