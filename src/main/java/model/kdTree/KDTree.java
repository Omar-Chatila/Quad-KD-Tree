package model.kdTree;

import model.Point;
import model.quadTree.Area;

import java.util.ArrayList;
import java.util.List;

import static util.ArrayListHelper.getMedian;
import static util.ArrayListHelper.splitArrayList;

public class KDTree {
    private final List<KDTree> nodeList = new ArrayList<>();
    private final Area area;
    private final int level;
    private final List<Point> points;
    private final List<Point> pointsX;
    private final List<Point> pointsY;
    private SplitLine verticalSplitLine;
    private SplitLine horizontalSplitLine;
    private KDTree leftChild, rightChild;

    public KDTree(List<Point> points, Area area, int level) {
        this.points = points;
        this.area = area;
        this.pointsX = new ArrayList<>(points);
        this.pointsY = new ArrayList<>(points);
        pointsX.sort((p1, p2) -> (int) (100 * (p1.x() - p2.x())));
        pointsY.sort((p1, p2) -> (int) (100 * (p1.y() - p2.y())));
        this.level = level;
        if (level % 2 == 0)
            this.verticalSplitLine = new SplitLine(getXMedian(), area.yMin(), getXMedian(), area.yMax());
        else
            this.horizontalSplitLine = new SplitLine(area.xMin(), getYMedian(), area.xMax(), getYMedian());
    }

    private KDTree(Area area, int level) {
        this.area = area;
        this.level = level;
        this.points = new ArrayList<>();
        this.pointsX = new ArrayList<>();
        this.pointsY = new ArrayList<>();
    }

    public KDTree(Area area) {
        this(area, 0);
    }

    public List<Point> getPoints() {
        return points;
    }

    public KDTree getLeftChild() {
        return leftChild;
    }

    public KDTree getRightChild() {
        return rightChild;
    }

    public int getLevel() {
        return level;
    }

    public void buildTree(KDTree kdTree, int level) {
        if (kdTree.points.size() > 1) {
            // vertical split
            if (level % 2 == 0) {
                setVerticalChildren(kdTree, level);
            } else {
                // horizontal split
                setHorizontalChildren(kdTree, level);
            }
            buildTree(kdTree.leftChild, level + 1);
            buildTree(kdTree.rightChild, level + 1);
        }
    }

    public SplitLine getSplitLine() {
        return this.horizontalSplitLine != null ? horizontalSplitLine : verticalSplitLine;
    }

    public boolean isEmpty() {
        return this.points.isEmpty();
    }

    public void add(Point point) {
        if (points.contains(point)) return;
        if (isEmpty()) {
            this.appendPoint(point);
        } else {
            KDTree current = this;
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
            current.appendPoint(point);
            if (level % 2 == 0) {
                current.setVerticalChildren(current, level);
            } else {
                current.setHorizontalChildren(current, level);
            }
        }
    }

    private void appendPoint(Point point) {
        points.add(point);
        pointsX.add(point);
        pointsY.add(point);
        pointsX.sort((p1, p2) -> (int) (100 * (p1.x() - p2.x())));
        pointsY.sort((p1, p2) -> (int) (100 * (p1.y() - p2.y())));
        setSplitLines();
    }

    private void setSplitLines() {
        if (level % 2 == 0)
            verticalSplitLine = new SplitLine(getXMedian(), area.yMin(), getXMedian(), area.yMax());
        else
            horizontalSplitLine = new SplitLine(area.xMin(), getYMedian(), area.xMax(), getYMedian());
    }


    public void asList(KDTree kdTree) {
        if (kdTree != null) {
            asList(kdTree.leftChild);
            this.nodeList.add(kdTree);
            asList(kdTree.rightChild);
        }
    }

    public List<KDTree> getNodeList() {
        asList(this);
        return nodeList;
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

    public int size(KDTree node) {
        if (node != null)
            return 1 + size(node.leftChild) + size(node.rightChild);
        return 0;
    }

    private void setVerticalChildren(KDTree kdTree, int level) {
        Area leftArea = new Area(kdTree.area.xMin(), kdTree.getXMedian(), kdTree.area.yMin(), kdTree.area.yMax());
        kdTree.leftChild = new KDTree(splitArrayList(kdTree.pointsX).get(0), leftArea, level + 1);
        Area rightArea = new Area(kdTree.getXMedian(), kdTree.area.xMax(), kdTree.area.yMin(), kdTree.area.yMax());
        kdTree.rightChild = new KDTree(splitArrayList(kdTree.pointsX).get(1), rightArea, level + 1);
    }

    private void setHorizontalChildren(KDTree kdTree, int level) {
        Area lowerArea = new Area(kdTree.area.xMin(), kdTree.area.xMax(), kdTree.area.yMin(), kdTree.getYMedian());
        kdTree.leftChild = new KDTree(splitArrayList(kdTree.pointsY).get(0), lowerArea, level + 1);
        Area higherArea = new Area(kdTree.area.xMin(), kdTree.area.xMax(), kdTree.getYMedian(), kdTree.area.yMax());
        kdTree.rightChild = new KDTree(splitArrayList(kdTree.pointsY).get(1), higherArea, level + 1);
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
