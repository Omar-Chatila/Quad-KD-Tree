package benchmarks;

import model.Point;
import model.Tree;
import model.kdTree.KDTreeEfficient;
import model.kdTree.MyKDTree;
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
    static MyKDTree slowKD;
    static KDTreeEfficient fastKD;
    static int testCycles = 10;
    static List<long[]> timesEach = new ArrayList<>();
    static List<Integer> pqtHeights = new ArrayList<>();
    static List<Integer> prqtHeights = new ArrayList<>();

    static Point[] setTestData(int numberPoints) {
        Point[] pointSet = new Point[numberPoints];
        for (int i = 0; i < numberPoints; i++) {
            double x = Math.random() * 1000000;
            double y = Math.random() * 1000000;
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
                tree2.query(area);

                tree.query(area);
                tree2.query(area);

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
        return setPlotData(record, record2);
    }

    static String KDbenchQuery() {
        int number = 1000;
        String[] record = new String[4];
        String[] record2 = new String[4];
        List<Area> queryAreas = queryAreas();
        int step = 1;
        for (int j = 0; j <= 3; j++) {
            testCycles = 1;
            Point[] data = setTestData(number);
            MyKDTree slowtree = new MyKDTree(List.of(data), benchArea, 0);
            KDTreeEfficient fasttree = new KDTreeEfficient(data, benchArea);


            slowtree.buildTree();
            fasttree.buildTree();


            double timePerArea = 0;
            double timePerArea2 = 0;

            for (Area area : queryAreas) {
                slowtree.query(area);
                fasttree.query(area);

                double average = 0;
                double average2 = 0;

                for (int i = 0; i < testCycles; i++) {
                    long start = System.nanoTime();
                    slowtree.query(area);
                    double time = Math.round((System.nanoTime() - start) / 1E5);
                    average += time;

                    long start2 = System.nanoTime();
                    fasttree.query(area);
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
            System.out.println(step++ + "/" + 5);
        }
        return setPlotData(record, record2);
    }

    private static String setPlotData(String[] record, String[] record2) {
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
        //queryAreas.add(new Area(2000, 2050, 5000, 5050)); // Extremely tiny area
        // queryAreas.add(new Area(0, 500, 0, 500)); // Very small area
        // queryAreas.add(new Area(25000, 25500, 90000, 90200)); // Very small rectangular area
        queryAreas.add(new Area(10000, 30000, 20000, 40000)); // Medium area
       /* queryAreas.add(new Area(50000, 100000, 35000, 100000)); // Large area
        queryAreas.add(new Area(8000, 50000, 12000, 70000)); // Rectangular area
        queryAreas.add(new Area(40000, 95000, 40000, 55000)); // Wide area
        queryAreas.add(new Area(80000, 82000, 2800, 90000)); // Tall area
        queryAreas.add(new Area(6000, 60000, 30000, 32000)); // Wide medium area
        queryAreas.add(new Area(60000, 60100, 500, 1000)); // Very small area

        */
        return queryAreas;
    }

    static double benchBuildTime(Tree<Point> tree) {
        long start = System.nanoTime();
        tree.buildTree();
        return ((System.nanoTime() - start));
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

    static double slowKDBuildTimes(Point[] pointSet) {
        // exclude first 3 results bcs of caching
        slowKD = new MyKDTree(List.of(pointSet), benchArea, 0);
        benchBuildTime(slowKD);
        slowKD = new MyKDTree(List.of(pointSet), benchArea, 0);
        benchBuildTime(slowKD);
        slowKD = new MyKDTree(List.of(pointSet), benchArea, 0);
        benchBuildTime(slowKD);
        slowKD = new MyKDTree(List.of(pointSet), benchArea, 0);
        double[] times = new double[testCycles];
        for (int i = 0; i < times.length; i++) {
            times[i] = benchBuildTime(slowKD);
            slowKD = new MyKDTree(List.of(pointSet), benchArea, 0);
        }
        double result = 0;
        for (double entry : times) {
            result += entry;
        }
        return (result / times.length);
    }

    static double fastKDBuildTimes(Point[] pointSet) {
        // exclude first 3 results bcs of caching
        fastKD = new KDTreeEfficient(pointSet, benchArea);
        benchBuildTime(fastKD);
        fastKD = new KDTreeEfficient(pointSet, benchArea);
        benchBuildTime(fastKD);
        fastKD = new KDTreeEfficient(pointSet, benchArea);
        benchBuildTime(fastKD);
        fastKD = new KDTreeEfficient(pointSet, benchArea);
        double[] times = new double[testCycles];
        for (int i = 0; i < times.length; i++) {
            times[i] = benchBuildTime(fastKD);
            fastKD = new KDTreeEfficient(pointSet, benchArea);
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
        Point[] points = setTestData(000);
        List<Point> pointList = new ArrayList<>(Arrays.asList(points));
        PointQuadTree quadTree = new PointQuadTree(new ArrayList<>(), new Area(0, 1000000, 0, 1000000));

        long start = System.nanoTime();
        for (Point p : pointList) {
            quadTree.add(p);
        }
        System.out.println((System.nanoTime() - start) / 1E9);


        //quadTree.buildTree();
        System.out.println(quadTree.getHeight());
        // System.out.println(getPRQTResults());
        // System.out.println(pqtHeights);
        //System.out.println(prqtHeights);

        //System.out.println(KDbenchQuery());
        //System.out.println(benchQuery());
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
            number *= 10;
            System.out.println("(" + i + "/6)");
            //System.gc();
        }
        return getRecords(pqttimes, pqrttimes);
    }

    static String getKDResults() {
        // Set pointSets
        List<Double> kDtimes = new ArrayList<>();
        List<Double> fastKDtimes = new ArrayList<>();
        int number = 1000;
        for (int i = 1; i <= 4; i++) {
            Point[] currentSet = (setTestData(number));
            kDtimes.add(slowKDBuildTimes(currentSet));
            fastKDtimes.add(fastKDBuildTimes(currentSet));
            number *= 10;
            System.out.println("(" + i + "/4)");
            //System.gc();
        }
        return getRecords(kDtimes, fastKDtimes);
    }

    private static String getRecords(List<Double> times, List<Double> times2) {
        String[] records = new String[times.size()];
        String[] records2 = new String[times.size()];
        for (int i = 0; i < records.length; i++) {
            records[i] = "(" + (3 + i) + ", " + times.get(i) + ")";
            records2[i] = "(" + (3 + i) + ", " + times2.get(i) + ")";
        }

        return setPlotData(records, records2);
    }
}
