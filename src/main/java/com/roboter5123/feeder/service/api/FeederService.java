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



}
