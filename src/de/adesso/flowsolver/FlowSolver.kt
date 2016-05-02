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
    newPoints.forEach { println(it) }
    println()
    
    val pairs = newPoints.groupBy { it.color }.values.map { it[0] to it[1] }
    
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

private fun solve(grid: Grid, pairs: List<Pair<Node, Node>>) {
    val w = grid.w
    val h = grid.h
    
    val shortestPaths = pairs.map { shortestPath(grid, it.first, it.second).size }
//    val shortestDistances = pairs.map{ distance(it.first, it.second) }
    
    val pathSum = w * h - shortestPaths.sum()
//    val distancesSum = w * h - shortestDistances.sum()
    
    val timePaths = measureTimeMillis {
        for ((index, pair) in pairs.withIndex()) {
            print("color ${index+1} ")
            
            val maxPathLengthPaths = pathSum + shortestPaths[index]
            print("maxLength $maxPathLengthPaths ")
            //            val maxPathLengthDistances = distancesSum + shortestDistances[color]!!
            //            println("maxPathLengthDistances = $maxPathLengthDistances")
    
            val paths = allPaths(grid, pair.first, pair.second, maxPathLengthPaths)
            //                    .forEach { println(it) }
            println("${paths.size} paths")
        }
    }
    println("timePaths = $timePaths ms")
    
    //    val paths = allPaths(grid, grid[0, 0], grid[w - 1, h - 1])
    //    
    //    paths.mapIndexed { i, path ->
    //        val grid = Grid(w, h)
    //        
    //        path.forEachIndexed { i, node -> grid[node.x, node.y].color = i + 1 }
    //        
    //        println("path $i")
    //        for (y in 0..w - 1) {
    //            for (x in 0..h - 1) {
    //                print(" " + grid[x, y].color)
    //            }
    //            println()
    //        }
    //        println()
    //    }
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
    return Grid(9, 9).apply {
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
    val grid = Grid(14, 14)

    grid[0, 7].color = D
    grid[1, 1].color = N
    grid[1, 4].color = I
    grid[2, 6].color = F
    grid[2, 10].color = G
    grid[3, 5].color = A
    grid[3, 10].color = E
    grid[4, 5].color = O
    grid[4, 6].color = F
    grid[4, 8].color = J
    grid[4, 9].color = B
    grid[4, 11].color = G
    grid[5, 1].color = N
    grid[6, 5].color = L
    grid[7, 6].color = A
    grid[7, 7].color = L
    grid[7, 8].color = E
    grid[7, 9].color = B
    grid[7, 13].color = K
    grid[8, 3].color = M
    grid[9, 8].color = C
    grid[9, 9].color = K
    grid[9, 13].color = M
    grid[10, 4].color = D
    grid[10, 11].color = C
    grid[10, 13].color = H
    grid[11, 2].color = O
    grid[11, 7].color = J
    grid[12, 12].color = I
    grid[13, 10].color = H
    
    return grid
}