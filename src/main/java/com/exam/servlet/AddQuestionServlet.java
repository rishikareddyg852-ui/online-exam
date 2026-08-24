package com.exam.servlet;

import com.exam.dao.QuestionDAO;
import com.exam.util.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/AddQuestionServlet")
public class AddQuestionServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null ||
                session.getAttribute("adminId") == null) {

            response.getWriter().print("Admin login required.");
            return;
        }

        String examIdText = request.getParameter("examId");
        String questionText = request.getParameter("questionText");
        String optionA = request.getParameter("optionA");
        String optionB = request.getParameter("optionB");
        String optionC = request.getParameter("optionC");
        String optionD = request.getParameter("optionD");
        String correctOption = request.getParameter("correctOption");

        if (examIdText == null || questionText == null ||
                optionA == null || optionB == null ||
                optionC == null || optionD == null ||
                correctOption == null) {

            response.getWriter().print("All fields are required.");
            return;
        }

        int examId;

        try {
            examId = Integer.parseInt(examIdText.trim());
        } catch (NumberFormatException e) {
            response.getWriter().print("Invalid Exam ID.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {

            QuestionDAO questionDAO = new QuestionDAO();

            questionDAO.addQuestion(
                    conn,
                    examId,
                    questionText,
                    optionA,
                    optionB,
                    optionC,
                    optionD,
                    correctOption
            );

            response.getWriter().print("Question added successfully.");

        } catch (SQLException e) {

            e.printStackTrace();

            response.getWriter().print(
                    "Database error: " + e.getMessage()
            );
        }
    }
}