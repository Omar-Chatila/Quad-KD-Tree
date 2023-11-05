package util;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import jfxtras.labs.util.event.MouseControlUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ShapeSelectionExample extends Application {

    private List<Shape> selected = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Group shapesGroup = new Group();
        AnchorPane root = new AnchorPane(shapesGroup);

        // Add whatever shapes you like...
        for (int i = 0; i < 10; i++) {
            Circle circle = new Circle(Math.random() * 400, Math.random() * 300, 2);
            shapesGroup.getChildren().add(circle);
        }

        final Rectangle selectionRect = new Rectangle(10, 10, Color.TRANSPARENT);
        selectionRect.setStroke(Color.BLACK);

        EventHandler<MouseEvent> mouseDragHanlder = event -> {
            shapesGroup.getChildren().removeIf(node -> node instanceof Rectangle);
            for (Node shape : shapesGroup.getChildren()) {
                handleSelection(selectionRect, (Shape) shape);
            }
        };

        final AtomicInteger[] width = {new AtomicInteger()};
        final AtomicInteger[] height = {new AtomicInteger()};
        final AtomicInteger[] x = {new AtomicInteger()};
        final AtomicInteger[] y = {new AtomicInteger()};

        // Add selection gesture
        MouseControlUtil.addSelectionRectangleGesture(root, selectionRect, mouseDragHanlder, null, e -> {

            width[0] = new AtomicInteger((int) selectionRect.getWidth());
            height[0] = new AtomicInteger((int) selectionRect.getHeight());
            x[0] = new AtomicInteger((int) selectionRect.getX());
            y[0] = new AtomicInteger((int) selectionRect.getY());
            Rectangle rectangle = new Rectangle(x[0].doubleValue(), y[0].doubleValue(), width[0].doubleValue(), height[0].doubleValue());
            shapesGroup.getChildren().add(rectangle);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setStroke(Color.BLUE);
            System.out.println(rectangle.getWidth() + ":" + rectangle.getHeight() + " at " + rectangle.getX() + " - " + rectangle.getY());
        });


        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();
    }

    private void handleSelection(Rectangle selectionRect, Shape shape) {
        if (selectionRect.getBoundsInParent().intersects(shape.getBoundsInParent())) {
            shape.setFill(Color.RED);
            if (!this.selected.contains(shape))
                this.selected.add(shape);
        } else {
            shape.setFill(Color.BLACK);
            this.selected.remove(shape);
        }
    }

}