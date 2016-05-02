package de.adesso.flowsolver

import java.util.*
import kotlin.system.measureTimeMillis

/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 28.04.2016
 */
private val A = 1;
private val B = 2;
private val C = 3;
private val D = 4;
private val E = 5;
private val F = 6;
private val G = 7;
private val H = 8;
private val I = 9;
private val J = 10;
private val K = 11;
private val L = 12;
private val M = 13;
private val N = 14;
private val O = 15;

fun main(args: Array<String>) {
    val grid = create9Grid()

    val points = extractPairs(grid).values.flatMap { listOf(it.first, it.second) }
    val newPoints = fillGrid(+grid, points)
    +grid

    val pairs = newPoints.mapValues { e -> e.value[0] to e.value[1] }

    solve(grid, pairs)
}

private fun extractPairs(grid: Grid): Map<Int, Pair<Node, Node>> {
    val pairs = HashMap<Int, Pair<Node, Node>>()
    colors@ for (color in A..O) {
        var first: Node? = null

        for (x in 0..grid.w - 1) {
            for (y in 0..grid.h - 1) {
                val node = grid[x, y]
                if (node.color == color) {
                    if (first == null) first = node
                    else {
                        pairs.put(color, first to node)
                        continue@colors
                    }
                }
            }
        }
    }

    return pairs
}

private fun solve(grid: Grid, pairs: Map<Int, Pair<Path, Path>>) {
    val w = grid.w
    val h = grid.h

    val shortestPaths = pairs.mapValues { entry ->
        val color = entry.key
        val pair = entry.value
        val start = pair.first.lastNode(color)
        val end = pair.second.lastNode(color)

        shortestPath(grid, start, end).size
    }
    val pathSum = w * h - shortestPaths.values.sum()

    val coloredPaths = HashMap<Int, MutableList<Path>>()

    println("timePaths = " + measureTimeMillis {
        for ((color, pair) in pairs.entries) {
            print("color $color")

            val maxPathLengthPaths = pathSum + shortestPaths[color]!!
            print(" maxLength $maxPathLengthPaths ")

            val start = pair.first.lastNode(color)
            val end = pair.second.lastNode(color)

            val paths = allPaths(grid, start, end, maxPathLengthPaths).toMutableList()
            println("${paths.size} paths")
            coloredPaths.put(color, paths)
        }
    } + " ms")

    println()
    println("filtering paths...")

    fun Path.intersects(other: Path) = (0..pos - 1).any { i -> (0..other.pos - 1).any { j -> this[i] == other[j] } }
    fun Path.intersectsAll(other: List<Path>): Boolean {
        return other.all { this.intersects(it) }
    }

    do {
        var changed = false
        
        val sizeSorted = coloredPaths.toList().sortedBy { it.second.size }
        for ((color, paths) in sizeSorted) {
            val sizeSorted2 = sizeSorted.toList().sortedBy { it.second.size }
            for ((otherColor, otherPaths) in sizeSorted2) {
                if (color == otherColor) continue
                print("color $color with $otherColor start " + paths.size)
                paths.retainAll { path ->
                    if (path.intersectsAll(otherPaths)) {
                        changed = true
                        return@retainAll false
                    }
                    return@retainAll true
                }
                println(" end " + paths.size)
            }
        }
    } while (changed)

    println("filling grid")

    coloredPaths.forEach { color, paths ->
        paths.single().nodes().forEach { node ->
            grid[x(node), y(node)].color = color
        }
    }

    +grid

    println("appending pre and postfix...")

    val completePaths = coloredPaths.mapValues { e ->
        val color = e.key
        val paths = e.value

        paths.map { path ->
            val pre = pairs[color]!!.first
            val post = pairs[color]!!.second
            val size = path.size + pre.size + post.size - 2

            val complete = Path(size)
            post.nodes().dropLast(1).forEach { complete.add(it) }
            path.nodes().forEach { complete.add(it) }
            pre.nodes().dropLast(1).reversed().forEach { complete.add(it) }
            complete
        }
    }

    println()
    println("filtered paths")
    completePaths.forEach { entry ->
        val color = entry.key
        val paths = entry.value

        println("color $color")
        paths.forEach { println(it) }
    }
}

private fun readGrid(): Grid {
    val input = Scanner(System.`in`.reader()).nextLine()

    return Grid.fromString(input)
}

private fun create5Grid(): Grid {
    return Grid(5, 5).apply {
        this[0, 0].color = A
        this[1, 3].color = B
        this[1, 4].color = A
        this[2, 0].color = B
        this[2, 1].color = C
        this[2, 4].color = C
        this[3, 3].color = D
        this[3, 4].color = E
        this[4, 0].color = D
        this[4, 1].color = E
    }
}

private fun create7Grid(): Grid {
    return Grid(7, 7).apply {
        this[1, 2].color = E
        this[2, 4].color = B
        this[3, 3].color = B
        this[4, 3].color = F
        this[4, 4].color = D
        this[4, 5].color = A
        this[5, 1].color = E
        this[5, 5].color = D
        this[5, 6].color = C
        this[6, 0].color = C
        this[6, 1].color = A
        this[6, 6].color = F
    }
}

private fun create9Grid(): Grid {
    return Grid(10, 9).apply {
        this[0, 5].color = D
        this[1, 1].color = A
        this[1, 4].color = B
        this[1, 5].color = G
        this[1, 6].color = I
        this[1, 7].color = H
        this[2, 1].color = F
        this[2, 6].color = D
        this[3, 1].color = C
        this[3, 2].color = F
        this[3, 4].color = B
        this[4, 2].color = E
        this[4, 4].color = A
        this[6, 2].color = E
        this[7, 2].color = C
        this[7, 3].color = G
        this[7, 5].color = H
        this[8, 5].color = I
    }
}

private fun create14Grid(): Grid {
    return Grid(14, 14).apply {
        this[0, 7].color = D
        this[1, 1].color = N
        this[1, 4].color = I
        this[2, 6].color = F
        this[2, 10].color = G
        this[3, 5].color = A
        this[3, 10].color = E
        this[4, 5].color = O
        this[4, 6].color = F
        this[4, 8].color = J
        this[4, 9].color = B
        this[4, 11].color = G
        this[5, 1].color = N
        this[6, 5].color = L
        this[7, 6].color = A
        this[7, 7].color = L
        this[7, 8].color = E
        this[7, 9].color = B
        this[7, 13].color = K
        this[8, 3].color = M
        this[9, 8].color = C
        this[9, 9].color = K
        this[9, 13].color = M
        this[10, 4].color = D
        this[10, 11].color = C
        this[10, 13].color = H
        this[11, 2].color = O
        this[11, 7].color = J
        this[12, 12].color = I
        this[13, 10].color = H
    }
}
