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

@WebServlet("/StudentDashboardServlet")
public class StudentDashboardServlet
        extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session =
            req.getSession(false);

        resp.setContentType(
            "application/json"
        );

        resp.setCharacterEncoding(
            "UTF-8"
        );

        PrintWriter out =
            resp.getWriter();

        if (session == null ||
            session.getAttribute("studentId") == null) {

            out.print(
                "{\"error\":\"Student login required.\"}"
            );

            return;
        }

        try {

            int studentId =
                (Integer) session.getAttribute(
                    "studentId"
                );

            String studentName =
                (String) session.getAttribute(
                    "studentName"
                );

            ExamDAO dao =
                new ExamDAO();

            List<Exam> exams =
                dao.getAvailableExamsForStudent(
                    studentId
                );

            out.print("{");

            out.print(
                "\"studentName\":\""
            );

            out.print(
                escapeJson(studentName)
            );

            out.print("\",");

            out.print("\"exams\":[");

            for (int i = 0;
                 i < exams.size();
                 i++) {

                Exam exam =
                    exams.get(i);

                out.print("{");

                out.print(
                    "\"id\":" +
                    exam.getId() +
                    ","
                );

                out.print(
                    "\"title\":\"" +
                    escapeJson(
                        exam.getTitle()
                    ) +
                    "\","
                );

                out.print(
                    "\"description\":\"" +
                    escapeJson(
                        exam.getDescription()
                    ) +
                    "\","
                );

                out.print(
                    "\"totalQuestions\":" +
                    exam.getTotalQuestions() +
                    ","
                );

                out.print(
                    "\"duration\":" +
                    exam.getDurationMinutes()
                );

                out.print("}");

                if (i < exams.size() - 1) {
                    out.print(",");
                }
            }

            out.print("]");

            out.print("}");

        } catch (SQLException e) {

            out.print(
                "{\"error\":\"Database error: " +
                escapeJson(
                    e.getMessage()
                ) +
                "\"}"
            );

        } catch (Exception e) {

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