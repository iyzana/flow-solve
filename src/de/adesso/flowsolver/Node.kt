package de.adesso.flowsolver

/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 28.04.2016
 */
class SimpleNode(x: Byte, y: Byte) {
    val point: Byte = (x * 16 + y).toByte()
    
    val x: Int
        get() = point.toInt() shr 4 and 0xF
    val y: Int
        get() = point.toInt() and 0xF
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SimpleNode) return false
        
        if (point != other.point) return false
        
        return true
    }
    
    override fun hashCode() = point.toInt()
    
    override fun toString() = "Node($x, $y)"
}

data class Node(val x: Byte, val y: Byte, var color: Int = 0) {
    fun simple() = SimpleNode(x, y)
}