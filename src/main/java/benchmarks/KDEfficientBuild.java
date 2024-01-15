package benchmarks;

import model.Point;
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
        List<String> lines = Files.readAllLines(Paths.get("src/main/java/benchmarks/random_points.txt"));
        Point[] points = new Point[lines.size()];
        int i = 0;
        for (String line : lines) {
            String[] coords = line.split(",");
            points[i++] = new Point(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]));
        }
        Area area = new Area(0, 10000, 0, 10000);
        KDTreeEfficient kdTreeEfficient = new KDTreeEfficient(points, area);
        kdTreeEfficient.buildTree();

        String treeString = treeToString(kdTreeEfficient);

        String outputPath = "src/main/java/benchmarks/outputJava.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            writer.write(treeString);
        }

        System.out.println("Result written to: " + outputPath);

        System.out.println("test equality");

        List<String> javaOutPut = Files.readAllLines(Paths.get(outputPath));
        List<String> cppOutput = Files.readAllLines(Paths.get("src/main/java/benchmarks/outputCpp.txt"));
        outPutsEqual(cppOutput, javaOutPut);

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
}
