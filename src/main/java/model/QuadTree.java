package model;

import java.util.ArrayList;
import java.util.List;

public class QuadTree {
    private List<Point> points;
    private Square square;
    private QuadTree northEast, northWest, southEast, southWest;

    public QuadTree(Square square, List<Point> points) {
        this.square = square;
        this.points = points;
    }

    public void partition() {
        Square[] quadrants = Square.split(this.square);
        double xMid = (square.xMin() + square.xMax()) / 2;
        double yMid = (square.yMin() + square.yMax()) / 2;
        List<Point> pointsNE = new ArrayList<>();
        List<Point> pointsNW = new ArrayList<>();
        List<Point> pointsSE = new ArrayList<>();
        List<Point> pointsSW = new ArrayList<>();
        for (Point point : points) {
            double pointX = point.x();
            double pointY = point.y();
            if (pointX > xMid && pointY > yMid) {
                pointsNE.add(point);
            } else if (pointX <= xMid && pointY > yMid) {
                pointsNW.add(point);
            } else if (pointX <= xMid && pointY <= yMid) {
                pointsSW.add(point);
            } else {
                pointsSE.add(point);
            }
        }
        this.northEast = new QuadTree(quadrants[0], pointsNE);
        this.northWest = new QuadTree(quadrants[1], pointsNW);
        this.southWest = new QuadTree(quadrants[2], pointsSW);
        this.southEast = new QuadTree(quadrants[3], pointsSE);
    }

    public void buildQuadTree(QuadTree quadTree) {
            System.out.println(quadTree.points);
            if (quadTree.points.size() >= 2) {
            quadTree.partition();
            buildQuadTree(quadTree.northEast);
            buildQuadTree(quadTree.northWest);
            buildQuadTree(quadTree.southWest);
            buildQuadTree(quadTree.southEast);
        }
    }

}
