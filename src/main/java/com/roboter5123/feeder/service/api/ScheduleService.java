package com.roboter5123.feeder.service.api;
import com.roboter5123.feeder.controller.DatabaseController;
import com.roboter5123.feeder.controller.SocketController;
import com.roboter5123.feeder.model.AccessToken;
import com.roboter5123.feeder.model.Feeder;
import com.roboter5123.feeder.model.Schedule;
import com.roboter5123.feeder.model.User;
import com.roboter5123.feeder.exception.BadRequestException;
import com.roboter5123.feeder.exception.ConflictException;
import com.roboter5123.feeder.exception.GoneException;
import com.roboter5123.feeder.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * Api used to manage schedules
 * @author roboter5123
 */
@RestController
public class ScheduleService {

    private final DatabaseController databaseController;
    private final SocketController socketController;

    @Autowired
    public ScheduleService(DatabaseController databaseController, SocketController socketController) {

        this.databaseController = databaseController;
        this.socketController = socketController;
    }

    /**
     * Creates a schedule for the given user
     * @param accessToken used to authenticate the user
     * @param name the for the schedule to be created
     * @return a list of all schedules the user owns
     */
    @RequestMapping(value = "/api/schedule", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public List<Schedule> createSchedule(@CookieValue(name = "access-token") AccessToken
                                                 accessToken, @RequestParam String name) {

        User user = databaseController.findByAccessToken(accessToken);
        name = name.strip();

        if (Objects.equals(name, "")){

            throw new BadRequestException();
        }

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

    /**
     * Gets a schedule from the given user by its schedule name
     * @param accessToken used to authenticate the user
     * @param name the name of the schedule to get
     * @return the schedule that was searched or
     */
    @RequestMapping(value = "/api/schedule", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
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

    /**
     * Used to get all schedules a user owns
     * @param accessToken used to authenticate a user
     * @return a list of all schedules a user owns
     */
    @RequestMapping(value = "/api/schedules", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<Schedule> retrieveSchedules(@CookieValue(name = "access-token") AccessToken accessToken) {

        User user = databaseController.findByAccessToken(accessToken);

        if (user == null) {

            throw new UnauthorizedException();
        }

        return user.getSchedules();
    }

    /**
     * deletes a schedule by its schedule name
     * @param accessToken used to authenticate the user
     * @param name the name of the schedule the user wants to delete
     */
    @RequestMapping(value = "/api/schedule", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
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

    /**
     * Canges a schedules name
     * @param accessToken used to authenticate the user
     * @param oldName used to find the schedule
     * @param newName the name to change sche schedules name to
     * @return the schedule that was changed
     */
    @RequestMapping(value = "/api/schedule", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public Schedule changeSchedule(@CookieValue(name = "access-token") AccessToken accessToken, @RequestParam String
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


}
