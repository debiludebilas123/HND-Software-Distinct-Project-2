package Database;

import Models.Insertable;

import java.lang.reflect.Constructor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    private static final String url = "jdbc:mariadb://localhost:3306/nojus_l_car_service";
    private static final String user = "root";
    private static final String password = "9809";

    public static Connection setupConnection() throws SQLException {
        // Method for setting up the connection to the database and closing after doing the operation each time
        return DriverManager.getConnection(url, user, password);
    }

    public static ResultSet fetchTable(String tableName) throws SQLException {
        try (Connection con = setupConnection()) {
            Statement stmt = con.createStatement();
            String query = "SELECT * FROM " + tableName;
            return stmt.executeQuery(query);
        }
    }

    public static ResultSet fetchAverageCustomerSpending() throws SQLException {
        try (Connection con = setupConnection()) {
            Statement stmt = con.createStatement();
            String query = "SELECT c.customerID, c.forename, c.surname, ROUND(AVG(j.cost), 2) AS averageJobCost FROM job j, car cc, customer c " +
                           "WHERE c.customerid = cc.customerid AND cc.regno = j.regno GROUP BY customerid;";
            return stmt.executeQuery(query);
        }
    }

    public static CarStayRecord fetchLongestStayingCar() throws SQLException {
        String query = "SELECT c.regNo, c.make, c.model, c.year, MAX(DATEDIFF(j.dateOut, j.dateIn)) AS days " +
                       "FROM job j JOIN car c ON c.regno = j.regno " +
                       "WHERE j.dateOut IS NOT NULL " +  // Only completed jobs
                       "GROUP BY c.regNo, c.make, c.model, c.year " +
                       "ORDER BY days DESC LIMIT 1";

        try (Connection con = setupConnection();
             PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return new CarStayRecord(
                        rs.getString("regNo"),
                        rs.getString("make"),
                        rs.getString("model"),
                        rs.getString("year"),
                        rs.getInt("days")
                );
            }
            return null; // No records found
        }
    }

    // Helper record class (Java 16+) - or use a regular class
    public record CarStayRecord(
            String regNo,
            String make,
            String model,
            String year,
            int daysStayed
    ) {}

    public static <T> List<T> fetchComboBoxValues(String tableName, String[] columnLabels, Class<T> clazz) throws SQLException {
        List<T> items = new ArrayList<>();

        try (Connection con = setupConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);

            while (rs.next()) {
                // Create an array to hold the values for the current row
                Object[] values = new Object[columnLabels.length];

                // Populate the array with values from the ResultSet
                for (int i = 0; i < columnLabels.length; i++) {
                    values[i] = rs.getObject(columnLabels[i]);
                }

                // Create an instance of the class using reflection
                T item = createInstance(clazz, values);
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Re-throw the exception after logging
        }

        return items;
    }

    private static <T> T createInstance(Class<T> clazz, Object[] values) {
        try {
            // Get the constructor that matches the number of columns
            Class<?>[] parameterTypes = new Class[values.length];
            for (int i = 0; i < values.length; i++) {
                parameterTypes[i] = values[i].getClass();
            }

            // Find the constructor
            Constructor<T> constructor = clazz.getConstructor(parameterTypes);

            // Create and return the instance
            return constructor.newInstance(values);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of " + clazz.getSimpleName(), e);
        }
    }

    public static void updateRecord(String tableName, String idColumnName,
                                    String id, String[] columns, Object[] values) throws SQLException {
        // Update a record in the database based on the given values
        StringBuilder query = new StringBuilder("UPDATE " + tableName + " SET ");
        // Based on how many values/columns were given we determine how many placeholders we will need and build a query based on that
        for (int i = 0; i < columns.length; i++) {
            query.append(columns[i]).append(" = ?");
            if (i < columns.length - 1) {
                query.append(", ");
            }
        }
        query.append(" WHERE ").append(idColumnName).append(" = ?");

        try (Connection conn = setupConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query.toString());
            // Change placeholders into actual values
            for (int i = 0; i < values.length; i++) {
                stmt.setObject(i + 1, values[i]);
            }
            // Set the WHERE placeholder which is going to be an ID
            stmt.setObject(values.length + 1, id);
            stmt.executeUpdate();
        }
    }

    public static void deleteRow(String tableName, String columnName, String id) throws SQLException {
        // Deleting a row from the database with given values
        String sql = "DELETE FROM " + tableName + " WHERE " + columnName + " = ?";

        try (Connection connection = setupConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, id);
            stmt.executeUpdate();
        }
    }

    public static void insertRecord(String tableName, String[] columns, Object[] values) throws SQLException {
        // Adding a record into specified table with values and columns
        StringBuilder query = new StringBuilder("INSERT INTO " + tableName + " (");
        // Building the query with loops by adding in placeholders
        for (int i = 0; i < columns.length; i++) {
            query.append(columns[i]);
            if (i < columns.length - 1) {
                query.append(", ");
            }
        }
        query.append(") VALUES (");
        for (int i = 0; i < values.length; i++) {
            query.append("?");
            if (i < values.length - 1) {
                query.append(", ");
            }
        }
        query.append(")");

        try (Connection conn = setupConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query.toString());
            // Setting the placeholders to values
            for (int i = 0; i < values.length; i++) {
                stmt.setObject(i + 1, values[i]);
            }
            stmt.executeUpdate();
        }
    }

    public static void insertEntity(Insertable entity) throws SQLException {
        insertRecord(entity.getDatabaseTableName(), entity.getColumns(), entity.getValues());
    }

    public static String getCustomerIDFromDatabase() throws SQLException {
        String sql = "SELECT MAX(customerID) AS maxID FROM " + "customer";
        try (Connection conn = setupConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getString("maxID") : "0001"; // Default if table is empty
        }
    }

    public static String getJobIDFromDatabase() throws SQLException {
        List<String> IDs = new ArrayList<>();
        int highestID = 0;

        // Get IDs
        String sql = "SELECT jobID FROM " + "job";
        try (Connection conn = setupConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                IDs.add(rs.getString("jobID"));
            }
        }

        // Extract the highest ID number from them all
        for (String ID : IDs) {
            if (highestID < Integer.parseInt(ID.substring(1))) {
                highestID = Integer.parseInt(ID.substring(1));
            }
        }

        // Determine how many zeros are needed before the number
        highestID++;
        if (highestID < 10) {
            return "J00" + highestID;
        } else if (highestID < 100) {
            return "J0" + highestID;
        } else {
            return "J" + highestID;
        }
    }

    public static String getGarageIDFromDatabase() throws SQLException {
        List<String> IDs = new ArrayList<>();
        List<String> garageIDNumbers = new ArrayList<>();

        // Get IDs
        String sql = "SELECT garageID FROM " + "garage";
        try (Connection conn = setupConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                IDs.add(rs.getString("garageID"));
            }
        }

        // Extract numbers from garage ID (G59-G)
        for (String ID : IDs) {
            String number = ID.substring(1, 2);
            garageIDNumbers.add(number);
        }

        // Make up a list from (1 - 99) except the extracted numbers and return a random from the list
        for (int i = 1; i <= 100; i++) {
            if (garageIDNumbers.contains(i)) {
                continue;
            } else {
                if (i < 10) {
                    String num = "0" + i;
                    return "G" + num + "-";
                }
                return "G" + i + "-";
            }
        }
        return null;
    }
}