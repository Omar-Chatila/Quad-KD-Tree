package application;

import javafx.application.Platform;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import model.quadTree.Pixel;
import model.quadTree.RegionQuadTree;

import java.util.List;
import java.util.Objects;

public class PixelGenerator implements Runnable {
    private final PixelWriter writer;
    private final List<RegionQuadTree> regionQuadTrees;

    public PixelGenerator(PixelWriter pw, List<RegionQuadTree> regionQuadTrees) {
        this.writer = Objects.requireNonNull(pw, "Writer cannot be null");
        this.regionQuadTrees = regionQuadTrees;
    }

    public void run() {
        try {
            int cycle = 0;
            for (RegionQuadTree node : this.regionQuadTrees) {
                Pixel corresponding = node.getElements().get(0);
                Color pixelColor = corresponding.color();
                for (int x = (int) node.getSquare().xMin(); x < node.getSquare().xMax(); x++) {
                    for (int y = (int) node.getSquare().yMin(); y < node.getSquare().yMax(); y++) {
                        int finalX = x;
                        int finalY = y;
                        Platform.runLater(() -> writer.setColor(finalX, finalY, pixelColor));
                    }
                }
                try {
                    if (++cycle % 2 == 0) {
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

