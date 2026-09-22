package com.exam.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;

public class DBConnection {

    // Reads from environment variables (set these in Render).
    // Falls back to your local MySQL settings if env vars aren't set,
    // so this still works unchanged on your own machine.

    private static final String HOST     = getEnv("DB_HOST", "bfqttba1kmkmwun7deog-mysql.services.clever-cloud.com");
    private static final String PORT     = getEnv("DB_PORT", "3306");
    private static final String DATABASE = getEnv("DB_NAME", "bfqttba1kmkmwun7deog");
    private static final String USER     = getEnv("DB_USER", "u8lrtvtngfcmrtko");
   // private static final String PASSWORD = getEnv("DB_PASSWORD", "");
    private static final String PASSWORD = getEnv("DB_PASSWORD", getPropertyFallback("db.password", ""));
    // SSL is required for Aiven's MySQL. Locally (localhost) this is
    // harmless - useSSL will just be ignored/not needed.
    private static final String URL =
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE +
            "?useSSL=true&requireSSL=true&verifyServerCertificate=false&serverTimezone=UTC";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(
                        "MySQL JDBC Driver not found. Add mysql-connector jar to WEB-INF/lib",
                        ex
                );
            }
        }
    }

    private static String getEnv(String key, String fallback) {
        String value = System.getenv(key);
        return (value == null || value.trim().isEmpty()) ? fallback : value;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    private static String getPropertyFallback(String propertyKey, String defaultFallback) {
        Properties props = new Properties();
        
        // This looks inside your project's classpath (e.g., src/main/resources)
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                return defaultFallback;
            }
            props.load(input);
            String value = props.getProperty(propertyKey);
            return (value == null || value.trim().isEmpty()) ? defaultFallback : value;
        } catch (IOException e) {
            System.err.println("Error reading config.properties file: " + e.getMessage());
            return defaultFallback;
        }
    }
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("Connected successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
}
