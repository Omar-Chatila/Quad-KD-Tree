package model.quadTree;

import javafx.scene.paint.Color;

public record Pixel(double x, double y, Color color) implements HasCoordinates {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pixel pixel = (Pixel) o;
        return areColorsClose(this.color, pixel.color);
    }

    private boolean areColorsClose(Color a, Color b) {
        double colorDistance = getColorDistance(a, b);
        return colorDistance == 0; //< 0.035;
    }

    private double getColorDistance(Color a, Color b) {
        double redDiff = Math.pow(a.getRed() - b.getRed(), 2);
        double greenDiff = Math.pow(a.getGreen() - b.getGreen(), 2);
        double blueDiff = Math.pow(a.getBlue() - b.getBlue(), 2);
        double alphaDiff = Math.pow(a.getOpacity() - b.getOpacity(), 2);
        return Math.sqrt(redDiff + greenDiff + blueDiff + alphaDiff);
    }

    @Override
    public double distance(HasCoordinates searchPoint) {
        Color b = ((Pixel) searchPoint).color;
        return getColorDistance(this.color, b);
    }
}
