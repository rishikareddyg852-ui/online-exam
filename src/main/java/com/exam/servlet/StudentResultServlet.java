package com.exam.servlet;

import com.exam.dao.ResultDAO;
import com.exam.model.Result;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/StudentResultServlet")
public class StudentResultServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session =
                request.getSession(false);

        if (session == null) {

            response.sendRedirect("index.html");
            return;
        }

        Object studentIdObject =
                session.getAttribute("studentId");

        if (studentIdObject == null) {

            response.sendRedirect("index.html");
            return;
        }

        int studentId;

        try {

            studentId =
                    Integer.parseInt(
                            studentIdObject.toString()
                    );

        } catch (Exception e) {

            response.sendRedirect("index.html");
            return;
        }

        try {

            ResultDAO resultDAO =
                    new ResultDAO();

            List<Result> results =
                    resultDAO.getResultsForStudent(
                            studentId
                    );

            response.setContentType(
                    "application/json"
            );

            response.setCharacterEncoding(
                    "UTF-8"
            );

            PrintWriter out =
                    response.getWriter();

            out.print("{\"results\":[");

            for (int i = 0;
                 i < results.size();
                 i++) {

                Result r =
                        results.get(i);

                out.print("{");

                out.print("\"examTitle\":\"");
                out.print(
                        escapeJson(
                                r.getExamTitle()
                        )
                );
                out.print("\"");

                out.print(",");

                out.print("\"score\":");
                out.print(r.getScore());

                out.print(",");

                out.print("\"totalMarks\":");
                out.print(r.getTotalMarks());

                out.print(",");

                out.print("\"percentage\":");
                out.print(r.getPercentage());

                out.print(",");

                out.print("\"submittedAt\":\"");
                out.print(
                        escapeJson(
                                r.getSubmittedAt()
                        )
                );
                out.print("\"");

                out.print("}");

                if (i < results.size() - 1) {
                    out.print(",");
                }
            }

            out.print("]}");

        } catch (SQLException e) {

            e.printStackTrace();

            response.setContentType(
                    "application/json"
            );

            response.getWriter().print(
                    "{\"error\":\"Database error\"}"
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