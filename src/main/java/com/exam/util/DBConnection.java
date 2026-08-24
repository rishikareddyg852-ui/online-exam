package com.exam.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class DBConnection {

    // Reads from environment variables (set these in Render).
    // Falls back to your local MySQL settings if env vars aren't set,
    // so this still works unchanged on your own machine.

    private static final String HOST     = getEnv("DB_HOST", "onlineexamdb-rishikareddyg852-c766.j.aivencloud.com");
    private static final String PORT     = getEnv("DB_PORT", "15069");
    private static final String DATABASE = getEnv("DB_NAME", "online_exam_db");
    private static final String USER     = getEnv("DB_USER", "avnadmin");
    private static final String PASSWORD = getEnv("DB_PASSWORD", "");

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
    
    
}
