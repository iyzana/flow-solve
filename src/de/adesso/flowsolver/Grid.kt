package de.adesso.flowsolver

/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 28.04.2016
 */
data class Grid(val w: Byte, val h: Byte) {
    
    val grid: Array<Array<Node>>
    
    init {
        grid = Array<Array<Node>>(w.toInt()) { x -> Array<Node>(h.toInt()) { y -> Node(x.toByte(), y.toByte()) } }
    }
    
    operator fun get(x: Int, y: Int) = grid[x][y]
    operator fun get(x: Byte, y: Byte) = get(x.toInt(), y.toInt())
}