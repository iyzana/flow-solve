package de.adesso.flowsolver.gui.functions;

import de.adesso.flowsolver.gui.controler.SSWController;
import java.util.LinkedList;
import java.util.List;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Created by slinde on 08.07.2016.
 */
public class SolveStateWindow {
	private Stage         primaryStage;
	private SSWController events;
	private Label         lblTitle;
	private HBox          hbTop;
	private HBox          hbBottom;
	private Button        btnHide;
	private Button        btnDetails;
	private VBox          vbCenter;
	private List<String>  states;

	public SolveStateWindow(Stage stage, SSWController controler) {
		primaryStage = stage;
		events = controler;
		states = new LinkedList<>();
		primaryStage.setTitle("Flow Solver - Solving State");
	}

	public void init() {
		BorderPane mainLayout = new BorderPane();

		hbTop = new HBox();
		lblTitle = new Label("Solving State");
		hbTop.getChildren().add(lblTitle);

		vbCenter = new VBox();
		states.clear();
		addState("Vorbereitungen werden getroffen");
		states.stream().forEach(state -> vbCenter.getChildren().add(new Label((states.indexOf(state) == states.size()-1) ? state + "..." : state)));


		hbBottom = new HBox();
		btnHide = new Button("Hide");
		btnDetails = new Button("Details");
		hbBottom.getChildren().add(btnHide);
		hbBottom.getChildren().add(btnDetails);

		btnHide.setOnAction(e -> events.hide());
		btnDetails.setOnAction(e -> events.showDetails());

		mainLayout.setTop(hbTop);
		mainLayout.setBottom(hbBottom);

		Scene scene = new Scene(mainLayout);
		scene.getStylesheets().add("css/FWDesign.css");
		primaryStage.setScene(scene);
	}

	public boolean addState(String state){
		return states.add(state);
	}

	public void show() {
		primaryStage.show();
	}

}
