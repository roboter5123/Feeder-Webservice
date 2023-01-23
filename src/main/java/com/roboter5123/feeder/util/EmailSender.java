package com.roboter5123.feeder.util;
import com.roboter5123.feeder.databaseobject.AccessToken;
import com.roboter5123.feeder.databaseobject.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

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

    public void verificationMail(User user, AccessToken accessToken) throws MessagingException {

        String htmlMsg = "<p>Please click this link to activate your account: <a href=\""+url+"/"+accessToken.getToken()+"/verify\">Link</a></p>";
        String subject = "Activate your automatic feeder account";
        sendMail(user.getEmail(), htmlMsg, subject);
    }

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
