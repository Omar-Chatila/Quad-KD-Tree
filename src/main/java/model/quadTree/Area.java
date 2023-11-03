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

    @Override
    public String toString() {
        return "[" + xMin + ":" + xMax + "] \n [" + yMin + ":" + yMax + ']';
    }
}
