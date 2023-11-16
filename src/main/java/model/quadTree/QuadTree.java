package model.quadTree;

import model.Point;
import model.Tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static util.ArrayListHelper.isDistinct;

public class QuadTree extends Tree { //TODO: Query range, insertion
    private final Area square;
    private final List<Point> points;
    private QuadTree northEast, northWest, southEast, southWest;

    public QuadTree(List<Point> points, Area square) {
        this.square = square;
        this.points = points;
    }

    public QuadTree(Area square) {
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

    public Area getSquare() {
        return square;
    }

    public void partition() {
        partition(this);
    }

    public void partition(QuadTree current) {
        Area[] quadrants = Area.split(current.square);
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
        current.northEast = new QuadTree(pointsNE, quadrants[0]);
        current.northWest = new QuadTree(pointsNW, quadrants[1]);
        current.southWest = new QuadTree(pointsSW, quadrants[2]);
        current.southEast = new QuadTree(pointsSE, quadrants[3]);
    }

    public boolean isPointLeaf() {
        return this.points.size() == 1;
    }

    public boolean isNodeLeaf() {
        return this.southEast == null && this.southWest == null && this.northEast == null && this.northWest == null;
    }

    public void buildTree() {
        if (this.points.size() > 1 && isDistinct(this.points)) {
            this.partition();
            this.northEast.buildTree();
            this.northWest.buildTree();
            this.southWest.buildTree();
            this.southEast.buildTree();
        }
    }

    // Returns all points contained by the Area queryRectangle
    public HashSet<Point> query(Area queryRectangle) {
        HashSet<Point> result = new HashSet<>();
        if (this.isPointLeaf()) {
            if (queryRectangle.containsPoint(this.points.get(0))) {
                result.addAll(this.points);
            }
        } else if (queryRectangle.containsArea(this.square)) {
            result.addAll(this.reportSubTree());
        }
        if (this.northEast != null && queryRectangle.intersects(this.northEast.square)) {
            result.addAll(this.northEast.query(queryRectangle));
        }
        if (this.northWest != null && queryRectangle.intersects(this.northWest.square)) {
            result.addAll(this.northWest.query(queryRectangle));
        }
        if (this.southEast != null && queryRectangle.intersects(this.southEast.square)) {
            result.addAll(this.southEast.query(queryRectangle));
        }
        if (this.southWest != null && queryRectangle.intersects(this.southWest.square)) {
            result.addAll(this.southWest.query(queryRectangle));
        }
        return result;
    }

    private List<Point> reportSubTree() {
        return this.points;
    }

    public boolean contains(Point point) {
        double pointX = point.x();
        double pointY = point.y();
        QuadTree current = this;
        while (!current.isPointLeaf()) {
            current = locateQuadrant(pointX, pointY, current);
        }
        return (current.points.contains(point));
    }


    public boolean isEmpty() {
        return this.points.isEmpty();
    }

    public void add(Point point) {
        QuadTree current = this;
        if (!current.isEmpty()) {
            double pointX = point.x();
            double pointY = point.y();
            while (!current.isNodeLeaf()) {
                current = locateQuadrant(pointX, pointY, current);
            }
            if (current.isPointLeaf()) {
                current.points.add(point);
                current.buildTree();
            } else {
                current.points.add(point);
            }
        } else {
            current.points.add(point);
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

    private QuadTree locateQuadrant(double pointX, double pointY, QuadTree current) {
        double centerX = current.square.xMid();
        double centerY = current.square.yMid();
        if (pointX > centerX && pointY > centerY) {
            current = current.northEast;
        } else if (pointX <= centerX && pointY > centerY) {
            current = current.northWest;
        } else if (pointX <= centerX && pointY <= centerY) {
            current = current.southWest;
        } else {
            current = current.southEast;
        }
        return current;
    }
}
