package com.exam.dao;

import com.exam.model.Exam;
import com.exam.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ExamDAO {

    public int createExam(
            String title,
            String description,
            int duration,
            int createdBy) throws SQLException {

        String sql =
                "INSERT INTO Exams " +
                "(title, description, duration_minutes, created_by) " +
                "VALUES (?, ?, ?, ?)";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )
        ) {

            ps.setString(1, title);
            ps.setString(2, description);
            ps.setInt(3, duration);
            ps.setInt(4, createdBy);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return -1;
    }


    public List<Exam> getAllExams()
            throws SQLException {

        List<Exam> list = new ArrayList<>();

        String sql =
                "SELECT e.id, e.title, e.description, " +
                "e.duration_minutes, " +
                "(SELECT COUNT(*) FROM questions q " +
                "WHERE q.exam_id = e.id) AS total_questions, " +
                "(SELECT COUNT(*) FROM results r " +
                "WHERE r.exam_id = e.id) AS total_attempts " +
                "FROM exams e " +
                "ORDER BY e.created_at DESC";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                Exam exam = new Exam();

                exam.setId(
                        rs.getInt("id")
                );

                exam.setTitle(
                        rs.getString("title")
                );

                exam.setDescription(
                        rs.getString("description")
                );

                exam.setDurationMinutes(
                        rs.getInt("duration_minutes")
                );

                exam.setTotalQuestions(
                        rs.getInt("total_questions")
                );

                exam.setTotalAttempts(
                        rs.getInt("total_attempts")
                );

                list.add(exam);
            }
        }

        return list;
    }


    public Exam getExamById(int examId)
            throws SQLException {

        String sql =
                "SELECT id, title, description, " +
                "duration_minutes, created_by " +
                "FROM exams WHERE id = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, examId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Exam exam = new Exam();

                    exam.setId(
                            rs.getInt("id")
                    );

                    exam.setTitle(
                            rs.getString("title")
                    );

                    exam.setDescription(
                            rs.getString("description")
                    );

                    exam.setDurationMinutes(
                            rs.getInt("duration_minutes")
                    );

                    exam.setCreatedBy(
                            rs.getInt("created_by")
                    );

                    return exam;
                }
            }
        }

        return null;
    }


    public boolean updateExam(
            int id,
            String title,
            String description,
            int duration)
            throws SQLException {

        String sql =
                "UPDATE Exams SET " +
                "title = ?, " +
                "description = ?, " +
                "duration_minutes = ? " +
                "WHERE id = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, title);
            ps.setString(2, description);
            ps.setInt(3, duration);
            ps.setInt(4, id);

            return ps.executeUpdate() > 0;
        }
    }


    public boolean deleteExam(int id)
            throws SQLException {

        String sql =
                "DELETE FROM exams WHERE id = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        }
    }


    public int countExams()
            throws SQLException {

        String sql =
                "SELECT COUNT(*) FROM exams";

        try (
                Connection conn = DBConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)
        ) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return 0;
    }


    /*
     * AVAILABLE EXAMS FOR STUDENT
     *
     * Shows exams which the student
     * has NOT attempted yet.
     *
     * NOT EXISTS is used instead of NOT IN.
     */

    public List<Exam> getAvailableExamsForStudent(
            int studentId)
            throws SQLException {

        List<Exam> list = new ArrayList<>();

        String sql =
                "SELECT e.id, e.title, e.description, " +
                "e.duration_minutes, " +

                "(SELECT COUNT(*) " +
                "FROM questions q " +
                "WHERE q.exam_id = e.id) " +
                "AS total_questions " +

                "FROM exams e " +

                "WHERE NOT EXISTS (" +

                "SELECT 1 " +
                "FROM results r " +

                "WHERE r.exam_id = e.id " +
                "AND r.student_id = ?" +

                ") " +

                "ORDER BY e.created_at DESC";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setInt(1, studentId);

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                while (rs.next()) {

                    Exam exam = new Exam();

                    exam.setId(
                            rs.getInt("id")
                    );

                    exam.setTitle(
                            rs.getString("title")
                    );

                    exam.setDescription(
                            rs.getString("description")
                    );

                    exam.setDurationMinutes(
                            rs.getInt("duration_minutes")
                    );

                    exam.setTotalQuestions(
                            rs.getInt("total_questions")
                    );

                    list.add(exam);
                }
            }
        }

        return list;
    }
}