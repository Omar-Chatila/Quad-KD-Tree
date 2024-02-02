package model;

import model.quadTree.HasCoordinates;

import java.util.Locale;

public record Point(String id, double x, double y) implements HasCoordinates {
    public Point(double x, double y) {
        this("", x, y);
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "[%.1f:%.1f]", x, y);
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
