package controller;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class HelloController {

    @FXML
    private Pane drawingPane;

    @FXML
    void drawPoint(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        Circle circle = new Circle(x, y, 3, Color.BLACK);
        drawingPane.getChildren().add(circle);
    }
}