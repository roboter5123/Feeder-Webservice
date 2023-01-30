package com.roboter5123.feeder.util;
import com.roboter5123.feeder.model.AccessToken;
import com.roboter5123.feeder.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * Used to send various kinds of emails needed in the process of using the API
 */
@Component
public class EmailSender{

    @Value("${server.url}")
    String url;
    JavaMailSender mailSender;
    @Value("${email.password}")
    String password;

    @Autowired
    public EmailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends a verification email so the user can activate their account
     * @param user The user to send the email to
     * @param accessToken Used to identify the user in the verification link
     * @throws MessagingException Thrown if the email doesn't exist anymore
     */
    public void verificationMail(User user, AccessToken accessToken) throws MessagingException {

        String htmlMsg = "<p>Please click this link to activate your account: <a href=\""+url+"/"+accessToken.getToken()+"/verify\">Link</a></p>";
        String subject = "Activate your automatic feeder account";
        sendMail(user.getEmail(), htmlMsg, subject);
    }

    /**
     * Sends a password reset email so the user can reactivate their account
     * @param user The user to send the email to
     * @param token Used to identify the user in the verification link
     * @throws MessagingException Thrown if the email doesn't exist anymore
     */
    public void resetMail(User user, AccessToken token) throws MessagingException {

        String htmlMsg = "<p>Please click this link to change your password: <a href=\""+url+"/"+token.getToken()+"/resetPassword\">Link</a></p>";
        String subject = "Reset your feeder account password";
        sendMail(user.getEmail(), htmlMsg, subject);
    }

    public void sendMail(String email, String htmlMessage, String subject) throws MessagingException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        helper.setText(htmlMessage, true);
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setFrom("automatic.feeder.service@gmail.com");
        mailSender.send(mimeMessage);
    }
}
