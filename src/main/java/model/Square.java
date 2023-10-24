package model;

public record Square(double xMin, double xMax, double yMin, double yMax) {

    public static Square[] split(Square square) {
        double xMid = (square.xMin + square.xMax) / 2;
        double yMid = (square.yMin + square.yMax) / 2;
        Square nE = new Square(xMid, square.xMax, yMid, square.yMax);
        Square nW = new Square(square.xMin, xMid, yMid, square.yMax);
        Square sW = new Square(square.xMin, xMid, square.yMin, yMid);
        Square sE = new Square(xMid, square.xMax, square.yMin, yMid);
        return new Square[]{nE, nW, sW, sE};
    }

    public double xMid() {
        return (this.xMin + this.xMax) / 2;
    }

    public double yMid() {
        return (this.yMin + this.yMax) / 2;
    }

    @Override
    public String toString() {
        return "[" + xMin + ":" + xMax + "] \n [" + yMin + ":" + yMax + ']';
    }
}
