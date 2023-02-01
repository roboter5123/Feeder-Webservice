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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;

/**
 * Api used to manage tasks
 * @author roboter5123
 */
@RestController
public class TaskService {

    private final DatabaseController databaseController;
    private final SocketController socketController;

    @Autowired
    public TaskService(DatabaseController databaseController, SocketController socketController) {

        this.databaseController = databaseController;
        this.socketController = socketController;
    }

    /**
     * Creates a task in a given schedule
     * @param accessToken used to authenticate the user
     * @param scheduleName used to find the schedule
     * @param task the task to create and put into the schedule
     * @return the schedule with the new task
     */
    @PostMapping(value = "/api/task")
    @ResponseStatus(HttpStatus.OK)
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

        if (task.isInvalid()) {

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

    /**
     * Deletes a task from a given schedule
     * @param accessToken used to authenticate the user
     * @param scheduleName used to find the schedule
     * @param task the task to delete from the schedule
     * @return the schedule without the given task
     */
    @DeleteMapping(value = "/api/task")
    @ResponseStatus(HttpStatus.OK)
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

        if (task.isInvalid()) {

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

    /**
     * Changes a tasks properties
     * @param accessToken used to find the schedule
     * @param scheduleName used to find the schedule
     * @param taskId used to find the task in the schedule
     * @param time at which the task should be done
     * @param day on which the task should be done
     * @param amount which should be dispensed
     * @return schedule the task is a child of
     */
    @PutMapping(value = "/api/task")
    @ResponseStatus(HttpStatus.OK)
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
