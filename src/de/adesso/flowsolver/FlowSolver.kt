package de.adesso.flowsolver

import de.adesso.flowsolver.gui.jfxTest
import de.adesso.flowsolver.solver.solve
import javafx.application.Application

/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 12.05.2016
 */
fun main(args: Array<String>) {
    Application.launch(jfxTest::class.java, *args)
    solve(TestGrids.create11Grid())
}