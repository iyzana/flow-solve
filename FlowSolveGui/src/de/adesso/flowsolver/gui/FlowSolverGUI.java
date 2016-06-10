package de.adesso.flowsolver.gui;

/**
 * Created by slinde on 12.05.2016.
 */

import de.adesso.flowsolver.gui.controler.FWController;
import javafx.application.Application;
import javafx.stage.Stage;

public class FlowSolverGUI extends Application {
	
	private FWController window = new FWController();
	
	@Override
	public void start(Stage primaryStage) {
		window.init(primaryStage);
	}
}