package model.kdTree;

public record SplitLine(double fromX, double fromY, double toX, double toY) {

    @Override
    public String toString() {
        return "x: " + fromX + " - " + toX + " | y: " + fromY + " - " + toY;
    }
}
