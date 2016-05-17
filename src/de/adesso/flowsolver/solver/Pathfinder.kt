package de.adesso.flowsolver.solver

import de.adesso.flowsolver.solver.model.Grid
import de.adesso.flowsolver.solver.model.Node
import de.adesso.flowsolver.solver.model.Path
import de.adesso.flowsolver.solver.model.PathsData
import java.util.*


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
        
        if (valid(grid, x, y - dy) && (grid[x, y - dy].color == 0 || grid[x, y - dy].color == end.color)) stack.add(grid[x, y - dy])
        if (valid(grid, x - dx, y) && (grid[x - dx, y].color == 0 || grid[x - dx, y].color == end.color)) stack.add(grid[x - dx, y])
        if (valid(grid, x, y + dy) && (grid[x, y + dy].color == 0 || grid[x, y + dy].color == end.color)) stack.add(grid[x, y + dy])
        if (valid(grid, x + dx, y) && (grid[x + dx, y].color == 0 || grid[x + dx, y].color == end.color)) stack.add(grid[x + dx, y])
    }
    
    return false
}

var foundPaths = 0;

fun allPaths(grid: Grid,
             start: Node,
             end: Node,
             pathsMap: PathsData,
             maxLength: Int,
             pairs: Map<Int, Pair<Path, Path>>,
             depth: Int = 0,
             path: Path = Path(maxLength),
             solutions: MutableList<Path> = ArrayList<Path>()): List<Path> {
    if (end.color == 4 && start.x == 4 && start.y == 6)
        Math.random();
    
    if (depth + distance(start, end) >= maxLength) return solutions
    
    if (start == end) {
        if (++foundPaths % 100000 == 0) println("foundPaths = ${foundPaths}")
        solutions.add(path.copy())
        return solutions
    }
    
    processNeighbors(path, grid, start, end, pathsMap, maxLength, pairs, depth, solutions)
    
    return solutions
}

private fun processNeighbors(path: Path,
                             grid: Grid,
                             start: Node,
                             end: Node,
                             pathsMap: PathsData,
                             maxLength: Int,
                             pairs: Map<Int, Pair<Path, Path>>,
                             depth: Int,
                             solutions: MutableList<Path>) {
    val x = start.x
    val y = start.y
    processNeighbor(x.toInt(), y - 1, path, grid, end, pathsMap, maxLength, pairs, depth, solutions)
    processNeighbor(x + 1, y.toInt(), path, grid, end, pathsMap, maxLength, pairs, depth, solutions)
    processNeighbor(x.toInt(), y + 1, path, grid, end, pathsMap, maxLength, pairs, depth, solutions)
    processNeighbor(x - 1, y.toInt(), path, grid, end, pathsMap, maxLength, pairs, depth, solutions)
}

private fun processNeighbor(x: Int,
                            y: Int,
                            path: Path,
                            grid: Grid,
                            end: Node,
                            pathsMap: PathsData,
                            maxLength: Int,
                            pairs: Map<Int, Pair<Path, Path>>,
                            depth: Int,
                            solutions: MutableList<Path>) {
    if (!valid(grid, x, y)) return
    
    var count = 0
    
    
    val startPath = pairs[end.color]!!.first
    val endPath = pairs[end.color]!!.second
    
    if (valid(grid, x, y - 1) && grid[x, y - 1].color == end.color && grid[x, y - 1] !in endPath) count++
    if (valid(grid, x + 1, y) && grid[x + 1, y].color == end.color && grid[x + 1, y] !in endPath) count++
    if (valid(grid, x, y + 1) && grid[x, y + 1].color == end.color && grid[x, y + 1] !in endPath) count++
    if (valid(grid, x - 1, y) && grid[x - 1, y].color == end.color && grid[x - 1, y] !in endPath) count++
    
    if (count >= 2) return
    
    val node = grid[x, y]
    if (node.color != 0 && node != end) return
    
    setCallReset(node, path, grid, end, pathsMap, depth, maxLength, pairs, solutions)
}

fun valid(grid: Grid, x: Int, y: Int) = x >= 0 && y >= 0 && x < grid.w && y < grid.h

private fun setCallReset(node: Node,
                         path: Path,
                         grid: Grid,
                         end: Node,
                         pathsMap: PathsData,
                         depth: Int,
                         maxLength: Int,
                         pairs: Map<Int, Pair<Path, Path>>,
                         solutions: MutableList<Path>) {
    
    
    val previousColor = node.color
    path.add(node.compressed())
    node.color = end.color
    
    if (isCutoff(grid, path, pairs, end.color)) {
        path.remove()
        node.color = previousColor
        return
    }
    
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
    
    allPaths(grid, node, end, pathsMap, maxLength, pairs, depth + 1, path, solutions)
    path.remove()
    node.color = previousColor
}