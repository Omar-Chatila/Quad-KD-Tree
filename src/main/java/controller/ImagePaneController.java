package controller;

import application.PixelGenerator;
import com.jfoenix.controls.JFXButton;
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
import model.quadTree.Area;
import model.quadTree.RegionQuadTree;

import java.io.File;
import java.util.List;

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
    private Pane treepane;
    @FXML
    private Label infoLabel;
    @FXML
    private JFXButton decodeButton;

    private RegionQuadTree regionQuadTree;
    private WritableImage qtImage;
    private PixelWriter pixelWriter;

    @FXML
    private void initialize() {
        qtImage = new WritableImage(IMAGE_WIDTH, IMAGE_HEIGHT);
        pixelWriter = qtImage.getPixelWriter();
        pickImageButton.setOnAction(event -> pickImage());
        encodeButton.setOnAction(actionEvent -> encode());
        decodeButton.setOnAction(e -> decode());
    }

    private void encode() {
        treepane.getChildren().clear();
        compressedImage.setImage(this.qtImage);
        Image image = originalImage.getImage();
        double time = System.nanoTime();
        this.regionQuadTree = new RegionQuadTree(image);
        this.regionQuadTree.buildTree();
        long end = (long) ((System.nanoTime() - time) / (int) 1E6);
        int treeHeight = this.regionQuadTree.getHeight() - 1;  // real height doesnt include root node
        double pixelCount = this.regionQuadTree.countLeaves(this.regionQuadTree);
        String compressionRate = Math.round((1 - pixelCount / (512.0 * 512.0)) * 100) + "%";
        infoLabel.setText("Tree height: " + treeHeight + ". Compression rate: " + compressionRate + " | " + end + " ms");
        decodeTree(this.regionQuadTree);
        decodeButton.setDisable(false);
    }

    private void pickImage() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
        File resourcesDirectory = new File("src/main/resources/images");
        fileChooser.setInitialDirectory(resourcesDirectory);
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            Image image = new Image(file.toURI().toString());
            originalImage.setImage(image);
            System.out.println("Original" + image.getHeight() * image.getWidth());
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
