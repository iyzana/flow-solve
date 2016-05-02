package de.adesso.flowsolver

/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 28.04.2016
 */
fun compress(x: Int, y: Int): Byte = ((x shl 4) or y).toByte()

fun x(compressed: Byte) = compressed.toInt() shr 4 and 0xF
fun y(compressed: Byte) = compressed.toInt() and 0xF

data class Node(val x: Int, val y: Int, var color: Int = 0) {
    constructor(compressed: Byte, color: Int = 0) : this(x(compressed), y(compressed), color)
    
    fun compressed() = compress(x, y)
}