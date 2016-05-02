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

    private val nodes = ByteArray(size)
    var pos = 0

    val size: Int
        get() = pos

    fun lastNode(color: Int): Node = Node(nodes[pos - 1], color)

    private var dirty = true
    private var nodesCache: List<Byte>? = null

    fun nodes(): List<Byte> {
        if (nodesCache == null || dirty) {
            dirty = false
            nodesCache = (0..pos - 1).map { nodes[it] }
        }

        return nodesCache ?: throw ConcurrentModificationException("Node cache changed in another thread")
    }

    fun add(node: Byte) {
        dirty = true
        nodes[pos++] = node
    }

    operator fun get(index: Int) = nodes[index]

    operator fun iterator(): Iterator<Byte> {
        return object : Iterator<Byte> {
            var iteratrion = 0

            override fun hasNext() = iteratrion < size

            override fun next() = nodes[iteratrion++]
        }
    }

    override fun toString() = "Path(path = [" +
            (0..pos - 1).joinToString(separator = ", ") { "(${x(this[it])}, ${y(this[it])})" } + "]"
}