package benchmarks;

import model.Point;
import model.Tree;
import model.kdTree.KDTreeEfficient;
import model.quadTree.Area;
import model.quadTree.QuadTree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataTreeFactory {
    /*
        [Vehicle Name, Car Type, Drive Train Type, Retail Price ($US), Cost Price ($US), Engine Size (litres), Number cylinders, Horsepower (hp), City  (km/100L), Open Road (km/100L), Weight (kg), Wheel base (cm), Length (cm), Width (cm)]
     */

    public static final String American_New_Cars_and_Trucks_of_2004 = "src/main/resources/Datasets/American New Cars and Trucks of 2004.csv";
    public static final String American_New_Cars_and_Trucks_of_1993 = "src/main/resources/Datasets/American New Cars of 1993.csv";

    public static void main(String[] args) {
        KDTreeEfficient carDTree = (KDTreeEfficient) carDataKDTree(American_New_Cars_and_Trucks_of_2004, TreeType.KDTree);
        carDTree.buildTree();
        System.out.println(carDTree.query(new Area(0, 40000, 300, 5000)));

        KDTreeEfficient carDTree2 = (KDTreeEfficient) carDataKDTree(American_New_Cars_and_Trucks_of_1993, TreeType.KDTree);
        carDTree2.buildTree();
        System.out.println(carDTree2.query(new Area(0, 40000, 100, 150)));

        QuadTree quadTree = (QuadTree) carDataKDTree(American_New_Cars_and_Trucks_of_2004, TreeType.QuadTree);
        quadTree.buildTree();
        System.out.println(quadTree.query(new Area(0, 40000, 300, 5000)));
    }

    public static Tree carDataKDTree(String dataSet, TreeType type) {
        int index3 = dataSet.equals(American_New_Cars_and_Trucks_of_2004) ? 7 : 11;
        List<List<String>> records = setCarRecord(dataSet);
        int i = 0;
        Point[] pointSet = new Point[records.size()];
        double xMax = 0, yMax = 0, xMin = 0, yMin = 0;
        for (List<String> entry : records) {
            String carModel = entry.get(0);
            double price = Double.parseDouble(entry.get(3));
            double horsePower = Double.parseDouble(entry.get(index3));
            xMax = Math.max(price, xMax);
            yMax = Math.max(horsePower, yMax);
            xMin = Math.min(price, xMin);
            yMin = Math.min(horsePower, yMin);
            pointSet[i++] = new Point(carModel, price, horsePower);
        }
        Area domain = new Area(xMin - 10, xMax + 10, yMin - 10, yMax + 10);
        return type == TreeType.KDTree ? new KDTreeEfficient(pointSet, domain) : new QuadTree(Arrays.asList(pointSet), domain);
    }

    private static List<List<String>> setCarRecord(String fileUrl) {
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileUrl))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        records.remove(0);
        return records;
    }

    public enum TreeType {
        KDTree, QuadTree
    }
}
