package de.adesso.flowsolver

import de.adesso.flowsolver.solver.model.Grid
import de.adesso.flowsolver.solver.verboseSolve
import java.io.File
import kotlin.system.measureTimeMillis

/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 12.05.2016
 */
var level = -1
val threading = false

fun main(args: Array<String>) {
    File("results").deleteRecursively()
    
    

//    Application.launch(FlowSolverGUI::class.java, *args)

//    for (i in 0..999) {
    val grids = TestGrids.loadGrids(Pack.Jumbo11)
//    val grid = TestGrids.loadGrid(Pack.Jumbo12, 3)
    
    println("complete time = " + measureTimeMillis {
        for ((index, grid) in grids.withIndex()) {
            level = index
            println("level $index")
            verboseSolve(grid)
        }
    } + " ms")
//    }
}