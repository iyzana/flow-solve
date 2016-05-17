package de.adesso.flowsolver.gui.functions;


import de.adesso.flowsolver.gui.controler.FWController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by slinde on 12.05.2016.
 */
public class FlowWindow{

	private Button btnUp, btnDown, btnGenerate, btnReset, btnSolve;
	private TextField tfGridSize;
	private TableView<String> grid;

	private Stage primaryStage;
	private FWController events;
	private HBox hbTop, hbBottom;
	private VBox vbLeft;
	private FlowPane fp;
	private List<Button> possibleFlowNodes;
	private StackPane center;

	public FlowWindow(){
	}

	public void init(Stage stage, FWController eventhandler, int heigth, int wight){
		primaryStage = stage;
		events = eventhandler;

		primaryStage.setTitle("Flow Solver");
		BorderPane mainLayout = new BorderPane();

		topBox();

		leftBox();

		centerBox();

		bottomBox();

		btnUp.setOnAction(e -> events.increase());
		btnDown.setOnAction(e -> events.decrease());
		btnGenerate.setOnAction(e -> events.generate());

		btnReset.setOnAction(e -> events.reset());
		btnSolve.setOnAction(e -> events.solve());

		mainLayout.setTop(hbTop);
		mainLayout.setLeft(vbLeft);
		mainLayout.setCenter(center);
		mainLayout.setBottom(hbBottom);
		Scene scene = new Scene(mainLayout, heigth, wight);
		//TODO css file richtig einbinden
		scene.getStylesheets().add("../design/FWDesign.css");
		primaryStage.setScene(scene);
	}

	private void centerBox(){/*
		flow	1 2 3 4 5 ..  n
		1
		2
		3
		4
		5
		..
		n
		 */
		center = new StackPane();

		grid = new TableView<String>();
		//TODO im internet nach row einfügen gucken und dann mit zwei for schleifen ne Tabelle machen
		generateTable();

		center.getChildren().add(grid);
	}

	public void generateTable(){
		grid.getColumns().clear();
		int size = events.getGridSize();
		grid.getColumns().add(new TableColumn("Flow"));
		for(int i = 0; i < size; i++) grid.getColumns().add(new TableColumn("" + (i+1)));
//			for(int i = 0; i < size; i++)
	}

	private void bottomBox(){
		btnReset = new Button("Reset");
		btnSolve = new Button("Solve");

		hbBottom = new HBox();
		hbBottom.setStyle("-fx-background-color: green;");
		hbBottom.setAlignment(Pos.CENTER_RIGHT);
		hbBottom.setPrefHeight(50);

		hbBottom.getChildren().add(btnReset);
		hbBottom.getChildren().add(btnSolve);
	}

	private void leftBox(){
		possibleFlowNodes = new LinkedList<Button>();

		vbLeft = new VBox();
		fp = new FlowPane();
		vbLeft.setStyle("-fx-background-color: red;");
		vbLeft.setPrefWidth(100);

		generateNotes();
		vbLeft.getChildren().add(fp);
	}

	public void generateNotes(){
		possibleFlowNodes.clear();
		fp.getChildren().clear();
		for(int i = 0; i < events.getAmountNotes(); i++){
			Button b1 = new Button("" + (char)('A' + i));
			Button b2 = new Button("" + (char)('A' + i));

			possibleFlowNodes.add(b1);
			possibleFlowNodes.add(b2);

			fp.getChildren().add(b1);
			fp.getChildren().add(new Button());
			fp.getChildren().add(b2);
			fp.getChildren().add(new Button());
		}
	}

	private void topBox(){
		tfGridSize = new TextField("5");
		btnUp = new Button("umgedrehtes v");
		btnDown = new Button("v");
		btnGenerate = new Button("Generate Grid");

		hbTop = new HBox();
		hbTop.setStyle("-fx-background-color: blue;");
		hbTop.setAlignment(Pos.CENTER_RIGHT);
		hbTop.setPrefHeight(50);

		hbTop.getChildren().add(tfGridSize);
		hbTop.getChildren().add(btnUp);
		hbTop.getChildren().add(btnDown);
		hbTop.getChildren().add(btnGenerate);
	}

	public void show(){
		primaryStage.show();
	}

	public String getGridSize(){
		return tfGridSize.getText();
	}

	public void setGridSize(String gridSize){
		tfGridSize.setText(gridSize);
	}
}
