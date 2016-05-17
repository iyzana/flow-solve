package de.adesso.flowsolver.gui;

/**
 * Created by slinde on 12.05.2016.
 */

import de.adesso.flowsolver.gui.controler.FWController;
import javafx.application.Application;
import javafx.stage.Stage;

public class FlowSolverGUI extends Application{

	FWController window = new FWController();

	public static void main(String[] args){
		launch(args);
	}

	@Override
	public void start(Stage primaryStage){
		window.init(primaryStage);
	}
}
