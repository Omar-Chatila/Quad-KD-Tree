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
}
