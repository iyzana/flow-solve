package de.adesso.flowsolver

import java.util.*


/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 28.04.2016
 */
data class Path(val nodes: LinkedList<Node>) : Deque<Node> by nodes {
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

            return Path(LinkedList(path.reversed()))
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

    if (depth + distance(start, end) > maxLength) return solutions

    if (start == end) {
        solutions.add(Path(end))
        return solutions
    }

    processNeighbors(depth, end, grid, maxLength, solutions, start)

    return solutions
}

private fun processNeighbors(depth: Int, end: Node, grid: Grid, maxLength: Int, solutions: LinkedList<Path>, start: Node) {
    val x = start.x
    val y = start.y
    processNeighbor(depth, end, grid, maxLength, solutions, start, x, y - 1)
    processNeighbor(depth, end, grid, maxLength, solutions, start, x + 1, y)
    processNeighbor(depth, end, grid, maxLength, solutions, start, x, y + 1)
    processNeighbor(depth, end, grid, maxLength, solutions, start, x - 1, y)
}

private fun processNeighbor(depth: Int, end: Node, grid: Grid, maxLength: Int, solutions: LinkedList<Path>, start: Node, x: Int, y: Int) {
    if (x < 0 || y < 0 || x >= grid.w || y >= grid.h) return

    val node = grid[x][y]
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
    paths.forEach { path -> path.add(start) }
}

private fun addToSolutions(paths: List<Path>, solutions: LinkedList<Path>) {
    solutions.addAll(paths)
}

private fun neighbors(x: Int, y: Int): List<Pair<Int, Int>> {
    return listOf(x to y - 1, x + 1 to y, x to y + 1, x - 1 to y)
}