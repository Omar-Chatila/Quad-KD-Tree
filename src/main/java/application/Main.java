package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        //FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("quadTreeView.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("mainMenu.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("QuadTree & KD-Tree - Demo");
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
    }
}