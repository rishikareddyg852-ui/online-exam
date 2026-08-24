package com.exam.servlet;

import com.exam.dao.ExamDAO;
import com.exam.dao.ResultDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;

@WebServlet("/AdminDashboardServlet")
public class AdminDashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req,
                          HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        if (session == null ||
            session.getAttribute("adminId") == null) {

            resp.sendRedirect("AdminLoginServlet");
            return;
        }

        int examCount = 0;
        int studentCount = 0;
        int resultCount = 0;

        String error = null;

        try {

            ExamDAO examDAO = new ExamDAO();
            ResultDAO resultDAO = new ResultDAO();

            examCount = examDAO.countExams();
            studentCount = resultDAO.countStudents();
            resultCount = resultDAO.countResults();

        } catch (SQLException e) {

            error = "Database error: " + e.getMessage();
        }

        String adminName =
                (String) session.getAttribute("adminName");

        StringBuilder url =
                new StringBuilder("AdminDashboard.html?");

        url.append("name=")
           .append(encode(adminName));

        url.append("&exams=")
           .append(examCount);

        url.append("&students=")
           .append(studentCount);

        url.append("&results=")
           .append(resultCount);

        if (error != null) {

            url.append("&error=")
               .append(encode(error));
        }

        resp.sendRedirect(url.toString());
    }

    private String encode(String value) {

        if (value == null) {
            return "";
        }

        try {

            return URLEncoder.encode(value, "UTF-8");

        } catch (UnsupportedEncodingException e) {

            return "";
        }
    }
}