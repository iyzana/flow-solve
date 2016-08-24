package de.adesso.flowsolver.gui.controler;

import de.adesso.flowsolver.gui.FlowSolverGUI;
import de.adesso.flowsolver.gui.functions.SolveStateWindow;
import de.adesso.flowsolver.solver.SolverKt;
import javafx.stage.Stage;

/**
 * Created by slinde on 08.07.2016.
 */
public class SSWController {

	private static SolveStateWindow window;
	private FlowSolverGUI controller;

	public void init(Stage primaryStage, FlowSolverGUI controller) {
		this.controller = controller;
		window = new SolveStateWindow(primaryStage, this);

		SolverKt.setStateListener(state -> {
			window.addState(state);
			window.init();
		});
		
		window.init();
	}
	
	public void showDetails() {
		controller.sdwController();
	}
	
	public void hide() {
	}

	public void show() {
		window.show();
	}
}
