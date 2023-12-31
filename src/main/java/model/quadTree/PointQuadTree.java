package model.quadTree;

import model.Point;

import java.util.ArrayList;
import java.util.List;

public class PointQuadTree extends QuadTree<Point> {

    private int capacity;
    private boolean clearList;

    public PointQuadTree(List<Point> points, Area square) {
        super(square, points);
        this.capacity = 1;
        this.clearList = true;
    }

    public PointQuadTree(Area square) {
        super(square);
        this.capacity = 1;
        this.clearList = true;
    }

    public PointQuadTree(List<Point> points, Area square, int capacity) {
        this(points, square);
        this.capacity = capacity;
        this.clearList = true;
    }

    public PointQuadTree(Area square, int capacity) {
        super(square);
        this.capacity = capacity;
        this.clearList = true;
    }

    public PointQuadTree(List<Point> points, Area square, int capacity, boolean clearList) {
        this(points, square);
        this.capacity = capacity;
        this.clearList = clearList;
    }

    public PointQuadTree(Area square, int capacity, boolean clearList) {
        super(square);
        this.capacity = capacity;
        this.clearList = clearList;
    }


    @Override
    protected QuadTree<Point> createSubtree(List<Point> elements, Area quadrant) {
        return new PointQuadTree(elements, quadrant, capacity);
    }

    @Override
    protected List<RegionQuadTree> getCropped(Area queryRectangle) {
        return null;
    }

    public boolean isPointLeaf() {
        return this.elements.size() <= capacity && !this.elements.isEmpty();
    }

    public void buildTree() {
        if (this.elements.size() > capacity) {
            super.partition(this.clearList);
            this.northEast.buildTree();
            this.northWest.buildTree();
            this.southWest.buildTree();
            this.southEast.buildTree();
        }
    }


    // Returns all points contained by the Area queryRectangle
    public List<Point> query(Area queryRectangle) {
        if (!this.square.intersects(queryRectangle)) {
            return new ArrayList<>();
        }
        List<Point> result = new ArrayList<>();
        if (this.isPointLeaf()) {
            for (Point p : this.elements) {
                if (queryRectangle.containsPoint(p)) {
                    result.add(p);
                }
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
        return this.elements;
    }

    public boolean contains(Point point) {
        double pointX = point.x();
        double pointY = point.y();
        PointQuadTree current = this;
        while (!current.isPointLeaf()) {
            current = locateQuadrant(pointX, pointY, current);
        }
        return (current.elements.contains(point));
    }


    public boolean isEmpty() {
        return this.elements.isEmpty();
    }

    public void add(Point point) {
        PointQuadTree current = this;
        if (!current.isEmpty()) {
            double pointX = point.x();
            double pointY = point.y();
            while (!current.isNodeLeaf()) {
                current = locateQuadrant(pointX, pointY, current);
            }
            if (current.isPointLeaf()) {
                current.elements.add(point);
                current.buildTree();
            } else {
                current.elements.add(point);
            }
        } else {
            current.elements.add(point);
        }
    }

    private PointQuadTree locateQuadrant(double pointX, double pointY, PointQuadTree current) {
        double centerX = current.square.xMid();
        double centerY = current.square.yMid();
        if (pointX > centerX && pointY > centerY) {
            current = (PointQuadTree) current.northEast;
        } else if (pointX <= centerX && pointY > centerY) {
            current = (PointQuadTree) current.northWest;
        } else if (pointX <= centerX && pointY <= centerY) {
            current = (PointQuadTree) current.southWest;
        } else {
            current = (PointQuadTree) current.southEast;
        }
        return current;
    }
}
