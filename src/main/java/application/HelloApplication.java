package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Point;
import model.QuadTree;
import model.Square;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Quad Tree - Demo");
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
        createTree();
    }

    public static void main(String[] args) {
        launch();
    }

    private void createTree() {
        Point[] points = {new Point(10, 23),
                new Point(50, 50),
                new Point(5  , 83),
                new Point(90, 63),
                new Point(15, 53),
                new Point(69, 42),
                new Point(88, 18),
                new Point(67, 48)};
        List<Point> pointList = new ArrayList<>();
        Collections.addAll(pointList, points);
        QuadTree quadTree = new QuadTree(new Square(0, 100, 0, 100), pointList);
        quadTree.buildQuadTree(quadTree);
        System.out.println("ende");
    }
}