package de.adesso.flowsolver

/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 28.04.2016
 */
fun main(args: Array<String>) {
    val w = 20
    val h = 20
    
    val grid = Grid(w, h)
    grid[0][0].color = 1
//    grid[1][1].color = 2
//    grid[1][4].color = 2
//    grid[0][7].color = 2
//    grid[2][6].color = 2
//    grid[3][5].color = 2
//    grid[4][5].color = 2
//    grid[4][6].color = 2
//    grid[4][8].color = 2
//    grid[4][9].color = 2
//    grid[4][11].color = 2
//    grid[3][10].color = 2
//    grid[2][10].color = 2
//    grid[5][1].color = 2
//    grid[6][5].color = 2
//    grid[7][6].color = 2
//    grid[7][7].color = 2
//    grid[7][8].color = 2
//    grid[7][9].color = 2
//    grid[7][13].color = 2
//    grid[8][3].color = 2
//    grid[9][13].color = 2
//    grid[9][9].color = 2
//    grid[9][8].color = 2
//    grid[10][13].color = 2
//    grid[10][11].color = 2
//    grid[10][4].color = 2
//    grid[11][2].color = 2
//    grid[11][7].color = 2
//    grid[12][12].color = 2
//    grid[13][10].color = 2
    grid[w - 1][h - 1].color = 1
    
    val shortest = shortestPath(grid, grid[0][0], grid[w - 1][h - 1])
    
    println(shortest.size)
    
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

fun Grid.solve(): Grid {
    val solved = copy()
    
    solved[0][0].color = 1
    
    return solved;
}