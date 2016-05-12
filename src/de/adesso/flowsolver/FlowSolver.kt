package de.adesso.flowsolver

import de.adesso.flowsolver.gui.jfxTest
import javafx.application.Application;
import de.adesso.flowsolver.solver.fillGrid
import de.adesso.flowsolver.solver.solve

/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 12.05.2016
 */
fun main(args: Array<String>) {
    Application.launch(jfxTest::class.java, *args)
    solve(TestGrids.create9Grid())
}