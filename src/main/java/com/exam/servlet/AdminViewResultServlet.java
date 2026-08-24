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

@WebServlet("/AdminViewResultServlet")
public class AdminViewResultServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null ||
                session.getAttribute("adminId") == null) {

            response.getWriter().print(
                    "{\"error\":\"Admin login required.\"}"
            );
            return;
        }

        PrintWriter out = response.getWriter();

        try {

            ResultDAO resultDAO = new ResultDAO();
            List<Result> results = resultDAO.getAllResults();

            StringBuilder json = new StringBuilder();
            json.append("{\"results\":[");

            boolean first = true;

            for (Result r : results) {

                if (!first) {
                    json.append(",");
                }
                first = false;

                int score = r.getScore();
                int totalMarks = r.getTotalMarks();

                double percentage = totalMarks > 0
                        ? (score * 100.0 / totalMarks)
                        : 0;

                json.append("{");
                json.append("\"studentName\":\"").append(escapeJson(r.getStudentName())).append("\",");
                json.append("\"examTitle\":\"").append(escapeJson(r.getExamTitle())).append("\",");
                json.append("\"score\":").append(score).append(",");
                json.append("\"totalMarks\":").append(totalMarks).append(",");
                json.append("\"percentage\":").append(Math.round(percentage)).append(",");
                json.append("\"submittedOn\":\"").append(escapeJson(r.getSubmittedAt())).append("\"");
                json.append("}");
            }

            json.append("]}");

            out.print(json.toString());

        } catch (SQLException e) {

            e.printStackTrace();

            out.print(
                    "{\"error\":\"Database error: " +
                    escapeJson(e.getMessage()) +
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