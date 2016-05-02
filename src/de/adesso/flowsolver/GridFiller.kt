package de.adesso.flowsolver

/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 29.04.2016
 */
//fun fill(grid: Grid, nodes: List<Node>): List<Node> {
//    for ((x, y) in nodes)
//        fillField(x, y, grid)
//    
//    for(x in 0..grid.w-1) {
//        for(y in 0..grid.h-1) {
//            grid[x, y].color = grid[x, y].color.positive
//        }
//    }
//    
//    +grid
//    
//    fun findNextNode(x: Int, y: Int, d: Int, c: Int): Triple<Int, Int, Int> {
//        if (d != 0 && isColor(x, y - 1, grid, c)) return Triple(2, x, y - 1)
//        if (d != 1 && isColor(x + 1, y, grid, c)) return Triple(3, x + 1, y)
//        if (d != 2 && isColor(x, y + 1, grid, c)) return Triple(0, x, y + 1)
//        if (d != 3 && isColor(x - 1, y, grid, c)) return Triple(1, x - 1, y)
//        return Triple(-1, x, y)
//    }
//    
//    return nodes.map { node ->
//        var d = -1
//        var current = node
//        
//        do {
//            val (nd, x, y) = findNextNode(current.x, current.y, d, current.color)
//            d = nd
//            current = Node(x, y, current.color)
//        } while (d != -1)
//        
//        current
//    }
//}
//
//private fun fillNeighbors(x: Int, y: Int, grid: Grid, color: Int) {
//    if (!valid(x, y, grid)) return
//    
//    grid[x, y].color = color.negative
//    
//    for (dx in -1..1) {
//        for (dy in -1..1) {
//            fillField(x + dx, y + dy, grid)
//        }
//    }
//}
//
//private fun fillField(x: Int, y: Int, grid: Grid) {
//    if (!valid(x, y, grid)) return
//    +grid
//    
//    val color = grid[x, y].color
//    if (color == 0) return
//    
//    val isStart = color >= 0
//    
//    var emptyCount = 0
//    emptyCount += if (isColor(x, y - 1, grid, 0)) 1 else 0
//    emptyCount += if (isColor(x + 1, y, grid, 0)) 2 else 0
//    emptyCount += if (isColor(x, y + 1, grid, 0)) 4 else 0
//    emptyCount += if (isColor(x - 1, y, grid, 0)) 8 else 0
//    
//    var colorNeighbors = 0
//    if (isColor(x, y - 1, grid, color) || isColor(x, y - 1, grid, -color)) colorNeighbors++
//    if (isColor(x + 1, y, grid, color) || isColor(x + 1, y, grid, -color)) colorNeighbors++
//    if (isColor(x, y + 1, grid, color) || isColor(x, y + 1, grid, -color)) colorNeighbors++
//    if (isColor(x - 1, y, grid, color) || isColor(x - 1, y, grid, -color)) colorNeighbors++
//    
//    if(isStart && colorNeighbors == 1) return
//    else if (colorNeighbors == 2) return
//    
//    when (emptyCount) {
//        1 -> fillNeighbors(x, y - 1, grid, color)
//        2 -> fillNeighbors(x + 1, y, grid, color)
//        4 -> fillNeighbors(x, y + 1, grid, color)
//        8 -> fillNeighbors(x - 1, y, grid, color)
//    }
//}

class FillNode(val x: Int, val y: Int, var color: Int, val end: Boolean = false) {
    fun u(grid: FillGrid) = grid[x, y - 1]
    fun d(grid: FillGrid) = grid[x, y + 1]
    fun l(grid: FillGrid) = grid[x + 1, y]
    fun r(grid: FillGrid) = grid[x - 1, y]
    
    fun neighbors(grid: FillGrid) = listOf(u(grid), r(grid), d(grid), l(grid))
    
    fun valid(grid: FillGrid) = valid(x, y, grid)
    
    fun toNode() = Node(x, y, color)
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FillNode) return false
        
        if (x != other.x) return false
        if (y != other.y) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = x
        result += 31 * result + y
        return result
    }
}

class FillGrid(grid: Grid, nodes: List<Node>) {
    val fillGrid: Array<Array<FillNode>>
    
    val w: Int
    val h: Int
    
    init {
        fun Node.forFilling(end: Boolean = false) = FillNode(this.x, this.y, this.color, end)
        
        w = grid.w
        h = grid.h
        fillGrid = Array<Array<FillNode>>(grid.w) { x ->
            Array<FillNode>(grid.h) { y ->
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
    
    // todo: copy back to grid
    
    return newStartPoints
}

fun fillNode(node: FillNode, grid: FillGrid): Map<FillNode, FillNode> {
    +grid
    
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

//val Int.positive: Int
//    get() = Math.abs(this)
//
//private val Int.negative: Int
//    get() = -this.positive

private fun valid(x: Int, y: Int, grid: FillGrid) = x >= 0 && y >= 0 && x < grid.w && y < grid.h

//private fun isEmpty(x: Int, y: Int, grid: Grid) = isColor(x, y, grid, 0)
//
//private fun isColor(x: Int, y: Int, grid: Grid, color: Int) =
//        if (!valid(x, y, grid)) false
//        else if (grid[x, y].color == color) true else false
