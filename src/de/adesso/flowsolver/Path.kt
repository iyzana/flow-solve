package de.adesso.flowsolver

import java.util.*

/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 28.04.2016
 */
class Path(size: Int) {
    constructor(size: Int, node: Byte) : this(size) {
        add(node)
    }

    val nodes = ByteArray(size)
    private var pos = 0

    val size: Int
        get() = pos

    fun add(node: Byte) {
        nodes[pos++] = node
    }

    operator fun get(index: Int) = nodes[index]

    fun lastNode(color: Int): Node = Node(nodes[pos - 1], color)

    fun nodes() = (0..pos - 1).map { nodes[it] }

    inline fun forEach(apply: (node: Byte) -> Unit) {
        for (node in this) apply(node)
    }

    inline fun any(predicate: (node: Byte) -> Boolean): Boolean {
        for (node in this) if(predicate(node)) return true
        return false
    }

    operator fun iterator(): Iterator<Byte> {
        return object : Iterator<Byte> {
            var iteration = 0

            override fun hasNext() = iteration < size

            override fun next() = nodes[iteration++]
        }
    }

    override fun toString() = "Path(path = [" +
            (0..pos - 1).joinToString(separator = ", ") { "(${x(this[it])}, ${y(this[it])})" } + "]"

    override fun equals(other: Any?): Boolean{
        if (this === other) return true
        if (other !is Path) return false

        if (!Arrays.equals(nodes, other.nodes)) return false

        return true
    }

    override fun hashCode(): Int{
        return Arrays.hashCode(nodes)
    }
}