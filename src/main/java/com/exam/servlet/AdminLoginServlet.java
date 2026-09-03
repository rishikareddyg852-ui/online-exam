package com.exam.servlet;

import com.exam.dao.AdminDAO;
import com.exam.model.Admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/AdminLoginServlet")
public class AdminLoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.sendRedirect("AdminLogin.html");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        try {
            AdminDAO dao = new AdminDAO();

            if (!dao.accountExists(username)) {
                resp.sendRedirect("AdminLogin.html?error=No admin account found with this username.");
                return;
            }

            Admin admin = dao.login(username, password);

            if (admin != null) {
                HttpSession session = req.getSession();
                session.setAttribute("adminId", admin.getId());
                session.setAttribute("adminName", admin.getName());
                resp.sendRedirect("AdminDashboardServlet");
                return;
            }

            resp.sendRedirect("AdminLogin.html?error=Incorrect password. Please try again.");

        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendRedirect("AdminLogin.html?error=Database error. Please check your database connection.");
        }
    }
}