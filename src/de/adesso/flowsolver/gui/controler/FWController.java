package de.adesso.flowsolver.gui.controler;

import de.adesso.flowsolver.gui.functions.FlowWindow;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

/**
 * Created by slinde on 12.05.2016.
 */
public class FWController{

	FlowWindow window;
	int heith = 600 , length = 600;

	public void init(Stage primaryStage){
		window = new FlowWindow();
		window.init(primaryStage, this, heith, length);
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

	public void reset(){}

	public void solve(){}

	public int getGridSize(){
		return Integer.parseInt(window.getGridSize());
	}
}
