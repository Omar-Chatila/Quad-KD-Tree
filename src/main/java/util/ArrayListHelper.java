package util;

import model.Point;

import java.util.ArrayList;
import java.util.List;

public class ArrayListHelper {
    public static <Point> List<ArrayList<Point>> splitArrayList(List<Point> originalList) {
        int midIndex = originalList.size() / 2;
        ArrayList<Point> sublist1 = new ArrayList<>(originalList.subList(0, midIndex));
        ArrayList<Point> sublist2 = new ArrayList<>(originalList.subList(midIndex, originalList.size()));
        ArrayList<ArrayList<Point>> result = new ArrayList<>();
        result.add(sublist1);
        result.add(sublist2);
        return result;
    }

    public static double getMedian(List<Point> list, boolean x) {
        if (x) {
            if (list.size() > 1) {
                return (list.size() % 2 == 0 ?
                        (list.get(list.size() / 2).x() + list.get((list.size() / 2) - 1).x()) / 2.0 : list.get(list.size() / 2).x());
            }
            return list.get(0).x();
        } else {
            if (list.size() > 1) {
                return (list.size() % 2 == 0 ?
                        (list.get(list.size() / 2).y() + list.get((list.size() / 2) - 1).y()) / 2.0 : list.get(list.size() / 2).y());
            }
            return list.get(0).y();
        }
    }

    public static double median(Point[] values, boolean x, int from, int to) {
        // Select the median element using quickselect
        int middleIndex = (from + to + 1) / 2;
        return quickselect(values, from, to, middleIndex, x);
    }

    private static double quickselect(Point[] values, int low, int high, int k, boolean x) {
        if (low == high) {
            return x ? values[low].x() : values[low].y();
        }
        int pivotIndex = partition(values, low, high, x);
        if (k == pivotIndex) {
            return x ? values[pivotIndex].x() : values[pivotIndex].y();
        } else if (k < pivotIndex) {
            return quickselect(values, low, pivotIndex - 1, k, x);
        } else {
            return quickselect(values, pivotIndex + 1, high, k, x);
        }
    }

    private static int partition(Point[] values, int low, int high, boolean x) {
        int mid = low + (high - low) / 2;
        double pivot = medianOfThree(values[low], values[mid], values[high], x);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            double element = x ? values[j].x() : values[j].y();
            if (element <= pivot) {
                i++;
                Point t = values[i];
                values[i] = values[j];
                values[j] = t;
            }
        }
        Point t = values[i + 1];
        values[i + 1] = values[high];
        values[high] = t;
        return i + 1;
    }

    private static double medianOfThree(Point a, Point b, Point c, boolean x) {
        double ax = x ? a.x() : a.y();
        double bx = x ? b.x() : b.y();
        double cx = x ? c.x() : c.y();

        if ((ax <= bx && bx <= cx) || (cx <= bx && bx <= ax)) {
            return bx;
        } else if ((bx <= ax && ax <= cx) || (cx <= ax && ax <= bx)) {
            return ax;
        } else {
            return cx;
        }
    }
}
