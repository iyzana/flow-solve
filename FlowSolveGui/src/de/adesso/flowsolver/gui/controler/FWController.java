package de.adesso.flowsolver.gui.controler;

import de.adesso.flowsolver.gui.FlowSolverGUI;
import de.adesso.flowsolver.gui.functions.FlowWindow;
import de.adesso.flowsolver.solver.SolverKt;
import de.adesso.flowsolver.solver.model.FlowColor;
import de.adesso.flowsolver.solver.model.Grid;
import de.adesso.flowsolver.solver.model.NodeKt;
import de.adesso.flowsolver.solver.model.Path;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.control.TreeTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
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
	private FlowSolverGUI controller;
	
	public void init(Stage primaryStage, FlowSolverGUI controller) {
		this.controller = controller;
		window = new FlowWindow(primaryStage, this, height, length);
		window.init();
	}

	public void show() {
		window.show();
	}
	
	public void decrease() {
		int i = Integer.parseInt(window.getGridSize()) - 1;
		window.setGridSize(Integer.toString(i >= 5 ? i <= 15 ? i : 15 : 5));
	}
	
	public void increase() {
		int i = Integer.parseInt(window.getGridSize()) + 1;
		window.setGridSize(Integer.toString(i <= 15 ? i >= 5 ? i : 5 : 15));
	}
	
	public void generate() {
		String gridSize = window.getGridSize();
		System.out.println(gridSize);
		int i = Integer.parseInt(gridSize);
		if (i <= 15 && i >= 5) {
			window.generateNodes();
			window.generateTable();
		}
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
	
	public int getAmountNodes() {
		return amountNodesMap.getOrDefault(Integer.parseInt(window.getGridSize()), 0);
	}
	
	public void reset() {
		generate();
	}

	public Pane newPane() {
		GridPane pane = new GridPane();
		pane.setId("pane");

		pane.setOnDragDropped(e -> dropped(e, pane));
		pane.setOnDragOver(e -> dropable(e, pane));
		return pane;
	}
	
	public void keyPressed(KeyEvent e) {
		switch (e.getCode()){
			case ENTER: generate(); break;
			case UP: increase(); break;
			case DOWN: decrease(); break;
		}
	}

	public void solve(GridPane guiPane) {
		Grid g = parseGridIntoModel(guiPane);
		Map<Integer, Path> solution = new HashMap<>();
		try {
			controller.sswController();
			solution = SolverKt.verboseSolve(g);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		if (solution.isEmpty()) System.out.println("Du bist doof2");
		else renderSolution(guiPane, solution);
	}
	
	private void renderSolution(GridPane guiPane, Map<Integer, Path> solution) {
		solution.forEach((color, path) -> {
			de.adesso.flowsolver.solver.model.Node lastNode = null;
			de.adesso.flowsolver.solver.model.Node currentNode = null;
			de.adesso.flowsolver.solver.model.Node nextNode = null;
			
			for (Iterator<Byte> it = path.iterator();
			     it.hasNext() || currentNode != null; nextNode = it.hasNext() ? toNode(it.next(), color) : null) {
				if (currentNode != null) {
					int x = currentNode.getX();
					int y = currentNode.getY();

					Pane container = (Pane) getNodeByRowColumnIndex(y, x, guiPane);

					ImageView imageView = new ImageView();
					String fileName = "";
					Color pathColor = Color.decode(FlowColor.getHex(color));
					BufferedImage endImage = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);

					fileName =
							(lastNode == null) || (nextNode == null) ? (lastNode == null) ? (nextNode.getY() == y - 1)
							                                                                ? "images/start1.png" : ""
							                                                              : (lastNode.getY() == y - 1)
							                                                                ? "images/start1.png" : "" :
							(lastNode.getY() == y - 1) || (nextNode.getY() == y - 1) ? "images/start1.png" : "";
					if (!fileName.isEmpty()) endImage = getBufferedImage(fileName, endImage);

					fileName =
							(lastNode == null) || (nextNode == null) ? (lastNode == null) ? (nextNode.getX() == x + 1)
							                                                                ? "images/start2.png" : ""
							                                                              : (lastNode.getX() == x + 1)
							                                                                ? "images/start2.png" : "" :
							(lastNode.getX() == x + 1) || (nextNode.getX() == x + 1) ? "images/start2.png" : "";
					if (!fileName.isEmpty()) endImage = getBufferedImage(fileName, endImage);

					fileName =
							(lastNode == null) || (nextNode == null) ? (lastNode == null) ? (nextNode.getY() == y + 1)
							                                                                ? "images/start3.png" : ""
							                                                              : (lastNode.getY() == y + 1)
							                                                                ? "images/start3.png" : "" :
							(lastNode.getY() == y + 1) || (nextNode.getY() == y + 1) ? "images/start3.png" : "";
					if (!fileName.isEmpty()) endImage = getBufferedImage(fileName, endImage);

					fileName =
							(lastNode == null) || (nextNode == null) ? (lastNode == null) ? (nextNode.getX() == x - 1)
							                                                                ? "images/start4.png" : ""
							                                                              : (lastNode.getX() == x - 1)
							                                                                ? "images/start4.png" : "" :
							(lastNode.getX() == x - 1) || (nextNode.getX() == x - 1) ? "images/start4.png" : "";
					if (!fileName.isEmpty()) endImage = getBufferedImage(fileName, endImage);

					endImage = transform(endImage, pathColor);

					imageView.setImage(SwingFXUtils.toFXImage(endImage, null));
					container.getChildren().add(imageView);
				}
				
				lastNode = currentNode;
				currentNode = nextNode;
			}
		});
	}

	private BufferedImage getBufferedImage(String fileName, BufferedImage endImage) {
		String source;
		BufferedImage image;
		if (fileName.length() > 0) {
			source = getClass().getClassLoader().getResource(fileName).toExternalForm();
			Image tmpimage = new Image(source);
			image = SwingFXUtils.fromFXImage(tmpimage, null);
			endImage = ImageMerge.merge(endImage, image);
		}
		return endImage;
	}
	
	private de.adesso.flowsolver.solver.model.Node toNode(byte b, int color) {
		return new de.adesso.flowsolver.solver.model.Node(NodeKt.getX(b), NodeKt.getY(b), color);
	}
	
	private de.adesso.flowsolver.solver.model.Node toNode(int x, int y, int color) {
		return new de.adesso.flowsolver.solver.model.Node(x, y, color);
	}
	
	private Node getNodeByRowColumnIndex(int row, int column, GridPane gridPane) {
		return gridPane.getChildren().stream()
		               .filter(node -> GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column)
		               .findFirst().orElseThrow(
						() -> new ArrayIndexOutOfBoundsException("No such gridpane child: " + row + ", " + column));
	}
	
	private static BufferedImage transform(BufferedImage image, Color color) {
		int w = 40;
		int h = 40;

		BufferedImage dyed = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = dyed.createGraphics();
		
		g.drawImage(image, 0, 0, w, h, null);
		g.setComposite(AlphaComposite.SrcAtop);
		g.setColor(color);
		g.fillRect(0, 0, w, h);
		g.dispose();
		
		return dyed;
	}
	
	private Grid parseGridIntoModel(GridPane guiPane) {
		Grid g = new Grid(getGridSize(), getGridSize());
		
		guiPane.getChildrenUnmodifiable().forEach(flow -> {
			int x = GridPane.getColumnIndex(flow);
			int y = GridPane.getRowIndex(flow);
			
			List<Node> boxedFlows = ((Pane) flow).getChildrenUnmodifiable();
			if (!boxedFlows.isEmpty() && boxedFlows.get(0) instanceof Labeled) {
				int color = ((Labeled) boxedFlows.get(0)).getText().toCharArray()[0] - 'A' + 1;
				
				g.get(x, y).setColor(color);
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
		System.out.println("FWController.dragged");
		/* drag was detected, start a drag-and-drop gesture*/
	    /* allow any transfer mode */
		Dragboard db = source.startDragAndDrop(TransferMode.ANY);

        /* Put a string on a dragboard */
		ClipboardContent content = new ClipboardContent();
		content.putString(source.getText());
		db.setContent(content);
		System.out.println("  Dragged source = " + source.getText());
		
		event.consume();
		System.out.println("###");
	}
	
	public void dropped(DragEvent event, Pane target) {
		System.out.println("FWController.dropped");
		 /* data dropped */
		/* if there is a string data on dragboard, read it and use it */
		Dragboard db = event.getDragboard();
		boolean success = false;
		
		if (db.hasString()) {
			Optional<Button> button = getButton(db.getString());
			button.ifPresent(b -> {
				if(target.getChildren().isEmpty()){
					flowNodes.add(b);
					target.getChildren().add(b);
					b.setOnDragDetected(e -> dragged(e, b));
					System.out.println("  'button = " + b.getText() + "' is dropped on 'target= " + target.toString() + "'");
				}
			});
			success = button.isPresent();
		}
		/* let the source know whether the string was successfully
		 * transferred and used */
		event.setDropCompleted(success);
		
		event.consume();
		System.out.println("###");
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
	
	public String transferToParsableInteger(String toTraslatedleInt) {
		toTraslatedleInt = toTraslatedleInt.replaceAll("[^0-9]","");
		System.out.println(toTraslatedleInt);
		return !toTraslatedleInt.equals("") ? toTraslatedleInt : "0";
	}
}
