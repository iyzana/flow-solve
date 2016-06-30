package de.adesso.flowsolver.solver.model

/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 28.04.2016
 */

val Byte.x: Int
    get() = Node.x(this)
val Byte.y: Int
    get() = Node.y(this)

data class Node(val x: Int, val y: Int, var color: Int = 0) {
    constructor(compressed: Byte, color: Int = 0) : this(x(compressed), y(compressed), color)

    fun compressed() = compressedInt().toByte()
    private fun compressedInt() = (((x shl 4) or y) + 1)
    
    fun u(grid: Grid) = grid[x, y - 1]
    fun d(grid: Grid) = grid[x, y + 1]
    fun l(grid: Grid) = grid[x - 1, y]
    fun r(grid: Grid) = grid[x + 1, y]

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Node) return false

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode() = compressedInt()

    override fun toString() = "Node($x, $y)"

    companion object {
        fun x(compressed: Byte) = (compressed.toInt() - 1) shr 4 and 0xF
        fun y(compressed: Byte) = (compressed.toInt() - 1) and 0xF
    }
}