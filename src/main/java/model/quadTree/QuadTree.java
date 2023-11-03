package model.quadTree;

import model.Point;

import java.util.ArrayList;
import java.util.List;

public class QuadTree { //TODO: Query range, insertion
    private final Rectangle square;
    private final List<Point> points;
    private QuadTree northEast, northWest, southEast, southWest;


    public QuadTree(Rectangle square, List<Point> points) {
        this.square = square;
        this.points = points;
    }

    public QuadTree(Rectangle square) {
        this.square = square;
        this.points = new ArrayList<>();
    }

    public QuadTree getNorthEast() {
        return northEast;
    }

    public QuadTree getNorthWest() {
        return northWest;
    }

    public QuadTree getSouthEast() {
        return southEast;
    }

    public List<Point> getPoints() {
        return points;
    }

    public QuadTree getSouthWest() {
        return southWest;
    }

    public Rectangle getSquare() {
        return square;
    }

    public void partition() {
        partition(this);
    }

    public void partition(QuadTree current) {
        Rectangle[] quadrants = Rectangle.split(current.square);
        double xMid = (current.square.xMin() + current.square.xMax()) / 2;
        double yMid = (current.square.yMin() + current.square.yMax()) / 2;
        List<Point> pointsNE = new ArrayList<>();
        List<Point> pointsNW = new ArrayList<>();
        List<Point> pointsSE = new ArrayList<>();
        List<Point> pointsSW = new ArrayList<>();
        for (Point point : current.points) {
            double pointX = point.x();
            double pointY = point.y();
            if (pointX > xMid && pointY > yMid) {
                pointsNE.add(point);
            } else if (pointX <= xMid && pointY > yMid) {
                pointsNW.add(point);
            } else if (pointX <= xMid && pointY <= yMid) {
                pointsSW.add(point);
            } else {
                pointsSE.add(point);
            }
        }
        current.northEast = new QuadTree(quadrants[0], pointsNE);
        current.northWest = new QuadTree(quadrants[1], pointsNW);
        current.southWest = new QuadTree(quadrants[2], pointsSW);
        current.southEast = new QuadTree(quadrants[3], pointsSE);
    }

    public boolean isPointLeaf() {
        return this.points.size() == 1;
    }

    public boolean isNodeLeaf() {
        return this.southEast == null && this.southWest == null && this.northEast == null && this.northWest == null;
    }

    public void buildQuadTree(QuadTree quadTree) {
        if (quadTree.points.size() > 1) {
            quadTree.partition();
            buildQuadTree(quadTree.northEast);
            buildQuadTree(quadTree.northWest);
            buildQuadTree(quadTree.southWest);
            buildQuadTree(quadTree.southEast);
        }
    }

    public boolean isEmpty() {
        return this.points.isEmpty();
    }

    public void add(Point point) {
        if (!points.contains(point)) {
            QuadTree current = this;
            double pointX = point.x();
            double pointY = point.y();
            while (!current.isNodeLeaf()) {
                double xMid = current.square.xMid();
                double yMid = current.square.yMid();
                if (pointX > xMid && pointY > yMid) {
                    current = current.northEast;
                } else if (pointX <= xMid && pointY > yMid) {
                    current = current.northWest;
                } else if (pointX <= xMid && pointY <= yMid) {
                    current = current.southWest;
                } else {
                    current = current.southEast;
                }
            }
            if (!this.isEmpty()) {
                if (current.isPointLeaf()) {
                    current.points.add(point);
                    buildQuadTree(current);
                } else {
                    current.points.add(point);
                }
            } else {
                current.points.add(point);
            }
        }
    }

    public int size(QuadTree node) {
        if (node != null) {
            return 1 + size(node.northEast) + size(node.northWest) + size(node.southEast) + size(node.southWest);
        }
        return 0;
    }

    public int getHeight() {
        if (isNodeLeaf()) {
            return 1;
        }
        int h1 = this.northEast.getHeight();
        int h2 = this.northWest.getHeight();
        int h3 = this.southWest.getHeight();
        int h4 = this.southEast.getHeight();
        if (h1 > h2 && h1 > h3 && h1 > h4) {
            return h1 + 1;
        } else if (h2 > h1 && h2 > h3 && h2 > h4) {
            return h2 + 1;
        } else if (h3 > h2 && h3 > h1 && h3 > h4) {
            return h3 + 1;
        } else {
            return h4 + 1;
        }
    }
}
