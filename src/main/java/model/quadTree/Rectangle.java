package model.quadTree;

public record Rectangle(double xMin, double xMax, double yMin, double yMax) {

    public static Rectangle[] split(Rectangle rectangle) {
        double xMid = (rectangle.xMin + rectangle.xMax) / 2;
        double yMid = (rectangle.yMin + rectangle.yMax) / 2;
        Rectangle nE = new Rectangle(xMid, rectangle.xMax, yMid, rectangle.yMax);
        Rectangle nW = new Rectangle(rectangle.xMin, xMid, yMid, rectangle.yMax);
        Rectangle sW = new Rectangle(rectangle.xMin, xMid, rectangle.yMin, yMid);
        Rectangle sE = new Rectangle(xMid, rectangle.xMax, rectangle.yMin, yMid);
        return new Rectangle[]{nE, nW, sW, sE};
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
