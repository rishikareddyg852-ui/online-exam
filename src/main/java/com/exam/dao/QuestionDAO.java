package com.exam.dao;

import com.exam.model.Question;
import com.exam.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {

    public void addQuestion(
            Connection conn,
            int examId,
            String questionText,
            String optA,
            String optB,
            String optC,
            String optD,
            String correctOption)
            throws SQLException {

        String sql =
                "INSERT INTO questions " +
                "(exam_id, question_text, option_a, option_b, option_c, option_d, correct_option) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (
                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setInt(1, examId);
            ps.setString(2, questionText);
            ps.setString(3, optA);
            ps.setString(4, optB);
            ps.setString(5, optC);
            ps.setString(6, optD);
            ps.setString(7, correctOption);

            ps.executeUpdate();
        }
    }


    public List<Question> getQuestionsByExam(
            int examId)
            throws SQLException {

        List<Question> list =
                new ArrayList<>();

        String sql =
                "SELECT id, exam_id, question_text, " +
                "option_a, option_b, option_c, " +
                "option_d, correct_option " +
                "FROM questions " +
                "WHERE exam_id = ?";

        try (
                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setInt(1, examId);

            try (ResultSet rs =
                         ps.executeQuery()) {

                while (rs.next()) {

                    Question q =
                            new Question();

                    q.setId(
                            rs.getInt("id")
                    );

                    q.setExamId(
                            rs.getInt("exam_id")
                    );

                    q.setQuestionText(
                            rs.getString("question_text")
                    );

                    q.setOptionA(
                            rs.getString("option_a")
                    );

                    q.setOptionB(
                            rs.getString("option_b")
                    );

                    q.setOptionC(
                            rs.getString("option_c")
                    );

                    q.setOptionD(
                            rs.getString("option_d")
                    );

                    q.setCorrectOption(
                            rs.getString("correct_option")
                    );

                    list.add(q);
                }
            }
        }

        return list;
    }


    public List<Question> getAnswerKeyByExam(
            int examId)
            throws SQLException {

        List<Question> list =
                new ArrayList<>();

        String sql =
                "SELECT id, correct_option " +
                "FROM questions " +
                "WHERE exam_id = ?";

        try (
                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setInt(1, examId);

            try (ResultSet rs =
                         ps.executeQuery()) {

                while (rs.next()) {

                    Question q =
                            new Question();

                    q.setId(
                            rs.getInt("id")
                    );

                    q.setCorrectOption(
                            rs.getString("correct_option")
                    );

                    list.add(q);
                }
            }
        }

        return list;
    }


    public boolean deleteQuestion(int id) throws SQLException {

        String sql = "DELETE FROM questions WHERE id = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        }
    }
}