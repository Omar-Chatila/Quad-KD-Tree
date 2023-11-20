package controller;

import application.PixelGenerator;
import com.jfoenix.controls.JFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import model.quadTree.Area;
import model.quadTree.RegionQuadTree;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImagePaneController {
    public static final int IMAGE_WIDTH = 512, IMAGE_HEIGHT = 512;
    int index = 0;
    @FXML
    private ImageView compressedImageView;
    @FXML
    private ImageView originalImageView;
    @FXML
    private JFXButton encodeButton;
    @FXML
    private JFXButton pickImageButton;
    @FXML
    private Pane treepane;
    @FXML
    private Label infoLabel;
    @FXML
    private JFXButton blurButton;
    @FXML
    private JFXButton decodeButton;
    private RegionQuadTree regionQuadTree;
    private WritableImage qtImage;
    private List<Image> blurredImages;
    private PixelWriter pixelWriter;
    private Timeline timeline;

    @FXML
    private void initialize() {
        qtImage = new WritableImage(IMAGE_WIDTH, IMAGE_HEIGHT);
        blurredImages = new ArrayList<>();
        pixelWriter = qtImage.getPixelWriter();
        pickImageButton.setOnAction(event -> pickImage());
        encodeButton.setOnAction(actionEvent -> encode());
        decodeButton.setOnAction(e -> decode());
        blurButton.setOnAction(e -> blur());
    }

    private void encode() {
        treepane.getChildren().clear();
        compressedImageView.setImage(this.qtImage);
        Image image = originalImageView.getImage();
        double time = System.nanoTime();
        this.regionQuadTree = new RegionQuadTree(image);
        this.regionQuadTree.buildTree();
        long end = (long) ((System.nanoTime() - time) / (int) 1E6);
        int treeHeight = this.regionQuadTree.getHeight() - 1;  // real height doesnt include root node
        double pixelCount = this.regionQuadTree.countLeaves(this.regionQuadTree);
        System.out.println(pixelCount);
        String compressionRate = Math.round((1 - pixelCount / (512.0 * 512.0)) * 10000) / 100 + "%";
        infoLabel.setText("Tree height: " + treeHeight + ". Compression rate: " + compressionRate + " | " + end + " ms");
        decodeTree(this.regionQuadTree);
        decodeButton.setDisable(false);
        blurButton.setDisable(false);
    }

    private void pickImage() {
        if (timeline != null) timeline.stop();
        blurredImages.clear();
        compressedImageView.setImage(null);
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
        File resourcesDirectory = new File("src/main/resources/images");
        fileChooser.setInitialDirectory(resourcesDirectory);
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            Image loadedImage = new Image(file.toURI().toString());
            originalImageView.setImage(loadedImage);
            System.out.println("Original" + loadedImage.getHeight() * loadedImage.getWidth());
            encodeButton.setDisable(false);
            treepane.getChildren().clear();
        }
    }

    private void drawSplitLines(RegionQuadTree node) {
        Area square = node.getSquare();
        double width = square.xMax() - square.xMin();
        if (width >= 6) {
            Rectangle rectangle = new Rectangle(square.xMin(), square.yMax(), width, width);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setStroke(Color.LIMEGREEN);
            treepane.getChildren().add(rectangle);
        }
    }

    private void blur() {
        for (int i = 0; i < regionQuadTree.getHeight(); i++) {
            WritableImage image = new WritableImage(IMAGE_WIDTH, IMAGE_HEIGHT);
            PixelWriter pixelWriterI = image.getPixelWriter();
            blurredImages.add(image);
            List<RegionQuadTree> list = this.regionQuadTree.getNodesAtLevel(i);
            System.out.println(i + ": " + list.size());
            for (RegionQuadTree node : list) {
                Color pixelColor = node.getBlendedColor();
                if (pixelColor != null) {
                    for (int x = (int) node.getSquare().xMin(); x < node.getSquare().xMax(); x++) {
                        for (int y = (int) node.getSquare().yMin(); y < node.getSquare().yMax(); y++) {
                            pixelWriterI.setColor(x, y, pixelColor);
                        }
                    }
                }
            }
        }
        playBlurredDiashow();
    }

    private void playBlurredDiashow() {
        this.timeline = new Timeline(
                new KeyFrame(Duration.millis(300),
                        event -> changeImage()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void changeImage() {
        int cycleLength = this.blurredImages.size();
        compressedImageView.setImage(blurredImages.get(index++ % cycleLength));
    }

    public void decodeTree(RegionQuadTree node) {
        if (!node.isMixedNode()) {
            drawSplitLines(node);
        }
        if (node.getNorthEast() != null)
            decodeTree((RegionQuadTree) node.getNorthEast());
        if (node.getNorthWest() != null)
            decodeTree((RegionQuadTree) node.getNorthWest());
        if (node.getSouthWest() != null)
            decodeTree((RegionQuadTree) node.getSouthWest());
        if (node.getSouthEast() != null)
            decodeTree((RegionQuadTree) node.getSouthEast());
    }

    private void decode() {
        List<RegionQuadTree> leaves = this.regionQuadTree.gatherLeaves();
        PixelGenerator generator = new PixelGenerator(this.pixelWriter, leaves);
        Thread th = new Thread(generator);
        th.setDaemon(true);
        th.start();
    }
}
