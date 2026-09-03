package com.exam.servlet;

import com.exam.dao.StudentDAO;
import com.exam.util.EmailUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/StudentRegisterServlet")
public class StudentRegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.sendRedirect("StudentRegister.html");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirm_password");

        if (name == null || name.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                username == null || username.trim().isEmpty() ||
                password == null || password.isEmpty() ||
                confirmPassword == null || confirmPassword.isEmpty()) {
            resp.sendRedirect("StudentRegister.html?error=Please%20fill%20all%20fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            resp.sendRedirect("StudentRegister.html?error=Passwords%20do%20not%20match.");
            return;
        }

        try {
            StudentDAO dao = new StudentDAO();

            if (dao.emailExists(email.trim())) {
                resp.sendRedirect("StudentRegister.html?error=Account%20already%20exists%20with%20this%20email.%20Please%20login.");
                return;
            }

            if (dao.usernameExists(username.trim())) {
                resp.sendRedirect("StudentRegister.html?error=Username%20already%20taken.%20Please%20choose%20another.");
                return;
            }

            int id = dao.register(name.trim(), email.trim(), username.trim(), password);

            EmailUtil.sendCredentials(email.trim(), name.trim(), username.trim(), password);

            HttpSession session = req.getSession();
            session.setAttribute("studentId", id);
            session.setAttribute("studentName", name.trim());

            resp.sendRedirect("StudentRegisterSuccess.html?username=" +
                    java.net.URLEncoder.encode(username.trim(), "UTF-8"));

        } catch (SQLException e) {
            resp.sendRedirect("StudentRegister.html?error=Database%20error");
        }
    }
}