package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.nio.IntBuffer;
import java.util.Objects;

public class PixelGeneratorTest
        extends Application {
    public static final int WIDTH = Integer.getInteger("width", 500);
    public static final int HEIGHT = Integer.getInteger("height", 500);

    private static final PixelFormat<IntBuffer> pixelFormat =
            PixelFormat.getIntArgbInstance();

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);

        stage.setTitle("PixelGenerator Test");
        stage.setScene(new Scene(new BorderPane(canvas)));
        stage.show();

        GraphicsContext gc = canvas.getGraphicsContext2D();
        PixelWriter pw = gc.getPixelWriter();

        PixelGenerator generator = new PixelGenerator(WIDTH, HEIGHT, pw);

        Thread th = new Thread(generator);
        th.setDaemon(true);
        th.start();
    }

    public class PixelGenerator
            implements Runnable {

        private final int width;
        private final int height;
        private final PixelWriter writer;

        public PixelGenerator(int width,
                              int height,
                              PixelWriter pw) {
            this.width = width;
            this.height = height;
            this.writer = Objects.requireNonNull(pw, "Writer cannot be null");
        }

        public void run() {
            try {
                int cycle = 0;
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        Color pixel = Color.hsb(
                                y * 360.0 / HEIGHT, 1, (double) x / WIDTH);

                        int finalX = x;
                        int finalY = y;
                        Platform.runLater(() ->
                                writer.setColor(finalX, finalY, pixel));

                        try {
                            if (++cycle % 8 == 0) {
                                Thread.sleep(1);  // Optional: to slow down the drawing for visualization.
                            }
                        } catch (InterruptedException e) {
                            System.err.println("Interrupted, exiting.");
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("An error occurred: " + e.getMessage());
            }
        }
    }
}
