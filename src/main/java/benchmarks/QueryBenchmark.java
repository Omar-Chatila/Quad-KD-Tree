package benchmarks;

import model.Point;
import model.Tree;
import model.kdTree.KDTreeEfficient;
import model.kdTree.MyKDTree;
import model.quadTree.Area;
import model.quadTree.QuadTree;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//Rudimentary benchmarks
public class QueryBenchmark {
    public static void main(String[] args) {
        // Create random Points for testing
        List<Point> points = new ArrayList<>();
        int pointCount = (int) 1E6;
        Point[] points1 = new Point[pointCount];
        for (int i = 0; i < pointCount; i++) {
            Point p = new Point(Math.random() * 10000, Math.random() * 10000);
            points.add(p);
            points1[i] = p;
        }

        // Create rootArea and QueryArea
        Area testArea = new Area(0, 10000, 0, 10000);
        Area queryRect = new Area(100, 5999, 1000, 8000);


        // Build KD-Tree
        long start3 = System.nanoTime();
        MyKDTree kdTree = new MyKDTree(points, testArea, 0);
        kdTree.buildTree(0);
        System.out.println("KD Build time " + Math.round((System.nanoTime() - start3) / 1E6));


        // Build Quad-Tree
        long start2 = System.nanoTime();
        QuadTree quadTree = new QuadTree(testArea, points);
        quadTree.buildQuadTree();
        long end = Math.round((System.nanoTime() - start2) / 1E6);
        System.out.println("QT Build time " + end);

        // Build Efficient KD-Tree
        long start4 = System.nanoTime();
        KDTreeEfficient ekdTree = new KDTreeEfficient(points1, testArea);
        ekdTree.buildTree(0);
        long end4 = Math.round((System.nanoTime() - start4) / 1E6);
        System.out.println("Efficient KD Build time " + end4);
        System.out.println("Start");

        // Create 3 Threads for each query operation
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        CountDownLatch latch = new CountDownLatch(3);

        Runnable[] tasks = new Runnable[3];
        tasks[0] = runQuery(kdTree, queryRect, latch);
        tasks[1] = runQuery(ekdTree, queryRect, latch);
        tasks[2] = runQuery(quadTree, queryRect, latch);

        for (Runnable task : tasks) {
            threadPool.execute(task);
        }
        
        try {
            latch.await(); // Wait for both tasks to complete
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        threadPool.shutdown();
        System.out.println("End");
    }

    // Creates 3 Query threads based on tree type
    private static Runnable runQuery(Tree tree, Area queryArea, CountDownLatch latch) {
        String type = tree.getClass().toString().substring(tree.getClass().toString().lastIndexOf('.') + 1);
        return () -> {
            double average = 0;
            for (int i = 0; i < 10; i++) {
                long start7 = System.nanoTime();
                tree.query(queryArea);
                double time = (System.nanoTime() - start7) / 1E6;
                average += time;
            }
            System.out.println("AVERAGE " + type + " : " + Math.round(average) / 100 + " ms");
            latch.countDown();
        };
    }
}
