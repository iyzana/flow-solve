package de.adesso.flowsolver.gui.controler;

import de.adesso.flowsolver.gui.functions.FlowWindow;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

/**
 * Created by slinde on 12.05.2016.
 */
public class FWControler{

	FlowWindow window;

	public void init(Stage primaryStage){
		window = new FlowWindow();
		window.init(primaryStage, this);
		window.show();
	}

	public void decrease(){
		window.setGridSize(Integer.toString(Integer.parseInt(window.getGridSize())-1));
	}

	public void increase(){
		window.setGridSize(Integer.toString(Integer.parseInt(window.getGridSize())+1));
	}
}
