package de.adesso.flowsolver

/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 28.04.2016
 */
data class Grid(val w: Int, val h: Int,
                val grid: List<List<Node>> =
                Array<List<Node>>(w) { x -> Array<Node>(h) { y -> Node(x, y) }.toList() }.toList())
: List<List<Node>> by grid {
    
    public fun copy() = Grid(w, h, map { list -> list.map { node -> node.copy() } })
}