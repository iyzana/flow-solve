package de.adesso.flowsolver.solver

import de.adesso.flowsolver.solver.model.Grid
import de.adesso.flowsolver.solver.model.Node
import de.adesso.flowsolver.solver.model.Path
import de.adesso.flowsolver.solver.model.PathsData
import java.util.ArrayList
import java.util.HashMap
import java.util.LinkedList


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
            
            if (!valid(grid, x, y)) continue
            
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

fun allPaths(grid: Grid, start: Node, end: Node, pathsMap: PathsData, maxLength: Int, depth: Int = 0, path: Path = Path(maxLength)): List<Path> {
    val solutions = ArrayList<Path>()
    
    if (depth + distance(start, end) >= maxLength) return solutions
    
    if (start == end) {
        if (++foundPaths % 100000 == 0) println("foundPaths = ${foundPaths}")
        solutions.add(path.copy())
        return solutions
    }
    
    processNeighbors(path, grid, start, end, pathsMap, maxLength, depth, solutions)
    
    return solutions
}

private fun processNeighbors(path: Path, grid: Grid, start: Node, end: Node, pathsMap: PathsData, maxLength: Int, depth: Int, solutions: MutableList<Path>) {
    val x = start.x
    val y = start.y
    processNeighbor(x.toInt(), y - 1, path, grid, start, end, pathsMap, maxLength, depth, solutions)
    processNeighbor(x + 1, y.toInt(), path, grid, start, end, pathsMap, maxLength, depth, solutions)
    processNeighbor(x.toInt(), y + 1, path, grid, start, end, pathsMap, maxLength, depth, solutions)
    processNeighbor(x - 1, y.toInt(), path, grid, start, end, pathsMap, maxLength, depth, solutions)
}

private fun processNeighbor(x: Int, y: Int, path: Path, grid: Grid, start: Node, end: Node, pathsMap: PathsData, maxLength: Int, depth: Int, solutions: MutableList<Path>) {
    if (!valid(grid, x, y)) return
    
    var count = 0
    if (valid(grid, x, y - 1) && grid[x, y - 1].color == end.color && grid[x, y] != end) count++
    if (valid(grid, x + 1, y) && grid[x + 1, y].color == end.color && grid[x, y] != end) count++
    if (valid(grid, x, y + 1) && grid[x, y + 1].color == end.color && grid[x, y] != end) count++
    if (valid(grid, x - 1, y) && grid[x - 1, y].color == end.color && grid[x, y] != end) count++
    
    if(count == 2) return
    
    val node = grid[x, y]
    if (node.color != 0 && node != end) return
    
    setCallReset(node, path, grid, start, end, pathsMap, depth, maxLength, solutions)
}

private fun valid(grid: Grid, x: Int, y: Int) = x >= 0 && y >= 0 && x < grid.w && y < grid.h

private fun setCallReset(node: Node, path: Path, grid: Grid, start: Node, end: Node, pathsMap: PathsData, depth: Int, maxLength: Int, solutions: MutableList<Path>) {
    val previousColor = node.color
    path.add(node.compressed())
    
    //    if (depth < 5) {
    //        // TODO: filter by filling/splitting
    //        // TODO: Apply B paths to A after allPaths
    //        // TODO: Sort by size before filtering
    //        // TODO: Use previously built data (cache?)
    //        for (color in 1..start.color - 1)
    //            if (pathsMap.intersectsAll(path, color)) {
    //                print("a")
    //                path.remove()
    //                return
    //            }
    //    }
    
    node.color = start.color
    recursiveCall(node, path, grid, start, end, pathsMap, depth, maxLength, solutions)
    path.remove()
    node.color = previousColor
}

private fun recursiveCall(node: Node, path: Path, grid: Grid, start: Node, end: Node, pathsMap: PathsData, depth: Int, maxLength: Int, solutions: MutableList<Path>) {
    val paths = callRecursion(grid, node, path, end, pathsMap, depth, maxLength)
    addToSolutions(paths, solutions)
}

private fun callRecursion(grid: Grid, node: Node, path: Path, end: Node, pathsMap: PathsData, depth: Int, maxLength: Int) =
        allPaths(grid, node, end, pathsMap, maxLength, depth + 1, path)


private fun addToSolutions(paths: List<Path>, solutions: MutableList<Path>) {
    solutions.addAll(paths)
}