package controller;

import application.PixelGenerator;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXToggleButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import jfxtras.labs.util.event.MouseControlUtil;
import model.quadTree.Area;
import model.quadTree.RegionQuadTree;
import util.SaveImage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

public class ImagePaneController {
    public static int IMAGE_WIDTH = 512, IMAGE_HEIGHT = 512;
    public static double ASPECT_RATIO;
    private final Stack<Rectangle> rectangles = new Stack<>();
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
    private JFXButton rotateButton;
    @FXML
    private Pane cropPane;
    @FXML
    private JFXToggleButton animationsToggle;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private JFXButton saveButton;
    @FXML
    private JFXCheckBox fancy;

    private boolean isAnimated = true;
    private RegionQuadTree regionQuadTree;
    private WritableImage qtImage;
    private List<WritableImage> blurredImages;
    private PixelWriter pixelWriter;
    private Timeline timeline;
    private boolean randomDecode;

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
        animationsToggle.setOnAction(e -> {
            this.isAnimated = !this.isAnimated;
            if (timeline != null) timeline.stop();
        });
        blurredImages = new ArrayList<>();
        pickImageButton.setOnAction(event -> pickImage());
        encodeButton.setOnAction(actionEvent -> encode());
        decodeButton.setOnAction(e -> decodeFromTree());
        blurButton.setOnAction(e -> blur());
        cropButton.setOnAction(e -> crop());
        rotateButton.setOnAction(e -> rotateImage());
        saveButton.setOnAction(e -> saveImage());
    }

    private void saveImage() {
        try {
            SaveImage.saveImageFile((WritableImage) compressedImageView.getImage(), (Stage) blurButton.getScene().getWindow());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void randomDecode() {
        randomDecode = fancy.isSelected();
    }


    private void crop() {
        showTreeSquares(this.regionQuadTree, 100, true, isAnimated);
        if (timeline != null) timeline.stop();
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
        double yMin = selectionRectangle.getY() / heightRatio * (ASPECT_RATIO);
        double yMax = (selectionRectangle.getY() + selectionRectangle.getHeight()) / heightRatio * (ASPECT_RATIO);

        return new Area(xMin, xMax, yMin, yMax);
    }

    private void renderCroppedAreaOnImage(Area cropArea, PixelWriter pixelWriter) {
        List<RegionQuadTree> list = this.regionQuadTree.getCropped(cropArea);
        renderImageFromTree(list, pixelWriter);
    }

    private void encode() {
        progressBar.setProgress(0);
        treepane.getChildren().clear();
        compressedImageView.setImage(this.qtImage);
        // Get image from original image view
        Image image = originalImageView.getImage();
        if (originalImageView.getImage().getHeight() * originalImageView.getImage().getWidth() > 500000) {
            animationsToggle.fire();
            animationsToggle.setDisable(true);
        }

        Thread encodingThread = new Thread(() -> {
            // Measure time taken to build quad tree
            double startTime = System.nanoTime();
            regionQuadTree = new RegionQuadTree(image);
            regionQuadTree.buildTree();
            long elapsedTimeMillis = (long) ((System.nanoTime() - startTime) / 1E6);

            // Compute tree height (exclude root node)
            int treeHeight = regionQuadTree.getHeight() - 1;

            // Compute pixel count and compression rate
            double pixelCount = regionQuadTree.countLeaves(regionQuadTree);
            double compressionRatePercentage = (1 - pixelCount / (IMAGE_WIDTH * IMAGE_HEIGHT)) * 100;

            // Update info labels and UI components in JavaFX application thread to avoid thread access issues.
            Platform.runLater(() -> {
                // Update info label with tree height, compression rate, and time taken
                infoLabel.setText(
                        String.format("RQT-Height: %s. Compr. rate: %.0f%% | Encode: %s ms",
                                treeHeight,
                                compressionRatePercentage,
                                elapsedTimeMillis)
                );

                // Show overlay of squares in tree in originalImagePane
                showTreeSquares(regionQuadTree, 1000, true, isAnimated);
                playSquaresAnimation();

                // Enable previously disabled buttons
            });
            decodeButton.setDisable(false);
            blurButton.setDisable(false);
            cropButton.setDisable(false);
            rotateButton.setDisable(false);
            animationsToggle.setDisable(false);
        });
        encodingThread.setDaemon(true);
        encodingThread.start();
        double aspect = 512 * image.getWidth() / image.getHeight();
        System.out.println(512 * aspect);
    }

    private void pickImage() {
        if (timeline != null) timeline.stop();
        blurredImages.clear();
        compressedImageView.setImage(null);
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.jpg");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
        File resourcesDirectory = new File("src/main/resources/images");
        fileChooser.setInitialDirectory(resourcesDirectory);
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            Image loadedImage = new Image(file.toURI().toString());
            IMAGE_WIDTH = (int) loadedImage.getWidth();
            IMAGE_HEIGHT = (int) loadedImage.getHeight();
            ASPECT_RATIO = loadedImage.getWidth() / loadedImage.getHeight();
            infoLabel.setText("Resolution: " + IMAGE_WIDTH + " × " + IMAGE_HEIGHT + " = " + IMAGE_HEIGHT * IMAGE_WIDTH + "px");
            originalImageView.setImage(loadedImage);
            qtImage = new WritableImage(IMAGE_WIDTH, IMAGE_HEIGHT);
            pixelWriter = qtImage.getPixelWriter();
            System.out.println("Original" + IMAGE_HEIGHT * IMAGE_WIDTH);
            encodeButton.setDisable(false);
            saveButton.setDisable(true);
            treepane.getChildren().clear();
        }
    }

    private void drawSquares(RegionQuadTree node, boolean encoding, boolean isAnimated) {
        Area square = node.getSquare();
        double width = square.getWidth();
        double height = square.getHeight();
        double aspect = (double) (512 * IMAGE_WIDTH) / IMAGE_HEIGHT;
        double widthRatio = (512.0 / IMAGE_WIDTH) * (aspect / 512.0);
        double heightRatio = 512.0 / (IMAGE_HEIGHT);
        if (!encoding || width >= 6) {
            Rectangle rectangle = new Rectangle(square.xMin() * widthRatio, square.yMin() * heightRatio, width * widthRatio, height * heightRatio);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setStroke(Color.DARKVIOLET);
            rectangle.setStrokeWidth(encoding ? 1 : 0.7);
            if (!isAnimated) treepane.getChildren().add(rectangle);
            else rectangles.push(rectangle);
        }
    }

    private void blur() {
        saveButton.setDisable(false);
        treepane.getChildren().clear();
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

    private void playSquaresAnimation() {
        Timeline timeline2 = new Timeline();
        int i = 1;
        progressBar.setVisible(true);
        double increment = 1.0 / rectangles.size();
        while (!rectangles.isEmpty()) {
            Rectangle rectangle = rectangles.pop();
            KeyFrame keyFrame = new KeyFrame(Duration.millis(5 * (i++ + 1)),
                    event -> {
                        progressBar.setProgress(progressBar.getProgress() + increment);
                        treepane.getChildren().add(rectangle);
                    });
            timeline2.getKeyFrames().add(keyFrame);
        }

        timeline2.setOnFinished(e -> {
            decodeButton.setDisable(false);
            blurButton.setDisable(false);
            cropButton.setDisable(false);
            progressBar.setVisible(false);
        });
        timeline2.setCycleCount(1);
        timeline2.play();
    }

    private int currentImageIndex;

    private void playBlurredDiashow() {
        this.timeline = new Timeline();
        for (int i = 0; i < regionQuadTree.getHeight(); i++) {
            int finalI = i;
            this.currentImageIndex = i;
            KeyFrame keyFrame = new KeyFrame(Duration.millis(1000 * (i + 1)),
                    event -> {
                        treepane.getChildren().clear();
                        WritableImage image = blurredImages.get(finalI);
                        compressedImageView.setImage(image);
                        showTreeSquares(regionQuadTree, finalI, false, false);
                    });
            timeline.getKeyFrames().add(keyFrame);
        }
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void showTreeSquares(RegionQuadTree node, int level, boolean encoding, boolean isAnimated) {
        if (!node.isMixedNode()) {
            drawSquares(node, encoding, isAnimated);
        }
        if (level > 0) {
            if (node.getNorthEast() != null)
                showTreeSquares((RegionQuadTree) node.getNorthEast(), level - 1, encoding, isAnimated);
            if (node.getNorthWest() != null)
                showTreeSquares((RegionQuadTree) node.getNorthWest(), level - 1, encoding, isAnimated);
            if (node.getSouthWest() != null)
                showTreeSquares((RegionQuadTree) node.getSouthWest(), level - 1, encoding, isAnimated);
            if (node.getSouthEast() != null)
                showTreeSquares((RegionQuadTree) node.getSouthEast(), level - 1, encoding, isAnimated);
        }
    }

    private void decodeFromTree() {
        if (this.timeline != null) timeline.stop();
        long start = System.nanoTime();
        List<RegionQuadTree> leaves = this.regionQuadTree.gatherLeaves();
        long time = System.nanoTime() - start;
        PixelGenerator generator = new PixelGenerator(this.pixelWriter, leaves, randomDecode);

        if (isAnimated) {
            Thread th = new Thread(generator);
            th.setDaemon(true);
            th.start();
        } else {
            renderImageFromTree(leaves, pixelWriter);
        }
        saveButton.setDisable(false);
        infoLabel.setText(infoLabel.getText() + " | Decode: " + (int) (time / 1E6) + "ms");
    }

    private void rotateImage() {
        treepane.getChildren().clear();
        regionQuadTree.rotate(this.regionQuadTree);
        System.out.println(regionQuadTree.getHeight());
        renderImageFromTree(regionQuadTree.gatherLeaves(), this.pixelWriter);
        originalImageView.setImage(null);
        showTreeSquares(regionQuadTree, 10000, true, false);
    }
}
