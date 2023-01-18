package com.roboter5123.feeder.service;

import com.roboter5123.feeder.controller.DatabaseController;
import com.roboter5123.feeder.controller.SocketController;
import com.roboter5123.feeder.databaseobject.*;
import com.roboter5123.feeder.exception.*;
import com.roboter5123.feeder.util.Weekday;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
public class FeederService {

    private final DatabaseController databaseController;
    private final SocketController socketController;

    @Autowired
    public FeederService(DatabaseController databaseController, SocketController socketController) {

        this.databaseController = databaseController;
        this.socketController = socketController;
    }

    //=======================================================================================================AccessToken
    @RequestMapping(value = "/api/access-token", method = RequestMethod.POST)
    @ResponseBody
    public AccessToken createAccessToken(@RequestBody User user, HttpServletResponse response) {

        User databaseUser = databaseController.findByEmail(user.getEmail());

        if (databaseUser == null) {

            throw new UnauthorizedException();
        }

        user.setSalt(databaseUser.getSalt());

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

    //==============================================================================================================User

    @RequestMapping(value = "/api/user", method = RequestMethod.POST)
    @ResponseBody
    public void postUser(@RequestBody User user) {

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

    //============================================================================================================Feeder
    @RequestMapping(value = "/api/feeder", method = RequestMethod.POST)
    public List<Feeder> registerFeeder(@CookieValue(name = "access-token") AccessToken accessToken, @RequestParam UUID uuid) {

        User user = databaseController.findByAccessToken(accessToken);

        if (user == null) {

            throw new UnauthorizedException();
        }

        List<Feeder> feeders = user.getFeeders();

        Feeder feeder = databaseController.findByUuid(uuid);

        if (feeder == null) {

            feeder = new Feeder(uuid);
        }

        if (feeders.contains(feeder)) {

            throw new ConflictException();
        }

        if (feeder.getSchedule() == null) {

            Schedule schedule = databaseController.findByScheduleId(1);
        }

        databaseController.save(feeder);
        feeders.add(feeder);
        user.setFeeders(feeders);
        databaseController.save(user);
        return feeders;
    }

    @RequestMapping(value = "/api/feeder", method = RequestMethod.GET)
    public Feeder getFeeder(@CookieValue(name = "access-token") AccessToken accessToken, @RequestParam UUID uuid) {

        User user = databaseController.findByAccessToken(accessToken);

        if (user == null) {

            throw new UnauthorizedException();
        }

        Feeder feeder = user.getFeeder(uuid);

        if (feeder == null) {

            throw new GoneException();
        }

        return feeder;
    }

    @RequestMapping(value = "/api/feeders", method = RequestMethod.GET)
    public List<Feeder> getFeeders(@CookieValue(name = "access-token") AccessToken accessToken) {

        User user = databaseController.findByAccessToken(accessToken);

        if (user == null) {

            throw new UnauthorizedException();
        }

        List<Feeder> feeders = user.getFeeders();

        if (feeders == null) {

            throw new GoneException();
        }

        return feeders;
    }

    @RequestMapping(value = "/api/feeder", method = RequestMethod.DELETE)
    public void deleteFeeders(@CookieValue(name = "access-token") AccessToken accessToken, @RequestParam UUID uuid) {

        User user = databaseController.findByAccessToken(accessToken);

        if (user == null) {

            throw new UnauthorizedException();
        }

        Feeder feeder = user.getFeeder(uuid);

        if (feeder == null) {

            throw new GoneException();
        }

        databaseController.delete(feeder);
        socketController.updateFeeder(feeder.getUuid(), feeder);
    }

    @RequestMapping(value = "/api/feeder", method = RequestMethod.PUT)
    public Feeder changeFeeder(@CookieValue(name = "access-token") AccessToken accessToken, @RequestParam UUID
            uuid, @RequestParam(required = false) String feederName, @RequestParam(required = false) String scheduleName) {

        if (feederName == null && scheduleName == null) {

            throw new BadRequestException();
        }

        User user = databaseController.findByAccessToken(accessToken);

        if (user == null) {

            throw new UnauthorizedException();
        }

        Feeder feeder = user.getFeeder(uuid);

        if (feeder == null) {

            throw new UnauthorizedException();
        }

        if (feederName != null) {

            feeder.setName(feederName);
        }

        if (scheduleName != null) {

            Schedule newSchedule = databaseController.findByScheduleName(scheduleName);
            Schedule oldSchedule = feeder.getSchedule();

            if (newSchedule == null) {

                throw new BadRequestException();
            }

            if (oldSchedule == null) {

                feeder.setSchedule(newSchedule);

            } else if (!Objects.equals(oldSchedule.getName(), newSchedule.getName())) {

                feeder.setSchedule(newSchedule);
            }
        }

        databaseController.save(feeder);
        socketController.updateFeeder(feeder.getUuid(), feeder);
        return feeder;
    }

    //==========================================================================================================Schedule

    @RequestMapping(value = "/api/schedule", method = RequestMethod.POST)
    public List<Schedule> createSchedule(@CookieValue(name = "access-token") AccessToken
                                                 accessToken, @RequestParam String name) {

        User user = databaseController.findByAccessToken(accessToken);
        name = name.strip();

        if (user == null) {

            throw new UnauthorizedException();
        }

        Schedule schedule = new Schedule(name);

        if (user.getSchedule(name) != null) {

            throw new ConflictException();
        }

        for (Feeder feeder : user.getFeeders()) {

            if (feeder.getSchedule() == null){

                feeder.setSchedule(schedule);
                socketController.updateFeeder(feeder.getUuid(), feeder);
                databaseController.save(schedule);
                databaseController.save(feeder);
            }
        }

        databaseController.save(schedule);
        user.addSchedule(schedule);
        databaseController.save(user);
        return user.getSchedules();
    }

    @RequestMapping(value = "/api/schedule", method = RequestMethod.GET)
    public Schedule retrieveSchedule(@CookieValue(name = "access-token") AccessToken
                                             accessToken, @RequestParam String name) {

        User user = databaseController.findByAccessToken(accessToken);

        if (user == null) {

            throw new UnauthorizedException();
        }

        Schedule schedule = user.getSchedule(name);

        if (schedule == null) {

            throw new GoneException();
        }

        return schedule;
    }

    @RequestMapping(value = "/api/schedules", method = RequestMethod.GET)
    public List<Schedule> retrieveSchedules(@CookieValue(name = "access-token") AccessToken accessToken) {

        User user = databaseController.findByAccessToken(accessToken);

        if (user == null) {

            throw new UnauthorizedException();
        }

        return user.getSchedules();
    }

    @RequestMapping(value = "/api/schedule", method = RequestMethod.DELETE)
    public void deleteSchedule(@CookieValue(name = "access-token") AccessToken accessToken, @RequestParam String
            name) {

        User user = databaseController.findByAccessToken(accessToken);

        if (user == null) {

            throw new UnauthorizedException();
        }

        Schedule schedule = user.getSchedule(name);

        if (schedule == null) {

            throw new GoneException();
        }

        for (Feeder feeder : databaseController.findBySchedule(schedule)) {

            socketController.updateFeeder(feeder.getUuid(), feeder);
        }

        databaseController.delete(schedule);
    }

    @RequestMapping(value = "/api/schedule", method = RequestMethod.PUT)
    public Schedule changeFeeder(@CookieValue(name = "access-token") AccessToken accessToken, @RequestParam String
            oldName, @RequestParam String newName) {

        User user = databaseController.findByAccessToken(accessToken);
        newName = newName.strip();

        if (user == null) {

            throw new UnauthorizedException();
        }

        Schedule schedule = user.getSchedule(oldName);

        if (schedule == null) {

            throw new GoneException();
        }

        if (Objects.equals(oldName, newName) || user.getSchedule(newName) != null) {

            throw new BadRequestException();
        }

        schedule.setName(newName.trim());
        databaseController.save(schedule);

        for (Feeder feeder : databaseController.findBySchedule(schedule)) {

            socketController.updateFeeder(feeder.getUuid(), feeder);
        }

        return schedule;
    }

    //==============================================================================================================Task

    @RequestMapping(value = "/api/task", method = RequestMethod.POST)
    public Schedule createTask(@CookieValue(name = "access-token") AccessToken accessToken, @RequestParam String
            scheduleName, @RequestBody Task task) {

        User user = databaseController.findByAccessToken(accessToken);

        if (user == null) {

            throw new UnauthorizedException();
        }

        Schedule schedule = user.getSchedule(scheduleName);

        if (schedule == null) {

            throw new GoneException();
        }

        if (task.isInValid()) {

            throw new BadRequestException();
        }

        if (schedule.getTasks().contains(task)) {

            throw new ConflictException();
        }


        databaseController.save(task);
        schedule.addTask(task);
        databaseController.save(schedule);
        user.addSchedule(schedule);
        databaseController.save(user);

        for (Feeder feeder : databaseController.findBySchedule(schedule)) {

            socketController.updateFeeder(feeder.getUuid(), feeder);
        }

        return user.getSchedule(scheduleName);
    }

    @RequestMapping(value = "/api/task", method = RequestMethod.DELETE)
    public Schedule deleteTask(@CookieValue(name = "access-token") AccessToken accessToken, @RequestParam String
            scheduleName, @RequestBody Task task) {

        User user = databaseController.findByAccessToken(accessToken);

        if (user == null) {

            throw new UnauthorizedException();
        }

        Schedule schedule = user.getSchedule(scheduleName);

        if (schedule == null) {

            throw new GoneException();
        }

        if (task.isInValid()) {

            throw new BadRequestException();
        }

        task = schedule.getTask(task);

        databaseController.delete(task);
        schedule.removeTask(task);
        databaseController.save(schedule);
        user.addSchedule(schedule);
        databaseController.save(user);

        for (Feeder feeder : databaseController.findBySchedule(schedule)) {

            socketController.updateFeeder(feeder.getUuid(), feeder);
        }

        return user.getSchedule(scheduleName);
    }

    @RequestMapping(value = "/api/task", method = RequestMethod.PUT)
    public Schedule changeTask(@CookieValue(name = "access-token") AccessToken accessToken, @RequestParam String
            scheduleName,
                               @RequestParam int taskId, @RequestParam(required = false) LocalTime time,
                               @RequestParam(required = false) Weekday day, @RequestParam(required = false) Integer amount) {

        if (time == null && day == null && amount == null) {

            throw new BadRequestException();
        }

        User user = databaseController.findByAccessToken(accessToken);

        if (user == null) {

            throw new UnauthorizedException();
        }

        Schedule schedule = user.getSchedule(scheduleName);

        if (schedule == null) {

            throw new GoneException();
        }

        Task task = schedule.getTask(databaseController.findByTaskId(taskId));

        if (task == null) {

            throw new GoneException();
        }
        if (time != null) {

            task.setTime(time);
        }

        if (day != null) {

            task.setWeekday(day);
        }

        if (amount != null) {

            task.setAmount(amount);
        }

        databaseController.save(task);

        for (Feeder feeder : databaseController.findBySchedule(schedule)) {

            socketController.updateFeeder(feeder.getUuid(), feeder);
        }

        return schedule;
    }

    //==========================================================================================================Dispense

    @RequestMapping(value = "/api/dispense", method = RequestMethod.POST)
    public void dispense(@CookieValue(name = "access-token") AccessToken accessToken, @RequestParam int amount,
                         @RequestParam UUID uuid) {

        User user = databaseController.findByAccessToken(accessToken);

        if (user == null) {

            throw new UnauthorizedException();
        }

        Feeder feeder = user.getFeeder(uuid);

        if (feeder == null) {

            throw new GoneException();
        }
        Dispensation dispensation = new Dispensation(amount, LocalDateTime.now());
        socketController.dispense(uuid, dispensation);
        feeder.addDispensation(dispensation);
        databaseController.save(dispensation);
    }

    @RequestMapping(value = "/api/{uuid}/dispense", method = RequestMethod.POST)
    public void piDispensed(@RequestParam int amount, @PathVariable UUID uuid) {

        Feeder feeder = databaseController.findByUuid(uuid);

        if (feeder == null) {

            return;
        }

        Dispensation dispensation = new Dispensation(amount, LocalDateTime.now());
        feeder.addDispensation(dispensation);
        databaseController.save(dispensation);
    }

    @RequestMapping(value = "/api/dispense", method = RequestMethod.GET)
    public List<Dispensation> dispense(@CookieValue(name = "access-token") AccessToken
                                               accessToken, @RequestParam UUID uuid) {

        User user = databaseController.findByAccessToken(accessToken);

        if (user == null) {

            throw new UnauthorizedException();
        }

        Feeder feeder = user.getFeeder(uuid);

        if (feeder == null) {

            throw new GoneException();
        }
        return feeder.getDispensations();
    }
}
