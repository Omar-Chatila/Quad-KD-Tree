package controller;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Random;

public class ImagePaneController {
    public static final int IMAGE_WIDTH = 512, IMAGE_HEIGHT = 512;
    @FXML
    private ImageView compressedImage;
    @FXML
    private JFXButton encodeButton;
    @FXML
    private ImageView originalImage;
    @FXML
    private JFXButton pickImageButton;

    @FXML
    private void initialize() {
        originalImage.setImage(randomPixels(IMAGE_WIDTH, IMAGE_HEIGHT));
        pickImageButton.setOnAction(event -> pickImage());
    }

    public Image randomPixels(int width, int height) {
        WritableImage img = new WritableImage(width, height);
        PixelWriter pw = img.getPixelWriter();
        Random rnd = new Random();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Do the pixel manipulation
                pw.setColor(x, y, Color.rgb(rnd.nextInt(255),
                        rnd.nextInt(255),
                        rnd.nextInt(255)));
            }
        }
        return img;
    }

    private void pickImage() {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

        //Show open file dialog
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            Image image = new Image(file.toURI().toString());
            originalImage.setImage(image);
        }
    }
}
