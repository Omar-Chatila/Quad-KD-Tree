package model;

import java.util.ArrayList;
import java.util.List;

public class QuadTree { //TODO: Query range, insertion
    private final List<Point> points;
    private final Square square;
    private QuadTree northEast, northWest, southEast, southWest;


    public QuadTree(Square square, List<Point> points) {
        this.square = square;
        this.points = points;
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

    public Square getSquare() {
        return square;
    }

    public void partition() {
        Square[] quadrants = Square.split(this.square);
        double xMid = (square.xMin() + square.xMax()) / 2;
        double yMid = (square.yMin() + square.yMax()) / 2;
        List<Point> pointsNE = new ArrayList<>();
        List<Point> pointsNW = new ArrayList<>();
        List<Point> pointsSE = new ArrayList<>();
        List<Point> pointsSW = new ArrayList<>();
        for (Point point : points) {
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
        this.northEast = new QuadTree(quadrants[0], pointsNE);
        this.northWest = new QuadTree(quadrants[1], pointsNW);
        this.southWest = new QuadTree(quadrants[2], pointsSW);
        this.southEast = new QuadTree(quadrants[3], pointsSE);
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
