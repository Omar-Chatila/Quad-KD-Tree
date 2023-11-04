package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXToggleButton;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
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
    static int n = 0;
    private final List<Point> pointSet = new ArrayList<>();
    private final Area rootArea = new Area(0, PANE_WIDTH, 0, PANE_HEIGHT);
    private final Node[][] grid = new Node[400][400];
    public ScrollPane scrollPane;
    TreeMode mode = TreeMode.QUAD_TREE;
    boolean colorized = false;
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
    private Label statsLabel;
    @FXML
    private Pane rectanglePane;
    private String KDBench, QTBench;

    @FXML
    private void initialize() {
        pointsLabel.setEditable(false);
        clearButton.setOnAction(actionEvent -> clearPane());
        rectanglePane.toBack();
        drawingPane.setStyle("-fx-background-color: transparent");
        rectanglePane.setStyle("-fx-background-color: white");
    }

    private void benchQT() {
        long start1 = System.nanoTime();
        dynamicQuadTree = new QuadTree(rootArea, pointSet);
        dynamicQuadTree.buildQuadTree(dynamicQuadTree);
        long end = (System.nanoTime() - start1) / 1000;
        updateLabel(dynamicQuadTree.getPoints().size(), dynamicQuadTree.getHeight(), dynamicQuadTree.size(dynamicQuadTree), end, "µs", TreeMode.QUAD_TREE);
    }

    @FXML
    void toggleMode() {
        removeLines();
        treePane.getChildren().clear();
        if (mode == TreeMode.KD_TREE) {
            toggleButton.setText("QuadTree");
            mode = TreeMode.QUAD_TREE;
            if (!pointSet.isEmpty()) {
                if (dynamicQuadTree.getPoints().isEmpty()) {
                    benchQT();
                    dynamicQuadTree.buildQuadTree(dynamicQuadTree);
                }
                drawQTRecursive(500, 20, 500, 20, dynamicQuadTree, dynamicQuadTree.getHeight(), Color.YELLOW);
            }
            statsLabel.setText(this.QTBench);
            setRectVisibility(colorized);
        } else {
            removeRectangles();
            toggleButton.setText("KD-Tree");
            mode = TreeMode.KD_TREE;
            if (!pointSet.isEmpty()) {
                if (dynamicKDTree.getPoints().isEmpty()) {
                    benchKD();
                }
                drawKDRecursive(500, 20, 500, 20, dynamicKDTree, dynamicKDTree.getHeight());
            }
            statsLabel.setText(this.KDBench);
        }
    }

    private void benchKD() {
        long start1 = System.nanoTime();
        dynamicKDTree = new KDTree(pointSet, rootArea, 0);
        dynamicKDTree.buildTree(dynamicKDTree, 0);
        long end = (System.nanoTime() - start1) / 1000;
        updateLabel(dynamicKDTree.getPoints().size(), dynamicKDTree.getHeight(), dynamicKDTree.size(dynamicKDTree), end, "µs", TreeMode.KD_TREE);
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
        for (int i = 0; i < 200; i++) {
            double x = Math.random() * 400;
            double y = Math.random() * 400;
            addPointToGui(x, y, new Point(x, y));
        }
    }

    @FXML
    void generate() {
        if (mode == TreeMode.QUAD_TREE) {
            generateQuadTree();
            drawQTRecursive(500, 20, 500, 20, dynamicQuadTree, dynamicQuadTree.getHeight(), Color.YELLOW);
            if (!colorized) {
                setRectVisibility(false);
            }
        } else {
            generateKDTree();
            drawKDRecursive(500, 20, 500, 20, dynamicKDTree, dynamicKDTree.getHeight());
        }
    }

    @FXML
    void stressTest() {
        clearPane();
        int pointsCount = 1000000;
        List<Point> testList = new ArrayList<>();
        for (int i = 0; i < pointsCount; i++) {
            testList.add(new Point(Math.random() * 4000, Math.random() * 4000));
        }
        long start = System.nanoTime();
        Area testArea = new Area(0, 4000, 0, 4000);
        if (mode == TreeMode.QUAD_TREE) {
            QuadTree quadTree = new QuadTree(testArea, testList);
            quadTree.buildQuadTree(quadTree);
            long time = (System.nanoTime() - start) / 1000000;
            int height = quadTree.getHeight();
            int number = quadTree.size(quadTree);
            updateLabel(pointsCount, height, number, time, "ms", mode);
        } else {
            KDTree kdTree = new KDTree(testList, testArea, 0);
            kdTree.buildTree(kdTree, 0);
            long time = (System.nanoTime() - start) / 1000000;
            int height = kdTree.getHeight();
            int number = kdTree.size(kdTree);
            updateLabel(pointsCount, height, number, time, "ms", mode);
        }
    }

    @FXML
    void colorize() {
        setRectVisibility(!colorized);
        colorized = !colorized;
    }

    private void clearPane() {
        pointSet.clear();
        drawingPane.getChildren().clear();
        pointsLabel.clear();
        treePane.getChildren().clear();
        dynamicQuadTree = new QuadTree(rootArea);
        dynamicKDTree = new KDTree(rootArea);
        statsLabel.setText("");
        removeRectangles();
    }

    private void addPoint(double x, double y) {
        Point point = new Point(x, PANE_HEIGHT - y);
        if (!pointSet.contains(point)) {
            treePane.getChildren().clear();
            addPointToGui(x, y, point);
            pointSet.add(point);
            dynamicKDTree.add(point);
            dynamicQuadTree.add(point);
            removeLines();
            removeRectangles();
            if (mode == TreeMode.KD_TREE) {
                drawKDRecursive(500, 20, 500, 20, dynamicKDTree, dynamicKDTree.getHeight());
            } else {
                drawQTRecursive(500, 20, 500, 20, dynamicQuadTree, dynamicQuadTree.getHeight(), Color.YELLOW);
            }
            setRectVisibility(colorized);
        }
    }

    void generateQuadTree() {
        if (!pointSet.isEmpty()) {
            removeLines();
            treePane.getChildren().clear();
            benchQT();
        }
    }

    public void generateKDTree() {
        if (!pointSet.isEmpty()) {
            removeLines();
            treePane.getChildren().clear();
            benchKD();
        }
    }

    public void drawQTRecursive(double x1, double y1, double x, double y, QuadTree node, int height, Color color) {
        Line line = new Line(x1, y1 + 5, x, y);
        treePane.getChildren().add(line);
        if (node.isPointLeaf()) {
            Rectangle rectangle = getRectangle(x, y, node);
            treePane.getChildren().add(rectangle);
        } else {
            Circle circle = new Circle(x, y, 10, color);
            if (!node.isNodeLeaf()) {
                drawSplitLines(node);
            }
            treePane.getChildren().add(circle);
        }
        if (node.getHeight() == 1) {
            Rectangle rectangle2 = generateRectangle(node, color);
            rectanglePane.getChildren().add(rectangle2);
        }
        int h = node.getHeight();
        double delta = (Math.pow(2.8, h - 1) + 20);
        if (node.getNorthEast() != null)
            drawQTRecursive(x, y, x - 1.5 * delta, y + (1 + h / 8.0) * 60, node.getNorthEast(), height - 1, Color.ORANGE);
        if (node.getNorthWest() != null)
            drawQTRecursive(x, y, x - 0.5 * delta, y + (1 + h / 8.0) * 60, node.getNorthWest(), height - 1, Color.GREEN);
        if (node.getSouthWest() != null)
            drawQTRecursive(x, y, x + 0.5 * delta, y + (1 + h / 8.0) * 60, node.getSouthWest(), height - 1, Color.BLUEVIOLET);
        if (node.getSouthEast() != null)
            drawQTRecursive(x, y, x + 1.5 * delta, y + (1 + h / 8.0) * 60, node.getSouthEast(), height - 1, Color.RED);
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

    public void drawKDRecursive(double x1, double y1, double x, double y, KDTree node, int height) {
        Line line = new Line(x1, y1 + 5, x, y);
        treePane.getChildren().add(line);
        if (!node.isLeaf()) {
            createInnerNode(x, y, node);
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

    private void createInnerNode(double x, double y, KDTree node) {
        Circle circle = new Circle(x, y, 10);
        circle.setFill(node.getLevel() % 2 == 0 ? Color.FORESTGREEN : Color.BLUEVIOLET);
        SplitLine sp = node.getSplitLine();
        Line splitline = new Line(sp.fromX(), PANE_HEIGHT - sp.fromY(), sp.toX(), PANE_HEIGHT - sp.toY());
        Text text = new Text(x + 20, y, (node.getLevel() % 2 == 0 ? "x = " + Math.round(sp.fromX() * 10) / 10.0 : "y = " + Math.round(sp.fromY() * 10.0) / 10.0));
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

    private Rectangle generateRectangle(QuadTree node, Color color) {
        Area square = node.getSquare();
        double size = square.xMax() - square.xMin();
        Rectangle rectangle2 = new Rectangle(square.xMin(), 400 - square.yMax(), size, size);
        rectangle2.setOpacity(0.5);
        rectangle2.setFill(color);
        rectangle2.toBack();
        return rectangle2;
    }

    private void removeLines() {
        drawingPane.getChildren().removeIf(node -> node instanceof Line);
    }

    private void removeRectangles() {
        rectanglePane.getChildren().removeIf(node -> node instanceof Rectangle);
    }

    private void setRectVisibility(boolean visible) {
        for (Node n : rectanglePane.getChildren()) {
            if (n instanceof Rectangle) {
                n.setVisible(visible);
            }
        }
    }

    private void updateLabel(int points, int height, int size, long time, String timeUnit, TreeMode mode) {
        if (mode == TreeMode.KD_TREE) {
            this.KDBench = points + " points -- " + "Height: " + height + " - # Nodes: " + size + " - Time: " + time + " " + timeUnit;
            statsLabel.setText(this.KDBench);
        } else {
            this.QTBench = points + " points -- " + "Height: " + height + " - # Nodes: " + size + " - Time: " + time + " " + timeUnit;
            statsLabel.setText(this.QTBench);
        }
    }

    public enum TreeMode {QUAD_TREE, KD_TREE}
}