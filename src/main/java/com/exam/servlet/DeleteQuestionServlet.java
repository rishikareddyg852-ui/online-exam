package com.exam.servlet;

import com.exam.dao.QuestionDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/DeleteQuestionServlet")
public class DeleteQuestionServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
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

        String idText = request.getParameter("id");

        if (idText == null || idText.trim().isEmpty()) {

            response.getWriter().print("Question ID is missing.");
            return;
        }

        int id;

        try {
            id = Integer.parseInt(idText.trim());
        } catch (NumberFormatException e) {
            response.getWriter().print("Invalid Question ID.");
            return;
        }

        try {

            QuestionDAO questionDAO = new QuestionDAO();
            boolean deleted = questionDAO.deleteQuestion(id);

            if (deleted) {
                response.getWriter().print("Question deleted successfully.");
            } else {
                response.getWriter().print("Question not found.");
            }

        } catch (SQLException e) {

            e.printStackTrace();

            response.getWriter().print(
                    "Database error: " + e.getMessage()
            );
        }
    }
}