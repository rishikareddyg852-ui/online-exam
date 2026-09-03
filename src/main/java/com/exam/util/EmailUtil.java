package com.exam.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtil {

    private static final String SENDER_EMAIL = "rishikareddyg852@gmail.com";
    private static final String SENDER_APP_PASSWORD = "xcaafdzbgeoywuid";

    public static void sendCredentials(String toEmail, String name, String username, String password) {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_APP_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Your Online Examination System Login Details");

            String body = "Hello " + name + ",\n\n"
                    + "Your account has been created successfully.\n\n"
                    + "Username: " + username + "\n"
                    + "Password: " + password + "\n\n"
                    + "Please keep these details safe. You will need them to log in.\n\n"
                    + "Regards,\nOnline Examination System";

            message.setText(body);

            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}