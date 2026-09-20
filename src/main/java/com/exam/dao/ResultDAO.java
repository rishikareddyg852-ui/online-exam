package com.exam.dao;

import com.exam.model.Result;
import com.exam.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultDAO {

    // ---------------------------------------
    // CHECK WHETHER STUDENT ALREADY SUBMITTED
    // ---------------------------------------

    public boolean hasAttempted(int studentId, int examId)
            throws SQLException {

        String sql =
                "SELECT id FROM results " +
                "WHERE student_id = ? AND exam_id = ? " +
                "LIMIT 1";

        try (Connection conn =
                     DBConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, examId);

            try (ResultSet rs =
                         ps.executeQuery()) {

                return rs.next();
            }
        }
    }


    // ---------------------------------------
    // SAVE RESULT
    // ---------------------------------------

    public void saveResult(
            int studentId,
            int examId,
            int score,
            int totalMarks)
            throws SQLException {

        String sql =
                "INSERT INTO results " +
                "(student_id, exam_id, score, total_marks) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn =
                     DBConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, examId);
            ps.setInt(3, score);
            ps.setInt(4, totalMarks);

            ps.executeUpdate();
        }
    }


    // ---------------------------------------
    // GET RESULTS FOR STUDENT
    // ---------------------------------------

    public List<Result> getResultsForStudent(
            int studentId)
            throws SQLException {

        List<Result> list =
                new ArrayList<>();

        String sql =
                "SELECT r.score, r.total_marks, " +
                "r.submitted_at, e.title " +
                "FROM results r " +
                "JOIN exams e ON r.exam_id = e.id " +
                "WHERE r.student_id = ? " +
                "ORDER BY r.submitted_at DESC";

        try (Connection conn =
                     DBConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs =
                         ps.executeQuery()) {

                while (rs.next()) {

                    Result r = new Result();

                    r.setScore(
                            rs.getInt("score")
                    );

                    r.setTotalMarks(
                            rs.getInt("total_marks")
                    );

                    r.setSubmittedAt(
                            rs.getString("submitted_at")
                    );

                    r.setExamTitle(
                            rs.getString("title")
                    );

                    list.add(r);
                }
            }
        }

        return list;
    }


    // ---------------------------------------
    // GET ALL RESULTS - ADMIN
    // ---------------------------------------

    public List<Result> getAllResults()
            throws SQLException {

        List<Result> list =
                new ArrayList<>();

        String sql =
                "SELECT r.score, r.total_marks, " +
                "r.submitted_at, " +
                "s.name AS student_name, " +
                "e.title AS exam_title " +
                "FROM results r " +
                "JOIN students s ON r.student_id = s.id " +
                "JOIN exams e ON r.exam_id = e.id " +
                "ORDER BY r.submitted_at DESC";

        try (Connection conn =
                     DBConnection.getConnection();
             Statement st =
                     conn.createStatement();
             ResultSet rs =
                     st.executeQuery(sql)) {

            while (rs.next()) {

                Result r = new Result();

                r.setScore(
                        rs.getInt("score")
                );

                r.setTotalMarks(
                        rs.getInt("total_marks")
                );

                r.setSubmittedAt(
                        rs.getString("submitted_at")
                );

                r.setStudentName(
                        rs.getString("student_name")
                );

                r.setExamTitle(
                        rs.getString("exam_title")
                );

                list.add(r);
            }
        }

        return list;
    }


    // ---------------------------------------
    // COUNT RESULTS
    // ---------------------------------------

    public int countResults()
            throws SQLException {

        String sql =
                "SELECT COUNT(*) c FROM results";

        try (Connection conn =
                     DBConnection.getConnection();
             Statement st =
                     conn.createStatement();
             ResultSet rs =
                     st.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("c");
            }
        }

        return 0;
    }


    // ---------------------------------------
    // COUNT STUDENTS
    // ---------------------------------------

    public int countStudents()
            throws SQLException {

        String sql =
                "SELECT COUNT(*) c FROM students";

        try (Connection conn =
                     DBConnection.getConnection();
             Statement st =
                     conn.createStatement();
             ResultSet rs =
                     st.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("c");
            }
        }

        return 0;
    }
}