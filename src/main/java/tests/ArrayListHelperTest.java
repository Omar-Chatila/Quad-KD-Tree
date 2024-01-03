package tests;

import model.Point;
import org.junit.Test;
import util.ArrayListHelper;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static util.ArrayListHelper.getMedian;
import static util.ArrayListHelper.median;

public class ArrayListHelperTest {
    @Test
    public void splitArrayListTest() {
        // Check even list size
        ArrayList<Integer> list = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8));
        assertEquals(ArrayListHelper.splitArrayList(list).get(0), List.of(1, 2, 3, 4));
        assertEquals(ArrayListHelper.splitArrayList(list).get(1), List.of(5, 6, 7, 8));
        // Check short list
        ArrayList<Integer> list2 = new ArrayList<>(List.of(1, 2));
        assertEquals(ArrayListHelper.splitArrayList(list2).get(0), List.of(1));
        assertEquals(ArrayListHelper.splitArrayList(list2).get(1), List.of(2));

        //Check uneven list size
        ArrayList<Integer> list3 = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        assertEquals(ArrayListHelper.splitArrayList(list3).get(0), List.of(1, 2, 3, 4, 5));
        assertEquals(ArrayListHelper.splitArrayList(list3).get(1), List.of(6, 7, 8, 9));

        // Check short list
        ArrayList<Integer> list4 = new ArrayList<>(List.of(1, 2, 3));
        assertEquals(ArrayListHelper.splitArrayList(list4).get(0), List.of(1, 2));
        assertEquals(ArrayListHelper.splitArrayList(list4).get(1), List.of(3));

        ArrayList<Integer> emptyList = new ArrayList<>();
        assertEquals(new ArrayList<Integer>(), ArrayListHelper.splitArrayList(emptyList).get(0));
        assertEquals(new ArrayList<Integer>(), ArrayListHelper.splitArrayList(emptyList).get(1));

        // Check one-element list
        ArrayList<Integer> oneElementList = new ArrayList<>(List.of(1));
        assertEquals(ArrayListHelper.splitArrayList(oneElementList).get(0), List.of(1));
        assertEquals(ArrayListHelper.splitArrayList(oneElementList).get(1), List.of());

        // large list size
        ArrayList<Integer> largeList = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16));
        assertEquals(ArrayListHelper.splitArrayList(largeList).get(0), List.of(1, 2, 3, 4, 5, 6, 7, 8));
        assertEquals(ArrayListHelper.splitArrayList(largeList).get(1), List.of(9, 10, 11, 12, 13, 14, 15, 16));
    }

    @Test
    public void testGetMedianXWithEvenSizeList() {
        List<Point> points = List.of(
                new Point(7, 2),
                new Point(3, 4),
                new Point(5, 6),
                new Point(1, 8)
        );
        double median = getMedian(points, true);
        assertEquals(4.0, median);
    }

    @Test
    public void testGetMedianYWithEvenSizeList() {
        List<Point> points = List.of(
                new Point(5, 2),
                new Point(3, 4),
                new Point(7, 6),
                new Point(1, 8)
        );
        double median = getMedian(points, false);
        assertEquals(5.0, median);
    }

    @Test
    public void testGetMedianXWithOddSizeList() {
        List<Point> points = List.of(
                new Point(5, 2),
                new Point(3, 4),
                new Point(1, 6)
        );
        double median = getMedian(points, true);
        assertEquals(3.0, median);
    }

    @Test
    public void testGetMedianYWithOddSizeList() {
        List<Point> points = List.of(
                new Point(1, 8),
                new Point(3, 4),
                new Point(5, 1)
        );
        double median = getMedian(points, false);
        assertEquals(4.0, median);
    }

    @Test
    public void testGetMedianOneElementList() {
        List<Point> points = List.of(new Point(4, 5));
        double median = getMedian(points, true);
        assertEquals(4.0, median);
        double medianY = getMedian(points, false);
        assertEquals(5.0, medianY);
    }

    @Test
    public void testMedianXWithEvenSizeSubarray() {
        Point[] points = {
                new Point(9, 2),
                new Point(11, 4),
                new Point(3, 6),
                new Point(7, 8),
                new Point(1, 10),
                new Point(5, 12),
        };
        double result = median(points, true, 0, 5);
        assertEquals(5.0, result);
    }

    @Test
    public void testMedianYWithOddSizeSubarray() {
        final Point[] points = {
                new Point(7, 8),
                new Point(9, 10),
                new Point(1, 2),
                new Point(5, 6),
                new Point(3, 4)
        };
        double result = median(points, true, 0, 4);
        assertEquals(5.0, result);
    }

    @Test
    public void testMedianXWithOddSizeSubarray() {
        Point[] points = {
                new Point(1, 2),
                new Point(3, 4),
                new Point(5, 6),
                new Point(7, 8),
                new Point(9, 10)
        };
        double result = median(points, true, 1, 4);
        assertEquals(5.0, result);
    }

    @Test
    public void testMedianYWithEvenSizeSubarray() {
        Point[] points = {
                new Point(1, 2),
                new Point(5, 6),
                new Point(9, 10),
                new Point(3, 4),
                new Point(7, 8)
        };
        double result = median(points, false, 1, 4);
        assertEquals(6.0, result);
    }

    @Test
    public void testMedianXVariousIntervals() {
        Point[] points = {
                new Point(3, 4),
                new Point(7, 8),
                new Point(9, 10),
                new Point(25, 61),
                new Point(1, 2),
                new Point(5, 6),
                new Point(13, 14),
                new Point(20, 61),
                new Point(17, 18),
                new Point(33, 14),
                new Point(237, 18),
                new Point(29, 10),
                new Point(11, 21)
        };
        double result = median(points, true, 2, 6);
        assertEquals(9.0, result);
        double result2 = median(points, true, 7, 11);
        assertEquals(29.0, result2);
    }

    @Test
    public void testShortInvervals() {
        Point[] points = {
                new Point(3, 4),
                new Point(7, 8),
        };
        double result = median(points, true, 0, 1);
        assertEquals(3.0, result);

        double result2 = median(points, false, 0, 1);
        assertEquals(4.0, result2);
    }


}
