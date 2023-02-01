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
import java.util.UUID;

/**
 * Api used to manage feeder
 *
 * @author roboter5123
 */
@RestController
public class FeederService {

    private final DatabaseController databaseController;
    private final SocketController socketController;

    @Autowired
    public FeederService(DatabaseController databaseController, SocketController socketController) {

        this.databaseController = databaseController;
        this.socketController = socketController;
    }

    /**
     * Used to register a feeder to a user. The feeder must exist in the database prior. Thus, the feeder has to be connected first
     *
     * @param accessToken used to authenticate a user and add the feeder to them
     * @param uuid        used to find the feeder in the database
     * @return a list of all feeders registered to the user
     */
    @PostMapping(value = "/api/feeder")
    @ResponseStatus(HttpStatus.OK)
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

        databaseController.save(feeder);
        feeders.add(feeder);
        user.setFeeders(feeders);
        databaseController.save(user);
        return feeders;
    }


    /**
     * @param accessToken used to authenticate a user
     * @param uuid        used to find the uuid
     * @return the feeder that was looked for
     * @throws UnauthorizedException thrown when the token isn't valid
     * @throws GoneException         thrown when the feeder doesn't exist or the user doesn't own it
     */
    @GetMapping(value = "/api/feeder")
    @ResponseStatus(HttpStatus.OK)
    public Feeder getFeeder(@CookieValue(name = "access-token") AccessToken accessToken, @RequestParam UUID uuid)
            throws UnauthorizedException, GoneException {

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

    /**
     * Gets all feeders that are registered to the user
     *
     * @param accessToken used to authenticate a user
     * @return all feeders that are registered to the given user
     */
    @GetMapping(value = "/api/feeders")
    @ResponseStatus(HttpStatus.OK)
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

    /**
     * Removes a feeder from the given user. and deletes the feeder if it's orphaned
     *
     * @param accessToken used to authenticate a user
     * @param uuid        used to find the feeder
     */
    @DeleteMapping(value = "/api/feeder")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFeeder(@CookieValue(name = "access-token") AccessToken accessToken, @RequestParam UUID uuid) {

        User user = databaseController.findByAccessToken(accessToken);

        if (user == null) {

            throw new UnauthorizedException();
        }

        Feeder feeder = user.getFeeder(uuid);

        if (feeder == null) {

            throw new GoneException();
        }

        user.removeFeeder(feeder);
        databaseController.save(user);

        if (databaseController.findUsersByFeeder(feeder) == null) {

            databaseController.delete(feeder);
        }

        socketController.updateFeeder(feeder.getUuid(), feeder);
    }

    /**
     * Updates a feeder with the given parameters
     *
     * @param accessToken  used to authenticate a user
     * @param uuid         used to find the feeder
     * @param feederName   Optional the new name the feeder should have
     * @param scheduleName Optional the name of the new schedule
     * @return the feeder that was changed
     */
    @PutMapping(value = "/api/feeder")
    @ResponseStatus(HttpStatus.OK)
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

            throw new GoneException();
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

            if (oldSchedule == null || !Objects.equals(oldSchedule.getName(), newSchedule.getName())) {

                feeder.setSchedule(newSchedule);
            }
        }

        databaseController.save(feeder);
        socketController.updateFeeder(feeder.getUuid(), feeder);
        return feeder;
    }


}
