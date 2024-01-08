package tests;

import model.Point;
import model.quadTree.Area;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.*;

public class AreaTests {

    @Test
    void splitTest() {
        Area area = new Area(0, 10, 0, 10);
        Area[] subAreas = Area.split(area);
        assertEquals(4, subAreas.length);
        Area ne = new Area(5, 10, 5, 10);
        Area nw = new Area(0, 5, 5, 10);
        Area sw = new Area(0, 5, 0, 5);
        Area se = new Area(5, 10, 0, 5);

        assertEquals(ne, subAreas[0]);
        assertEquals(nw, subAreas[1]);
        assertEquals(sw, subAreas[2]);
        assertEquals(se, subAreas[3]);
    }

    @Test
    void rotate90degreesTest() {
        Area area = new Area(0, 10, 0, 5);
        Area rotatedArea = area.rotate90degrees();
        assertEquals(5.0, rotatedArea.getWidth());
        assertEquals(10.0, rotatedArea.getHeight());
    }

    @Test
    void intersectsTest() {
        Area area1 = new Area(0, 10, 0, 10);
        Area area2 = new Area(5, 15, 5, 15);
        assertTrue(area1.intersects(area2));

        Area area3 = new Area(20, 30, 20, 30);
        assertFalse(area1.intersects(area3));
    }

    @Test
    void containsAreaTest() {
        Area area1 = new Area(0, 10, 0, 10);
        Area area2 = new Area(2, 8, 2, 8);
        assertTrue(area1.containsArea(area2));

        Area area3 = new Area(12, 15, 12, 15);
        Area area4 = new Area(0, 10, 0, 25);
        assertFalse(area1.containsArea(area3));
        assertFalse(area1.containsArea(area4));
    }

    @Test
    void intersectionTest() {
        Area area1 = new Area(0, 10, 0, 10);
        Area area2 = new Area(5, 15, 5, 15);
        Area intersection = area1.intersection(area2);
        assertNotNull(intersection);
        assertEquals(5.0, intersection.getWidth());
        assertEquals(5.0, intersection.getHeight());

        Area area3 = new Area(20, 30, 20, 30);
        assertNull(area1.intersection(area3));
    }

    @Test
    void containsPointTest() {
        Area area = new Area(0, 10, 0, 10);
        Point insidePoint = new Point(5, 5);
        Point outsidePoint = new Point(15, 15);

        assertTrue(area.containsPoint(insidePoint));
        assertFalse(area.containsPoint(outsidePoint));
    }

    @Test
    void sqDistanceFromTest() {
        Area area = new Area(0, 10, 0, 10);
        Point pointInside = new Point(5, 5);
        Point pointOutside = new Point(15, 15);

        assertEquals(0.0, area.sqDistanceFrom(pointInside));
        assertEquals(50.0, area.sqDistanceFrom(pointOutside));
    }
}