package benchmarks;

import model.Point;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class SetPointsFile {
    public static void main(String[] args) {
        try {
            // Create FileWriter and BufferedWriter
            FileWriter fileWriter = new FileWriter("src/main/java/benchmarks/random_points.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            // Create Random object
            Random random = new Random();

            // Generate and write 100,000 random points
            for (int i = 0; i < 1000000; i++) {
                double x = random.nextDouble() * 10000;  // Change this as needed
                double y = random.nextDouble() * 10000;  // Change this as needed

                Point p = new Point(x, y);

                String line = p.x() + "," + p.y();
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }

            // Close the BufferedWriter
            bufferedWriter.close();

            System.out.println("Random points written to random_points.txt");

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
