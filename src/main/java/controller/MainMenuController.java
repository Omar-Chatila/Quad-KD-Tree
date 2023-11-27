package controller;

import application.Main;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;

import java.io.IOException;

public class MainMenuController {

    @FXML
    private JFXButton demoButton;
    @FXML
    private JFXButton kdTreeButton;
    @FXML
    private JFXButton rQTButton;

    @FXML
    private void initialize() {
        demoButton.setOnAction(e -> loadDemo("quadTreeView.fxml"));
        rQTButton.setOnAction(e -> loadDemo("ImagePane.fxml"));
    }

    private void loadDemo(String file) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(file));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Main.primaryStage.setTitle("QuadTree & KD-Tree - Demo");
        Main.primaryStage.setScene(scene);
        Main.primaryStage.setResizable(false);
        Main.primaryStage.show();
        
        Main.primaryStage.setX((screenBounds.getWidth() - Main.primaryStage.getWidth()) / 2);
        Main.primaryStage.setY((screenBounds.getHeight() - Main.primaryStage.getHeight()) / 2);

    }

}