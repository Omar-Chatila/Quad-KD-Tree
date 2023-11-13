package model.kdTree;

import model.Point;
import model.Tree;
import model.quadTree.Area;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static util.ArrayListHelper.median;
import static util.ArrayListHelper.splitArrayList;

public class KDTreeEfficient extends Tree {
    private final int level;
    private final List<Point> points;
    private Area area;
    private KDTreeEfficient leftChild, rightChild;
    private double xMedian, yMedian;

    public KDTreeEfficient(List<Point> points, int level, Area area) {
        this.points = points;
        this.level = level;
        this.area = area;
        if (level % 2 == 0) {
            this.xMedian = getXMedian();
        } else {
            this.yMedian = getYMedian();
        }
    }

    public HashSet<Point> query(Area queryRectangle) {
        HashSet<Point> result = new HashSet<>();
        if (this.isLeaf()) {
            if (queryRectangle.containsPoint(this.points.get(0))) {
                result.add(this.points.get(0));
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

    private ArrayList<Point> reportSubTree() {
        ArrayList<Point> result = new ArrayList<>();
        if (this.isLeaf())
            result.addAll(this.points);
        if (this.leftChild != null)
            result.addAll(this.leftChild.reportSubTree());
        if (this.rightChild != null)
            result.addAll(this.rightChild.reportSubTree());
        return result;
    }

    public void buildTree(int level) {
        if (this.points.size() > 1) {
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
        return (current.points.contains(point));
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
        int h1 = this.leftChild.getHeight();
        int h2 = this.rightChild.getHeight();
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
        Area leftArea = new Area(this.area.xMin(), this.xMedian, this.area.yMin(), this.area.yMax());
        this.leftChild = new KDTreeEfficient(splitArrayList(this.points).get(0), level + 1, leftArea);
        Area rightArea = new Area(this.xMedian, this.area.xMax(), this.area.yMin(), this.area.yMax());
        this.rightChild = new KDTreeEfficient(splitArrayList(this.points).get(1), level + 1, rightArea);
    }

    private void setHorizontalChildren(int level) {
        Area lowerArea = new Area(this.area.xMin(), this.area.xMax(), this.area.yMin(), this.yMedian);
        this.leftChild = new KDTreeEfficient(splitArrayList(this.points).get(0), level + 1, lowerArea);
        Area higherArea = new Area(this.area.xMin(), this.area.xMax(), this.yMedian, this.area.yMax());
        this.rightChild = new KDTreeEfficient(splitArrayList(this.points).get(1), level + 1, higherArea);
    }

    public boolean isLeaf() {
        return this.points.size() == 1;
    }

    private double getXMedian() {
        return median(points, true);
    }

    private double getYMedian() {
        return median(points, false);
    }
}
