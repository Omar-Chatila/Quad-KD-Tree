package model.quadTree;

import javafx.scene.paint.Color;
import model.Tree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public abstract class QuadTree<T extends HasCoordinates> extends Tree<T> {
    protected Area square;
    protected List<T> elements;
    protected QuadTree<T> northEast, northWest, southEast, southWest;
    protected Color blendedColor;   // for region QT only

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

    public List<T> getElements() {
        return elements;
    }

    public Area getSquare() {
        return square;
    }

    public boolean isNodeLeaf() {
        return this.southEast == null && this.southWest == null && this.northEast == null && this.northWest == null;
    }

    public List<T> kNearestNeighbors(T queryPoint, int k) {
        PriorityQueue<QuadTree<T>> queue = new PriorityQueue<>(Comparator.comparingDouble(qt -> qt.square.sqDistanceFrom(queryPoint)));
        List<T> result = new ArrayList<>();
        kNearestNeighborsHelper(this, k, queue, result);
        return result;
    }

    private void kNearestNeighborsHelper(QuadTree<T> node, int k, PriorityQueue<QuadTree<T>> queue, List<T> result) {
        if (node == null) {
            return;
        }

        if (node.isNodeLeaf() && !node.elements.isEmpty()) {
            queue.offer(node);
        } else {
            queue.offer(node.getNorthEast());
            queue.offer(node.getNorthWest());
            queue.offer(node.getSouthEast());
            queue.offer(node.getSouthWest());
        }

        while (!queue.isEmpty() && result.size() < k) {
            QuadTree<T> current = queue.poll();
            if (current.isNodeLeaf()) {
                result.addAll(current.getElements());
            } else {
                kNearestNeighborsHelper(current, k, queue, result);
            }
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
        int maxHeight = Math.max(Math.max(h1, h2), Math.max(h3, h4));
        return maxHeight + 1;
    }

    protected abstract QuadTree<T> createSubtree(List<T> elements, Area quadrant);

    protected abstract List<RegionQuadTree> getCropped(Area queryRectangle);

    public void partition(boolean clearList) {
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
        if (clearList)
            this.getElements().clear();
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

    @Override
    public String toString() {
        return "QuadTree{" +
                "square=" + square.toString() +
                ", elements=" + elements +
                '}';
    }

    public class KNearestResult {
        List<T> found;
        double furthestDistance;

        public KNearestResult(List<T> found, double furthestDistance) {
            this.found = found;
            this.furthestDistance = furthestDistance;
        }


        @Override
        public String toString() {
            return furthestDistance + ": " + found;
        }
    }
}
