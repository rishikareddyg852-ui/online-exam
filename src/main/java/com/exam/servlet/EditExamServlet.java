package com.exam.servlet;

import com.exam.dao.ExamDAO;
import com.exam.model.Exam;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/EditExamServlet")
public class EditExamServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;


    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session =
                req.getSession(false);


        if (session == null ||
                session.getAttribute("adminId") == null) {

            resp.setContentType(
                    "application/json"
            );

            resp.setCharacterEncoding(
                    "UTF-8"
            );

            resp.getWriter().print(
                    "{\"error\":\"Admin login required.\"}"
            );

            return;
        }


        String idText =
                req.getParameter("id");


        resp.setContentType(
                "application/json"
        );

        resp.setCharacterEncoding(
                "UTF-8"
        );


        if (idText == null ||
                idText.trim().isEmpty()) {

            resp.getWriter().print(
                    "{\"error\":\"Exam ID is missing.\"}"
            );

            return;
        }


        try {

            int id =
                    Integer.parseInt(idText);


            ExamDAO dao =
                    new ExamDAO();


            Exam exam =
                    dao.getExamById(id);


            if (exam == null) {

                resp.getWriter().print(
                        "{\"error\":\"Exam not found.\"}"
                );

                return;
            }


            PrintWriter out =
                    resp.getWriter();


            out.print("{");


            out.print("\"id\":");
            out.print(exam.getId());
            out.print(",");


            out.print("\"title\":\"");
            out.print(
                    escapeJson(
                            exam.getTitle()
                    )
            );
            out.print("\",");


            out.print("\"description\":\"");
            out.print(
                    escapeJson(
                            exam.getDescription()
                    )
            );
            out.print("\",");


            out.print("\"duration\":");
            out.print(
                    exam.getDurationMinutes()
            );


            out.print("}");


        } catch (NumberFormatException e) {

            resp.getWriter().print(
                    "{\"error\":\"Invalid exam ID.\"}"
            );


        } catch (SQLException e) {

            e.printStackTrace();

            resp.getWriter().print(
                    "{\"error\":\"Database error.\"}"
            );

        }

    }


    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {


        HttpSession session =
                req.getSession(false);


        if (session == null ||
                session.getAttribute("adminId") == null) {

            resp.setContentType(
                    "text/plain"
            );

            resp.getWriter().print(
                    "Admin login required."
            );

            return;
        }


        String idText =
                req.getParameter("id");

        String title =
                req.getParameter("title");

        String description =
                req.getParameter("description");

        String durationText =
                req.getParameter("duration");


        resp.setContentType(
                "text/plain"
        );

        resp.setCharacterEncoding(
                "UTF-8"
        );


        if (idText == null ||
                title == null ||
                description == null ||
                durationText == null) {

            resp.getWriter().print(
                    "All fields are required."
            );

            return;
        }


        if (title.trim().isEmpty() ||
                description.trim().isEmpty() ||
                durationText.trim().isEmpty()) {

            resp.getWriter().print(
                    "All fields are required."
            );

            return;
        }


        try {

            int id =
                    Integer.parseInt(idText);


            int duration =
                    Integer.parseInt(
                            durationText
                    );


            if (duration <= 0) {

                resp.getWriter().print(
                        "Duration must be greater than 0."
                );

                return;
            }


            ExamDAO dao =
                    new ExamDAO();


            boolean updated =
                    dao.updateExam(
                            id,
                            title.trim(),
                            description.trim(),
                            duration
                    );


            if (updated) {

                resp.getWriter().print(
                        "Exam updated successfully."
                );

            } else {

                resp.getWriter().print(
                        "Exam was not updated."
                );

            }


        } catch (NumberFormatException e) {

            resp.getWriter().print(
                    "Invalid ID or duration."
            );


        } catch (SQLException e) {

            e.printStackTrace();

            resp.getWriter().print(
                    "Database error."
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