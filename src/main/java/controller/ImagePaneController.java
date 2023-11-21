package controller;

import application.PixelGenerator;
import com.jfoenix.controls.JFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import jfxtras.labs.util.event.MouseControlUtil;
import model.quadTree.Area;
import model.quadTree.RegionQuadTree;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ImagePaneController {
    public static int IMAGE_WIDTH = 512, IMAGE_HEIGHT = 512;
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
    @FXML
    private JFXButton cropButton;
    @FXML
    private Pane cropPane;
    private RegionQuadTree regionQuadTree;
    private WritableImage qtImage;
    private List<Image> blurredImages;
    private PixelWriter pixelWriter;
    private Timeline timeline;

    static Rectangle setSelectionRect(Rectangle selectionRect, AtomicInteger[] width, AtomicInteger[] height, AtomicInteger[] x, AtomicInteger[] y, Pane cropPane) {
        width[0] = new AtomicInteger((int) selectionRect.getWidth());
        height[0] = new AtomicInteger((int) selectionRect.getHeight());
        x[0] = new AtomicInteger((int) selectionRect.getX());
        y[0] = new AtomicInteger((int) selectionRect.getY());
        Rectangle rectangle = new Rectangle(x[0].doubleValue(), y[0].doubleValue(), width[0].doubleValue(), height[0].doubleValue());
        cropPane.getChildren().add(rectangle);
        rectangle.setFill(Color.TRANSPARENT);
        return rectangle;
    }

    @FXML
    private void initialize() {
        blurredImages = new ArrayList<>();
        pickImageButton.setOnAction(event -> pickImage());
        encodeButton.setOnAction(actionEvent -> encode());
        decodeButton.setOnAction(e -> showTreeSquares());
        blurButton.setOnAction(e -> blur());
        cropButton.setOnAction(e -> crop());
    }

    private void crop() {
        if (timeline != null) timeline.stop();
        if (qtImage != null)
            compressedImageView.setImage(qtImage);
        else
            compressedImageView.setImage(originalImageView.getImage());
        cropPane.getChildren().clear();
        final Rectangle selectionRect = new Rectangle(10, 10, Color.TRANSPARENT);
        selectionRect.setStroke(Color.YELLOW);
        EventHandler<MouseEvent> mouseDragHandler = event -> cropPane.getChildren().removeIf(node -> node != selectionRect && (node instanceof Rectangle));
        final AtomicInteger[] width = {new AtomicInteger()};
        final AtomicInteger[] height = {new AtomicInteger()};
        final AtomicInteger[] x = {new AtomicInteger()};
        final AtomicInteger[] y = {new AtomicInteger()};

        MouseControlUtil.addSelectionRectangleGesture(cropPane, selectionRect, mouseDragHandler, null, e -> {
            // Create and adjust selection rectangle
            Rectangle rectangle = setSelectionRect(selectionRect, width, height, x, y, cropPane);
            rectangle.setStroke(Color.GREEN);
            rectangle.setId("query");

            // Calculate scale factors for the image
            final double widthRatio = 512.0 / IMAGE_WIDTH;
            final double heightRatio = 512.0 / IMAGE_HEIGHT;

            // Calculate the bounds of the cropping area using the scale factors
            Area cropArea = calculateCropArea(rectangle, widthRatio, heightRatio);

            // Create writable image and its PixelWriter
            WritableImage image = new WritableImage(IMAGE_WIDTH, IMAGE_HEIGHT);
            PixelWriter pixelWriterI = image.getPixelWriter();

            // Set the Image View to display the new image
            compressedImageView.setImage(image);

            // Render the cropped area from the tree onto the image
            renderCroppedAreaOnImage(cropArea, pixelWriterI);
        });
    }

    private Area calculateCropArea(Rectangle selectionRectangle, double widthRatio, double heightRatio) {
        double xMin = selectionRectangle.getX() / widthRatio;
        double xMax = (selectionRectangle.getX() + selectionRectangle.getWidth()) / widthRatio;
        double yMin = selectionRectangle.getY() / heightRatio;
        double yMax = (selectionRectangle.getY() + selectionRectangle.getHeight()) / heightRatio;

        return new Area(xMin, xMax, yMin, yMax);
    }

    private void renderCroppedAreaOnImage(Area cropArea, PixelWriter pixelWriter) {
        List<RegionQuadTree> list = this.regionQuadTree.getCropped(cropArea);
        renderImageFromTree(list, pixelWriter);
    }

    private void encode() {
        treepane.getChildren().clear();
        compressedImageView.setImage(this.qtImage);
        // Get image from original image view
        Image image = originalImageView.getImage();

        // Measure time taken to build quad tree
        double startTime = System.nanoTime();
        this.regionQuadTree = new RegionQuadTree(image);
        this.regionQuadTree.buildTree();
        long elapsedTimeMillis = (long) ((System.nanoTime() - startTime) / 1E6);

        // Compute tree height (exclude root node)
        int treeHeight = this.regionQuadTree.getHeight() - 1;

        // Compute pixel count and compression rate
        double pixelCount = this.regionQuadTree.countLeaves(this.regionQuadTree);
        double compressionRatePercentage = (1 - pixelCount / (IMAGE_WIDTH * IMAGE_HEIGHT)) * 100;

        // Update info label with tree height, compression rate, and time taken
        infoLabel.setText(
                String.format("Tree height: %s. Compression rate: %.0f%% | %s ms",
                        treeHeight,
                        compressionRatePercentage,
                        elapsedTimeMillis)
        );

        // Show overlay of squares in tree in originalImagePane
        showTreeSquares(this.regionQuadTree);
        decodeButton.setDisable(false);
        blurButton.setDisable(false);
        cropButton.setDisable(false);
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
            IMAGE_WIDTH = (int) loadedImage.getWidth();
            IMAGE_HEIGHT = (int) loadedImage.getHeight();
            originalImageView.setImage(loadedImage);
            qtImage = new WritableImage(IMAGE_WIDTH, IMAGE_HEIGHT);
            pixelWriter = qtImage.getPixelWriter();
            System.out.println("Original" + loadedImage.getHeight() * loadedImage.getWidth());
            encodeButton.setDisable(false);
            treepane.getChildren().clear();
        }
    }

    private void drawSquares(RegionQuadTree node) {
        Area square = node.getSquare();
        double width = square.getWidth();
        double height = square.getHeight();
        double widthRatio = 512.0 / IMAGE_WIDTH;
        double heightRatio = 512.0 / IMAGE_HEIGHT;
        if (width >= 6) {
            Rectangle rectangle = new Rectangle(square.xMin() * widthRatio, square.yMin() * heightRatio, width * widthRatio, height * heightRatio);
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
            renderImageFromTree(list, pixelWriterI);
        }
        playBlurredDiashow();
    }

    private void renderImageFromTree(List<RegionQuadTree> list, PixelWriter pixelWriterI) {
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

    public void showTreeSquares(RegionQuadTree node) {
        if (!node.isMixedNode()) {
            drawSquares(node);
        }
        if (node.getNorthEast() != null)
            showTreeSquares((RegionQuadTree) node.getNorthEast());
        if (node.getNorthWest() != null)
            showTreeSquares((RegionQuadTree) node.getNorthWest());
        if (node.getSouthWest() != null)
            showTreeSquares((RegionQuadTree) node.getSouthWest());
        if (node.getSouthEast() != null)
            showTreeSquares((RegionQuadTree) node.getSouthEast());
    }

    private void showTreeSquares() {
        if (this.timeline != null) timeline.stop();
        List<RegionQuadTree> leaves = this.regionQuadTree.gatherLeaves();
        PixelGenerator generator = new PixelGenerator(this.pixelWriter, leaves);
        Thread th = new Thread(generator);
        th.setDaemon(true);
        th.start();
    }
}
