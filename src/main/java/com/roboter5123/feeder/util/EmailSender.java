package com.roboter5123.feeder.util;

import com.roboter5123.feeder.databaseobject.AccessToken;
import com.roboter5123.feeder.databaseobject.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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

    public void verificationMail(User user, AccessToken accessToken){

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("automatic.feeder.service@gmail.com");
        message.setTo(user.getEmail());
        message.setSubject("Email verification for your feeder account");
//        TODO: Change url
        message.setText("Please follow the following link to activate your feeder account:"+ url + "/"+accessToken.getToken()+"/verify");
        mailSender.send(message);
    }
}
