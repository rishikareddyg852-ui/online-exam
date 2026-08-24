package com.exam.util;

public class HtmlUtil {

    // Standard page header - every servlet-generated page starts with this
    public static String header(String title) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n");
        sb.append("<meta charset=\"UTF-8\">\n");
        sb.append("<title>").append(escape(title)).append("</title>\n");
        sb.append("<link rel=\"stylesheet\" href=\"css/style.css\">\n");
        sb.append("</head>\n<body>\n");
        return sb.toString();
    }

    // Standard page footer
    public static String footer() {
        return "</body>\n</html>";
    }

    // Escapes special HTML characters to prevent broken markup / XSS
    public static String escape(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}