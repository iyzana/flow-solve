package de.adesso.flowsolver.gui.functions;

import de.adesso.flowsolver.gui.controler.SDWController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Created by slinde on 08.07.2016.
 */
public class SolveDetailsWindow {
	private Stage         primaryStage;
	private SDWController events;

	public SolveDetailsWindow(Stage stage, SDWController controller) {
		primaryStage = stage;
		events = controller;

		primaryStage.setTitle("Flow Solver - Solving Details");
	}

	public void init() {
		BorderPane mainLayout = new BorderPane();



		Scene scene = new Scene(mainLayout);
		scene.getStylesheets().add("css/FWDesign.css");
		primaryStage.setScene(scene);
	}


	public void show() {
		primaryStage.show();
	}
}
