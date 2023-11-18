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
import java.util.HashSet;
import java.util.List;

import static benchmarks.JDBCAccessTest.HEALTH_DB_URL;
import static benchmarks.JDBCAccessTest.INCOME_DB_URL;

public class DataTreeFactory {
    /*
        [Vehicle Name, Car Type, Drive Train Type, Retail Price ($US), Cost Price ($US), Engine Size (litres), Number cylinders, Horsepower (hp), City  (km/100L), Open Road (km/100L), Weight (kg), Wheel base (cm), Length (cm), Width (cm)]
     */

    public static final String American_New_Cars_and_Trucks_of_2004 = "src/main/resources/Datasets/American New Cars and Trucks of 2004.csv";
    public static final String American_New_Cars_and_Trucks_of_1993 = "src/main/resources/Datasets/American New Cars of 1993.csv";

    public static void main(String[] args) {

        KDTreeEfficient dbTree = (KDTreeEfficient) getDBTree(TreeType.KDTree, HEALTH_DB_URL);
        dbTree.buildTree();
        for (Point p : dbTree.query(new Area(2000, 2015, 50, 100))) {
            System.out.println(p);
        }

        KDTreeEfficient dbTree2 = (KDTreeEfficient) getDBTree(TreeType.KDTree, INCOME_DB_URL);
        dbTree2.buildTree();
        System.out.println(dbTree2.getHeight());
        for (Point p : dbTree2.query(new Area(1900, 2015, 45000, 50000))) {
            System.out.println(p);
        }
    }

    public static Tree carDataKDTree(String dataSet, TreeType type) {
        int index3 = dataSet.equals(American_New_Cars_and_Trucks_of_2004) ? 7 : 11;
        List<List<String>> records = setCarRecord(dataSet);
        HashSet<Point> pointSet = new HashSet<>();
        double xMax = 0, yMax = 0, xMin = 0, yMin = 0;
        for (List<String> entry : records) {
            String carModel = entry.get(0);
            double price = Double.parseDouble(entry.get(3));
            double horsePower = Double.parseDouble(entry.get(index3));
            xMax = Math.max(price, xMax);
            yMax = Math.max(horsePower, yMax);
            xMin = Math.min(price, xMin);
            yMin = Math.min(horsePower, yMin);
            pointSet.add(new Point(carModel, price, horsePower));
        }
        Area domain = new Area(xMin - 10, xMax + 10, yMin - 10, yMax + 10);
        Point[] points = new Point[pointSet.size()];
        pointSet.toArray(points);
        return getTree(points, domain, type);
    }

    public static Tree getDBTree(TreeType type, String URL) {
        return getTree(JDBCAccessTest.connectAndQuery(URL), new Area(0, 100000, 0, 100000), type);
    }

    public static Tree getTree(Point[] points, Area domain, TreeType type) {
        return type == TreeType.KDTree ? new KDTreeEfficient(points, domain) : new QuadTree(new ArrayList<>(Arrays.asList(points)), domain);
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
