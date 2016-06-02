package de.adesso.flowsolver

import de.adesso.flowsolver.solver.verboseSolve
import kotlin.system.measureTimeMillis

/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 12.05.2016
 */
var level = -1

fun main(args: Array<String>) {
//    Application.launch(FlowSolverGUI::class.java, *args)

//    for (i in 0..999) {
    val grids = TestGrids.loadGrids(Pack.Jumbo13)
    val grid = TestGrids.create14Grid()
    
    println("complete time = " + measureTimeMillis {
//            for ((index, grid) in grids.withIndex()) {
//                level = index
//                println("level $index")
                grid.print()
                verboseSolve(grid)
//            }
        } + " ms")
//    }
}