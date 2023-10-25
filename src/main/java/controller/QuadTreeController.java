package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import model.Point;
import model.QuadTree;
import model.Square;

import java.util.ArrayList;
import java.util.List;

public class QuadTreeController {

    public static final double PANE_WIDTH = 400, PANE_HEIGHT = 400;
    private final List<Point> pointSet = new ArrayList<>();
    @FXML
    private Pane drawingPane;
    @FXML
    private JFXTextArea pointsLabel;
    @FXML
    private JFXButton clearButton;
    @FXML
    private Pane treePane;
    //private Square rootSquare = new Square(0, PANE_WIDTH, 0, PANE_HEIGHT);


    @FXML
    private void initialize() {
        pointsLabel.setEditable(false);
        clearButton.setOnAction(actionEvent -> clearPane());
    }

    @FXML
    void drawPoint(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        addPoint(x, y);
    }

    @FXML
    void randomize() {
        clearPane();
        for (int i = 0; i < 32; i++) {
            double x = Math.random() * 400;
            double y = Math.random() * 400;
            addPoint(x, y);
        }
    }

    private void clearPane() {
        pointSet.clear();
        drawingPane.getChildren().clear();
        pointsLabel.clear();
        treePane.getChildren().clear();
    }

    private void addPoint(double x, double y) {
        Circle circle = new Circle(x, y, 2, Color.BLACK);
        drawingPane.getChildren().add(circle);
        Point p = new Point(x, PANE_HEIGHT - y);
        if (!pointSet.contains(p)) {
            pointSet.add(new Point(x, PANE_HEIGHT - y));
        }
        pointsLabel.setText("P = { " + pointSet.toString().substring(1, pointSet.toString().length() - 1) + " }");
    }

    @FXML
    void drawTree() {
        treePane.getChildren().clear();
        QuadTree quadTree = new QuadTree(new Square(0, PANE_WIDTH, 0, PANE_HEIGHT), pointSet);
        quadTree.buildQuadTree(quadTree);
        System.out.println(quadTree.getHeight());
        drawNodeRecursive(400, 20, 400, 20, quadTree, quadTree.getHeight());
    }


    public void drawNodeRecursive(double x1, double y1, double x, double y, QuadTree node, int height) {
        Line line = new Line(x1, y1 + 5, x, y);
        treePane.getChildren().add(line);
        if (node.isPointLeaf()) {
            Rectangle rectangle = new Rectangle(x, y, 10, 10);
            treePane.getChildren().add(rectangle);
        } else {
            Circle circle = new Circle(x, y, 10, Paint.valueOf("blue"));
            if (!node.isNodeLeaf()) {
                drawSplitLines(node);
            }
            treePane.getChildren().add(circle);
        }
        int h = node.getHeight();
        double delta = (Math.pow(2, h) * 10);
        if (node.getNorthEast() != null)
            drawNodeRecursive(x, y, x - 1.5 * delta, y + 80, node.getNorthEast(), height - 1);
        if (node.getNorthWest() != null)
            drawNodeRecursive(x, y, x - 0.5 * delta, y + 80, node.getNorthWest(), height - 1);
        if (node.getSouthWest() != null)
            drawNodeRecursive(x, y, x + 0.5 * delta, y + 80, node.getSouthWest(), height - 1);
        if (node.getSouthEast() != null)
            drawNodeRecursive(x, y, x + 1.5 * delta, y + 80, node.getSouthEast(), height - 1);
    }

    private void drawSplitLines(QuadTree node) {
        Square square = node.getSquare();
        Line horizontalSplit = new Line(square.xMin(), PANE_HEIGHT - square.yMid(), square.xMax(), PANE_HEIGHT - square.yMid());
        Line verticalSplit = new Line(square.xMid(), PANE_HEIGHT - square.yMin(), square.xMid(), PANE_HEIGHT - square.yMax());
        drawingPane.getChildren().addAll(horizontalSplit, verticalSplit);
    }
}