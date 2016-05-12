package de.adesso.flowsolver.gui.functions;


import de.adesso.flowsolver.gui.controler.FWControler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Created by slinde on 12.05.2016.
 */
public class FlowWindow{

	private Button btnup, btndown;
	private TextField tfGridSize;
	private TableView<Integer> grid;

	private Stage primaryStage;
	private FWControler events;

	public void init(Stage stage, FWControler eventhandler){
		primaryStage = stage;
		events = eventhandler;

		primaryStage.setTitle("Flow Solver");
		BorderPane mainLayout = new BorderPane();

		btnup = new Button("umgedrehtes v");
		btndown = new Button("v");

		btnup.setOnAction(e -> events.increase());
		btndown.setOnAction(e -> events.decrease());

		tfGridSize = new TextField("5");

		HBox hb = new HBox();
		hb.getChildren().add(tfGridSize);
		hb.getChildren().add(btnup);
		hb.getChildren().add(btndown);



		mainLayout.setTop(hb);
		Scene scene = new Scene(mainLayout, 300, 250);
		primaryStage.setScene(scene);
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
