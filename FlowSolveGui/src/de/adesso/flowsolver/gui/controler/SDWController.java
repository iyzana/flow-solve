package de.adesso.flowsolver.gui.controler;

import de.adesso.flowsolver.gui.FlowSolverGUI;
import de.adesso.flowsolver.gui.functions.SolveDetailsWindow;
import javafx.stage.Stage;

/**
 * Created by slinde on 08.07.2016.
 */
public class SDWController {

	private SolveDetailsWindow window;
	private FlowSolverGUI controller;

	public void init(Stage primaryStage, FlowSolverGUI controller) {
		this.controller = controller;
		window = new SolveDetailsWindow(primaryStage, this);
		window.init();
	}

	public void show() {
		window.show();
	}
}
