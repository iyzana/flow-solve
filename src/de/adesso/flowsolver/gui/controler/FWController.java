package de.adesso.flowsolver.gui.controler;

import de.adesso.flowsolver.gui.functions.FlowWindow;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Created by slinde on 12.05.2016.
 */
public class FWController{

	FlowWindow window;
	private Stage primaryStage;
	int heith = 600 , length = 600;

	public void init(Stage primaryStage){
		window = new FlowWindow(primaryStage, this, heith, length);
		this.primaryStage = primaryStage;
		window.init();
		window.show();
	}

	public void decrease(){
		int i = Integer.parseInt(window.getGridSize())-1;
		window.setGridSize(Integer.toString(i >= 5 ? i : 5));
	}

	public void increase(){
		int i = Integer.parseInt(window.getGridSize())+1;
		window.setGridSize(Integer.toString(i <= 15 ? i : 15));
	}

	public void generate(){
		window.generateNotes();
		window.generateTable();
	}

	public int getAmountNotes(){
		switch(Integer.parseInt(window.getGridSize())){
			case 5:
				return 4;
			case 6:
				return 5;
			case 7:
				return 6;
			case 8:
				return 7;
			case 9:
				return 8;
			case 10:
				return 10;
			case 11:
				return 12;
			case 12:
				return 14;
			case 13:
				return 17;
			case 14:
				return 18;
			case 15:
				return 20;
			default:
				return 0;
		}
	}

	public void reset(){ generate(); }

	public void solve(){}

	public int getGridSize(){ return Integer.parseInt(window.getGridSize()); }

	public void resize(){
		int gridsize = getGridSize();
		window.setWindowheigth(150 + (gridsize*40));
		window.setWindowwidth(250 + (gridsize*40));
		window.stageSize();
	}

	public void draged(MouseEvent event, Button source){
        /* drag was detected, start a drag-and-drop gesture*/
        /* allow any transfer mode */
				Dragboard db = source.startDragAndDrop(TransferMode.ANY);

        /* Put a string on a dragboard */
				ClipboardContent content = new ClipboardContent();
				content.putString(source.getText());
				db.setContent(content);

				event.consume();
	}

	public void droped(DragEvent event, GridPane target){
		 /* data dropped */
        /* if there is a string data on dragboard, read it and use it */
		Dragboard db = event.getDragboard();
		boolean success = false;
		if (db.hasString()) {
			success = true;
		}
        /* let the source know whether the string was successfully
         * transferred and used */
		event.setDropCompleted(success);

		event.consume();
	}
}
