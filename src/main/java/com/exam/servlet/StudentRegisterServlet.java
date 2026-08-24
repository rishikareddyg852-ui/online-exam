package com.exam.servlet;

import com.exam.dao.StudentDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/StudentRegisterServlet")
public class StudentRegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req,
                          HttpServletResponse resp)
            throws ServletException, IOException {

        resp.sendRedirect("StudentRegister.html");
    }


    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws ServletException, IOException {

        String name =
                req.getParameter("name");

        String email =
                req.getParameter("email");

        String password =
                req.getParameter("password");

        String confirmPassword =
                req.getParameter("confirm_password");


        /*
         * Check empty fields.
         */
        if (name == null ||
                name.trim().isEmpty() ||
                email == null ||
                email.trim().isEmpty() ||
                password == null ||
                password.isEmpty() ||
                confirmPassword == null ||
                confirmPassword.isEmpty()) {

            resp.sendRedirect(
                "StudentRegister.html?error=Please%20fill%20all%20fields."
            );

            return;
        }


        /*
         * Check passwords.
         */
        if (!password.equals(confirmPassword)) {

            resp.sendRedirect(
                "StudentRegister.html?error=Passwords%20do%20not%20match."
            );

            return;
        }


        try {

            StudentDAO dao =
                    new StudentDAO();


            /*
             * Check email already exists.
             */
            if (dao.emailExists(email.trim())) {

                resp.sendRedirect(
                    "StudentRegister.html?error=Account%20already%20exists%20with%20this%20email.%20Please%20login."
                );

                return;
            }


            /*
             * Register student.
             */
            int id =
                    dao.register(
                        name.trim(),
                        email.trim(),
                        password
                    );


            /*
             * Create session.
             */
            HttpSession session =
                    req.getSession();


            session.setAttribute(
                    "studentId",
                    id
            );


            session.setAttribute(
                    "studentName",
                    name.trim()
            );


            /*
             * Registration successful.
             */
            resp.sendRedirect(
                    "StudentDashboard.html"
            );

        } catch (SQLException e) {

            resp.sendRedirect(
                "StudentRegister.html?error=Database%20error"
            );
        }
    }
}