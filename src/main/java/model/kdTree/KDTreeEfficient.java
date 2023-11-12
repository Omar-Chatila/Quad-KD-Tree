package model.kdTree;

import model.Point;
import model.quadTree.Area;

import java.util.ArrayList;
import java.util.List;

import static util.ArrayListHelper.median;
import static util.ArrayListHelper.splitArrayList;

public class KDTreeEfficient {
    private final int level;
    private final List<Point> points;
    private KDTreeEfficient leftChild, rightChild;
    private double xMedian, yMedian;

    public KDTreeEfficient(List<Point> points, int level) {
        this.points = points;
        this.level = level;
        if (level % 2 == 0) {
            this.xMedian = getXMedian();
        } else {
            this.yMedian = getYMedian();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int pointsCount = (int) 1E7;
        List<Point> testList = new ArrayList<>();

        Area testArea = new Area(0, 4000, 0, 4000);
        for (int i = 0; i < pointsCount; i++) {
            testList.add(new Point(Math.random() * 4000, Math.random() * 4000));
        }

        long start = System.nanoTime();
        KDTreeEfficient kdTreeEfficient = new KDTreeEfficient(testList, 0);
        kdTreeEfficient.buildTree(0);
        long time = (System.nanoTime() - start) / 1000000;
        int height = kdTreeEfficient.getHeight();
        int number = kdTreeEfficient.size(kdTreeEfficient);
        System.out.println(time + "ms\n" + "Height: " + height + "\nNodes: " + number);
        
        System.out.println("VS\n");
        Thread.sleep(1000);
        System.gc();

        long start1 = System.nanoTime();
        KDTree kdTree = new KDTree(testList, testArea, 0);
        kdTree.buildTree(kdTree, 0);
        long time2 = (System.nanoTime() - start1) / 1000000;
        int height2 = kdTree.getHeight();
        int number2 = kdTree.size(kdTree);
        System.out.println(time2 + "ms\n" + "Height: " + height2 + "\nNodes: " + number2);
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
        this.leftChild = new KDTreeEfficient(splitArrayList(this.points).get(0), level + 1);
        this.rightChild = new KDTreeEfficient(splitArrayList(this.points).get(1), level + 1);
    }

    private void setHorizontalChildren(int level) {
        this.leftChild = new KDTreeEfficient(splitArrayList(this.points).get(0), level + 1);
        this.rightChild = new KDTreeEfficient(splitArrayList(this.points).get(1), level + 1);
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
