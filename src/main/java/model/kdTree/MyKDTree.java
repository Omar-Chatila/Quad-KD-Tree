package model.kdTree;

import model.Point;
import model.Tree;
import model.quadTree.Area;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static util.ArrayListHelper.getMedian;
import static util.ArrayListHelper.splitArrayList;

public class MyKDTree extends Tree<Point> {
    private final Area area;
    private final int level;
    private final List<Point> points;
    private SplitLine verticalSplitLine;
    private SplitLine horizontalSplitLine;
    private MyKDTree leftChild, rightChild;

    public MyKDTree(List<Point> points, Area area, int level) {
        this.points = points;
        this.area = area;
        this.level = level;
        if (level % 2 == 0) {
            points.sort(Comparator.comparingDouble(Point::x));
            this.verticalSplitLine = new SplitLine(getXMedian(), area.yMin(), getXMedian(), area.yMax());
        } else {
            points.sort(Comparator.comparingDouble(Point::y));
            this.horizontalSplitLine = new SplitLine(area.xMin(), getYMedian(), area.xMax(), getYMedian());
        }
    }

    private MyKDTree(Area area, int level) {
        this.area = area;
        this.level = level;
        this.points = new ArrayList<>();
    }

    public MyKDTree(Area area) {
        this(area, 0);
    }

    public List<Point> getPoints() {
        return points;
    }

    public MyKDTree getLeftChild() {
        return leftChild;
    }

    public MyKDTree getRightChild() {
        return rightChild;
    }

    public int getLevel() {
        return level;
    }

    public void buildTree() {
        this.buildTree(0);
    }

    private void buildTree(int level) {
        if (this.points.size() > 1) {
            // vertical split
            if (level % 2 == 0) {
                this.setVerticalChildren(level);
            } else {
                // horizontal split
                this.setHorizontalChildren(level);
            }
            leftChild.buildTree(level + 1);
            rightChild.buildTree(level + 1);
        }
    }

    public SplitLine getSplitLine() {
        return this.horizontalSplitLine != null ? horizontalSplitLine : verticalSplitLine;
    }

    public boolean isEmpty() {
        return this.points.isEmpty();
    }

    public boolean contains(Point point) {
        double pointX = point.x();
        double pointY = point.y();
        MyKDTree current = this;
        while (!current.isLeaf()) {
            if (current.level % 2 == 0) {
                current = current.verticalSplitLine.fromX() >= pointX ? current.leftChild : current.rightChild;
            } else {
                current = current.horizontalSplitLine.fromY() >= pointY ? current.leftChild : current.rightChild;
            }
        }
        return (current.points.contains(point));
    }

    public void add(Point point) {
        if (!isEmpty() && this.contains(point)) return;
        if (isEmpty()) {
            this.appendPoint(point, 0);
        } else {
            MyKDTree current = this;
            double x = point.x();
            double y = point.y();
            int level = 0;
            while (!current.isLeaf()) {
                if ((level++ % 2) == 0) {
                    if (x <= current.verticalSplitLine.toX()) {
                        current = current.leftChild;
                    } else {
                        current = current.rightChild;
                    }
                } else {
                    if (y <= current.horizontalSplitLine.toY()) {
                        current = current.leftChild;
                    } else {
                        current = current.rightChild;
                    }
                }
            }
            current.appendPoint(point, level);
            if (level % 2 == 0) {
                current.setVerticalChildren(level);
            } else {
                current.setHorizontalChildren(level);
            }
        }
    }

    private void appendPoint(Point point, int level) {
        points.add(point);
        if (level % 2 == 0) {
            points.sort((p1, p2) -> (int) (100 * (p1.x() - p2.x())));
        } else {
            points.sort((p1, p2) -> (int) (100 * (p1.y() - p2.y())));
        }
        setSplitLines();
    }

    public Area getArea() {
        return area;
    }

    // Returns all points contained by the Area queryRectangle
    public List<Point> query(Area queryRectangle) {
        List<Point> result = new ArrayList<>();
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

    private List<Point> reportSubTree() {
        return this.points;
    }

    private void setSplitLines() {
        if (level % 2 == 0)
            verticalSplitLine = new SplitLine(getXMedian(), area.yMin(), getXMedian(), area.yMax());
        else
            horizontalSplitLine = new SplitLine(area.xMin(), getYMedian(), area.xMax(), getYMedian());
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

    public int size(MyKDTree node) {
        if (node != null)
            return 1 + size(node.leftChild) + size(node.rightChild);
        return 0;
    }

    private void setVerticalChildren(int level) {
        Area leftArea = new Area(this.area.xMin(), this.getXMedian(), this.area.yMin(), this.area.yMax());
        this.leftChild = new MyKDTree(splitArrayList(this.points).get(0), leftArea, level + 1);
        Area rightArea = new Area(this.getXMedian(), this.area.xMax(), this.area.yMin(), this.area.yMax());
        this.rightChild = new MyKDTree(splitArrayList(this.points).get(1), rightArea, level + 1);
    }

    private void setHorizontalChildren(int level) {
        Area lowerArea = new Area(this.area.xMin(), this.area.xMax(), this.area.yMin(), this.getYMedian());
        this.leftChild = new MyKDTree(splitArrayList(this.points).get(0), lowerArea, level + 1);
        Area higherArea = new Area(this.area.xMin(), this.area.xMax(), this.getYMedian(), this.area.yMax());
        this.rightChild = new MyKDTree(splitArrayList(this.points).get(1), higherArea, level + 1);
    }

    public boolean isLeaf() {
        return this.points.size() == 1;
    }

    private double getXMedian() {
        return getMedian(points, true);
    }

    private double getYMedian() {
        return getMedian(points, false);
    }

}
