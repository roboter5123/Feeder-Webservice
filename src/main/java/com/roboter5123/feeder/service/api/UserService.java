package com.roboter5123.feeder.service.api;

import com.roboter5123.feeder.controller.DatabaseController;
import com.roboter5123.feeder.exception.BadRequestException;
import com.roboter5123.feeder.exception.GoneException;
import com.roboter5123.feeder.exception.InternalErrorException;
import com.roboter5123.feeder.exception.UnauthorizedException;
import com.roboter5123.feeder.model.AccessToken;
import com.roboter5123.feeder.model.LoginData;
import com.roboter5123.feeder.model.User;
import com.roboter5123.feeder.util.EmailSender;
import com.roboter5123.feeder.util.MakeAbstractRequest;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * Api used to manage users and access tokens
 * @author roboter5123
 */
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

    /**
     * creates an access token to authenticate and authorize a user with
     * @param loginData must include email and password. Everything else is not needed.
     * @param response used to send back a cookie with the access token
     * @return an access token the user can use to authenticate for using the api
     */
    @PostMapping(value = "/api/access-token")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public AccessToken createAccessToken(@RequestBody LoginData loginData, HttpServletResponse response) {

        User user = loginData.toUser();
        User databaseUser = databaseController.findByLoginData(loginData);

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

    /**
     * Generates and saves an access token
     * @return a new access token that's saved in the database
     */
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

    /**
     * Gets an access token from the database for checking the expiration date
     * @param accessToken the access token to find
     * @return the access token that was found
     */
    @GetMapping(value = "/api/access-token")
    @ResponseStatus(HttpStatus.OK)
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

    /**
     * Deletes an access token from the database thus cutting authorization for the user
     * @param accessToken the access token to delete
     * @param response used to update the users cookie with an expired one
     */
    @DeleteMapping(value = "/api/access-token")
    @ResponseStatus(HttpStatus.OK)
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

    /**
     * Used to register a user to the service
     * @param loginData must include email and password. Everything else is not needed.
     * @throws MessagingException thrown when the email address couldn't be delivered to
     */
    @PostMapping(value = "/api/user")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void postUser(@RequestBody LoginData loginData) throws MessagingException {

        User user = loginData.toUser();
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

    /**
     * @return salt for salting is salty
     */
    private byte[] generateSalt() {

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * @param password to hash and salt
     * @param salt to salt the hash with
     * @return base 64 encoded salted and hashed password
     * @throws NoSuchAlgorithmException Never really thrown but has to be declared
     */
    private String saltAndHashPassword(String password, byte[] salt) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(salt);
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        byte[] hashedPassword = md.digest(passwordBytes);
        return Base64.getEncoder().encodeToString(hashedPassword);
    }

    /**
     * used to check if an email is already registered to the service
     * @param user only needs to include an email
     * @return registration status of email
     */
    private boolean isEmailRegistered(User user) {

        return databaseController.findByEmail(user.getEmail()) != null;
    }

    /**
     * I don't know what this was used for or how it was supposed to verify the user
     * @deprecated
     */
    @PutMapping(value = "/api/user/verify")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Deprecated(since = "idk")
    public User verifyUser(@RequestParam AccessToken token) throws MessagingException {

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

    /**
     * Sends a password reset email to the user
     * @param loginData must include email
     * @return the user whose password was changed
     * @throws MessagingException thrown if the email isn't deliverable
     */
    @PostMapping(value = "/api/user/resetPassword")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public User resetPassword(@RequestBody LoginData loginData) throws MessagingException {

        User user = databaseController.findByEmail(loginData.getEmail());
        user.setActivated(false);
        AccessToken accessToken = generateAccessToken();
        user.setAccessToken(accessToken);
        databaseController.save(user);
        emailSender.resetMail(user,accessToken);
        return user;
    }

    /**
     * Sets the password of the given user to the given password
     * @param token used to authenricate the user
     * @param password to change
     * @return the user which has been changed
     * @throws NoSuchAlgorithmException Never really thrown but has to be declared
     */
    @PutMapping(value = "/api/user/resetPassword")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public User resetPassword(@RequestParam AccessToken token, @RequestBody String password) throws NoSuchAlgorithmException {

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
