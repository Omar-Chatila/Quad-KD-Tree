package model.quadTree;

import javafx.scene.paint.Color;

public record Pixel(double x, double y, Color color) implements HasCoordinates {
    public boolean equals(Object pixel) {
        Pixel other = (Pixel) pixel;
        return other != null && other.color.equals(this.color);
    }
}
