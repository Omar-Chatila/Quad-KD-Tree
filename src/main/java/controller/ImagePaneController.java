package controller;

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
import model.quadTree.Pixel;
import model.quadTree.QuadTree;
import model.quadTree.RegionQuadTree;

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
    private Pane treepane;
    @FXML
    private Label infoLabel;
    private QuadTree<Pixel> regionTree;

    private WritableImage qtImage;
    private PixelWriter pixelWriter;

    @FXML
    private void initialize() {
        qtImage = new WritableImage(IMAGE_WIDTH, IMAGE_HEIGHT);
        pixelWriter = qtImage.getPixelWriter();
        originalImage.setImage(randomPixels(IMAGE_WIDTH, IMAGE_HEIGHT));
        pickImageButton.setOnAction(event -> pickImage());
        encodeButton.setOnAction(actionEvent -> encode());
    }

    private void encode() {
        treepane.getChildren().clear();
        System.out.println("start");
        Image image = originalImage.getImage();
        RegionQuadTree regionQuadTree = new RegionQuadTree(image);
        regionQuadTree.buildTree();
        this.regionTree = regionQuadTree;
        int treeHeight = regionQuadTree.getHeight();
        System.out.println(treeHeight);
        double pixelCount = regionQuadTree.countLeaves(regionQuadTree);
        System.out.println(pixelCount);
        System.out.println("end");
        String compressionRate = Math.round((1 - pixelCount / (512.0 * 512.0)) * 100) + "%";
        infoLabel.setText("Tree height: " + treeHeight + ". Compression rate: " + compressionRate);
        decodeTree(regionQuadTree);
        compressedImage.setImage(this.qtImage);


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
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            Image image = new Image(file.toURI().toString());
            originalImage.setImage(image);
            System.out.println("Original" + image.getHeight() * image.getWidth());
        }
    }

    private void drawSplitLines(RegionQuadTree node) {
        Area square = node.getSquare();
        double width = square.xMax() - square.xMin();

        Rectangle rectangle = new Rectangle(square.xMax(), square.yMin(), width, width);
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.BLACK);
        treepane.getChildren().add(rectangle);

    }

    public void decodeTree(RegionQuadTree node) {
        if (!node.getElements().isEmpty()) {
            drawSplitLines(node);
            Color c = node.getElements().get(0).color();
            for (double x = node.getSquare().xMin(); x < node.getSquare().xMax(); x++) {
                for (double y = node.getSquare().yMin(); y < node.getSquare().yMax(); y++) {
                    int finalX = (int) x;
                    int finalY = (int) y;
                    this.pixelWriter.setColor(finalX, finalY, c);
                }
            }
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
}
