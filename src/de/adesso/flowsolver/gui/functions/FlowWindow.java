package de.adesso.flowsolver.gui.functions;


import de.adesso.flowsolver.gui.controler.FWController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
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
	private int heigth, wight;
	private HBox hbTop, hbBottom;
	private VBox vbLeft;
	private FlowPane fpNotes;
	private GridPane center;
	private List<Button> possibleFlowNodes;

	public FlowWindow(Stage stage, FWController eventhandler, int heigth, int wight){
		primaryStage = stage;
		events = eventhandler;
		this.heigth = heigth;
		this.wight = wight;

		primaryStage.setTitle("Flow Solver");
	}

	public void init(){
		BorderPane mainLayout = new BorderPane();

		topBox();
		hbTop.setId("hbTop");
		hbTop.getStyleClass().add("hb");
		btnUp.setId("btnUp");
		btnUp.getStyleClass().add("UpDown");
		btnDown.setId("btnDown");
		btnDown.getStyleClass().add("UpDown");
		btnGenerate.setId("btnGenerate");

		leftBox();

		centerBox();

		bottomBox();
		hbBottom.setId("hbBottom");
		hbBottom.getStyleClass().add("hb");

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
		scene.getStylesheets().add("de/adesso/flowsolver/gui/design/FWDesign.css");
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
		center = new GridPane();
		center.setStyle("-fx-background-color: yellow; -fx-grid-lines-visible: true");
		center.setAlignment(Pos.CENTER);

		grid = new TableView<>();
		//TODO im internet nach row einfügen gucken und dann mit zwei for schleifen ne Tabelle machen
		generateTable();

//		center.getChildren().add(grid);

	}

	public void generateTable(){
		int size = events.getGridSize();

		center.getColumnConstraints().clear();
		center.getRowConstraints().clear();

		for(int i = 0; i < size; i++) {
			ColumnConstraints column = new ColumnConstraints(40);
			center.getColumnConstraints().add(column);
		}

		for(int i = 0; i < size; i++) {
			RowConstraints row = new RowConstraints(40);
			center.getRowConstraints().add(row);
		}

		grid.getColumns().clear();
		grid.getColumns().add(new TableColumn("Flow"));
		for(int i = 0; i < size; i++) grid.getColumns().add(new TableColumn("" + (i+1)));
//			for(int i = 0; i < size; i++)
		events.resize();
	}

	private void bottomBox(){
		btnReset = new Button("Reset");
		btnSolve = new Button("Solve");

		hbBottom = new HBox();

		hbBottom.getChildren().add(btnReset);
		hbBottom.getChildren().add(btnSolve);
	}

	private void leftBox(){
		possibleFlowNodes = new LinkedList<>();

		vbLeft = new VBox();
		fpNotes = new FlowPane();
		vbLeft.setStyle("-fx-background-color: red;");
		vbLeft.setPrefWidth(100);

		generateNotes();
		vbLeft.getChildren().add(fpNotes);
	}

	public void generateNotes(){
		possibleFlowNodes.clear();
		fpNotes.getChildren().clear();
		for(int i = 0; i < events.getAmountNotes(); i++){
			Button b1 = new Button("" + (char)('A' + i));
			Button b2 = new Button("" + (char)('A' + i));

			Button bb1 = new Button();
			Button bb2 = new Button();

			possibleFlowNodes.add(b1);
			possibleFlowNodes.add(b2);

			fpNotes.getChildren().add(b1);
			fpNotes.getChildren().add(bb1);
			fpNotes.getChildren().add(b2);
			fpNotes.getChildren().add(bb2);
		}
	}

	private void topBox(){
		tfGridSize = new TextField("5");
		btnUp = new Button("umgedrehtes v");
		btnDown = new Button("v");
		btnGenerate = new Button("Generate Grid");

		hbTop = new HBox();

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
