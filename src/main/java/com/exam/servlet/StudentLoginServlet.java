package com.exam.servlet;

import com.exam.dao.StudentDAO;
import com.exam.model.Student;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/StudentLoginServlet")
public class StudentLoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req,
                          HttpServletResponse resp)
            throws ServletException, IOException {

        resp.sendRedirect("StudentLogin.html");
    }


    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws ServletException, IOException {

        String email =
                req.getParameter("email");

        String password =
                req.getParameter("password");


        if (email == null ||
                email.trim().isEmpty() ||
                password == null ||
                password.trim().isEmpty()) {

            resp.sendRedirect(
                    "StudentLogin.html?error=Please%20enter%20email%20and%20password"
            );

            return;
        }


        try {

            StudentDAO dao =
                    new StudentDAO();


            /*
             * Check whether account exists.
             */
            if (!dao.accountExists(email)) {

                resp.sendRedirect(
                    "StudentLogin.html?error=No%20account%20found%20with%20this%20email.%20Please%20create%20an%20account."
                );

                return;
            }


            /*
             * Check login details.
             */
            Student student =
                    dao.login(email, password);


            if (student != null) {

                HttpSession session =
                        req.getSession();


                session.setAttribute(
                        "studentId",
                        student.getId()
                );


                session.setAttribute(
                        "studentName",
                        student.getName()
                );


                /*
                 * Login successful.
                 */
                resp.sendRedirect(
                        "StudentDashboard.html"
                );

                return;

            } else {

                /*
                 * Wrong password.
                 */
                resp.sendRedirect(
                    "StudentLogin.html?error=Incorrect%20password.%20Please%20try%20again."
                );

            }


        } catch (SQLException e) {

            resp.sendRedirect(
                "StudentLogin.html?error=Database%20error"
            );
        }
    }
}