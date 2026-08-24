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

    public int register(String name, String email, String password) throws SQLException {
        String sql = "INSERT INTO students (name, email, password) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, PasswordUtil.hash(password));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    // returns Student if email+password match, otherwise null
    public Student login(String email, String password) throws SQLException {
        String sql = "SELECT id, name, email, password FROM students WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    if (PasswordUtil.verify(password, storedHash)) {
                        Student s = new Student();
                        s.setId(rs.getInt("id"));
                        s.setName(rs.getString("name"));
                        s.setEmail(rs.getString("email"));
                        return s;
                    }
                }
            }
        }
        return null;
    }

    // used to distinguish "no account" vs "wrong password"
    public boolean accountExists(String email) throws SQLException {
        return emailExists(email);
    }
}