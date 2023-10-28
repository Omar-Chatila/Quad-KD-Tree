package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Glow;
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
    private final Square rootSquare = new Square(0, PANE_WIDTH, 0, PANE_HEIGHT);
    private final Node[][] grid = new Node[400][400];
    public ScrollPane scrollPane;
    @FXML
    private Pane drawingPane;
    @FXML
    private JFXTextArea pointsLabel;
    @FXML
    private JFXButton clearButton;
    @FXML
    private Pane treePane;
    private QuadTree dynamicTree = new QuadTree(rootSquare);


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
            addPointToGui(x, y, new Point(x, y));
        }
    }

    private void clearPane() {
        pointSet.clear();
        drawingPane.getChildren().clear();
        pointsLabel.clear();
        treePane.getChildren().clear();
        dynamicTree = new QuadTree(rootSquare);
    }

    private void addPoint(double x, double y) {
        Point point = new Point(x, PANE_HEIGHT - y);
        addPointToGui(x, y, point);
        dynamicTree.add(point);
        treePane.getChildren().clear();
        drawNodeRecursive(500, 20, 500, 20, dynamicTree, dynamicTree.getHeight());
    }

    @FXML
    void drawTree() {
        treePane.getChildren().clear();
        if (!pointSet.isEmpty()) {
            QuadTree quadTree = new QuadTree(new Square(0, PANE_WIDTH, 0, PANE_HEIGHT), pointSet);
            quadTree.buildQuadTree(quadTree);
            drawNodeRecursive(1000, 20, 1000, 20, quadTree, quadTree.getHeight());
        }

    }


    public void drawNodeRecursive(double x1, double y1, double x, double y, QuadTree node, int height) {
        Line line = new Line(x1, y1 + 5, x, y);
        treePane.getChildren().add(line);
        if (node.isPointLeaf()) {
            Rectangle rectangle = getRectangle(x, y, node);
            treePane.getChildren().add(rectangle);
        } else {
            Circle circle = new Circle(x, y, 10, Paint.valueOf("blue"));
            if (!node.isNodeLeaf()) {
                drawSplitLines(node);
            }
            treePane.getChildren().add(circle);
        }
        int h = node.getHeight();
        double delta = (Math.pow(2.8, h - 1) + 20);
        if (node.getNorthEast() != null)
            drawNodeRecursive(x, y, x - 1.5 * delta, y + (1 + h / 8.0) * 60, node.getNorthEast(), height - 1);
        if (node.getNorthWest() != null)
            drawNodeRecursive(x, y, x - 0.5 * delta, y + (1 + h / 8.0) * 60, node.getNorthWest(), height - 1);
        if (node.getSouthWest() != null)
            drawNodeRecursive(x, y, x + 0.5 * delta, y + (1 + h / 8.0) * 60, node.getSouthWest(), height - 1);
        if (node.getSouthEast() != null)
            drawNodeRecursive(x, y, x + 1.5 * delta, y + (1 + h / 8.0) * 60, node.getSouthEast(), height - 1);
    }

    private Rectangle getRectangle(double x, double y, QuadTree node) {
        Rectangle rectangle = new Rectangle(x, y, 10, 10);
        Point p = node.getPoints().get(0);
        rectangle.setId(node.getPoints().get(0).toString());

        rectangle.setOnMouseEntered(e -> {
            Circle corresponding = (Circle) grid[(int) p.x()][(int) p.y()];
            corresponding.setScaleX(2.5);
            corresponding.setScaleY(2.5);
            corresponding.setFill(Color.RED);
            corresponding.setEffect(new Glow(0.8));
        });

        rectangle.setOnMouseExited(e -> {
            Circle corresponding = (Circle) grid[(int) p.x()][(int) p.y()];
            corresponding.setScaleX(1);
            corresponding.setScaleY(1);
            corresponding.setFill(Color.BLACK);
            corresponding.setEffect(null);
        });
        return rectangle;
    }

    private void drawSplitLines(QuadTree node) {
        Square square = node.getSquare();
        Line horizontalSplit = new Line(square.xMin(), PANE_HEIGHT - square.yMid(), square.xMax(), PANE_HEIGHT - square.yMid());
        Line verticalSplit = new Line(square.xMid(), PANE_HEIGHT - square.yMin(), square.xMid(), PANE_HEIGHT - square.yMax());
        drawingPane.getChildren().addAll(horizontalSplit, verticalSplit);
    }

    private void addPointToGui(double x, double y, Point p) {
        Circle circle = new Circle(x, y, 2, Color.BLACK);
        grid[(int) x][(int) (PANE_HEIGHT - y)] = circle;
        drawingPane.getChildren().add(circle);

        if (!pointSet.contains(p)) {
            pointSet.add(new Point(x, PANE_HEIGHT - y));
        }
        pointsLabel.setText("P = { " + pointSet.toString().substring(1, pointSet.toString().length() - 1) + " }");
    }
}