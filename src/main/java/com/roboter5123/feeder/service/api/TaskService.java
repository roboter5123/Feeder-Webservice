package com.roboter5123.feeder.service.api;
import com.roboter5123.feeder.controller.DatabaseController;
import com.roboter5123.feeder.controller.SocketController;
import com.roboter5123.feeder.model.*;
import com.roboter5123.feeder.exception.BadRequestException;
import com.roboter5123.feeder.exception.ConflictException;
import com.roboter5123.feeder.exception.GoneException;
import com.roboter5123.feeder.exception.UnauthorizedException;
import com.roboter5123.feeder.util.Weekday;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;

@RestController
public class TaskService {

    private final DatabaseController databaseController;
    private final SocketController socketController;

    @Autowired
    public TaskService(DatabaseController databaseController, SocketController socketController) {

        this.databaseController = databaseController;
        this.socketController = socketController;
    }

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

}
