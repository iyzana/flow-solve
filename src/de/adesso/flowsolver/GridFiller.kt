package de.adesso.flowsolver

/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 29.04.2016
 */
data class FillNode(val x: Int, val y: Int, var color: Int, val end: Boolean = false) {
    fun u(grid: FillGrid) = grid[x, y - 1]
    fun d(grid: FillGrid) = grid[x, y + 1]
    fun l(grid: FillGrid) = grid[x + 1, y]
    fun r(grid: FillGrid) = grid[x - 1, y]

    fun neighbors(grid: FillGrid) = listOf(u(grid), r(grid), d(grid), l(grid))

    fun valid(grid: FillGrid) = valid(x, y, grid)

    fun toNode() = Node(x, y, color)
}

class FillGrid(grid: Grid, nodes: List<Node>) {
    val fillGrid: Array<Array<FillNode>>

    val w: Int
    val h: Int

    init {
        fun Node.forFilling(end: Boolean = false) = FillNode(this.x, this.y, this.color, end)

        w = grid.w
        h = grid.h
        
        fillGrid = Array(grid.w) { x ->
            Array(grid.h) { y ->
                grid[x, y].forFilling(end = grid[x, y] in nodes)
            }
        }
    }
    
    operator fun get(x: Int, y: Int) = if (valid(x, y, this)) fillGrid[x][y] else FillNode(x, y, -1)
    
    operator fun unaryPlus(): FillGrid {
        for (y in 0..h - 1) {
            for (x in 0..w - 1) {
                if (this[x, y].color == 0) print(" .")
                else print(" " + this[x, y].color)
            }
            println()
        }
        println()
        
        return this
    }
}

fun fillGrid(grid: Grid, nodes: List<Node>): List<Node> {
    val fillGrid = FillGrid(grid, nodes)

    val newStartPointMapping = hashMapOf<FillNode, FillNode>()
    for (node in nodes) {
        val fillNode = fillGrid[node.x, node.y]

        newStartPointMapping.putAll(fillNode(fillNode, fillGrid))
    }

    val newStartPoints = nodes.map { fillGrid[it.x, it.y] }.map { node ->
        var current = node
        while (current in newStartPointMapping)
            current = newStartPointMapping[current]!!
        current
    }.map { it.toNode() }

    for (x in 0..fillGrid.w - 1) {
        for (y in 0..fillGrid.h - 1) {
            grid[x, y].color = fillGrid[x, y].color
        }
    }

    return newStartPoints
}

fun fillNode(node: FillNode, grid: FillGrid): Map<FillNode, FillNode> {
    val color = node.color

    val end = node.end

    val neighbors = node.neighbors(grid)
    val sameCount = neighbors.filter { it.valid(grid) && it.color == color }.count()

    if (sameCount == 2) return emptyMap()
    else if (end && sameCount == 1) return emptyMap()

    val emptyNodes = neighbors.filter { it.color == 0 && it.valid(grid) }

    when (emptyNodes.size) {
        1 -> {
            val neighbor = emptyNodes.single()
            return hashMapOf<FillNode, FillNode>().apply {
                put(node, neighbor)
                putAll(fillNeighbors(neighbor, color, grid))
            }
        }
        else -> return emptyMap()
    }
}

fun fillNeighbors(node: FillNode, color: Int, grid: FillGrid): Map<FillNode, FillNode> {
    val mapping = hashMapOf<FillNode, FillNode>()

    node.color = color
    +grid
    
    for (dx in -1..1) {
        for (dy in -1..1) {
            val cx = node.x + dx
            val cy = node.y + dy
            if (!valid(cx, cy, grid)) continue
            val neighbor = grid[cx, cy]
            if (neighbor.color == 0) continue
            mapping.putAll(fillNode(neighbor, grid))
        }
    }

    return mapping
}

private fun valid(x: Int, y: Int, grid: FillGrid) = x >= 0 && y >= 0 && x < grid.w && y < grid.h