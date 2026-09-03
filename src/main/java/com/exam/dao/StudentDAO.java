package com.exam.dao;

import com.exam.model.Student;
import com.exam.util.DBConnection;
import com.exam.util.PasswordUtil;

import java.sql.*;

public class StudentDAO {

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT id FROM students WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT id FROM students WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int register(String name, String email, String username, String password) throws SQLException {
        String sql = "INSERT INTO students (name, email, username, password) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, username);
            ps.setString(4, PasswordUtil.hash(password));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public Student login(String username, String password) throws SQLException {
        String sql = "SELECT id, name, email, username, password FROM students WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    if (PasswordUtil.verify(password, storedHash)) {
                        Student s = new Student();
                        s.setId(rs.getInt("id"));
                        s.setName(rs.getString("name"));
                        s.setEmail(rs.getString("email"));
                        s.setUsername(rs.getString("username"));
                        return s;
                    }
                }
            }
        }
        return null;
    }

    public boolean accountExists(String username) throws SQLException {
        return usernameExists(username);
    }
}