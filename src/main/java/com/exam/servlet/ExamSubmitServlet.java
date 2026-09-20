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
import java.sql.SQLException;
import java.util.List;

@WebServlet("/ExamSubmitServlet")
public class ExamSubmitServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        // ---------------------------------------
        // 1. Check student session
        // ---------------------------------------

        HttpSession session =
                req.getSession(false);

        if (session == null) {

            resp.sendRedirect("index.html");

            return;
        }


        // ---------------------------------------
        // 2. Get student ID
        // ---------------------------------------

        Object studentIdObject =
                session.getAttribute("studentId");

        if (studentIdObject == null) {

            resp.sendRedirect("index.html");

            return;
        }


        int studentId;

        try {

            studentId =
                    Integer.parseInt(
                            studentIdObject.toString()
                    );

        } catch (Exception e) {

            resp.sendRedirect("index.html");

            return;
        }


        // ---------------------------------------
        // 3. Get exam ID
        // ---------------------------------------

        String examIdText =
                req.getParameter("exam_id");

        if (examIdText == null ||
                examIdText.trim().isEmpty()) {

            examIdText =
                    req.getParameter("examId");
        }


        if (examIdText == null ||
                examIdText.trim().isEmpty()) {

            resp.sendRedirect(
                    "StudentDashboardServlet"
            );

            return;
        }


        int examId;

        try {

            examId =
                    Integer.parseInt(
                            examIdText.trim()
                    );

        } catch (NumberFormatException e) {

            resp.sendRedirect(
                    "StudentDashboardServlet"
            );

            return;
        }


        try {

            ResultDAO resultDAO =
                    new ResultDAO();


            // ---------------------------------------
            // 4. Check already submitted
            // ---------------------------------------

            if (resultDAO.hasAttempted(
                    studentId,
                    examId
            )) {

                resp.sendRedirect(
                        "StudentResult.html?examId="
                                + examId
                );

                return;
            }


            // ---------------------------------------
            // 5. Get exam
            // ---------------------------------------

            ExamDAO examDAO =
                    new ExamDAO();

            Exam exam =
                    examDAO.getExamById(
                            examId
                    );


            if (exam == null) {

                resp.sendRedirect(
                        "StudentDashboardServlet"
                );

                return;
            }


            // ---------------------------------------
            // 6. Get questions
            // ---------------------------------------

            QuestionDAO questionDAO =
                    new QuestionDAO();

            List<Question> questions =
                    questionDAO.getQuestionsByExam(
                            examId
                    );


            if (questions == null ||
                    questions.isEmpty()) {

                throw new ServletException(
                        "No questions found for this exam."
                );
            }


            // ---------------------------------------
            // 7. Calculate score
            // ---------------------------------------

            int score = 0;

            int totalMarks =
                    questions.size();


            for (Question question :
                    questions) {

                String studentAnswer =
                        req.getParameter(
                                "q_" +
                                question.getId()
                        );


                String correctAnswer =
                        question.getCorrectOption();


                if (studentAnswer != null &&
                        correctAnswer != null) {

                    studentAnswer =
                            studentAnswer.trim();

                    correctAnswer =
                            correctAnswer.trim();


                    if (studentAnswer.equalsIgnoreCase(
                            correctAnswer
                    )) {

                        score++;
                    }
                }
            }


            // ---------------------------------------
            // 8. Save result
            // ---------------------------------------

            try {

                resultDAO.saveResult(
                        studentId,
                        examId,
                        score,
                        totalMarks
                );

            } catch (SQLException e) {

                /*
                 * Database UNIQUE constraint protects
                 * against duplicate submissions.
                 */

                if (resultDAO.hasAttempted(
                        studentId,
                        examId
                )) {

                    resp.sendRedirect(
                            "StudentResult.html?examId="
                                    + examId
                    );

                    return;
                }

                throw e;
            }


            // ---------------------------------------
            // 9. Go to result page
            // ---------------------------------------

            resp.sendRedirect(
                    "StudentResult.html?examId="
                            + examId
            );


        } catch (SQLException e) {

            e.printStackTrace();

            throw new ServletException(
                    "Database error while submitting exam: "
                            + e.getMessage(),
                    e
            );


        } catch (Exception e) {

            e.printStackTrace();

            throw new ServletException(
                    "Error while submitting exam: "
                            + e.getMessage(),
                    e
            );
        }
    }
}