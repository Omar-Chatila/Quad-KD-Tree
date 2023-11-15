package util;

import model.Point;

import java.util.ArrayList;
import java.util.Collections;
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

    public static double median(List<Point> values, boolean x, int from, int to) {
        // Select the median element using quickselect
        int middleIndex = (from + to + 1) / 2;
        return quickselect(values, from, to, middleIndex, x);
    }

    private static double quickselect(List<Point> values, int low, int high, int k, boolean x) {
        if (low == high) {
            return x ? values.get(low).x() : values.get(low).y();
        }

        int pivotIndex = partition(values, low, high, x);

        if (k == pivotIndex) {
            return x ? values.get(pivotIndex).x() : values.get(pivotIndex).y();
        } else if (k < pivotIndex) {
            return quickselect(values, low, pivotIndex - 1, k, x);
        } else {
            return quickselect(values, pivotIndex + 1, high, k, x);
        }
    }

    private static int partition(List<Point> values, int low, int high, boolean x) {
        double pivot = x ? values.get(high).x() : values.get(high).y();
        int i = low - 1;
        for (int j = low; j < high; j++) {
            double element = x ? values.get(j).x() : values.get(j).y();
            if (element <= pivot) {
                i++;
                Collections.swap(values, i, j);
            }
        }
        Collections.swap(values, i + 1, high);
        return i + 1;
    }
}
