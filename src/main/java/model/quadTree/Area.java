package model.quadTree;

public record Area(double xMin, double xMax, double yMin, double yMax) {

    public static Area[] split(Area area) {
        double xMid = (area.xMin + area.xMax) / 2.0;
        double yMid = (area.yMin + area.yMax) / 2.0;
        Area nE = new Area(xMid, area.xMax, yMid, area.yMax);
        Area nW = new Area(area.xMin, xMid, yMid, area.yMax);
        Area sW = new Area(area.xMin, xMid, area.yMin, yMid);
        Area sE = new Area(xMid, area.xMax, area.yMin, yMid);
        return new Area[]{nE, nW, sW, sE};
    }

    public double xMid() {
        return (this.xMin + this.xMax) / 2.0;
    }

    public double yMid() {
        return (this.yMin + this.yMax) / 2.0;
    }

    public boolean intersects(Area other) {
        return this.xMin < other.xMax && this.xMax > other.xMin && this.yMax > other.yMin && this.yMin < other.yMax;
    }

    public boolean contains(Area other) {
        return this.equals(other) || (this.xMin <= other.xMin && this.xMax >= other.xMax && this.yMin <= other.yMin && this.yMax <= other.yMax);
    }

    @Override
    public String toString() {
        return "[" + xMin + ":" + xMax + "] \n [" + yMin + ":" + yMax + ']';
    }
}
