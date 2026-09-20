package com.exam.servlet;

import com.exam.dao.ExamDAO;
import com.exam.dao.QuestionDAO;
import com.exam.dao.ResultDAO;
import com.exam.model.Exam;
import com.exam.model.Question;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/ExamTakeServlet")
public class ExamTakeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // ---------------------------------------
        // 0. Check student session
        // ---------------------------------------

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("studentId") == null) {
            response.getWriter().print(
                    "{\"error\":\"Please login again.\",\"redirect\":\"index.html\"}"
            );
            return;
        }

        int studentId;

        try {
            studentId = Integer.parseInt(
                    session.getAttribute("studentId").toString()
            );
        } catch (Exception e) {
            response.getWriter().print(
                    "{\"error\":\"Please login again.\",\"redirect\":\"index.html\"}"
            );
            return;
        }

        String examIdText = request.getParameter("id");

        if (examIdText == null || examIdText.trim().isEmpty()) {
            examIdText = request.getParameter("examId");
        }

        if (examIdText == null || examIdText.trim().isEmpty()) {
            response.getWriter().print(
                    "{\"error\":\"Exam ID is missing.\"}"
            );
            return;
        }

        int examId;

        try {
            examId = Integer.parseInt(examIdText.trim());
        } catch (NumberFormatException e) {
            response.getWriter().print(
                    "{\"error\":\"Invalid Exam ID.\"}"
            );
            return;
        }

        try {

            // ---------------------------------------
            // 1. Block access if already attempted
            // ---------------------------------------

            ResultDAO resultDAO = new ResultDAO();

            if (resultDAO.hasAttempted(studentId, examId)) {
                response.getWriter().print(
                        "{\"error\":\"You have already submitted this exam.\","
                        + "\"alreadyAttempted\":true,"
                        + "\"redirect\":\"StudentResult.html?examId=" + examId + "\"}"
                );
                return;
            }

            ExamDAO examDAO = new ExamDAO();

            Exam exam = examDAO.getExamById(examId);

            if (exam == null) {
                response.getWriter().print(
                        "{\"error\":\"Exam not found.\"}"
                );
                return;
            }

            QuestionDAO questionDAO = new QuestionDAO();

            List<Question> questions =
                    questionDAO.getQuestionsByExam(examId);

            PrintWriter out = response.getWriter();

            out.print("{");

            out.print("\"id\":");
            out.print(exam.getId());

            out.print(",");

            out.print("\"title\":\"");
            out.print(escapeJson(exam.getTitle()));
            out.print("\"");

            out.print(",");

            out.print("\"description\":\"");
            out.print(escapeJson(exam.getDescription()));
            out.print("\"");

            out.print(",");

            out.print("\"duration\":");
            out.print(exam.getDurationMinutes());

            out.print(",");

            out.print("\"questions\":[");

            if (questions != null) {

                for (int i = 0; i < questions.size(); i++) {

                    Question q = questions.get(i);

                    out.print("{");

                    out.print("\"id\":");
                    out.print(q.getId());

                    out.print(",");

                    out.print("\"questionText\":\"");
                    out.print(
                            escapeJson(
                                    q.getQuestionText()
                            )
                    );
                    out.print("\"");

                    out.print(",");

                    out.print("\"optionA\":\"");
                    out.print(
                            escapeJson(
                                    q.getOptionA()
                            )
                    );
                    out.print("\"");

                    out.print(",");

                    out.print("\"optionB\":\"");
                    out.print(
                            escapeJson(
                                    q.getOptionB()
                            )
                    );
                    out.print("\"");

                    out.print(",");

                    out.print("\"optionC\":\"");
                    out.print(
                            escapeJson(
                                    q.getOptionC()
                            )
                    );
                    out.print("\"");

                    out.print(",");

                    out.print("\"optionD\":\"");
                    out.print(
                            escapeJson(
                                    q.getOptionD()
                            )
                    );
                    out.print("\"");

                    out.print("}");

                    if (i < questions.size() - 1) {
                        out.print(",");
                    }
                }
            }

            out.print("]");

            out.print("}");

        } catch (Exception e) {

            e.printStackTrace();

            response.getWriter().print(
                    "{\"error\":\"" +
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