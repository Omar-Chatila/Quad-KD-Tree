package model;

import java.util.Objects;

public record Point(String id, double x, double y) {

    public Point(double x, double y) {
        this("", x, y);
    }

    @Override
    public String toString() {
        return id + "  (" + (int) x + ", " + (int) y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Double.compare(x, point.x) == 0 && Double.compare(y, point.y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
