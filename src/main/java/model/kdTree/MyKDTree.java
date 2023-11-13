package model.kdTree;

import model.Point;
import model.Tree;
import model.quadTree.Area;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import static util.ArrayListHelper.getMedian;
import static util.ArrayListHelper.splitArrayList;

public class MyKDTree extends Tree {
    private final List<MyKDTree> nodeList = new ArrayList<>();
    private final Area area;
    private final int level;
    private final List<Point> points;
    private List<Point> pointsX;
    private List<Point> pointsY;
    private SplitLine verticalSplitLine;
    private SplitLine horizontalSplitLine;
    private MyKDTree leftChild, rightChild;

    public MyKDTree(List<Point> points, Area area, int level) {
        this.points = points;
        this.area = area;
        this.level = level;
        if (level % 2 == 0) {
            this.pointsX = new ArrayList<>(points);
            pointsX.sort(Comparator.comparingDouble(Point::x));
            this.verticalSplitLine = new SplitLine(getXMedian(), area.yMin(), getXMedian(), area.yMax());
        } else {
            this.pointsY = new ArrayList<>(points);
            pointsY.sort(Comparator.comparingDouble(Point::y));
            this.horizontalSplitLine = new SplitLine(area.xMin(), getYMedian(), area.xMax(), getYMedian());
        }
    }

    private MyKDTree(Area area, int level) {
        this.area = area;
        this.level = level;
        this.points = new ArrayList<>();
        this.pointsX = new ArrayList<>();
        this.pointsY = new ArrayList<>();
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

    public void buildTree(int level) {
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
            pointsX.add(point);
            pointsX.sort((p1, p2) -> (int) (100 * (p1.x() - p2.x())));
        } else {
            pointsY.add(point);
            pointsY.sort((p1, p2) -> (int) (100 * (p1.y() - p2.y())));
        }
        setSplitLines();
    }

    // Returns all points contained by the Area queryRectangle
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
        int h1 = this.leftChild.getHeight();
        int h2 = this.rightChild.getHeight();
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
        this.leftChild = new MyKDTree(splitArrayList(this.pointsX).get(0), leftArea, level + 1);
        Area rightArea = new Area(this.getXMedian(), this.area.xMax(), this.area.yMin(), this.area.yMax());
        this.rightChild = new MyKDTree(splitArrayList(this.pointsX).get(1), rightArea, level + 1);
    }

    private void setHorizontalChildren(int level) {
        Area lowerArea = new Area(this.area.xMin(), this.area.xMax(), this.area.yMin(), this.getYMedian());
        this.leftChild = new MyKDTree(splitArrayList(this.pointsY).get(0), lowerArea, level + 1);
        Area higherArea = new Area(this.area.xMin(), this.area.xMax(), this.getYMedian(), this.area.yMax());
        this.rightChild = new MyKDTree(splitArrayList(this.pointsY).get(1), higherArea, level + 1);
    }

    public boolean isLeaf() {
        return this.points.size() == 1;
    }

    private double getXMedian() {
        return getMedian(pointsX, true);
    }

    private double getYMedian() {
        return getMedian(pointsY, false);
    }

}
