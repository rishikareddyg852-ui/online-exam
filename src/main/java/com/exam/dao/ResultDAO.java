package com.exam.dao;

import com.exam.model.Result;
import com.exam.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultDAO {

    // ------------------------------------------------
    // CHECK WHETHER STUDENT ALREADY SUBMITTED EXAM
    // ------------------------------------------------

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


    // ------------------------------------------------
    // SAVE RESULT - ONLY ONCE
    // ------------------------------------------------

    public boolean saveResult(
            int studentId,
            int examId,
            int score,
            int totalMarks)
            throws SQLException {

        /*
         * IMPORTANT:
         * Check again immediately before INSERT.
         * This prevents the same student from submitting
         * the same exam more than once.
         */

        String checkSql =
                "SELECT id FROM results " +
                "WHERE student_id = ? AND exam_id = ? " +
                "LIMIT 1";

        String insertSql =
                "INSERT INTO results " +
                "(student_id, exam_id, score, total_marks) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn =
                     DBConnection.getConnection()) {

            // -----------------------------------------
            // CHECK FIRST