package com.exam.servlet;

import com.exam.dao.ExamDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/DeleteExamServlet")
public class DeleteExamServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Check admin login
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("adminId") == null) {
            resp.sendRedirect("AdminLoginServlet");
            return;
        }

        // Get exam ID
        String idString = req.getParameter("id");

        if (idString == null || idString.trim().isEmpty()) {
            resp.sendRedirect("ManageExamServlet?error=Invalid exam ID");
            return;
        }

        try {

            int id = Integer.parseInt(idString);

            // Delete exam
            ExamDAO examDAO = new ExamDAO();
            examDAO.deleteExam(id);

            // Go back to Manage Exams
            resp.sendRedirect(
                "ManageExamServlet?success=Exam deleted successfully!"
            );

        } catch (NumberFormatException e) {

            resp.sendRedirect(
                "ManageExamServlet?error=Invalid exam ID"
            );

        } catch (SQLException e) {

            e.printStackTrace();

            resp.sendRedirect(
                "ManageExamServlet?error=Unable to delete exam"
            );
        }
    }
}