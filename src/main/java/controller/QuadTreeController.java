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
import javafx.scene.text.Text;
import model.Point;
import model.kdTree.KDTree;
import model.kdTree.SplitLine;
import model.quadTree.QuadTree;
import model.quadTree.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class QuadTreeController {

    public static final double PANE_WIDTH = 400, PANE_HEIGHT = 400;
    private final List<Point> pointSet = new ArrayList<>();
    private final Rectangle rootRectangle = new Rectangle(0, PANE_WIDTH, 0, PANE_HEIGHT);
    private final Node[][] grid = new Node[400][400];
    public ScrollPane scrollPane;
    private QuadTree dynamicQuadTree = new QuadTree(rootRectangle);
    private KDTree dynamicKDTree = new KDTree(rootRectangle);
    @FXML
    private Pane drawingPane;
    @FXML
    private JFXTextArea pointsLabel;
    @FXML
    private JFXButton clearButton;
    @FXML
    private Pane treePane;

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
        for (int i = 0; i < 100; i++) {
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
        dynamicQuadTree = new QuadTree(rootRectangle);
    }

    private void addPoint(double x, double y) {
        Point point = new Point(x, PANE_HEIGHT - y);
        addPointToGui(x, y, point);
        dynamicQuadTree.add(point);
        dynamicKDTree.add(point);
        System.out.println(dynamicKDTree.size(dynamicKDTree));
        treePane.getChildren().clear();
        drawKDRecursive(500, 20, 500, 20, dynamicKDTree, dynamicKDTree.getHeight());
    }

    @FXML
    void drawTree() {
        treePane.getChildren().clear();
        if (!pointSet.isEmpty()) {
            QuadTree quadTree = new QuadTree(new Rectangle(0, PANE_WIDTH, 0, PANE_HEIGHT), pointSet);
            quadTree.buildQuadTree(quadTree);
            drawQTRecursive(1000, 20, 1000, 20, quadTree, quadTree.getHeight());
        }
    }

    public void drawQTRecursive(double x1, double y1, double x, double y, QuadTree node, int height) {
        Line line = new Line(x1, y1 + 5, x, y);
        treePane.getChildren().add(line);
        if (node.isPointLeaf()) {
            javafx.scene.shape.Rectangle rectangle = getRectangle(x, y, node);
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
            drawQTRecursive(x, y, x - 1.5 * delta, y + (1 + h / 8.0) * 60, node.getNorthEast(), height - 1);
        if (node.getNorthWest() != null)
            drawQTRecursive(x, y, x - 0.5 * delta, y + (1 + h / 8.0) * 60, node.getNorthWest(), height - 1);
        if (node.getSouthWest() != null)
            drawQTRecursive(x, y, x + 0.5 * delta, y + (1 + h / 8.0) * 60, node.getSouthWest(), height - 1);
        if (node.getSouthEast() != null)
            drawQTRecursive(x, y, x + 1.5 * delta, y + (1 + h / 8.0) * 60, node.getSouthEast(), height - 1);
    }

    private javafx.scene.shape.Rectangle getRectangle(double x, double y, QuadTree node) {
        javafx.scene.shape.Rectangle rectangle = new javafx.scene.shape.Rectangle(x, y, 10, 10);
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
        Rectangle rectangle = node.getSquare();
        Line horizontalSplit = new Line(rectangle.xMin(), PANE_HEIGHT - rectangle.yMid(), rectangle.xMax(), PANE_HEIGHT - rectangle.yMid());
        Line verticalSplit = new Line(rectangle.xMid(), PANE_HEIGHT - rectangle.yMin(), rectangle.xMid(), PANE_HEIGHT - rectangle.yMax());
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

    public void drawKDTree() {
        KDTree kdTree = new KDTree(pointSet, rootRectangle, 0);
        kdTree.buildTree(kdTree, 0);
        drawKDRecursive(500, 20, 500, 20, kdTree, kdTree.getHeight());
        System.out.println(kdTree.size(kdTree));
        QuadTree quadTree = new QuadTree(rootRectangle, pointSet);
        quadTree.buildQuadTree(quadTree);
        System.out.println(quadTree.size(quadTree));
    }

    public void drawKDRecursive(double x1, double y1, double x, double y, KDTree node, int height) {
        Line line = new Line(x1, y1 + 5, x, y);
        treePane.getChildren().add(line);
        if (!node.isLeaf()) {
            createInnerNode(x, y, height, node);
        } else {
            createLeaf(x, y, node);
        }
        int h = node.getHeight();
        double delta = ((h - 1) * 20 + 20);
        if (node.getLeftChild() != null)
            drawKDRecursive(x, y, x - delta, y + (1 + h / 8.0) * 60, node.getLeftChild(), height - 1);
        if (node.getRightChild() != null)
            drawKDRecursive(x, y, x + delta, y + (1 + h / 8.0) * 60, node.getRightChild(), height - 1);
    }

    private void createInnerNode(double x, double y, int height, KDTree node) {
        Circle circle = new Circle(x, y, 10);
        circle.setFill(node.getLevel() % 2 == 0 ? Color.FORESTGREEN : Color.BLUEVIOLET);
        SplitLine sp = node.getSplitLine();
        Line splitline = new Line(sp.fromX(), PANE_HEIGHT - sp.fromY(), sp.toX(), PANE_HEIGHT - sp.toY());
        Text text = new Text(x + 20, y, (height % 2 == 0 ? "y = " + Math.round(sp.toY() * 10) / 10.0 : "x = " + Math.round(sp.toX() * 10.0) / 10.0));
        drawingPane.getChildren().add(splitline);
        treePane.getChildren().addAll(circle, text);
    }

    private void createLeaf(double x, double y, KDTree node) {
        javafx.scene.shape.Rectangle rectangle = new javafx.scene.shape.Rectangle(x, y);
        rectangle.setWidth(10);
        rectangle.setHeight(10);
        rectangle.setFill(Color.BLACK);
        Text text = new Text(x - 20, y + 10, node.getPoints().toString());
        treePane.getChildren().add(text);
    }
}