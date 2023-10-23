package model;

public record Point(double x, double y) {
    @Override
    public String toString() {
        return "P(" + x + ", " + y + ")";
    }
}
