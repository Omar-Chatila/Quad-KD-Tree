package model.kdTree;

import model.Point;

import java.util.ArrayList;
import java.util.List;

public class KDTree {
    private final ArrayList<Point> points;
    private final ArrayList<Point> pointsX;
    private final ArrayList<Point> pointsY;
    private double verticalSplitLine;
    private double horizontalSplitLine;
    private KDTree leftChild, rightChild;

    public KDTree(ArrayList<Point> points) {
        this.points = points;
        this.pointsX = new ArrayList<>(points);
        this.pointsY = new ArrayList<>(points);
        pointsX.sort((o1, o2) -> (int) (100 * (o1.x() - o2.x())));
        pointsY.sort((o1, o2) -> (int) (100 * (o1.y() - o2.y())));
        this.verticalSplitLine = getXMedian();
        this.horizontalSplitLine = getYMedian();
    }

    public static <Point> List<ArrayList<Point>> splitArrayList(ArrayList<Point> originalList) {
        int size = originalList.size();
        int midpoint = (size - 1) / 2;

        ArrayList<Point> firstHalf = new ArrayList<>(originalList.subList(0, midpoint + 1));
        ArrayList<Point> secondHalf = new ArrayList<>(originalList.subList(midpoint + 1, size));

        List<ArrayList<Point>> result = new ArrayList<>();
        result.add(firstHalf);
        result.add(secondHalf);

        return result;
    }

    public static void main(String[] args) {
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(0.5, 1.5));
        points.add(new Point(1.5, 3.5));
        points.add(new Point(2.5, 2));
        points.add(new Point(3, 4.5));
        points.add(new Point(4.5, 0.5));
        points.add(new Point(4.5, 3.5));
        points.add(new Point(5.5, 5.5));
        KDTree kdTree = new KDTree(points);
        System.out.println("horitontalsplit: " + kdTree.horizontalSplitLine);
        System.out.println("verticalsplit: " + kdTree.verticalSplitLine);
        System.out.println(kdTree.pointsX);
        System.out.println(kdTree.pointsY);
        kdTree.buildTree(kdTree, 0);
        System.out.println("level 1");
        System.out.println(kdTree.leftChild.points);
        System.out.println(kdTree.rightChild.points);
        System.out.println("level 2");
        System.out.println(kdTree.leftChild.leftChild.points);
        System.out.println(kdTree.leftChild.rightChild.points);
        System.out.println("Height: " + kdTree.getHeight());
    }

    public void buildTree(KDTree kdTree, int level) {
        if (kdTree.points.size() > 1) {
            if (level % 2 == 0) {
                kdTree.leftChild = new KDTree(splitArrayList(kdTree.pointsX).get(0));
                kdTree.rightChild = new KDTree(splitArrayList(kdTree.pointsX).get(1));
            } else {
                kdTree.leftChild = new KDTree(splitArrayList(kdTree.pointsY).get(0));
                kdTree.rightChild = new KDTree(splitArrayList(kdTree.pointsY).get(1));
            }
            buildTree(kdTree.leftChild, level + 1);
            buildTree(kdTree.rightChild, level + 1);
        }
    }

    public boolean isLeaf() {
        return this.points.size() == 1;
    }

    private double getXMedian() {
        return pointsX.size() % 2 == 0 ? (pointsX.get(pointsX.size() / 2).x() + pointsX.get((pointsX.size() / 2) - 1).x()) / 2.0 : pointsX.get(pointsX.size() / 2).x();
    }

    private double getYMedian() {
        return pointsY.size() % 2 == 0 ? (pointsY.get(pointsY.size() / 2).y() + pointsY.get((pointsY.size() / 2) - 1).y()) / 2.0 : pointsY.get(pointsY.size() / 2).y();
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
}
