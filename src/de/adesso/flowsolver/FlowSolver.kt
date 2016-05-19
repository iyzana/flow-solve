package de.adesso.flowsolver

import de.adesso.flowsolver.solver.model.Grid
import de.adesso.flowsolver.solver.solve
import kotlin.system.measureTimeMillis

/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 12.05.2016
 */
fun main(args: Array<String>) {
//    Application.launch(FlowSolverGUI::class.java, *args)

    val grids = Grid.fromFile(TestGrids.javaClass.getResource("/10x10 Jumbo.dat").readText())
    
    println("complete time = " + measureTimeMillis {
        for (grid in grids)
            solve(grid)
    } + " ms")
}