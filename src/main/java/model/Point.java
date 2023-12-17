package model;

import model.quadTree.HasCoordinates;

import java.text.DecimalFormat;

public record Point(String id, double x, double y) implements HasCoordinates {
    public Point(double x, double y) {
        this("", x, y);
    }

    private static String formatDouble(double value) {
        DecimalFormat decimalFormat;
        if (value == (int) value) {
            decimalFormat = new DecimalFormat("#");
        } else {
            decimalFormat = new DecimalFormat("#.##");
        }
        return decimalFormat.format(value);
    }

    @Override
    public String toString() {
        return id + "(" + formatDouble(x) + ", " + formatDouble(y) + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Double.compare(x, point.x) == 0 && Double.compare(y, point.y) == 0;
    }

    @Override
    public double distance(HasCoordinates searchPoint) {
        double dx = searchPoint.x() - x;
        double dy = searchPoint.y() - y;
        return dx * dx + dy * dy;
    }
}
