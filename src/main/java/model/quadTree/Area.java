package model.quadTree;

import model.Point;

import java.util.Locale;

@SuppressWarnings("SuspiciousNameCombination")
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

    public double getWidth() {
        return this.xMax - this.xMin;
    }

    public double getHeight() {
        return this.yMax - this.yMin;
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

    public boolean containsArea(Area other) {
        return other != null && (this.xMin <= other.xMin && this.xMax >= other.xMax && this.yMin <= other.yMin && this.yMax >= other.yMax);
    }

    public Area intersection(Area other) {
        double xMin = Math.max(this.xMin, other.xMin);
        double xMax = Math.min(this.xMax, other.xMax);
        double yMin = Math.max(this.yMin, other.yMin);
        double yMax = Math.min(this.yMax, other.yMax);

        if (xMin < xMax && yMin < yMax)
            return new Area(xMin, xMax, yMin, yMax);
        else
            return null; // No intersection
    }

    public boolean containsPoint(Point point) {
        return (point.x() >= xMin && point.y() >= yMin && point.x() <= xMax && point.y() <= yMax);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Area area = (Area) o;
        return Double.compare(xMin, area.xMin) == 0 && Double.compare(xMax, area.xMax) == 0 && Double.compare(yMin, area.yMin) == 0 && Double.compare(yMax, area.yMax) == 0;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "[%.1f:%.1f] : [%.1f:%.1f]", xMin, xMax, yMin, yMax);
    }

    public double sqDistanceFrom(HasCoordinates point) {
        double dx = Math.max(Math.max(xMin - point.x(), 0), point.x() - xMax);
        double dy = Math.max(Math.max(yMin - point.y(), 0), point.y() - yMax);
        return dx * dx + dy * dy;
    }

    public Area rotate90degrees() {
        return new Area(yMin, yMax, xMin, xMax);
    }
}
