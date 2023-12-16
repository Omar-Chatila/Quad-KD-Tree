package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXToggleButton;
import javafx.animation.ScaleTransition;
import javafx.event.EventHandler;
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
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import jfxtras.labs.util.event.MouseControlUtil;
import model.Point;
import model.Tree;
import model.kdTree.KDTreeEfficient;
import model.kdTree.MyKDTree;
import model.kdTree.SplitLine;
import model.quadTree.Area;
import model.quadTree.PointQuadTree;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TreeController {
    public static final double PANE_WIDTH = 400, PANE_HEIGHT = 400;
    static int level = 100;
    private final List<Point> pointSet = new ArrayList<>();
    private final Area rootArea = new Area(0, PANE_WIDTH, 0, PANE_HEIGHT);
    private final Node[][] grid = new Node[400][400];
    private final List<Shape> selected = new ArrayList<>();
    public ScrollPane scrollPane;
    TreeMode mode = TreeMode.QUAD_TREE;
    private boolean colorized = false;
    private int leafCapacity = 1;
    private PointQuadTree dynamicPointQuadTree = new PointQuadTree(rootArea, leafCapacity, false);
    private MyKDTree dynamicKDTree = new MyKDTree(rootArea);
    @FXML
    private JFXButton clearButton;
    @FXML
    private Pane drawingPane;
    @FXML
    private JFXTextArea pointsLabel;
    @FXML
    private JFXCheckBox isDemo;
    @FXML
    private Pane rectanglePane;
    @FXML
    private Label statsLabel;
    @FXML
    private JFXToggleButton toggleButton;
    @FXML
    private Pane treePane;
    @FXML
    private JFXCheckBox stepByStep;
    private boolean isDrawMode = true;
    private String KDBench, QTBench;
    private boolean isDemoMode;

    @FXML
    private void initialize() {
        pointsLabel.setEditable(false);
        clearButton.setOnAction(actionEvent -> clearPane());
        rectanglePane.toBack();
        drawingPane.setStyle("-fx-background-color: transparent");
        rectanglePane.setStyle("-fx-background-color: white");
        isDemo.setOnAction(e -> isDemoMode = !isDemoMode);
        initQuery();
    }

    @FXML
    void selectDrawMode() {
        this.isDrawMode = true;
        removeQueryRect();
    }

    @FXML
    void setSteps() {
        level = stepByStep.isSelected() ? 0 : 100;
    }

    @FXML
    void selectQueryMode() {
        this.isDrawMode = false;
    }

    private void benchQT() {
        long start1 = System.nanoTime();
        dynamicPointQuadTree = new PointQuadTree(pointSet, rootArea, leafCapacity, false);
        dynamicPointQuadTree.buildTree();
        long end = (System.nanoTime() - start1) / 1000;
        updateLabel(dynamicPointQuadTree.getElements().size(), dynamicPointQuadTree.getHeight() - 1, dynamicPointQuadTree.size(dynamicPointQuadTree), end, "µs", TreeMode.QUAD_TREE);
    }

    @FXML
    void toggleMode() {
        if (stepByStep.isSelected()) level = 0;
        removeLines();
        removeSquares();
        treePane.getChildren().clear();
        if (mode == TreeMode.KD_TREE) {
            toggleButton.setText("QuadTree");
            mode = TreeMode.QUAD_TREE;
            if (!pointSet.isEmpty()) {
                if (dynamicPointQuadTree.getElements().isEmpty()) {
                    benchQT();
                    dynamicPointQuadTree.buildTree();
                }
                //drawQTRecursive(500, 20, 500, 20, dynamicPointQuadTree, dynamicPointQuadTree.getHeight(), Color.YELLOW);
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
                drawKDRecursive(500, 20, 500, 20, dynamicKDTree, dynamicKDTree.getHeight(), level++);
            }
            statsLabel.setText(this.KDBench);
        }
    }

    private void benchKD() {
        long start1 = System.nanoTime();
        dynamicKDTree = new MyKDTree(pointSet, rootArea, 0);
        dynamicKDTree.buildTree();
        System.out.println(dynamicKDTree.getHeight());
        long end = (System.nanoTime() - start1) / 1000;
        updateLabel(dynamicKDTree.getPoints().size(), dynamicKDTree.getHeight() - 1, dynamicKDTree.size(dynamicKDTree), end, "µs", TreeMode.KD_TREE);
    }

    @FXML
    void drawPoint(MouseEvent event) {
        if (isDemoMode) removeSquares();
        if (this.isDrawMode) {
            double x = event.getX();
            double y = event.getY();
            addPoint(x, y);
        }
    }

    @FXML
    void randomize() {
        clearPane();
        if (isDemoMode) {
            Point[] demoPoints = {new Point(61.2, 280.4), new Point(182, 366.8), new Point(198.8, 356.4),
                    new Point(340.4, 202), new Point(206.8, 253.2), new Point(109.2, 218.8), new Point(54.8, 103.6),
                    new Point(261.2, 40.4)};
            for (Point p : demoPoints) {
                addPointToGui(p.x(), p.y(), p);
            }
        } else {
            for (int i = 0; i < 100; i++) {
                double x = Math.random() * PANE_WIDTH;
                double y = Math.random() * PANE_HEIGHT;
                addPointToGui(x, y, new Point(x, y));
            }
        }
    }

    @FXML
    void generate() {
        if (mode == TreeMode.QUAD_TREE) {
            generateQuadTree();
            displayQuadTree(500, 20, 500, 20, dynamicPointQuadTree, dynamicPointQuadTree.getHeight(), Color.YELLOW, level++);
            if (!colorized) {
                setRectVisibility(false);
            }
        } else {
            generateKDTree();
            drawKDRecursive(500, 20, 500, 20, dynamicKDTree, dynamicKDTree.getHeight(), level++);
        }
    }

    // sdf
    @FXML
    void stressTest() {
        clearPane();
        int pointsCount = (int) 1E6;
        List<Point> testList = new ArrayList<>();
        for (int i = 0; i < pointsCount; i++) {
            testList.add(new Point(Math.random() * 400000, Math.random() * 400000));
        }
        Point[] points = testList.toArray(new Point[0]);
        long start = System.nanoTime();
        Area testArea = new Area(0, 400000, 0, 400000);
        if (mode == TreeMode.QUAD_TREE) {
            PointQuadTree pointQuadTree = new PointQuadTree(testList, testArea, leafCapacity, false);
            pointQuadTree.buildTree();
            long time = (System.nanoTime() - start) / 1000000;
            int height = pointQuadTree.getHeight();
            int number = pointQuadTree.size(pointQuadTree);
            updateLabel(pointsCount, height - 1, number, time, "ms", mode);
        } else {
            KDTreeEfficient kdTree = new KDTreeEfficient(points, testArea);
            kdTree.buildTree();
            long time = (System.nanoTime() - start) / 1000000;
            int height = kdTree.getHeight();
            int number = kdTree.size(kdTree);
            updateLabel(pointsCount, height - 1, number, time, "ms", mode);
        }
    }

    @FXML
    void colorize() {
        setRectVisibility(!colorized);
        colorized = !colorized;
    }

    private void clearPane() {
        if (stepByStep.isSelected()) level = 0;
        pointSet.clear();
        drawingPane.getChildren().clear();
        pointsLabel.clear();
        treePane.getChildren().clear();
        dynamicPointQuadTree = new PointQuadTree(rootArea, leafCapacity, false);
        dynamicKDTree = new MyKDTree(rootArea);
        statsLabel.setText("");
        removeRectangles();
    }

    private void addPoint(double x, double y) {
        Point point = new Point(x, PANE_HEIGHT - y);
        if (!pointSet.contains(point)) {
            treePane.getChildren().clear();
            addPointToGui(x, y, point);
            dynamicKDTree.add(point);
            dynamicPointQuadTree.add(point);
            removeLines();
            removeRectangles();
            if (!stepByStep.isSelected()) {
                if (mode == TreeMode.KD_TREE) {
                    drawKDRecursive(500, 20, 500, 20, dynamicKDTree, dynamicKDTree.getHeight(), 100);
                } else {
                    displayQuadTree(500, 20, 500, 20, dynamicPointQuadTree, dynamicPointQuadTree.getHeight(), Color.YELLOW, 100);
                }
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

    public void displayQuadTree(double x1, double y1, double x, double y, PointQuadTree node, int height, Color color, int level) {
        if (node == null) node = new PointQuadTree(pointSet, rootArea, 1, false);
        Line line = new Line(x1, y1 + 5, x, y);
        treePane.getChildren().add(line);
        if (node.isPointLeaf()) {
            Rectangle rectangle = getRectangle(x, y, node);
            treePane.getChildren().add(rectangle);
        } else {
            Circle circle = new Circle(x, y, 10, color);
            if (!node.isNodeLeaf() && !stepByStep.isSelected()) {
                drawSplitLines(node);
            } else {
                drawQuadrants(node);
            }
            treePane.getChildren().add(circle);
        }
        if (node.getHeight() == 1) {
            Rectangle rectangle2 = generateRectangle(node, color);
            drawQuadrants(node);
            rectanglePane.getChildren().add(rectangle2);
        }
        int h = node.getHeight();
        double delta = (Math.pow(1.9, h) * 3 + 30);
        if (node.getNorthEast() != null && level > 0)
            displayQuadTree(x, y, x - 1.5 * delta, y + (1 + h / 8.0) * 60, (PointQuadTree) node.getNorthEast(), height - 1, Color.ORANGE, level - 1);
        if (node.getNorthWest() != null && level > 0)
            displayQuadTree(x, y, x - 0.5 * delta, y + (1 + h / 8.0) * 60, (PointQuadTree) node.getNorthWest(), height - 1, Color.GREEN, level - 1);
        if (node.getSouthWest() != null && level > 0)
            displayQuadTree(x, y, x + 0.5 * delta, y + (1 + h / 8.0) * 60, (PointQuadTree) node.getSouthWest(), height - 1, Color.BLUEVIOLET, level - 1);
        if (node.getSouthEast() != null && level > 0)
            displayQuadTree(x, y, x + 1.5 * delta, y + (1 + h / 8.0) * 60, (PointQuadTree) node.getSouthEast(), height - 1, Color.RED, level - 1);
    }

    private Rectangle getRectangle(double x, double y, PointQuadTree node) {
        Rectangle rectangle = new Rectangle(x, y, 10, 10);
        rectangle.setOnMouseEntered(e -> {
            for (Point p : node.getElements()) {
                Circle corresponding = (Circle) grid[(int) p.x()][(int) p.y()];
                corresponding.setScaleX(2.5);
                corresponding.setScaleY(2.5);
                corresponding.setFill(Color.RED);
                corresponding.setEffect(new Glow(0.8));
            }
        });

        rectangle.setOnMouseExited(e -> {
            for (Point p : node.getElements()) {
                Circle corresponding = (Circle) grid[(int) p.x()][(int) p.y()];
                corresponding.setScaleX(1);
                corresponding.setScaleY(1);
                corresponding.setFill(Color.BLACK);
                corresponding.setEffect(null);
            }
        });
        return rectangle;
    }

    private void drawQuadrants(PointQuadTree node) {
        Area area = node.getSquare();
        Rectangle rectangle = new Rectangle(area.xMin(), PANE_HEIGHT - area.yMax(), area.getWidth(), area.getHeight());
        rectangle.setStroke(Color.BLACK);
        rectangle.setFill(Color.TRANSPARENT);
        drawingPane.getChildren().add(rectangle);
    }


    private void drawSplitLines(PointQuadTree node) {
        Area area = node.getSquare();
        Line horizontalSplit = new Line(area.xMin(), PANE_HEIGHT - area.yMid(), area.xMax(), PANE_HEIGHT - area.yMid());
        Line verticalSplit = new Line(area.xMid(), PANE_HEIGHT - area.yMin(), area.xMid(), PANE_HEIGHT - area.yMax());
        drawingPane.getChildren().addAll(horizontalSplit, verticalSplit);

    }

    private void addPointToGui(double x, double y, Point p) {
        Circle circle = new Circle(x, y, 4, Color.BLACK);
        circle.setId("(" + (int) x + ", " + (int) (PANE_HEIGHT - y) + ")");
        if (isDemoMode)
            drawingPane.getChildren().add(new Text(x + 3, y, circle.getId()));
        else
            circle.setOnMouseEntered(mouseEvent -> drawingPane.getChildren().add(new Text(x + 5, y - 5, circle.getId())));
        //circle.setOnMouseExited(mouseEvent -> drawingPane.getChildren().removeIf((node) -> node instanceof Text));
        grid[(int) x][(int) (PANE_HEIGHT - y)] = circle;
        drawingPane.getChildren().add(circle);
        if (!pointSet.contains(p)) {
            pointSet.add(new Point(x, PANE_HEIGHT - y));
        }
        pointsLabel.setText("P = { " + pointSet.toString().substring(1, pointSet.toString().length() - 1) + " }");
    }

    public void drawKDRecursive(double x1, double y1, double x, double y, MyKDTree node, int height, int level) {
        Line line = new Line(x1, y1 + 5, x, y);
        treePane.getChildren().add(line);
        if (!node.isLeaf()) {
            createInnerNode(x, y, node);
        } else {
            Rectangle rectangle = generateRectangle(node, Color.TRANSPARENT);
            rectangle.setStroke(Color.BLACK);
            rectangle.setStrokeWidth(1.5);
            drawingPane.getChildren().add(rectangle);
            createLeaf(x, y, node);
        }
        int h = node.getHeight();
        double delta = (Math.pow(2.8, h - 1) * 5 + 50);
        if (h == 1) delta = 30;
        if (node.getLeftChild() != null && level > 0)
            drawKDRecursive(x, y, x - delta, y + (1 + h / 8.0) * 60, node.getLeftChild(), height - 1, level - 1);
        if (node.getRightChild() != null && level > 0)
            drawKDRecursive(x, y, x + delta, y + (1 + h / 8.0) * 60, node.getRightChild(), height - 1, level - 1);
    }

    private void createInnerNode(double x, double y, MyKDTree node) {
        Circle circle = new Circle(x, y, 10);
        Color c = node.getLevel() % 2 == 0 ? Color.FORESTGREEN : Color.BLUEVIOLET;
        circle.setFill(c);
        SplitLine sp = node.getSplitLine();
        Line splitline = new Line(sp.fromX(), PANE_HEIGHT - sp.fromY(), sp.toX(), PANE_HEIGHT - sp.toY());
        Text text = new Text(x + 20, y, (node.getLevel() % 2 == 0 ? "x = " + Math.round(sp.fromX() * 10) / 10.0 : "y = " + Math.round(sp.fromY() * 10.0) / 10.0));
        Rectangle rectangle = generateRectangle(node, Color.TRANSPARENT);
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(1.5);
        drawingPane.getChildren().add(rectangle);
        treePane.getChildren().addAll(circle, text);
    }

    private void createLeaf(double x, double y, MyKDTree node) {
        Rectangle rectangle = new Rectangle(x, y);
        rectangle.setWidth(10);
        rectangle.setHeight(10);
        rectangle.setFill(Color.BLACK);
        Text text = new Text(x - 20, y + 10, node.getPoints().toString());
        treePane.getChildren().add(text);
    }

    private Rectangle generateRectangle(Tree<Point> node, Color color) {
        Area square;
        if (mode == TreeMode.QUAD_TREE) square = ((PointQuadTree) node).getSquare();
        else square = ((MyKDTree) node).getArea();
        double width = square.xMax() - square.xMin();
        double height = square.yMax() - square.yMin();
        Rectangle rectangle2 = new Rectangle(square.xMin(), 400 - square.yMax(), width, height);
        rectangle2.setOpacity(0.5);
        rectangle2.setFill(color);
        rectangle2.toBack();
        return rectangle2;
    }

    private void removeLines() {
        drawingPane.getChildren().removeIf(node -> node instanceof Line);
    }

    private void removeSquares() {
        drawingPane.getChildren().removeIf(node -> node instanceof Rectangle);
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

    private void initQuery() {
        final Rectangle selectionRect = new Rectangle(10, 10, Color.TRANSPARENT);
        selectionRect.setStroke(Color.BLACK);
        EventHandler<MouseEvent> mouseDragHandler = event -> {
            if (!this.isDrawMode) {
                drawingPane.getChildren().removeIf(node -> node != selectionRect && (node instanceof Rectangle));
            }
        };
        final AtomicInteger[] width = {new AtomicInteger()};
        final AtomicInteger[] height = {new AtomicInteger()};
        final AtomicInteger[] x = {new AtomicInteger()};
        final AtomicInteger[] y = {new AtomicInteger()};

        MouseControlUtil.addSelectionRectangleGesture(drawingPane, selectionRect, mouseDragHandler, null, e -> {
            if (!this.isDrawMode) {
                Rectangle rectangle = ImagePaneController.setSelectionRect(selectionRect, width, height, x, y, drawingPane);
                rectangle.setStroke(Color.BLUE);
                rectangle.setId("query");
                performQuery(rectangle, selectionRect);
            }
        });
    }

    private void performQuery(Rectangle rectangle, Rectangle selectionRect) {
        Area queryArea = new Area(rectangle.getX(), rectangle.getX() + rectangle.getWidth(), PANE_HEIGHT - rectangle.getY() - rectangle.getHeight(), PANE_HEIGHT - rectangle.getY());
        List<Point> queried;
        if (mode == TreeMode.KD_TREE) {
            queried = dynamicKDTree.query(queryArea);
        } else {
            queried = dynamicPointQuadTree.query(queryArea);
        }
        statsLabel.setText("Points in " + queryArea + ": " + queried);
        statsLabel.setFont(Font.font(18));
        for (Node shape : drawingPane.getChildren()) {
            handleSelection(selectionRect, (Shape) shape);
        }
    }

    private void removeQueryRect() {
        this.drawingPane.getChildren().removeIf(node -> node.getId() != null && node.getId().equals("query"));
    }

    private void handleSelection(Rectangle selectionRect, Shape shape) {
        if (!this.isDrawMode) {
            if (selectionRect.getBoundsInParent().intersects(shape.getBoundsInParent())) {
                if (shape instanceof Circle circle) {
                    circle.setFill(Color.RED);
                    playScaleAnimation(circle);
                }
                if (!this.selected.contains(shape))
                    this.selected.add(shape);
            } else {
                shape.setFill(Color.BLACK);
                this.selected.remove(shape);
            }
        }
    }

    private void playScaleAnimation(Circle button) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.5), button);
        scaleTransition.setToX(2.2);
        scaleTransition.setToY(2.2);
        scaleTransition.setCycleCount(10);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();
    }

    public enum TreeMode {QUAD_TREE, KD_TREE}
}