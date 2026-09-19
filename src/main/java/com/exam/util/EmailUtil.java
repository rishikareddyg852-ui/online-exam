package com.exam.util;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class EmailUtil {

    private static String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }

    private static final String BREVO_API_KEY = getEnv("BREVO_API_KEY", "");
    private static final String SENDER_EMAIL = getEnv("SENDER_EMAIL", "onlineexamsystem26@gmail.com");
    private static final String SENDER_NAME = "Online Examination System";

    public static void sendCredentials(String toEmail, String name, String username, String password) {

        try {
            URL url = new URL("https://api.brevo.com/v3/smtp/email");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("accept", "application/json");
            conn.setRequestProperty("api-key", BREVO_API_KEY);
            conn.setRequestProperty("content-type", "application/json");
            conn.setDoOutput(true);

            String bodyText = "Hello " + name + ",\\n\\n"
                    + "Your account has been created successfully.\\n\\n"
                    + "Username: " + username + "\\n"
                    + "Password: " + password + "\\n\\n"
                    + "Please keep these details safe. You will need them to log in.\\n\\n"
                    + "Regards,\\nOnline Examination System";

            String jsonPayload = "{"
                    + "\"sender\":{\"name\":\"" + SENDER_NAME + "\",\"email\":\"" + SENDER_EMAIL + "\"},"
                    + "\"to\":[{\"email\":\"" + toEmail + "\",\"name\":\"" + name + "\"}],"
                    + "\"subject\":\"Your Online Examination System Login Details\","
                    + "\"textContent\":\"" + bodyText + "\""
                    + "}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Brevo API response code: " + responseCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}