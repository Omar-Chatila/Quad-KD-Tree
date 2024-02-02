package tests;

import model.Point;
import model.quadTree.Area;
import model.quadTree.PointQuadTree;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.testng.AssertJUnit.*;

public class QuadTreeTest {
    @Test
    public static void testContains() {
        PointQuadTree quadTree = getPointQuadTree(1);
        PointQuadTree quadTree2 = getPointQuadTree(4);
        testContainsHelper(quadTree);
        testContainsHelper(quadTree2);
    }

    private static void testContainsHelper(PointQuadTree quadTree2) {
        assertTrue(quadTree2.contains(new Point(34, 2384)));
        assertTrue(quadTree2.contains(new Point(344, 834)));
        assertTrue(quadTree2.contains(new Point(344, 34)));
        assertTrue(quadTree2.contains(new Point(3234, 9234)));

        assertFalse(quadTree2.contains(new Point(234, 2342)));
        assertFalse(quadTree2.contains(new Point(-234, 2342)));
        assertFalse(quadTree2.contains(new Point(0, 0)));
        assertFalse(quadTree2.contains(new Point(234, 232)));
    }

    private static PointQuadTree getPointQuadTree(int capacity) {
        Area area = new Area(0, 10000, 0, 10000);
        List<Point> points = new ArrayList<>();
        points.add(new Point(3422, 2354));
        points.add(new Point(3424, 2434));
        points.add(new Point(344, 34));
        points.add(new Point(5464, 3934));
        points.add(new Point(34, 2384));
        points.add(new Point(9224, 644));
        points.add(new Point(344, 834));
        points.add(new Point(3234, 9234));

        PointQuadTree quadTree = new PointQuadTree(points, area, capacity);
        quadTree.buildTree();
        return quadTree;
    }

    @Test
    public static void testQuery() {
        Area area = new Area(0, 10000, 0, 10000);
        Point[] points1 = new Point[10000];
        for (int i = 0; i < points1.length; i++) {
            points1[i] = new Point(Math.random() * 10000, Math.random() * 10000);
        }
        List<Point> points = new ArrayList<>(List.of(points1));
        List<Point> points2 = new ArrayList<>(List.copyOf(points));

        PointQuadTree quadTree = new PointQuadTree(points, area);
        quadTree.buildTree();
        PointQuadTree quadTree2 = new PointQuadTree(points2, area, 4);
        quadTree2.buildTree();

        Area query3 = new Area(500, 3939, 232, 23423);
        assertEquals(new HashSet<>(quadTree.query(query3)), naiveQuery(points1, query3));
        assertEquals(new HashSet<>(quadTree2.query(query3)), naiveQuery(points1, query3));


        //random tests
        Area[] areas = new Area[10000];
        for (int i = 0; i < areas.length; i++) {
            double fromX = Math.random() * 8000;
            double toX = fromX + Math.random() * 5000;
            double fromY = Math.random() * 8000;
            double toY = fromY + Math.random() * 5000;

            areas[i] = new Area(fromX, toX, fromY, toY);
        }

        for (Area a : areas) {
            HashSet<Point> naive = naiveQuery(points1, a);
            assertEquals(new HashSet<>(quadTree.query(a)), naive);
            assertEquals(new HashSet<>(quadTree2.query(a)), naive);
        }
    }

    @Test
    static void insertTest() {
        PointQuadTree quadTree = getPointQuadTree(1);
        PointQuadTree quadTree2 = getPointQuadTree(4);
        Point p = new Point(456, 4323);
        quadTree.add(p);
        quadTree2.add(p);

        assertTrue(quadTree.contains(p));
        assertTrue(quadTree2.contains(p));

        // test point out of bounds
        Point p2 = new Point(456234, 433423);
        quadTree.add(p2);
        quadTree2.add(p2);

        assertFalse(quadTree.contains(p2));
        assertFalse(quadTree2.contains(p2));
    }

    private static HashSet<Point> naiveQuery(Point[] points, Area area) {
        HashSet<Point> result = new HashSet<>();
        for (Point p : points) {
            if (area.containsPoint(p)) {
                result.add(p);
            }
        }
        return result;
    }
}
