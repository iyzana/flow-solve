package de.adesso.flowsolver.gui;

/**
 * Created by slinde on 12.05.2016.
 */

import de.adesso.flowsolver.gui.controler.FWControler;
import de.adesso.flowsolver.gui.functions.FlowWindow;
import javafx.application.Application;
import javafx.stage.Stage;

public class FlowSolverGUI extends Application{

	FWControler window = new FWControler();

	public static void main(String[] args){
		launch(args);
	}

	@Override
	public void start(Stage primaryStage){
		window.init(primaryStage);
	}
}
