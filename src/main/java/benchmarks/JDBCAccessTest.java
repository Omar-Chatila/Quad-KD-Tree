package benchmarks;

import model.Point;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class JDBCAccessTest {
    static final String USER = "oc98jax7";
    static final String MY_ACCESS_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqYXZhLWpkYmM6b2M5OGpheDciLCJpc3MiOiJjbGllbnQ6amF2YS1qZGJjOmFnZW50Om9jOThqYXg3Ojo5YTYyZjQwYS02OTFkLTQzY2EtYmU5Yy1hMzhlM2VlMTQzNmUiLCJpYXQiOjE3MDAyMjg1MTUsInJvbGUiOlsidXNlcl9hcGlfYWRtaW4iLCJ1c2VyX2FwaV9yZWFkIiwidXNlcl9hcGlfd3JpdGUiXSwiZ2VuZXJhbC1wdXJwb3NlIjp0cnVlLCJzYW1sIjp7fX0.l5roSTQIULWYBldeBXG8Dp6qxarUXFKTNWDuWU2ebUIzbJM3SZ6wXTll0ymgTQNAIm6D5HLmvXuNT2v4mKAdaA";
    static final String HEALTH_DB_URL = "jdbc:data:world:sql:health:big-cities-health";
    static final String INCOME_DB_URL = "jdbc:data:world:sql:uscensusbureau:income-poverty-health-ins";
    private static final String HEALTH_DB_QUERY = "SELECT year, indicator_category, value, bchc_requested_methodology FROM big_cities_health_data_inventory Where bchc_requested_methodology like '%100,000%' LIMIT 5000";
    private static final String INCOME_DB_QUERY = "SELECT column_a, column_l  FROM table_a_4_number_real_median_earnings_total_workers_full_time_year_round_workers_sex_female_male_earnings_ratio_1960_2015";

    public static void main(String[] args) {
        Point[] record = connectAndQuery(HEALTH_DB_URL);
        for (Point p : record) {
            System.out.println(p);
        }
    }

    public static Point[] connectAndQuery(String DB_URL) {
        // Open a connection
        List<Point> record = new ArrayList<>();
        String QUERY = DB_URL.equals(HEALTH_DB_URL) ? HEALTH_DB_QUERY : INCOME_DB_QUERY;
        try (final Connection connection =    // get a connection to the database, which will automatically be closed when done
                     DriverManager.getConnection(DB_URL, USER, MY_ACCESS_TOKEN);
             final PreparedStatement statement = // get a connection to the database, which will automatically be closed when done
                     connection.prepareStatement(QUERY)) {
            try (final ResultSet resultSet = statement.executeQuery()) { //execute the query
                resultSet.next();
                while (resultSet.next()) { //loop through the query results
                    record.add(getPoint(DB_URL, resultSet));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        HashSet<Point> hashSet = new HashSet<>(record);
        Point[] points = new Point[hashSet.size()];
        hashSet.toArray(points);
        return points;
    }

    private static Point getPoint(String database, ResultSet resultSet) throws Exception {
        if (database.equals(INCOME_DB_URL)) {
            return new Point("Year : Income | ", Double.parseDouble(resultSet.getString(1).substring(0, 4)),
                    resultSet.getDouble(2));
        } else if (database.equals(HEALTH_DB_URL)) {
            return new Point(resultSet.getString(2) + " | ", resultSet.getDouble(1), resultSet.getDouble(3));
        }
        throw new IllegalArgumentException("Illegal Database URL");
    }
}