package benchmarks;

import model.Point;
import model.Tree;
import model.kdTree.KDTreeEfficient;
import model.quadTree.Area;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class KDEfficientBuild {
    public static void main(String[] args) throws IOException {
        Point[] points = getPointsFromFile(100000);
        Area area = new Area(0, 10000, 0, 10000);
        KDTreeEfficient kdTreeEfficient = new KDTreeEfficient(points, area);
        kdTreeEfficient.buildTree();

        String treeString = treeToString(kdTreeEfficient);

        String outputPath = "src/main/java/benchmarks/outputJava.txt";
        List<String> javaOutPut = QuadtreeBuild.writeOutput(treeString, outputPath);
        List<String> cppOutput = Files.readAllLines(Paths.get("src/main/java/benchmarks/outputCpp.txt"));
        outPutsEqual(cppOutput, javaOutPut);

        String outputPath2 = "src/main/java/benchmarks/queryOutputJava.txt";
        writeQueryToFile(kdTreeEfficient, outputPath2);

        List<String> javaQueryOutPut = Files.readAllLines(Paths.get(outputPath2));
        List<String> cppQueryOutput = Files.readAllLines(Paths.get("src/main/java/benchmarks/queryOutputCpp.txt"));
        queryOutPutsEqual(cppQueryOutput, javaQueryOutPut);


    }

    static void writeQueryToFile(Tree<Point> tree, String outputPath2) {
        System.out.println("test equality - Query");
        Area queryArea = new Area(234, 7000, 2000, 9000);
        long start = System.nanoTime();
        List<Point> queryResult = tree.query(queryArea);
        long time = (long) ((System.nanoTime() - start) / 1E6);
        System.out.println("Query time: " + time);
        StringBuilder queryString = new StringBuilder();
        for (Point p : queryResult) {
            queryString.append("[").append(p.x()).append(":").append(p.y()).append("]").append("\n");
        }

        String result = queryString.toString();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath2))) {
            writer.write(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static String treeToString(KDTreeEfficient node) {
        String result = node.toString();
        if (node.getLeftChild() != null) {
            result += treeToString(node.getLeftChild());
        }
        if (node.getRightChild() != null) {
            result += treeToString(node.getRightChild());
        }
        return result;
    }

    static Point[] getPointsFromFile(int pointNumber) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("src/main/java/benchmarks/random_points.txt"));
        Point[] points = new Point[pointNumber];

        for (int i = 0; i < pointNumber; i++) {
            String line = lines.get(i);
            String[] coords = line.split(",");
            points[i] = new Point(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]));
        }
        return points;
    }

    static void outPutsEqual(List<String> cpp, List<String> java) {
        for (int i = 0; i < cpp.size(); i++) {
            if (!cpp.get(i).equals(java.get(i))) {
                System.err.println("Difference at line: " + i);
                System.out.println(cpp.get(i) + "\n" + java.get(i));
                return;
            }
        }
        System.out.println("success!");
    }

    static void queryOutPutsEqual(List<String> cpp, List<String> java) {
        for (int i = 0; i < cpp.size(); i++) {
            String[] p1 = cpp.get(i).split(":");
            double x1 = Double.parseDouble(p1[0].substring(1));
            double y1 = Double.parseDouble(p1[1].substring(0, p1[1].length() - 1));

            String[] p2 = java.get(i).split(":");
            double x2 = Double.parseDouble(p2[0].substring(1));
            double y2 = Double.parseDouble(p2[1].substring(0, p2[1].length() - 1));

            if (Math.abs(x1 - x2) > 0.1 || Math.abs(y1 - y2) > 0.1) {
                System.err.println("Difference at line: " + i);
                System.out.println(cpp.get(i) + "\n" + java.get(i));
                return;
            }
        }
        System.out.println("success!");
    }
}
