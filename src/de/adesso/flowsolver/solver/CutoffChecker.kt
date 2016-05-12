package de.adesso.flowsolver.solver

import de.adesso.flowsolver.solver.model.Grid
import de.adesso.flowsolver.solver.model.Node
import de.adesso.flowsolver.solver.model.Path

/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 06.05.2016
 */
fun main(args: Array<String>) {
    val grid = Grid.fromString("baa,aaa,baa")
    val path = Path(3)
//    path.add(Node(0, 1).compressed())
    
    println(isCutoff(grid, path, 0, listOf(1)))
}

fun isCutoff(grid: Grid, by: Path, color: Int, colors: List<Int>): Boolean {
    val closed = by.nodes().map { grid[Node.x(it), Node.y(it)] }.toMutableSet()
    
    fun valid(x: Int, y: Int) = x >= 0 && y >= 0 && x < grid.w && y < grid.h
    
    fun neighbor(x: Int, y: Int, opened: MutableList<Node>, closed: MutableSet<Node>, result: MutableList<Int>) {
        if (valid(x, y)) {
            val neighbor = grid[x, y]
            if (!closed.add(neighbor)) return
            neighbor.color = -1
            if (neighbor.color == 0) opened.add(neighbor)
            else if (neighbor.color > 0) result.add(neighbor.color)
        }
    }
    
    val nodePairs = mutableSetOf<Int>();
    
    for (x in 0..grid.w - 1) {
        for (y in 0..grid.h - 1) {
            val node = grid[x, y]
            if (node in closed) continue
            if (node.color != 0) continue
            
            val opened = mutableListOf<Node>()
            val results = mutableListOf<Int>()
            opened.add(node)
            while (!opened.isEmpty()) {
                val current = opened[0]
                if (!closed.add(current)) continue
                
                neighbor(current.x, current.y - 1, opened, closed, results)
                neighbor(current.x + 1, current.y, opened, closed, results)
                neighbor(current.x, current.y + 1, opened, closed, results)
                neighbor(current.x - 1, current.y, opened, closed, results)
            }
            
            results.removeAll(results.distinct())
            nodePairs.addAll(results)
        }
    }
    
    return colors.size > nodePairs.size
}