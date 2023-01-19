package com.roboter5123.feeder.service.api;
import com.roboter5123.feeder.controller.DatabaseController;
import com.roboter5123.feeder.controller.SocketController;
import com.roboter5123.feeder.databaseobject.AccessToken;
import com.roboter5123.feeder.databaseobject.Feeder;
import com.roboter5123.feeder.databaseobject.Schedule;
import com.roboter5123.feeder.databaseobject.User;
import com.roboter5123.feeder.exception.BadRequestException;
import com.roboter5123.feeder.exception.ConflictException;
import com.roboter5123.feeder.exception.GoneException;
import com.roboter5123.feeder.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
public class ScheduleService {

    private final DatabaseController databaseController;
    private final SocketController socketController;

    @Autowired
    public ScheduleService(DatabaseController databaseController, SocketController socketController) {

        this.databaseController = databaseController;
        this.socketController = socketController;
    }

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


}
