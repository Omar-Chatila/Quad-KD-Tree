package model.kdTree;

import model.Point;
import model.quadTree.Area;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import static util.ArrayListHelper.getMedian;
import static util.ArrayListHelper.splitArrayList;

public class KDTree {
    private final List<KDTree> nodeList = new ArrayList<>();
    private final Area area;
    private final int level;
    private final List<Point> points;
    private List<Point> pointsX;
    private List<Point> pointsY;
    private SplitLine verticalSplitLine;
    private SplitLine horizontalSplitLine;
    private KDTree leftChild, rightChild;

    public KDTree(List<Point> points, Area area, int level) {
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

    public static void main(String[] args) {
        Point[] points1 = {new Point(1, 1), new Point(1.8, 4.8), new Point(2.5, 3.5), new Point(4, 6)
                , new Point(4.8, 1.8), new Point(6.8, 1.5), new Point(9, 2), new Point(8, 6), new Point(4, 4)};
        List<Point> pointss = new ArrayList<>(List.of(points1));
        KDTree kdTree = new KDTree(pointss, new Area(0, 10, 0, 10), 0);
        kdTree.buildTree(kdTree, 0);
        System.out.println(kdTree.reportSubTree());
        System.out.println(kdTree.query(new Area(2, 20, 2, 20)));
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

    public boolean contains(Point point) {
        double pointX = point.x();
        double pointY = point.y();
        KDTree current = this;
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
            current.appendPoint(point, level);
            if (level % 2 == 0) {
                current.setVerticalChildren(current, level);
            } else {
                current.setHorizontalChildren(current, level);
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
            result.addAll(reportSubTree());
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
