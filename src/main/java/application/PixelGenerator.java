package application;

import javafx.application.Platform;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import model.quadTree.Pixel;

import java.util.List;
import java.util.Objects;

public class PixelGenerator implements Runnable {
    private final PixelWriter writer;
    private final List<Pixel> pixels;

    public PixelGenerator(PixelWriter pw, List<Pixel> pixels) {
        this.writer = Objects.requireNonNull(pw, "Writer cannot be null");
        this.pixels = pixels;
    }

    public void run() {
        try {
            int cycle = 0;
            for (Pixel toDraw : this.pixels) {
                Color pixel = toDraw.color();
                int finalX = (int) toDraw.x();
                int finalY = (int) toDraw.y();
                Platform.runLater(() -> writer.setColor(finalX, finalY, pixel));
                try {
                    if (++cycle % 12 == 0) {
                        Thread.sleep(1);  // Optional: to slow down the drawing for visualization.
                    }
                } catch (InterruptedException e) {
                    System.err.println("Interrupted, exiting.");
                    return;
                }
            }

        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }
}

