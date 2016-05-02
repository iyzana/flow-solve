package de.adesso.flowsolver

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

fun allPaths(grid: Grid, start: Node, end: Node, pathsMap: PathsData, maxLength: Int = 0, depth: Int = 0): List<Path> {
    val solutions = ArrayList<Path>()

    if (depth + distance(start, end) >= maxLength) return solutions

    if (start == end) {
        if (++foundPaths % 100000 == 0) println("foundPaths = $foundPaths")
        solutions.add(Path(maxLength, end.compressed()))
        return solutions
    }

    processNeighbors(grid, start, end, pathsMap, maxLength, depth, solutions)

    return solutions
}

private fun processNeighbors(grid: Grid, start: Node, end: Node, pathsMap: PathsData, maxLength: Int, depth: Int, solutions: MutableList<Path>) {
    val x = start.x
    val y = start.y
    processNeighbor(x.toInt(), y - 1, grid, start, end, pathsMap, maxLength, depth, solutions)
    processNeighbor(x + 1, y.toInt(), grid, start, end, pathsMap, maxLength, depth, solutions)
    processNeighbor(x.toInt(), y + 1, grid, start, end, pathsMap, maxLength, depth, solutions)
    processNeighbor(x - 1, y.toInt(), grid, start, end, pathsMap, maxLength, depth, solutions)
}

private fun processNeighbor(x: Int, y: Int, grid: Grid, start: Node, end: Node, pathsMap: PathsData, maxLength: Int, depth: Int, solutions: MutableList<Path>) {
    if (x < 0 || y < 0 || x >= grid.w || y >= grid.h) return

    val node = grid[x, y]
    if (node.color != 0 && node != end) return

    setCallReset(node, grid, start, end, pathsMap, depth, maxLength, solutions)
}

private fun setCallReset(node: Node, grid: Grid, start: Node, end: Node, pathsMap: PathsData, depth: Int, maxLength: Int, solutions: MutableList<Path>) {
    val previousColor = node.color
    node.color = start.color
    recursiveCall(node, grid, start, end, pathsMap, depth, maxLength, solutions)
    node.color = previousColor
}

private fun recursiveCall(node: Node, grid: Grid, start: Node, end: Node, pathsMap: PathsData, depth: Int, maxLength: Int, solutions: MutableList<Path>) {
    val paths = callRecursion(grid, node, end, pathsMap, depth, maxLength)
    addCurrentFirst(paths, start)
    addToSolutions(paths, solutions)
}

private fun callRecursion(grid: Grid, node: Node, end: Node, pathsMap: PathsData, depth: Int, maxLength: Int) =
        allPaths(grid, node, end, pathsMap, maxLength, depth + 1)

private fun addCurrentFirst(paths: List<Path>, start: Node) {
    for (path in paths) path.add(start.compressed())
}

private fun addToSolutions(paths: List<Path>, solutions: MutableList<Path>) {
    solutions.addAll(paths)
}