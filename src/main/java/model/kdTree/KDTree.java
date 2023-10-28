package model.kdTree;

import model.Point;
import model.quadTree.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class KDTree {
    private final List<Point> points;
    private final List<Point> pointsX;
    private final List<Point> pointsY;
    private final List<KDTree> nodeList = new ArrayList<>();
    private final Rectangle rectangle;
    private final int level;
    private SplitLine verticalSplitLine;
    private SplitLine horizontalSplitLine;
    private KDTree leftChild, rightChild;

    public KDTree(List<Point> points, Rectangle rectangle, int level) {
        this.points = points;
        this.rectangle = rectangle;
        this.pointsX = new ArrayList<>(points);
        this.pointsY = new ArrayList<>(points);
        pointsX.sort((o1, o2) -> (int) (100 * (o1.x() - o2.x())));
        pointsY.sort((o1, o2) -> (int) (100 * (o1.y() - o2.y())));
        this.level = level;
        if (level % 2 == 0)
            this.verticalSplitLine = new SplitLine(getXMedian(), rectangle.yMin(), getXMedian(), rectangle.yMax());
        else
            this.horizontalSplitLine = new SplitLine(rectangle.xMin(), getYMedian(), rectangle.xMax(), getYMedian());
    }

    public static <Point> List<ArrayList<Point>> splitArrayList(List<Point> originalList) {
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
        KDTree kdTree = new KDTree(points, new Rectangle(0, 10, 0, 10), 0);
        System.out.println("verticalsplit: " + kdTree.verticalSplitLine);
        System.out.println(kdTree.pointsX);
        System.out.println(kdTree.pointsY);
        kdTree.buildTree(kdTree, 0);
        System.out.println("level 1");
        System.out.println(kdTree.leftChild.points);
        System.out.println("horitontalsplit: " + kdTree.leftChild.horizontalSplitLine);
        System.out.println(kdTree.rightChild.points);
        System.out.println("horitontalsplit: " + kdTree.rightChild.horizontalSplitLine);
        System.out.println("level 2");
        System.out.println("verticalsplit: " + kdTree.leftChild.leftChild.verticalSplitLine);
        System.out.println(kdTree.leftChild.leftChild.points);
        System.out.println(kdTree.leftChild.rightChild.points);
        System.out.println("Height: " + kdTree.getHeight());
        kdTree.asList(kdTree);
        System.out.println(kdTree.nodeList);
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
                Rectangle leftRectangle = new Rectangle(kdTree.rectangle.xMin(), kdTree.getXMedian(), kdTree.rectangle.yMin(), kdTree.rectangle.yMax());
                kdTree.leftChild = new KDTree(splitArrayList(kdTree.pointsX).get(0), leftRectangle, level + 1);
                Rectangle rightRectangle = new Rectangle(kdTree.getXMedian(), kdTree.rectangle.xMax(), kdTree.rectangle.yMin(), kdTree.rectangle.yMax());
                kdTree.rightChild = new KDTree(splitArrayList(kdTree.pointsX).get(1), rightRectangle, level + 1);
            } else {
                // horizontal split
                Rectangle lowerRectangle = new Rectangle(kdTree.rectangle.xMin(), kdTree.rectangle.xMax(), kdTree.rectangle.yMin(), kdTree.getYMedian());
                kdTree.leftChild = new KDTree(splitArrayList(kdTree.pointsY).get(0), lowerRectangle, level + 1);
                Rectangle higherRectangle = new Rectangle(kdTree.rectangle.xMin(), kdTree.rectangle.xMax(), kdTree.getYMedian(), kdTree.rectangle.yMax());
                kdTree.rightChild = new KDTree(splitArrayList(kdTree.pointsY).get(1), higherRectangle, level + 1);
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
}
