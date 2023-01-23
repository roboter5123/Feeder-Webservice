package com.roboter5123.feeder.service.api;
import com.roboter5123.feeder.controller.DatabaseController;
import com.roboter5123.feeder.controller.SocketController;
import com.roboter5123.feeder.model.AccessToken;
import com.roboter5123.feeder.model.Dispensation;
import com.roboter5123.feeder.model.Feeder;
import com.roboter5123.feeder.model.User;
import com.roboter5123.feeder.exception.GoneException;
import com.roboter5123.feeder.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
public class DispensationService {

    private final DatabaseController databaseController;
    private final SocketController socketController;

    @Autowired
    public DispensationService(DatabaseController databaseController, SocketController socketController) {

        this.databaseController = databaseController;
        this.socketController = socketController;
    }


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
