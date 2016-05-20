package de.adesso.flowsolver.gui.controler;

import de.adesso.flowsolver.gui.functions.FlowWindow;
import de.adesso.flowsolver.solver.SolverKt;
import de.adesso.flowsolver.solver.model.Grid;
import de.adesso.flowsolver.solver.model.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Created by slinde on 12.05.2016.
 */
public class FWController {

	FlowWindow window;
	private Stage primaryStage;
	int heith = 375 , length = 475;
	List<Button> flowNodes = new LinkedList<>();

	public void init(Stage primaryStage) {
		window = new FlowWindow(primaryStage, this, heith, length);
		this.primaryStage = primaryStage;
		window.init();
		window.show();
	}

	public void decrease() {
		int i = Integer.parseInt(window.getGridSize()) - 1;
		window.setGridSize(Integer.toString(i >= 5 ? i : 5));
	}

	public void increase() {
		int i = Integer.parseInt(window.getGridSize()) + 1;
		window.setGridSize(Integer.toString(i <= 15 ? i : 15));
	}

	public void generate() {
		window.generateNotes();
		window.generateTable();
	}

	public int getAmountNotes() {
		switch (Integer.parseInt(window.getGridSize())) {
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

	public void reset() {
		generate();
	}

	public void solve(GridPane guiPane) {
		Grid g = parseGridIntoModel(guiPane);
		Map<Integer, Path> solution = new HashMap<>();
		try {
			solution = SolverKt.solve(g);
		} catch(IllegalArgumentException e) {
			System.out.println("Du bist doof");
		}
		if(solution.isEmpty()) System.out.println("Du bist doof2");
		else renderSolution(guiPane, solution);
	}
	
	private void renderSolution(GridPane guiPane, Map<Integer, Path> solution) {
		solution.forEach((color, path) -> {
			guiPane.getChildrenUnmodifiable().forEach(node -> {
				int x = GridPane.getColumnIndex(node);
				int y = GridPane.getRowIndex(node);
				de.adesso.flowsolver.solver.model.Node gridNode = new de.adesso.flowsolver.solver.model.Node(x,y, color);
				
				int gridNodeIndex;
				if((gridNodeIndex = path.indexOf(gridNode)) > -1) {
					de.adesso.flowsolver.solver.model.Node previousNode = new de.adesso.flowsolver.solver.model.Node(path.get(gridNodeIndex - 1), color);
					de.adesso.flowsolver.solver.model.Node nextNode = new de.adesso.flowsolver.solver.model.Node(path.get(gridNodeIndex + 1), color);
					if(((previousNode.getX() == gridNode.getX()) && (nextNode.getX() == gridNode.getX())) ||
					   ((previousNode.getY() == gridNode.getY()) && (nextNode.getY() == gridNode.getY()))) {
						
						
					} else {
						
					}
				}
			});
		});
	}
	
	private Grid parseGridIntoModel(GridPane guiPane) {
		Grid g = new Grid(getGridSize(), getGridSize());
		guiPane.getChildrenUnmodifiable().forEach(flow -> {
			int x = GridPane.getColumnIndex(flow);
			int y = GridPane.getRowIndex(flow);

			List<Node> boxedFlows = ((Pane)flow).getChildrenUnmodifiable();
			if (!boxedFlows.isEmpty()) {
				int color = ((Labeled)boxedFlows.get(0)).getText().toCharArray()[0] - 'A' + 1;
				de.adesso.flowsolver.solver.model.Node node = new de.adesso.flowsolver.solver.model.Node(x, y, color);
				g.set(x, y, node);
			}
		});
		return g;
	}
	
	public int getGridSize() {
		return Integer.parseInt(window.getGridSize());
	}

	public void resize() {
		int gridsize = getGridSize();
		window.setWindowheigth(175 + (gridsize*40));
		window.setWindowwidth(275 + (gridsize*40));
		window.stageSize();
		flowNodes.clear();
	}

	public void draged(MouseEvent event, Button source) {
	    /* drag was detected, start a drag-and-drop gesture*/
        /* allow any transfer mode */
		Dragboard db = source.startDragAndDrop(TransferMode.ANY);

        /* Put a string on a dragboard */
		ClipboardContent content = new ClipboardContent();
		content.putString(source.getText());
		db.setContent(content);

		event.consume();
	}

	public void droped(DragEvent event, Pane target) {
		 /* data dropped */
        /* if there is a string data on dragboard, read it and use it */
		Dragboard db = event.getDragboard();
		boolean success = false;
		if (db.hasString()) {
			Button b = getButton(db.getString());
			flowNodes.add(b);
			target.getChildren().add(b);
			b.setOnDragDetected(e -> draged(e, b));

			success = true;
		}
        /* let the source know whether the string was successfully
         * transferred and used */
		event.setDropCompleted(success);

		event.consume();
	}

	private Button getButton(String string) {
		List<Button> buttons = window.getPossibleNotes();
		for (Button b : buttons) {
			if (b.getText() == string) {
				buttons.remove(b);
				return b;
			}
		}
		return null;
	}

	public void dropable(DragEvent event, Pane target) {
		if (event.getGestureSource() != target && event.getDragboard().hasString()) {
            /* allow for both copying and moving, whatever user chooses */
			event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
		}

		event.consume();
	}

	public void challange() {
	}
}
