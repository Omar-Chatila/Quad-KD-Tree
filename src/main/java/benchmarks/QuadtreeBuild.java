package benchmarks;

import model.Point;
import model.quadTree.Area;
import model.quadTree.PointQuadTree;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static benchmarks.KDEfficientBuild.writeQueryToFile;

public class QuadtreeBuild {
    public static void main(String[] args) throws IOException {
        Point[] points = KDEfficientBuild.getPointsFromFile(100000);
        List<Point> pointList = new ArrayList<>(Arrays.asList(points));
        Area area = new Area(0, 10000, 0, 10000);
        long start = System.nanoTime();
        PointQuadTree quadTree = new PointQuadTree(pointList, area);
        quadTree.buildTree();
        long time = (long) ((System.nanoTime() - start) / 1E6);
        System.out.println(time);
        //verifyBuild(quadTree);

        String outputPath2 = "src/main/java/benchmarks/qtQueryOutputJava.txt";
        writeQueryToFile(quadTree, outputPath2);

        List<Point> javaResult = new ArrayList<>();
        List<Point> cppResult = new ArrayList<>();

        List<String> javaLines = Files.readAllLines(Paths.get(outputPath2));
        List<String> cppLines = Files.readAllLines(Paths.get("src/main/java/benchmarks/qtQueryOutputJava.txt"));

        for (int i = 0; i < javaLines.size(); i++) {
            String[] p1 = javaLines.get(i).split(":");
            double x1 = Double.parseDouble(p1[0].substring(1));
            double y1 = Double.parseDouble(p1[1].substring(0, p1[1].length() - 1));

            String[] p2 = cppLines.get(i).split(":");
            double x2 = Double.parseDouble(p2[0].substring(1));
            double y2 = Double.parseDouble(p2[1].substring(0, p2[1].length() - 1));

            javaResult.add(new Point(x1, y1));
            cppResult.add(new Point(x2, y2));
        }

        javaResult.sort((o1, o2) -> (int) (Math.sqrt(o1.x() * o1.x() + o1.y() * o1.y()) - Math.sqrt(o2.x() * o2.x() + o2.y() * o2.y())));
        cppResult.sort((o1, o2) -> (int) (Math.sqrt(o1.x() * o1.x() + o1.y() * o1.y()) - Math.sqrt(o2.x() * o2.x() + o2.y() * o2.y())));

        boolean success = true;
        for (int i = 0; i < javaResult.size(); i++) {
            if (Math.abs(javaResult.get(i).x() - cppResult.get(i).x()) > 0.1 || Math.abs(javaResult.get(i).y() - cppResult.get(i).y()) > 0.1) {
                System.err.println("difference at line: " + i);
                System.out.println(javaResult.get(i) + " --- " + cppResult.get(i));
                success = false;
                break;
            }
        }
        System.out.println("Equals? " + success);

    }

    static void verifyBuild(PointQuadTree quadTree) throws IOException {
        String treeString = qtToString(quadTree);

        String outputPath = "src/main/java/benchmarks/qtOutputJava.txt";
        List<String> javaOutPut = writeOutput(treeString, outputPath);
        List<String> cppOutput = Files.readAllLines(Paths.get("src/main/java/benchmarks/qtBuildOutput.txt"));

        KDEfficientBuild.outPutsEqual(cppOutput, javaOutPut);
    }

    static List<String> writeOutput(String treeString, String outputPath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            writer.write(treeString);
        }

        System.out.println("Result written to: " + outputPath);

        System.out.println("test equality - Build");

        return Files.readAllLines(Paths.get(outputPath));
    }

    static String qtToString(PointQuadTree node) {
        String result = node.toString();
        if (node.getNorthEast() != null) {
            result = result + qtToString(node.getNorthEast());
        }
        if (node.getNorthWest() != null) {
            result = result + qtToString(node.getNorthWest());
        }
        if (node.getSouthWest() != null) {
            result = result + qtToString(node.getSouthWest());
        }
        if (node.getSouthEast() != null) {
            result = result + qtToString(node.getSouthEast());
        }
        return result;
    }
}
