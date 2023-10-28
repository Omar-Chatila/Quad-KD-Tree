package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import javafx.event.ActionEvent;
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
import model.Point;
import model.kdTree.KDTree;
import model.kdTree.SplitLine;
import model.quadTree.QuadTree;
import model.quadTree.Rectangle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuadTreeController {

    public static final double PANE_WIDTH = 400, PANE_HEIGHT = 400;
    private final List<Point> pointSet = new ArrayList<>();
    private final Rectangle rootRectangle = new Rectangle(0, PANE_WIDTH, 0, PANE_HEIGHT);
    private final Node[][] grid = new Node[400][400];
    public ScrollPane scrollPane;
    private QuadTree dynamicTree = new QuadTree(rootRectangle);
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
        for (int i = 0; i < 10; i++) {
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
        dynamicTree = new QuadTree(rootRectangle);
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
            QuadTree quadTree = new QuadTree(new Rectangle(0, PANE_WIDTH, 0, PANE_HEIGHT), pointSet);
            quadTree.buildQuadTree(quadTree);
            drawNodeRecursive(1000, 20, 1000, 20, quadTree, quadTree.getHeight());
        }

    }


    public void drawNodeRecursive(double x1, double y1, double x, double y, QuadTree node, int height) {
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
            drawNodeRecursive(x, y, x - 1.5 * delta, y + (1 + h / 8.0) * 60, node.getNorthEast(), height - 1);
        if (node.getNorthWest() != null)
            drawNodeRecursive(x, y, x - 0.5 * delta, y + (1 + h / 8.0) * 60, node.getNorthWest(), height - 1);
        if (node.getSouthWest() != null)
            drawNodeRecursive(x, y, x + 0.5 * delta, y + (1 + h / 8.0) * 60, node.getSouthWest(), height - 1);
        if (node.getSouthEast() != null)
            drawNodeRecursive(x, y, x + 1.5 * delta, y + (1 + h / 8.0) * 60, node.getSouthEast(), height - 1);
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

    public void drawKDTree(ActionEvent actionEvent) {
        clearPane();
        Point[] points = {new Point(1, 1), new Point(2.3, 3.3), new Point(1.5, 5)
                , new Point(4.8, 6), new Point(4.7, 1.9), new Point(5.5, 5), new Point(6.5, 6)
                , new Point(6.8, 1.5), new Point(8, 6.3), new Point(9.3, 5.3), new Point(9.1, 2)};
        KDTree kdTree = new KDTree(Arrays.asList(points), new Rectangle(0, 10, 0, 10), 0);
        kdTree.buildTree(kdTree, 0);
        System.out.println("root  " + kdTree.getLevel());
        for (Point p : points) {
            Circle circle = new Circle(40 * p.x(), 400 - 40 * p.y(), 3);
            drawingPane.getChildren().add(circle);
        }


        for (KDTree r : kdTree.getNodeList()) {
            Line horizontalSplit;
            Line verticalSplit;
            if (!r.isLeaf()) {
                if (r.getLevel() % 2 == 0) {
                    SplitLine vertical = r.getVerticalSplitLine();
                    verticalSplit = new Line(40 * vertical.fromX(), PANE_HEIGHT - 40 * vertical.fromY(), 40 * vertical.toX(), PANE_HEIGHT - 40 * vertical.toY());
                    drawingPane.getChildren().add(verticalSplit);
                } else {
                    SplitLine horizontal = r.getHorizontalSplitLine();
                    horizontalSplit = new Line(40 * horizontal.fromX(), PANE_HEIGHT - 40 * horizontal.fromY(), 40 * horizontal.toX(), PANE_HEIGHT - 40 * horizontal.toY());
                    drawingPane.getChildren().add(horizontalSplit);
                }
            }

        }


    }
}