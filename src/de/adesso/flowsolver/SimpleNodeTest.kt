package de.adesso.flowsolver

import org.junit.Test

/**
 * FlowSolve
 * adesso AG
 
 * @author kaiser
 * *         Created on 29.04.2016
 */
class SimpleNodeTest {
    @Test
    fun testMax() {
        val x: Byte = 15
        val y: Byte = 15
        val node = SimpleNode(x, y)
    
        println(Integer.toBinaryString(node.point.toInt()))
        println("node.x = ${node.x}")
        println("node.y = ${node.y}")
    }
}