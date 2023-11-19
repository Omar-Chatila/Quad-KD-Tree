package model.quadTree;

import javafx.scene.paint.Color;

public record Pixel(double x, double y, Color color) implements HasCoordinates {
}
