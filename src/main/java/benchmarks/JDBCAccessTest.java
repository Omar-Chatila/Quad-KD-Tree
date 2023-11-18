package benchmarks;

import model.Point;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCAccessTest {
    static final String USER = "oc98jax7";
    static final String MY_ACCESS_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqYXZhLWpkYmM6b2M5OGpheDciLCJpc3MiOiJjbGllbnQ6amF2YS1qZGJjOmFnZW50Om9jOThqYXg3Ojo5YTYyZjQwYS02OTFkLTQzY2EtYmU5Yy1hMzhlM2VlMTQzNmUiLCJpYXQiOjE3MDAyMjg1MTUsInJvbGUiOlsidXNlcl9hcGlfYWRtaW4iLCJ1c2VyX2FwaV9yZWFkIiwidXNlcl9hcGlfd3JpdGUiXSwiZ2VuZXJhbC1wdXJwb3NlIjp0cnVlLCJzYW1sIjp7fX0.l5roSTQIULWYBldeBXG8Dp6qxarUXFKTNWDuWU2ebUIzbJM3SZ6wXTll0ymgTQNAIm6D5HLmvXuNT2v4mKAdaA";
    static final String QUERY = "SELECT column_a, column_l  FROM table_a_4_number_real_median_earnings_total_workers_full_time_year_round_workers_sex_female_male_earnings_ratio_1960_2015";
    static String[] urls = {
            "uscensusbureau:income-poverty-health-ins",
            "city-of-ny:2t32-hbca"
    };
    static final String DB_URL = "jdbc:data:world:sql:" + urls[0];

    public static void main(String[] args) {
        Point[] record = connectAndQuery();
        for (Point p : record) {
            System.out.println(p);
        }
    }

    public static Point[] connectAndQuery() {
        // Open a connection
        List<Point> record = new ArrayList<>();
        try (final Connection connection =    // get a connection to the database, which will automatically be closed when done
                     DriverManager.getConnection(DB_URL, USER, MY_ACCESS_TOKEN);
             final PreparedStatement statement = // get a connection to the database, which will automatically be closed when done
                     connection.prepareStatement(QUERY)) {
            try (final ResultSet resultSet = statement.executeQuery()) { //execute the query
                resultSet.next();
                while (resultSet.next()) { //loop through the query results
                    double x;
                    double y;
                    x = Double.parseDouble(resultSet.getString(1).substring(0, 4));
                    y = (resultSet.getDouble(2));
                    record.add(new Point("year : median income", x, y));
                }
                // Note: when calling ResultSet.getObject() prefer the version that takes an explicit Class argument:
                // Integer n = resultSet.getObject(param, Integer.class);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Point[] points = new Point[record.size()];
        record.toArray(points);
        return points;
    }
}