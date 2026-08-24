package com.exam.servlet;

import com.exam.dao.ExamDAO;
import com.exam.model.Exam;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/ManageExamServlet")
public class ManageExamServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req,
                          HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session =
                req.getSession(false);


        if (session == null ||
            session.getAttribute("adminId") == null) {

            resp.sendRedirect("AdminLoginServlet");
            return;
        }


        resp.setContentType(
            "application/json"
        );

        resp.setCharacterEncoding(
            "UTF-8"
        );


        try {

            ExamDAO examDAO =
                    new ExamDAO();


            List<Exam> exams =
                    examDAO.getAllExams();


            PrintWriter out =
                    resp.getWriter();


            out.print("[");


            for (int i = 0;
                 i < exams.size();
                 i++) {

                Exam ex =
                        exams.get(i);


                out.print("{");


                out.print("\"id\":");
                out.print(ex.getId());
                out.print(",");


                out.print("\"title\":\"");

                out.print(
                    escapeJson(
                        ex.getTitle()
                    )
                );

                out.print("\",");


                out.print(
                    "\"totalQuestions\":"
                );

                out.print(
                    ex.getTotalQuestions()
                );

                out.print(",");


                out.print("\"duration\":");

                out.print(
                    ex.getDurationMinutes()
                );

                out.print(",");


                out.print("\"attempts\":");

                out.print(
                    ex.getTotalAttempts()
                );


                out.print("}");


                if (i < exams.size() - 1) {
                    out.print(",");
                }

            }


            out.print("]");

            out.flush();


        } catch (SQLException e) {

            PrintWriter out =
                    resp.getWriter();

            out.print(
                "{\"error\":\"" +
                escapeJson(
                    e.getMessage()
                ) +
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