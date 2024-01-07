package model.kdTree;

import model.Point;
import model.Tree;
import model.quadTree.Area;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static util.ArrayListHelper.median;

public class KDTreeEfficient extends Tree<Point> {
    private final Point[] points;
    private final Area area;
    private final int from;
    private final int to;
    private KDTreeEfficient leftChild, rightChild;
    private double xMedian, yMedian;

    private KDTreeEfficient(Point[] points, int level, Area area, int from, int to) {
        this.points = points;
        this.area = area;
        this.from = from;
        this.to = to;
        if (level % 2 == 0) {
            this.xMedian = getXMedian();
        } else {
            this.yMedian = getYMedian();
        }
    }

    public KDTreeEfficient(Point[] points, Area area) {
        this.points = points;
        this.area = area;
        this.from = 0;
        this.to = points.length - 1;
        this.xMedian = getXMedian();
    }

    public static void main(String[] args) {
        Point[] points1 = {
                new Point(3, 42345),
                new Point(5, 22),
                new Point(3234, 4),
                new Point(5, 25),
                new Point(312, 445),
                new Point(554, 21),
                new Point(31, 4456),
                new Point(5, 2345),
                new Point(3234, 4431),
                new Point(5, 213),
                new Point(311, 44),
                new Point(5, 23),
                new Point(333, 42),
                new Point(555, 211),
                new Point(32, 443),
                new Point(325, 212),
                new Point(3, 4),
                new Point(51, 234),
                new Point(3333, 43),
                new Point(543, 21),
                new Point(31, 42),
                new Point(54, 254),
        };
        KDTreeEfficient kdTreeEfficient = new KDTreeEfficient(points1, new Area(0, 200000, 0, 200000));
        kdTreeEfficient.buildTree();
        System.out.println(kdTreeEfficient.size(kdTreeEfficient));
    }

    public List<Point> query(Area queryRectangle) {
        List<Point> result = new ArrayList<>();
        if (this.isLeaf()) {
            Point leafPoint = this.points[from];
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
        return Arrays.asList(this.points).subList(from, to + 1);
    }

    public void buildTree() {
        buildTree(0);
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
        Area rightArea = new Area(this.xMedian, this.area.xMax(), this.area.yMin(), this.area.yMax());
        this.leftChild = new KDTreeEfficient(this.points, level + 1, leftArea, from, midIndex);
        this.rightChild = new KDTreeEfficient(this.points, level + 1, rightArea, midIndex + 1, to);
    }

    private void setHorizontalChildren(int level) {
        int midIndex = (from + to) / 2;
        Area lowerArea = new Area(this.area.xMin(), this.area.xMax(), this.area.yMin(), this.yMedian);
        Area higherArea = new Area(this.area.xMin(), this.area.xMax(), this.yMedian, this.area.yMax());
        this.leftChild = new KDTreeEfficient(this.points, level + 1, lowerArea, from, midIndex);
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
