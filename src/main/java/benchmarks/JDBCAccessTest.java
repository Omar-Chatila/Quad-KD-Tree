package benchmarks;

import java.sql.*;

public class JDBCAccessTest {
    static final String DB_URL = "jdbc:data:world:sql:city-of-ny:2t32-hbca";
    static final String USER = "oc98jax7";
    static final String MY_ACCESS_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqYXZhLWpkYmM6b2M5OGpheDciLCJpc3MiOiJjbGllbnQ6amF2YS1qZGJjOmFnZW50Om9jOThqYXg3Ojo5YTYyZjQwYS02OTFkLTQzY2EtYmU5Yy1hMzhlM2VlMTQzNmUiLCJpYXQiOjE3MDAyMjg1MTUsInJvbGUiOlsidXNlcl9hcGlfYWRtaW4iLCJ1c2VyX2FwaV9yZWFkIiwidXNlcl9hcGlfd3JpdGUiXSwiZ2VuZXJhbC1wdXJwb3NlIjp0cnVlLCJzYW1sIjp7fX0.l5roSTQIULWYBldeBXG8Dp6qxarUXFKTNWDuWU2ebUIzbJM3SZ6wXTll0ymgTQNAIm6D5HLmvXuNT2v4mKAdaA";
    static final String QUERY = "select * from assembly_district_breakdowns_1 where count_female > count_male";
    String[] urls = {
            "uscensusbureau:income-poverty-health-ins"
    };

    public static void main(String[] args) {
        // Open a connection
        try (final Connection connection =    // get a connection to the database, which will automatically be closed when done
                     DriverManager.getConnection(DB_URL, USER, MY_ACCESS_TOKEN);
             final PreparedStatement statement = // get a connection to the database, which will automatically be closed when done
                     connection.prepareStatement(QUERY)) {
            try (final ResultSet resultSet = statement.executeQuery()) { //execute the query
                ResultSetMetaData rsmd = resultSet.getMetaData();  //print out the column headers
                int columnsNumber = rsmd.getColumnCount();
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(",  ");
                    System.out.print(rsmd.getColumnName(i));
                }
                System.out.println();
                while (resultSet.next()) { //loop through the query results
                    for (int i = 1; i <= columnsNumber; i++) { //print out the column headers
                        if (i > 1) System.out.print(",  ");
                        String columnValue = resultSet.getString(i);
                        System.out.print(columnValue);
                    }
                    System.out.println();

                    // Note: when calling ResultSet.getObject() prefer the version that takes an explicit Class argument:
                    // Integer n = resultSet.getObject(param, Integer.class);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}