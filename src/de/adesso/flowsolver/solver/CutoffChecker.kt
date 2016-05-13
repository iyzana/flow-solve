package de.adesso.flowsolver.solver

import de.adesso.flowsolver.solver.model.Grid
import de.adesso.flowsolver.solver.model.Node
import de.adesso.flowsolver.solver.model.Path
import java.util.LinkedList

/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 06.05.2016
 */
fun neighbor(grid: Grid, x: Int, y: Int, opened: MutableList<Node>, closed: MutableSet<Node>, result: MutableList<Node>) {
    if (valid(grid, x, y)) {
        val neighbor = grid[x, y]
        if(!closed.add(neighbor)) return
    
        if (neighbor.color == 0) opened.add(neighbor)
        else if (neighbor.color > 0) result.add(neighbor)
    }
}

fun isCutoff(grid: Grid, by: Path, colors: Map<Int, Pair<Path, Path>>): Boolean {
    val closed = by.nodes().map { grid[Node.x(it), Node.y(it)] }.toMutableSet()
    
    val nodePairs = mutableSetOf<Int>();
    
    for (x in 0..grid.w - 1) {
        for (y in 0..grid.h - 1) {
            val node = grid[x, y]
            if (node in closed) continue
            if (node.color != 0) continue
            
            val opened = LinkedList<Node>()
            val results = mutableListOf<Node>()
            opened.add(node)
            closed.add(node)
            while (!opened.isEmpty()) {
                val current = opened.pop()
                
                neighbor(grid, current.x, current.y - 1, opened, closed, results)
                neighbor(grid, current.x + 1, current.y, opened, closed, results)
                neighbor(grid, current.x, current.y + 1, opened, closed, results)
                neighbor(grid, current.x - 1, current.y, opened, closed, results)
            }
            
            if (results.isEmpty()) return true
            
            closed.removeAll(results)
            
            val resultColors = results.map { it.color }.toMutableList()
            resultColors.distinct().forEach { color -> resultColors.remove(color) }
            nodePairs.addAll(resultColors)
        }
    }
    
    val cutOffColors = colors.keys.toMutableList()
    cutOffColors.removeAll(nodePairs)
    
    for (color in cutOffColors) {
        val pair = colors[color]!!
        
        if (pathExists(grid, pair.first.lastNode(color), pair.second.lastNode(color))) nodePairs.add(color)
        else return true
    }
    
    return !nodePairs.containsAll(colors.keys)
}