package com.roboter5123.feeder.service.api;

import com.roboter5123.feeder.controller.DatabaseController;
import com.roboter5123.feeder.model.AccessToken;
import com.roboter5123.feeder.model.User;
import com.roboter5123.feeder.exception.BadRequestException;
import com.roboter5123.feeder.exception.GoneException;
import com.roboter5123.feeder.exception.InternalErrorException;
import com.roboter5123.feeder.exception.UnauthorizedException;
import com.roboter5123.feeder.util.EmailSender;
import com.roboter5123.feeder.util.MakeAbstractRequest;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@RestController
public class UserService {

    private final DatabaseController databaseController;
    private final EmailSender emailSender;
    @Value("${encrypted.property}")
    private String abstractAPIKey;


    @Autowired
    public UserService(DatabaseController databaseController, EmailSender emailSender) {

        this.databaseController = databaseController;
        this.emailSender = emailSender;
    }

    @RequestMapping(value = "/api/access-token", method = RequestMethod.POST)
    @ResponseBody
    public AccessToken createAccessToken(@RequestBody User user, HttpServletResponse response) {

        User databaseUser = databaseController.findByEmail(user.getEmail());

        if (databaseUser == null) {

            throw new UnauthorizedException();
        }

        user.setSalt(databaseUser.getSalt());

        if (!databaseUser.getActivated()) {

            throw new UnauthorizedException();
        }

        try {

            user.setPassword(saltAndHashPassword(user.getPassword(), user.getSalt()));

        } catch (NoSuchAlgorithmException e) {

            throw new InternalErrorException();

        } catch (NullPointerException e) {

            throw new UnauthorizedException();
        }

        if (!user.getPassword().equals(databaseUser.getPassword())) {

            throw new UnauthorizedException();
        }

        AccessToken accessToken = generateAccessToken();
        databaseController.save(accessToken);

        if (databaseUser.getAccessToken() != null) {

            databaseController.delete(databaseUser.getAccessToken());
        }

        databaseUser.setAccessToken(accessToken);
        databaseController.save(databaseUser);

        Cookie cookie = new Cookie("access-token", accessToken.getToken());
        cookie.setMaxAge(24 * 60 * 60);
        response.addCookie(cookie);

        return accessToken;
    }

    public AccessToken generateAccessToken() {

        SecureRandom secureRandom = new SecureRandom();
        Base64.Encoder base64Encoder = Base64.getUrlEncoder();
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        AccessToken accessToken = new AccessToken();
        accessToken.setToken(base64Encoder.encodeToString(randomBytes));
        accessToken.setExpires(LocalDateTime.now().plusDays(1));
        databaseController.save(accessToken);
        return accessToken;
    }

    @RequestMapping(value = "/api/access-token", method = RequestMethod.GET)
    public AccessToken retrieveAccessToken(@CookieValue(name = "access-token") AccessToken accessToken) {

        accessToken = databaseController.findByToken(accessToken.getToken());

        if (accessToken == null) {

            throw new UnauthorizedException();
        }

        if (accessToken.getExpires().isBefore(LocalDateTime.now())) {

            databaseController.delete(accessToken);
            throw new UnauthorizedException();
        }

        return accessToken;
    }

    @RequestMapping(value = "/api/access-token", method = RequestMethod.DELETE)
    public void deleteAccessToken(@CookieValue(name = "access-token") AccessToken accessToken, HttpServletResponse response) {

        accessToken = databaseController.findByToken(accessToken.getToken());

        if (accessToken == null) {

            throw new GoneException();
        }

        databaseController.delete(accessToken);

        Cookie cookie = new Cookie("access-token", accessToken.getToken());
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    @RequestMapping(value = "/api/user", method = RequestMethod.POST)
    @ResponseBody
    public void postUser(@RequestBody User user) throws MessagingException {

        if (!MakeAbstractRequest.checkEmail(user.getEmail(), abstractAPIKey)) {

            throw new BadRequestException();
        }

        if (isEmailRegistered(user)) {

            throw new UnauthorizedException();
        }

        byte[] salt = generateSalt();

        try {

            user.setPassword(saltAndHashPassword(user.getPassword(), salt));
            user.setSalt(salt);

        } catch (NoSuchAlgorithmException e) {

            throw new InternalErrorException();
        }

        user.setActivated(false);
        AccessToken accessToken = generateAccessToken();
        emailSender.verificationMail(user, accessToken);
        databaseController.save(accessToken);
        user.setAccessToken(accessToken);
        databaseController.save(user);
    }

    private byte[] generateSalt() {

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    private String saltAndHashPassword(String password, byte[] salt) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(salt);
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        byte[] hashedPassword = md.digest(passwordBytes);
        return Base64.getEncoder().encodeToString(hashedPassword);
    }

    private boolean isEmailRegistered(User user) {

        return databaseController.findByEmail(user.getEmail()) != null;
    }

    @RequestMapping(value = "/api/user/verify", method = RequestMethod.PUT)
    @ResponseBody
    private User verifyUser(@RequestParam AccessToken token) throws MessagingException {

        User user = databaseController.findByAccessToken(token);
        user.setActivated(true);
        databaseController.save(user);
        databaseController.delete(token);
        token = generateAccessToken();
        emailSender.resetMail(user, token);
        user.setAccessToken(token);
        databaseController.save(user);
        return user;
    }

    @RequestMapping(value = "/api/user/resetPassword", method = RequestMethod.POST)
    @ResponseBody
    private User resetPassword(@RequestBody User user) throws MessagingException {

        user = databaseController.findByEmail(user.getEmail());
        user.setActivated(false);
        AccessToken accessToken = generateAccessToken();
        user.setAccessToken(accessToken);
        databaseController.save(user);
        emailSender.resetMail(user,accessToken);
        return user;
    }

    @RequestMapping(value = "/api/user/resetPassword", method = RequestMethod.PUT)
    @ResponseBody
    private User resetPassword(@RequestParam AccessToken token, @RequestBody String password) throws NoSuchAlgorithmException {

        User user = databaseController.findByAccessToken(token);
        password = password.strip();

        if (user == null){

            throw new GoneException();
        }

        if (password == null || password.equals("")){

            throw new BadRequestException();
        }

        if (user.getActivated()){

            throw new UnauthorizedException();
        }

        byte[] salt = user.getSalt();

        if (salt == null){

            throw new InternalErrorException();
        }

        password = saltAndHashPassword(password, salt);
        user.setPassword(password);
        user.setActivated(true);
        databaseController.delete(token);
        databaseController.save(user);
        return user;
    }}
