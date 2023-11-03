package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXToggleButton;
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
import javafx.scene.text.Text;
import model.Point;
import model.kdTree.KDTree;
import model.kdTree.SplitLine;
import model.quadTree.Area;
import model.quadTree.QuadTree;

import java.util.ArrayList;
import java.util.List;

public class QuadTreeController {
    public static final double PANE_WIDTH = 400, PANE_HEIGHT = 400;
    private final List<Point> pointSet = new ArrayList<>();
    private final Area rootArea = new Area(0, PANE_WIDTH, 0, PANE_HEIGHT);
    private final Node[][] grid = new Node[400][400];
    public ScrollPane scrollPane;
    TreeMode mode = TreeMode.QUAD_TREE;
    private QuadTree dynamicQuadTree = new QuadTree(rootArea);
    private KDTree dynamicKDTree = new KDTree(rootArea);
    @FXML
    private Pane drawingPane;
    @FXML
    private JFXTextArea pointsLabel;
    @FXML
    private JFXButton clearButton;
    @FXML
    private Pane treePane;
    @FXML
    private JFXToggleButton toggleButton;

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

    @FXML
    void generate() {
        if (mode == TreeMode.QUAD_TREE) {
            generateQuadTree();
            drawQTRecursive(500, 20, 500, 20, dynamicQuadTree, dynamicQuadTree.getHeight());
        } else {
            generateKDTree();
            drawKDRecursive(500, 20, 500, 20, dynamicKDTree, dynamicKDTree.getHeight());
        }
    }

    private void clearPane() {
        pointSet.clear();
        drawingPane.getChildren().clear();
        pointsLabel.clear();
        treePane.getChildren().clear();
        dynamicQuadTree = new QuadTree(rootArea);
        dynamicKDTree = new KDTree(rootArea);
    }

    private void addPoint(double x, double y) {
        treePane.getChildren().clear();
        Point point = new Point(x, PANE_HEIGHT - y);
        addPointToGui(x, y, point);
        if (!pointSet.contains(point)) pointSet.add(point);
        dynamicKDTree.add(point);
        dynamicQuadTree.add(point);
        if (mode == TreeMode.KD_TREE) {
            drawKDRecursive(500, 20, 500, 20, dynamicKDTree, dynamicKDTree.getHeight());
        } else {
            drawQTRecursive(500, 20, 500, 20, dynamicQuadTree, dynamicQuadTree.getHeight());
        }
    }

    void generateQuadTree() {
        treePane.getChildren().clear();
        removeLines();
        if (!pointSet.isEmpty()) {
            dynamicQuadTree = new QuadTree(new Area(0, PANE_WIDTH, 0, PANE_HEIGHT), pointSet);
            dynamicQuadTree.buildQuadTree(dynamicQuadTree);
        }
    }

    public void drawQTRecursive(double x1, double y1, double x, double y, QuadTree node, int height) {
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
            drawQTRecursive(x, y, x - 1.5 * delta, y + (1 + h / 8.0) * 60, node.getNorthEast(), height - 1);
        if (node.getNorthWest() != null)
            drawQTRecursive(x, y, x - 0.5 * delta, y + (1 + h / 8.0) * 60, node.getNorthWest(), height - 1);
        if (node.getSouthWest() != null)
            drawQTRecursive(x, y, x + 0.5 * delta, y + (1 + h / 8.0) * 60, node.getSouthWest(), height - 1);
        if (node.getSouthEast() != null)
            drawQTRecursive(x, y, x + 1.5 * delta, y + (1 + h / 8.0) * 60, node.getSouthEast(), height - 1);
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
        Area area = node.getSquare();
        Line horizontalSplit = new Line(area.xMin(), PANE_HEIGHT - area.yMid(), area.xMax(), PANE_HEIGHT - area.yMid());
        Line verticalSplit = new Line(area.xMid(), PANE_HEIGHT - area.yMin(), area.xMid(), PANE_HEIGHT - area.yMax());
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

    public void generateKDTree() {
        if (!pointSet.isEmpty()) {
            removeLines();
            treePane.getChildren().clear();
            dynamicKDTree = new KDTree(pointSet, rootArea, 0);
            dynamicKDTree.buildTree(dynamicKDTree, 0);
        }
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
        double delta = (Math.pow(Math.E, h / 1.9 - 1) * 20 + 20);
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
        Rectangle rectangle = new Rectangle(x, y);
        rectangle.setWidth(10);
        rectangle.setHeight(10);
        rectangle.setFill(Color.BLACK);
        Text text = new Text(x - 20, y + 10, node.getPoints().toString());
        treePane.getChildren().add(text);
    }

    private void removeLines() {
        drawingPane.getChildren().removeIf(node -> node instanceof Line);
    }

    @FXML
    void toggleMode() {
        removeLines();
        treePane.getChildren().clear();
        if (mode == TreeMode.KD_TREE) {
            toggleButton.setText("QuadTree");
            mode = TreeMode.QUAD_TREE;
            generateQuadTree();
            drawQTRecursive(500, 20, 500, 20, dynamicQuadTree, dynamicQuadTree.getHeight());
        } else {
            toggleButton.setText("KD-Tree");
            mode = TreeMode.KD_TREE;
            generateKDTree();
            drawKDRecursive(500, 20, 500, 20, dynamicKDTree, dynamicKDTree.getHeight());
        }
    }

    public enum TreeMode {QUAD_TREE, KD_TREE}
}