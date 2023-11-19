package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        //FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("quadTreeView.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("ImagePane.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("QuadTree & KD-Tree - Demo");
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
    }
}