package util;

import model.Point;

import java.util.ArrayList;
import java.util.List;

public class ArrayListHelper {
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

    public static double getMedian(List<Point> list) {
        return list.size() > 1 ? (list.size() % 2 == 0 ? (list.get(list.size() / 2).x() + list.get((list.size() / 2) - 1).x()) / 2.0 : list.get(list.size() / 2).x()) : list.get(0).x();
    }
}
