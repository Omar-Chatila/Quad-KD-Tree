package model.quadTree;

import model.Tree;

import java.util.ArrayList;
import java.util.List;

public abstract class QuadTree<T extends HasCoordinates> extends Tree<T> {
    protected final Area square;
    protected List<T> elements;
    protected QuadTree<T> northEast, northWest, southEast, southWest;

    public QuadTree(Area square, List<T> elements) {
        this.square = square;
        this.elements = elements;
    }

    public QuadTree(Area square) {
        this.square = square;
        this.elements = new ArrayList<>();
    }

    public QuadTree<T> getNorthEast() {
        return northEast;
    }

    public QuadTree<T> getNorthWest() {
        return northWest;
    }

    public QuadTree<T> getSouthEast() {
        return southEast;
    }

    public QuadTree<T> getSouthWest() {
        return southWest;
    }

    public List<T> getPoints() {
        return elements;
    }

    public Area getSquare() {
        return square;
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

    public boolean isNodeLeaf() {
        return this.southEast == null && this.southWest == null && this.northEast == null && this.northWest == null;
    }

    protected abstract QuadTree<T> createSubtree(List<T> elements, Area quadrant);

    public void partition() {
        Area[] quadrants = Area.split(this.square);
        double xMid = (this.square.xMin() + this.square.xMax()) / 2;
        double yMid = (this.square.yMin() + this.square.yMax()) / 2;
        List<T> pointsNE = new ArrayList<>();
        List<T> pointsNW = new ArrayList<>();
        List<T> pointsSE = new ArrayList<>();
        List<T> pointsSW = new ArrayList<>();
        for (T point : this.elements) {
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
        this.northEast = createSubtree(pointsNE, quadrants[0]);
        this.northWest = createSubtree(pointsNW, quadrants[1]);
        this.southWest = createSubtree(pointsSW, quadrants[2]);
        this.southEast = createSubtree(pointsSE, quadrants[3]);
    }

    public int size(QuadTree<T> node) {
        if (node != null) {
            return 1 + size(node.northEast) + size(node.northWest) + size(node.southEast) + size(node.southWest);
        }
        return 0;
    }
}
