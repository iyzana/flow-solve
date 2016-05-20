package de.adesso.flowsolver.gui.controler;

import de.adesso.flowsolver.gui.functions.FlowWindow;
import de.adesso.flowsolver.solver.SolverKt;
import de.adesso.flowsolver.solver.model.Grid;
import de.adesso.flowsolver.solver.model.NodeKt;
import de.adesso.flowsolver.solver.model.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
	
	private FlowWindow window;
	private int length = 475, height = 375;
	private List<Button> flowNodes = new LinkedList<>();
	
	public void init(Stage primaryStage) {
		window = new FlowWindow(primaryStage, this, height, length);
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
	
	private Map<Integer, Integer> amountNodesMap = new HashMap<>();
	
	{
		amountNodesMap.put(5, 4);
		amountNodesMap.put(6, 5);
		amountNodesMap.put(7, 6);
		amountNodesMap.put(8, 7);
		amountNodesMap.put(9, 8);
		amountNodesMap.put(10, 10);
		amountNodesMap.put(11, 12);
		amountNodesMap.put(12, 14);
		amountNodesMap.put(13, 17);
		amountNodesMap.put(14, 18);
		amountNodesMap.put(15, 20);
	}
	
	public int getAmountNotes() {
		int size = Integer.parseInt(window.getGridSize());
		
		if (amountNodesMap.containsKey(size)) return amountNodesMap.get(size);
		else return 0;
	}
	
	public void reset() {
		generate();
	}
	
	public void solve(GridPane guiPane) {
		Grid g = parseGridIntoModel(guiPane);
		Map<Integer, Path> solution = new HashMap<>();
		try {
			solution = SolverKt.solve(g);
		} catch (IllegalArgumentException e) {
			System.out.println("Du bist doof");
		}
		if (solution.isEmpty()) System.out.println("Du bist doof2");
		else renderSolution(guiPane, solution);
	}
	
	private void renderSolution(GridPane guiPane, Map<Integer, Path> solution) {
		solution.forEach((color, path) -> {
			de.adesso.flowsolver.solver.model.Node lastNode = null;
			de.adesso.flowsolver.solver.model.Node currentNode = null;
			de.adesso.flowsolver.solver.model.Node nextNode = null;
			
			for (Iterator<Byte> it = path.iterator(); it.hasNext(); nextNode = toNode(it.next(), color)) {
				if (currentNode != null) {
					if (lastNode == null) {
						// Only one connection
					} else if (((lastNode.getX() == currentNode.getX()) && (nextNode.getX() == currentNode.getX())) ||
					           ((lastNode.getY() == currentNode.getY()) && (nextNode.getY() == currentNode.getY()))) {
						// Straight connection
					} else {
						// Curvy connection
					}
				}
				
				lastNode = currentNode;
				currentNode = nextNode;
			}
		});
		
		// solution.forEach((color, path) -> {
		// 	guiPane.getChildrenUnmodifiable().forEach(node -> {
		// 		int x = GridPane.getColumnIndex(node);
		// 		int y = GridPane.getRowIndex(node);
		// 		de.adesso.flowsolver.solver.model.Node gridNode = new de.adesso.flowsolver.solver.model.Node(x, y,
		// 		                                                                                             color);
		//		
		// 		int gridNodeIndex = path.indexOf(gridNode);
		// 		if (gridNodeIndex > -1) {
		// 			de.adesso.flowsolver.solver.model.Node previousNode = new de.adesso.flowsolver.solver.model.Node(
		// 					path.get(gridNodeIndex - 1), color);
		// 			de.adesso.flowsolver.solver.model.Node nextNode = new de.adesso.flowsolver.solver.model.Node(
		// 					path.get(gridNodeIndex + 1), color);
		// 			if (((previousNode.getX() == gridNode.getX()) && (nextNode.getX() == gridNode.getX())) ||
		// 			    ((previousNode.getY() == gridNode.getY()) && (nextNode.getY() == gridNode.getY()))) {
		//				
		// 			} else {
		//				
		// 			}
		// 		}
		// 	});
		// });
	}
	
	private de.adesso.flowsolver.solver.model.Node toNode(byte b, int color) {
		return new de.adesso.flowsolver.solver.model.Node(NodeKt.getX(b), NodeKt.getY(b), color);
	}
	
	private Grid parseGridIntoModel(GridPane guiPane) {
		Grid g = new Grid(getGridSize(), getGridSize());
		guiPane.getChildrenUnmodifiable().forEach(flow -> {
			int x = GridPane.getColumnIndex(flow);
			int y = GridPane.getRowIndex(flow);
			
			List<Node> boxedFlows = ((Pane) flow).getChildrenUnmodifiable();
			if (!boxedFlows.isEmpty()) {
				int color = ((Labeled) boxedFlows.get(0)).getText().toCharArray()[0] - 'A' + 1;
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
		window.setWindowheigth(175 + (gridsize * 40));
		window.setWindowwidth(275 + (gridsize * 40));
		window.stageSize();
		flowNodes.clear();
	}
	
	public void dragged(MouseEvent event, Button source) {
	    /* drag was detected, start a drag-and-drop gesture*/
	    /* allow any transfer mode */
		Dragboard db = source.startDragAndDrop(TransferMode.ANY);

        /* Put a string on a dragboard */
		ClipboardContent content = new ClipboardContent();
		content.putString(source.getText());
		db.setContent(content);
		
		event.consume();
	}
	
	public void dropped(DragEvent event, Pane target) {
		 /* data dropped */
		/* if there is a string data on dragboard, read it and use it */
		Dragboard db = event.getDragboard();
		boolean success = false;
		
		if (db.hasString()) {
			Optional<Button> button = getButton(db.getString());
			button.ifPresent(b -> {
				flowNodes.add(b);
				target.getChildren().add(b);
				b.setOnDragDetected(e -> dragged(e, b));
			});
			success = button.isPresent();
		}
		/* let the source know whether the string was successfully
		 * transferred and used */
		event.setDropCompleted(success);
		
		event.consume();
	}
	
	private Optional<Button> getButton(String string) {
		List<Button> possibleNodes = window.getPossibleNodes();
		
		return possibleNodes.stream().filter(b -> b.getText().equals(string)).findFirst().map(button -> {
			possibleNodes.remove(button);
			return button;
		});
	}
	
	public void dropable(DragEvent event, Pane target) {
		if (event.getGestureSource() != target && event.getDragboard().hasString()) {
		    /* allow for both copying and moving, whatever user chooses */
			event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
		}
		
		event.consume();
	}
	
	public void challenge() {
	}
}
