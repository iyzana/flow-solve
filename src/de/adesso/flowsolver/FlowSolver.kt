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

    val w = grid.w
    val h = grid.h

    val pairs = HashMap<Int, Pair<Node, Node>>()
    colors@ for (color in A..O) {
        var first: Node? = null

        for (x in 0..w - 1) {
            for (y in 0..h - 1) {
                val node = grid[x][y]
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

    val shortestPaths = pairs.mapValues { entry -> shortestPath(grid, entry.value.first, entry.value.second).size }
    val shortestDistances = pairs.mapValues { entry -> distance(entry.value.first, entry.value.second) }

    val pathSum = w * h - shortestPaths.values.sum()
    val distancesSum = w * h - shortestDistances.values.sum()
    val timePaths = measureTimeMillis {
        for (color in pairs.keys) {
            print("$color: ")

            val maxPathLengthPaths = pathSum + shortestPaths[color]!!
            print("maxLength $maxPathLengthPaths ")
//            val maxPathLengthDistances = distancesSum + shortestDistances[color]!!
//            println("maxPathLengthDistances = $maxPathLengthDistances")

            val pair = pairs[color]!!
            val paths = allPaths(grid, pair.first, pair.second, maxPathLengthPaths)
//                    .forEach { println(it) }
            println("${paths.size} paths")
        }
    }
    println("timePaths = $timePaths")

    //    val paths = allPaths(grid, grid[0][0], grid[w - 1][h - 1])
    //    
    //    paths.mapIndexed { i, path ->
    //        val grid = Grid(w, h)
    //        
    //        path.forEachIndexed { i, node -> grid[node.x][node.y].color = i + 1 }
    //        
    //        println("path $i")
    //        for (y in 0..w - 1) {
    //            for (x in 0..h - 1) {
    //                print(" " + grid[x][y].color)
    //            }
    //            println()
    //        }
    //        println()
    //    }
}

private fun create5Grid(): Grid {
    return Grid(5, 5).apply {
        grid[0][0].color = A
        grid[1][3].color = B
        grid[1][4].color = A
        grid[2][0].color = B
        grid[2][1].color = C
        grid[2][4].color = C
        grid[3][3].color = D
        grid[3][4].color = E
        grid[4][0].color = D
        grid[4][1].color = E
    }
}

private fun create7Grid(): Grid {
    return Grid(7, 7).apply {
        grid[1][2].color = E
        grid[2][4].color = B
        grid[3][3].color = B
        grid[4][3].color = F
        grid[4][4].color = D
        grid[4][5].color = A
        grid[5][1].color = E
        grid[5][5].color = D
        grid[5][6].color = C
        grid[6][0].color = C
        grid[6][1].color = A
        grid[6][6].color = F
    }
}

private fun create9Grid(): Grid {
    return Grid(9, 9).apply {
        grid[0][5].color = D
        grid[1][1].color = A
        grid[1][4].color = B
        grid[1][5].color = G
        grid[1][6].color = I
        grid[1][7].color = H
        grid[2][1].color = F
        grid[2][6].color = D
        grid[3][1].color = C
        grid[3][2].color = F
        grid[3][4].color = B
        grid[4][2].color = E
        grid[4][4].color = A
        grid[6][2].color = E
        grid[7][2].color = C
        grid[7][3].color = G
        grid[7][5].color = H
        grid[8][5].color = I
    }
}

private fun create14Grid(): Grid {
    val grid = Grid(14, 14)

    grid[0][7].color = D
    grid[1][1].color = N
    grid[1][4].color = I
    grid[2][6].color = F
    grid[2][10].color = G
    grid[3][5].color = A
    grid[3][10].color = E
    grid[4][5].color = O
    grid[4][6].color = F
    grid[4][8].color = J
    grid[4][9].color = B
    grid[4][11].color = G
    grid[5][1].color = N
    grid[6][5].color = L
    grid[7][6].color = A
    grid[7][7].color = L
    grid[7][8].color = E
    grid[7][9].color = B
    grid[7][13].color = K
    grid[8][3].color = M
    grid[9][8].color = C
    grid[9][9].color = K
    grid[9][13].color = M
    grid[10][4].color = D
    grid[10][11].color = C
    grid[10][13].color = H
    grid[11][2].color = O
    grid[11][7].color = J
    grid[12][12].color = I
    grid[13][10].color = H
    
    return grid
}

fun Grid.solve(): Grid {
    val solved = copy()

    solved[0][0].color = 1

    return solved;
}