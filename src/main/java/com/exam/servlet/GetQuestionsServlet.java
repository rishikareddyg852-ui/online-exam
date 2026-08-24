package com.exam.servlet;

import com.exam.dao.QuestionDAO;
import com.exam.model.Question;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/GetQuestionsServlet")
public class GetQuestionsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null ||
                session.getAttribute("adminId") == null) {

            response.getWriter().print(
                    "{\"error\":\"Admin login required.\"}"
            );
            return;
        }

        String examIdText = request.getParameter("examId");

        if (examIdText == null || examIdText.trim().isEmpty()) {

            response.getWriter().print(
                    "{\"error\":\"Exam ID is missing.\"}"
            );
            return;
        }

        int examId;

        try {
            examId = Integer.parseInt(examIdText.trim());
        } catch (NumberFormatException e) {
            response.getWriter().print(
                    "{\"error\":\"Invalid Exam ID.\"}"
            );
            return;
        }

        PrintWriter out = response.getWriter();

        try {

            QuestionDAO questionDAO = new QuestionDAO();
            List<Question> questions = questionDAO.getQuestionsByExam(examId);

            StringBuilder json = new StringBuilder();
            json.append("{\"questions\":[");

            boolean first = true;

            for (Question q : questions) {

                if (!first) {
                    json.append(",");
                }
                first = false;

                json.append("{");
                json.append("\"id\":").append(q.getId()).append(",");
                json.append("\"questionText\":\"").append(escapeJson(q.getQuestionText())).append("\",");
                json.append("\"optionA\":\"").append(escapeJson(q.getOptionA())).append("\",");
                json.append("\"optionB\":\"").append(escapeJson(q.getOptionB())).append("\",");
                json.append("\"optionC\":\"").append(escapeJson(q.getOptionC())).append("\",");
                json.append("\"optionD\":\"").append(escapeJson(q.getOptionD())).append("\",");
                json.append("\"correctOption\":\"").append(escapeJson(q.getCorrectOption())).append("\"");
                json.append("}");
            }

            json.append("]}");

            out.print(json.toString());

        } catch (SQLException e) {

            e.printStackTrace();

            out.print(
                    "{\"error\":\"Database error: " +
                    escapeJson(e.getMessage()) +
                    "\"}"
            );
        }
    }

    private String escapeJson(String text) {

        if (text == null) {
            return "";
        }

        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}