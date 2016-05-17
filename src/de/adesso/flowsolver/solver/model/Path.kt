package de.adesso.flowsolver.solver.model

import java.util.Arrays

/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 28.04.2016
 */
class Path private constructor(val nodes: ByteArray) {
    constructor(size: Int, node: Byte) : this(size) {
        add(node)
    }
    
    constructor(size: Int) : this(ByteArray(size))
    
    private var pos = 0
    
    val size: Int
        get() = pos
    
    fun add(node: Byte) {
        nodes[pos++] = node
    }
    
    fun remove(): Byte {
        pos--
        return nodes[pos]
    }
    
    operator fun get(index: Int) = nodes[index]
    
    fun lastNode(color: Int): Node = Node(nodes[pos - 1], color)
    
    fun nodes() = (0..pos - 1).map { nodes[it] }
    
    fun copy(): Path {
        val copy = Path(nodes.copyOf(pos))
        copy.pos = pos
        return copy
    }
    
    operator fun contains(node: Node): Boolean {
        val compressed = node.compressed()
        forEach { current ->
            if (current == compressed) return true
        }
        return false
    }
    
    inline fun forEach(apply: (node: Byte) -> Unit) {
        for (node in nodes) apply(node)
    }
    
    //    inline fun any(predicate: (node: Byte) -> Boolean): Boolean {
    //        for (node in this) if (predicate(node)) return true
    //        return false
    //    }
    
    operator fun iterator(): Iterator<Byte> {
        return object : Iterator<Byte> {
            var iteration = 0
            
            override fun hasNext() = iteration < size
            
            override fun next() = nodes[iteration++]
        }
    }
    
    override fun toString() = "Path(path = [" +
            (0..pos - 1).joinToString(separator = ", ") { "${Node(nodes[it]).toString()}" } + "]"
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Path) return false
        
        if (pos != other.pos) return false
        for (i in 0..pos - 1)
            if (this[i] != other[i]) return false
        
        return true
    }
    
    
    
    override fun hashCode(): Int {
        var result = Arrays.hashCode(nodes)
        result += 31 * result + pos
        return result
    }
}