package utils;

import commons.Paths;

import java.sql.*;

public class DatabaseConnection {

    public static String getEmail;
    public static String getPassword;

        public static void dbConnection() throws SQLException {

            String dbURL = Paths.DB_URL;
            String dbUsername = Paths.DB_USERNAME;
            String dbPassword = Paths.DB_PASSWORD;

            try (Connection connection = DriverManager.getConnection(dbURL, dbUsername, dbPassword)) {
                try (Statement statement = connection.createStatement();
                     ResultSet resultSet = statement.executeQuery("SELECT * FROM loginUser WHERE id = 8")) {
                    while (resultSet.next()) {
                        getEmail = resultSet.getString("email");
                        getPassword = resultSet.getString("password");
                        System.out.println("Email: " + getEmail + ", Password: " + getPassword);
                    }
                } catch (SQLException e) {
                    System.out.println("Error executing query: " + e.getMessage());
                }// Connection auto-closes here, even if exception occurs, preventing connection leaks

            }
        }

}
