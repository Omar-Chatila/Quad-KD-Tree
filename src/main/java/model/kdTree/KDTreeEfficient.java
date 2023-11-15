package model.kdTree;

import model.Point;
import model.Tree;
import model.quadTree.Area;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static util.ArrayListHelper.median;

public class KDTreeEfficient extends Tree {
    private final int level;
    private final List<Point> points;
    private final Area area;
    private KDTreeEfficient leftChild, rightChild;
    private double xMedian, yMedian;
    private int from, to;

    private KDTreeEfficient(List<Point> points, int level, Area area, int from, int to) {
        this.points = points;
        this.level = level;
        this.area = area;
        this.from = from;
        this.to = to;
        if (level % 2 == 0) {
            this.xMedian = getXMedian();
        } else {
            this.yMedian = getYMedian();
        }
    }

    public KDTreeEfficient(List<Point> points, Area area) {
        this.points = points;
        this.area = area;
        this.level = 0;
        this.from = 0;
        this.to = points.size() - 1;
        this.xMedian = getXMedian();
    }

    public HashSet<Point> query(Area queryRectangle) {
        HashSet<Point> result = new HashSet<>();
        if (this.isLeaf()) {
            Point leafPoint = this.points.get(from);
            if (queryRectangle.containsPoint(leafPoint)) {
                result.add(leafPoint);
            }
        } else if (queryRectangle.containsArea(this.area)) {
            result.addAll(this.reportSubTree());
        }
        if (this.leftChild != null && queryRectangle.intersects(this.leftChild.area)) {
            result.addAll(this.leftChild.query(queryRectangle));
        }
        if (this.rightChild != null && queryRectangle.intersects(this.rightChild.area)) {
            result.addAll(this.rightChild.query(queryRectangle));
        }
        return result;
    }

    private List<Point> reportSubTree() {
        return new ArrayList<>(this.points).subList(from, to + 1);
    }

    public void buildTree(int level) {
        if ((this.to - this.from) > 0) {
            // vertical split
            if (level % 2 == 0) {
                this.setVerticalChildren(level);
            } else {
                // horizontal split
                this.setHorizontalChildren(level);
            }
            this.leftChild.buildTree(level + 1);
            this.rightChild.buildTree(level + 1);
        }
    }

    public boolean isEmpty() {
        return this.points.isEmpty();
    }

    public boolean contains(Point point) {
        double pointX = point.x();
        double pointY = point.y();
        KDTreeEfficient current = this;
        while (!current.isLeaf()) {
            if (current.level % 2 == 0) {
                current = current.xMedian >= pointX ? current.leftChild : current.rightChild;
            } else {
                current = current.yMedian >= pointY ? current.leftChild : current.rightChild;
            }
        }
        return (current.points.get(0).equals(point));
    }

    public void add(Point point) {
        if (!isEmpty() && this.contains(point)) return;
        if (isEmpty()) {
            this.appendPoint(point);
        } else {
            KDTreeEfficient current = this;
            double x = point.x();
            double y = point.y();
            int level = 0;
            while (!current.isLeaf()) {
                if ((level++ % 2) == 0) {
                    if (x <= current.xMedian) {
                        current = current.leftChild;
                    } else {
                        current = current.rightChild;
                    }
                } else {
                    if (y <= current.yMedian) {
                        current = current.leftChild;
                    } else {
                        current = current.rightChild;
                    }
                }
            }
            current.appendPoint(point);
            if (level % 2 == 0) {
                current.setVerticalChildren(level);
            } else {
                current.setHorizontalChildren(level);
            }
        }
    }

    private void appendPoint(Point point) {
        points.add(point);
    }

    public int getHeight() {
        if (isLeaf()) {
            return 1;
        }
        int h1 = 0, h2 = 0;
        if (leftChild != null)
            h1 = this.leftChild.getHeight();
        if (rightChild != null)
            h2 = this.rightChild.getHeight();
        if (h1 > h2) {
            return h1 + 1;
        } else {
            return h2 + 1;
        }
    }

    public int size(KDTreeEfficient node) {
        if (node != null)
            return 1 + size(node.leftChild) + size(node.rightChild);
        return 0;
    }

    private void setVerticalChildren(int level) {
        int midIndex = (from + to) / 2;
        Area leftArea = new Area(this.area.xMin(), this.xMedian, this.area.yMin(), this.area.yMax());
        this.leftChild = new KDTreeEfficient(this.points, level + 1, leftArea, from, midIndex);
        Area rightArea = new Area(this.xMedian, this.area.xMax(), this.area.yMin(), this.area.yMax());
        this.rightChild = new KDTreeEfficient(this.points, level + 1, rightArea, midIndex + 1, to);
    }

    private void setHorizontalChildren(int level) {
        int midIndex = (from + to) / 2;
        Area lowerArea = new Area(this.area.xMin(), this.area.xMax(), this.area.yMin(), this.yMedian);
        this.leftChild = new KDTreeEfficient(this.points, level + 1, lowerArea, from, midIndex);
        Area higherArea = new Area(this.area.xMin(), this.area.xMax(), this.yMedian, this.area.yMax());
        this.rightChild = new KDTreeEfficient(this.points, level + 1, higherArea, midIndex + 1, to);
    }

    public boolean isLeaf() {
        return from == to;
    }

    private double getXMedian() {
        return median(points, true, from, to);
    }

    private double getYMedian() {
        return median(points, false, from, to);
    }
}
