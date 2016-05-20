package de.adesso.flowsolver

import de.adesso.flowsolver.gui.FlowSolverGUI
import de.adesso.flowsolver.solver.model.Grid
import de.adesso.flowsolver.solver.solve
import javafx.application.Application
import kotlin.system.measureTimeMillis

/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 12.05.2016
 */
var level = -1

fun main(args: Array<String>) {
    Application.launch(FlowSolverGUI::class.java, *args)

//    for (i in 0..999) {
//        val grids = Grid.fromFile(TestGrids.javaClass.getResource("/10x10 Jumbo.dat").readText())
//
//        println("complete time = " + measureTimeMillis {
//            for ((index, grid) in grids.withIndex()) {
//                level = index
//                println("level $index")
//                solve(grid)
//            }
//        } + " ms")
//    }
}