package benchmarks;

import model.Point;
import model.Tree;
import model.quadTree.Area;
import model.quadTree.PointQuadTree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuadBenchMarks {
    static final Area benchArea = new Area(0, 1E5, 0, 1E5);
    static PointQuadTree pointQT;
    static PointQuadTree pointRegionQT;
    static int testCycles = 10;
    static List<long[]> timesEach = new ArrayList<>();
    static List<Integer> pqtHeights = new ArrayList<>();
    static List<Integer> prqtHeights = new ArrayList<>();

    static Point[] setTestData(int numberPoints) {
        Point[] pointSet = new Point[numberPoints];
        for (int i = 0; i < numberPoints; i++) {
            double x = Math.random() * 1E5;
            double y = Math.random() * 1E5;
            pointSet[i] = new Point(x, y);
        }
        return pointSet;
    }

    static String benchQuery() {
        int number = 1000;
        String[] record = new String[4];
        String[] record2 = new String[4];
        List<Area> queryAreas = queryAreas();
        int step = 1;
        for (int j = 0; j <= 3; j++) {
            List<Point> points = Arrays.asList(setTestData(number));
            PointQuadTree tree = new PointQuadTree(points, benchArea);
            PointQuadTree tree2 = new PointQuadTree(points, benchArea, (int) Math.log10(points.size()));


            tree.buildTree();
            tree2.buildTree();


            double timePerArea = 0;
            double timePerArea2 = 0;

            for (Area area : queryAreas) {
                tree.query(area);
                tree.query(area);

                double average = 0;
                double average2 = 0;

                for (int i = 0; i < testCycles; i++) {
                    long start = System.nanoTime();
                    tree.query(area);
                    double time = Math.round((System.nanoTime() - start) / 1E5);
                    average += time;

                    long start2 = System.nanoTime();
                    tree2.query(area);
                    double time2 = Math.round((System.nanoTime() - start2) / 1E5);
                    average2 += time2;
                }
                average = average / testCycles;
                average2 = average2 / testCycles;

                timePerArea += average;
                timePerArea2 += average2;
            }
            record[j] = "(" + (2 + j) + "," + timePerArea + ")";
            record2[j] = "(" + (2 + j) + "," + timePerArea2 + ")";
            number = number * 10;
            System.out.println(step++ + "/" + 4);
        }
        StringBuilder toPaste = new StringBuilder();
        for (String s : record) {
            toPaste.append(s);
        }
        toPaste.append("\n");
        for (String s : record2) {
            toPaste.append(s);
        }
        return toPaste.toString();
    }

    private static List<Area> queryAreas() {
        List<Area> queryAreas = new ArrayList<>();
        queryAreas.add(new Area(2000, 2050, 5000, 5050)); // Extremely tiny area
        queryAreas.add(new Area(0, 500, 0, 500)); // Very small area
        queryAreas.add(new Area(25000, 25500, 90000, 90200)); // Very small rectangular area
        queryAreas.add(new Area(10000, 30000, 20000, 40000)); // Medium area
        queryAreas.add(new Area(50000, 100000, 35000, 100000)); // Large area
        queryAreas.add(new Area(8000, 50000, 12000, 70000)); // Rectangular area
        queryAreas.add(new Area(40000, 95000, 40000, 55000)); // Wide area
        queryAreas.add(new Area(80000, 82000, 2800, 90000)); // Tall area
        queryAreas.add(new Area(6000, 60000, 30000, 32000)); // Wide medium area
        queryAreas.add(new Area(60000, 60100, 500, 1000)); // Very small area
        return queryAreas;
    }

    static double benchBuildTime(Tree<Point> tree) {
        long start = System.nanoTime();
        tree.buildTree();
        return ((System.nanoTime() - start) / 1E6);
    }

    static double pQTBuildTimes(Point[] pointSet) {
        // exclude first 3 results bcs of caching
        pointQT = new PointQuadTree(List.of(pointSet), benchArea);
        benchBuildTime(pointQT);
        pointQT = new PointQuadTree(List.of(pointSet), benchArea);
        benchBuildTime(pointQT);
        pointQT = new PointQuadTree(List.of(pointSet), benchArea);
        benchBuildTime(pointQT);
        pqtHeights.add(pointQT.getHeight());
        pointQT = new PointQuadTree(List.of(pointSet), benchArea);
        double[] times = new double[testCycles];
        for (int i = 0; i < times.length; i++) {
            times[i] = benchBuildTime(pointQT);
            pointQT = new PointQuadTree(List.of(pointSet), benchArea);
        }
        double result = 0;
        for (double entry : times) {
            result += entry;
        }

        return (result / times.length);
    }

    static double prQTBuildTimes(Point[] pointSet) {
        // exclude first 3 results bcs of caching
        int capacity = (int) (10 * Math.log10(pointSet.length));
        pointRegionQT = new PointQuadTree(List.of(pointSet), benchArea, capacity);
        benchBuildTime(pointRegionQT);
        pointRegionQT = new PointQuadTree(List.of(pointSet), benchArea, capacity);
        benchBuildTime(pointRegionQT);
        pointRegionQT = new PointQuadTree(List.of(pointSet), benchArea, capacity);
        benchBuildTime(pointRegionQT);
        prqtHeights.add(pointRegionQT.getHeight());
        pointRegionQT = new PointQuadTree(List.of(pointSet), benchArea, capacity);


        double[] times = new double[testCycles];
        for (int i = 0; i < times.length; i++) {
            times[i] = benchBuildTime(pointRegionQT);
            byte[] bytes = new byte[32000];
            pointRegionQT = new PointQuadTree(List.of(pointSet), benchArea, capacity);
        }
        double result = 0;
        for (double entry : times) {
            result += entry;
        }
        return result / times.length;
    }

    public static void main(String[] args) throws IOException {
        byte[] bytes = new byte[32000];
        /*System.out.println(getPRQTResults());
        System.out.println(pqtHeights);
        System.out.println(prqtHeights);

         */
        System.out.println(benchQuery());
    }

    static String getPRQTResults() {
        // Set pointSets
        List<Double> pqttimes = new ArrayList<>();
        List<Double> pqrttimes = new ArrayList<>();
        int number = 1000;
        for (int i = 1; i <= 5; i++) {
            Point[] currentSet = (setTestData(number));
            pqttimes.add(pQTBuildTimes(currentSet));
            pqrttimes.add(prQTBuildTimes(currentSet));
            System.out.println(pqttimes);
            System.out.println(pqrttimes);
            number *= 10;
            System.out.println("(" + i + "/6)");
            //System.gc();
        }

        return getRecords(pqttimes, pqrttimes);
    }

    private static String getRecords(List<Double> times, List<Double> times2) {
        String[] records = new String[times.size()];
        String[] records2 = new String[times.size()];
        for (int i = 0; i < records.length; i++) {
            records[i] = "(" + (3 + i) + ", " + times.get(i) + ")";
            records2[i] = "(" + (3 + i) + ", " + times2.get(i) + ")";
        }

        StringBuilder toPaste = new StringBuilder();
        for (String s : records) {
            toPaste.append(s);
        }
        toPaste.append("\n");
        for (String s : records2) {
            toPaste.append(s);
        }
        return toPaste.toString();
    }
}
