package util;

import model.Point;

public class QuickSelect {

    public static double findMedian(Point[] points, int left, int right, boolean x) {
        int pos = (left + right) / 2;
        return quickSelect(points, pos, left, right, x);
    }

    private static double quickSelect(Point[] points, int pos, int left, int right, boolean x) {
        if (left == right && left == pos) {
            return x ? points[left].x() : points[left].y();
        }
        int posRes = partition(points, left, right, x);
        if (posRes == pos) {
            return x ? points[posRes].x() : points[posRes].y();
        } else if (posRes < pos) {
            return quickSelect(points, pos, posRes + 1, right, x);
        } else {
            return quickSelect(points, pos, left, posRes - 1, x);
        }
    }

    private static int partition(Point[] points, int left, int right, boolean x) {
        double pivot = x ? points[left].x() : points[left].y();
        int position = left;
        for (int i = left + 1; i <= right; i++) {
            if ((x ? points[i].x() : points[i].y()) <= pivot) {
                position++;
                swap(points, i, position);
            }
        }
        swap(points, left, position);
        return position;
    }

    private static void swap(Point[] points, int first, int second) {
        Point temp = points[first];
        points[first] = points[second];
        points[second] = temp;
    }
}