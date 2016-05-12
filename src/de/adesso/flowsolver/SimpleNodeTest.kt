package de.adesso.flowsolver

import de.adesso.flowsolver.solver.model.Node
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
        val node = Node(0, 0)
        
        val compressed = node.compressed()
        
        println(Integer.toBinaryString(compressed.toInt()))
        println("node = ${Node(compressed).toString()}")
    }
}