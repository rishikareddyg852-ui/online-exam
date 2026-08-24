package com.exam.servlet;

import com.exam.dao.AdminDAO;
import com.exam.util.HtmlUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.SQLException;

@WebServlet("/AdminRegisterServlet")
public class AdminRegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String TEMPLATE_PATH = "/adminRegister.html";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        renderPage(req, resp, null, "", "");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirm_password");

        String error = null;

        if (name == null || name.trim().isEmpty() || email == null || email.trim().isEmpty()
                || password == null || password.isEmpty()) {
            error = "Please fill all fields.";
        } else if (!password.equals(confirmPassword)) {
            error = "Passwords do not match.";
        } else {
            try {
                AdminDAO dao = new AdminDAO();
                if (dao.emailExists(email)) {
                    error = "Account already exists with this email. Please login.";
                } else {
                    int id = dao.register(name.trim(), email.trim(), password);
                    HttpSession session = req.getSession();
                    session.setAttribute("adminId", id);
                    session.setAttribute("adminName", name.trim());
                    resp.sendRedirect("AdminDashboardServlet");
                    return;
                }
            } catch (SQLException e) {
                error = "Database error: " + e.getMessage();
            }
        }

        renderPage(req, resp, error, name, email);
    }

    private void renderPage(HttpServletRequest req, HttpServletResponse resp,
                             String error, String name, String email) throws IOException {

        String templatePath = getServletContext().getRealPath(TEMPLATE_PATH);
        String html = new String(Files.readAllBytes(new File(templatePath).toPath()), StandardCharsets.UTF_8);

        String errorBlock = "";
        if (error != null) {
            errorBlock = "<div class=\"msg error\">" + HtmlUtil.escape(error) + "</div>";
        }

        html = html.replace("<!--ERROR_BLOCK-->", errorBlock);
        html = html.replace("__NAME__", HtmlUtil.escape(name == null ? "" : name));
        html = html.replace("__EMAIL__", HtmlUtil.escape(email == null ? "" : email));

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.print(html);
        out.flush();
    }
}