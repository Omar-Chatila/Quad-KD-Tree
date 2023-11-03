package model.kdTree;

import model.Point;
import model.quadTree.Rectangle;

import java.util.ArrayList;
import java.util.List;

import static util.ArrayListHelper.getMedian;
import static util.ArrayListHelper.splitArrayList;

public class KDTree {
    private final List<KDTree> nodeList = new ArrayList<>();
    private final Rectangle rectangle;
    private final int level;
    private final List<Point> points;
    private final List<Point> pointsX;
    private final List<Point> pointsY;
    private SplitLine verticalSplitLine;
    private SplitLine horizontalSplitLine;
    private KDTree leftChild, rightChild;

    public KDTree(List<Point> points, Rectangle rectangle, int level) {
        this.points = points;
        this.rectangle = rectangle;
        this.pointsX = new ArrayList<>(points);
        this.pointsY = new ArrayList<>(points);
        pointsX.sort((p1, p2) -> (int) (100 * (p1.x() - p2.x())));
        pointsY.sort((p1, p2) -> (int) (100 * (p1.y() - p2.y())));
        this.level = level;
        if (level % 2 == 0)
            this.verticalSplitLine = new SplitLine(getXMedian(), rectangle.yMin(), getXMedian(), rectangle.yMax());
        else
            this.horizontalSplitLine = new SplitLine(rectangle.xMin(), getYMedian(), rectangle.xMax(), getYMedian());
    }

    private KDTree(Rectangle rectangle, int level) {
        this.rectangle = rectangle;
        this.level = level;
        this.points = new ArrayList<>();
        this.pointsX = new ArrayList<>();
        this.pointsY = new ArrayList<>();
    }

    public KDTree(Rectangle rectangle) {
        this(rectangle, 0);
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

    public SplitLine getVerticalSplitLine() {
        return verticalSplitLine;
    }

    public SplitLine getHorizontalSplitLine() {
        return horizontalSplitLine;
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
            verticalSplitLine = new SplitLine(getXMedian(), rectangle.yMin(), getXMedian(), rectangle.yMax());
        else
            horizontalSplitLine = new SplitLine(rectangle.xMin(), getYMedian(), rectangle.xMax(), getYMedian());
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
        Rectangle leftRectangle = new Rectangle(kdTree.rectangle.xMin(), kdTree.getXMedian(), kdTree.rectangle.yMin(), kdTree.rectangle.yMax());
        kdTree.leftChild = new KDTree(splitArrayList(kdTree.pointsX).get(0), leftRectangle, level + 1);
        Rectangle rightRectangle = new Rectangle(kdTree.getXMedian(), kdTree.rectangle.xMax(), kdTree.rectangle.yMin(), kdTree.rectangle.yMax());
        kdTree.rightChild = new KDTree(splitArrayList(kdTree.pointsX).get(1), rightRectangle, level + 1);
    }

    private void setHorizontalChildren(KDTree kdTree, int level) {
        Rectangle lowerRectangle = new Rectangle(kdTree.rectangle.xMin(), kdTree.rectangle.xMax(), kdTree.rectangle.yMin(), kdTree.getYMedian());
        kdTree.leftChild = new KDTree(splitArrayList(kdTree.pointsY).get(0), lowerRectangle, level + 1);
        Rectangle higherRectangle = new Rectangle(kdTree.rectangle.xMin(), kdTree.rectangle.xMax(), kdTree.getYMedian(), kdTree.rectangle.yMax());
        kdTree.rightChild = new KDTree(splitArrayList(kdTree.pointsY).get(1), higherRectangle, level + 1);
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
