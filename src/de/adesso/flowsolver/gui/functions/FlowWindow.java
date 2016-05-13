package de.adesso.flowsolver.gui.functions;


import de.adesso.flowsolver.gui.controler.FWControler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by slinde on 12.05.2016.
 */
public class FlowWindow{

	private Button btnUp, btnDown;
	private TextField tfGridSize;
	private TableView<Integer> grid;

	private Stage primaryStage;
	private FWControler events;
	private Button btnGenerate;
	private HBox hb;
	private VBox vb;
	private FlowPane fp;
	private List<Button> possibleFlowNodes;

	public void init(Stage stage, FWControler eventhandler, int heigth, int width){
		primaryStage = stage;
		events = eventhandler;

		primaryStage.setTitle("Flow Solver");
		BorderPane mainLayout = new BorderPane();


		topBox();


		leftBox();

		btnUp.setOnAction(e -> events.increase());
		btnDown.setOnAction(e -> events.decrease());
		btnGenerate.setOnAction(e -> events.generate());

		mainLayout.setTop(hb);
		mainLayout.setLeft(vb);
		Scene scene = new Scene(mainLayout, heigth, width);
		//TODO css file richtig einbinden
		scene.getStylesheets().add("../design/FWDesign.css");
		primaryStage.setScene(scene);
	}

	private void leftBox(){
		possibleFlowNodes = new LinkedList<Button>();

		vb = new VBox();
		fp = new FlowPane();
		vb.setStyle("-fx-background-color: red;");
		vb.setPrefWidth(100);

		generateNotes();
		vb.getChildren().add(fp);
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
//			fp.getChildren().add();
			fp.getChildren().add(b2);
//			fp.getChildren().add();
		}
	}

	private void topBox(){
		tfGridSize = new TextField("5");
		btnUp = new Button("umgedrehtes v");
		btnDown = new Button("v");
		btnGenerate = new Button("Generate Grid");

		hb = new HBox();
		hb.setStyle("-fx-background-color: blue;");
		hb.setAlignment(Pos.CENTER_RIGHT);
		hb.setPrefHeight(50);

		hb.getChildren().add(tfGridSize);
		hb.getChildren().add(btnUp);
		hb.getChildren().add(btnDown);
		hb.getChildren().add(btnGenerate);
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
