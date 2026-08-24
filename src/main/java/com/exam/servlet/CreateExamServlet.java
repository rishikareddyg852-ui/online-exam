package com.exam.servlet;

import com.exam.dao.QuestionDAO;
import com.exam.util.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet("/CreateExamServlet")
public class CreateExamServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("adminId") == null) {
            resp.sendRedirect("AdminLoginServlet");
            return;
        }

        resp.sendRedirect("CreateExam.html");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("adminId") == null) {
            resp.sendRedirect("AdminLoginServlet");
            return;
        }

        Object adminIdObject = session.getAttribute("adminId");

        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String durationStr = req.getParameter("duration");

        String[] questionText =
                req.getParameterValues("question_text[]");

        String[] optionA =
                req.getParameterValues("option_a[]");

        String[] optionB =
                req.getParameterValues("option_b[]");

        String[] optionC =
                req.getParameterValues("option_c[]");

        String[] optionD =
                req.getParameterValues("option_d[]");

        String[] correctOption =
                req.getParameterValues("correct_option[]");

        int duration = 0;

        try {
            duration = Integer.parseInt(durationStr);
        } catch (Exception e) {
            duration = 0;
        }

        if (title == null || title.trim().isEmpty()
                || duration <= 0
                || questionText == null
                || questionText.length == 0) {

            resp.sendRedirect(
                "CreateExam.html?error=Please fill exam details and add at least one question."
            );

            return;
        }

        Connection conn = null;

        try {

            conn = DBConnection.getConnection();

            conn.setAutoCommit(false);

            int examId;

            String examSql =
                "INSERT INTO exams " +
                "(title, description, duration_minutes, created_by) " +
                "VALUES (?, ?, ?, ?)";

            try (PreparedStatement ps =
                    conn.prepareStatement(
                        examSql,
                        Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, title.trim());

                ps.setString(
                    2,
                    description == null
                        ? ""
                        : description.trim()
                );

                ps.setInt(3, duration);

                ps.setInt(
                    4,
                    (Integer) adminIdObject
                );

                ps.executeUpdate();

                try (ResultSet keys =
                        ps.getGeneratedKeys()) {

                    if (keys.next()) {
                        examId = keys.getInt(1);
                    } else {
                        throw new SQLException(
                            "Failed to create exam."
                        );
                    }
                }
            }

            QuestionDAO questionDAO = new QuestionDAO();

            for (int i = 0; i < questionText.length; i++) {

                if (questionText[i] == null
                        || questionText[i].trim().isEmpty()) {
                    continue;
                }

                questionDAO.addQuestion(
                    conn,
                    examId,
                    questionText[i],
                    optionA[i],
                    optionB[i],
                    optionC[i],
                    optionD[i],
                    correctOption[i]
                );
            }

            conn.commit();

            resp.sendRedirect(
                "CreateExam.html?success=Exam created successfully!"
            );

        } catch (SQLException e) {

            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {
                }
            }

            e.printStackTrace();

            resp.sendRedirect(
                "CreateExam.html?error=Failed to create exam: "
                + e.getMessage()
            );

        } finally {

            if (conn != null) {

                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }
}