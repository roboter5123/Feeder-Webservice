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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Api used to manage Dispensations
 * @author roboter5123
 */
@RestController
public class DispensationService {

    private final DatabaseController databaseController;
    private final SocketController socketController;

    @Autowired
    public DispensationService(DatabaseController databaseController, SocketController socketController) {

        this.databaseController = databaseController;
        this.socketController = socketController;
    }

    /**
     * Sends a dispense command to the give feeder if the user has authorization for that feeder.
     * @param accessToken Used to authorize a user
     * @param amount The amount of food to dispense
     * @param uuid used to find the feeder
     */
    @PostMapping(value = "/api/dispense")
    @ResponseStatus(HttpStatus.OK)
    public Dispensation dispense(@CookieValue(name = "access-token") AccessToken accessToken, @RequestParam int amount,
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
        return dispensation;
    }

    /**
     * Called by a feeder when it dispenses on schedule
     * @param amount the amount of food dispensed
     * @param uuid used to find the feeder that dispensed
     */
    @PostMapping(value = "/api/{uuid}/dispense")
    @ResponseStatus(HttpStatus.OK)
    public Dispensation piDispensed(@RequestParam int amount, @PathVariable UUID uuid) {

        Feeder feeder = databaseController.findByUuid(uuid);

        if (feeder == null) {

            throw new GoneException();
        }

        Dispensation dispensation = new Dispensation(amount, LocalDateTime.now());
        feeder.addDispensation(dispensation);
        databaseController.save(dispensation);
        return dispensation;
    }

    /**
     * Gets all dispenses associated with a feeder
     * @param accessToken used to check authorization of a user
     * @param uuid used to find the feeder whose dispensations to get
     * @return a list of all dispensations the feeder has done in its lifetime
     */
    @GetMapping(value = "/api/dispense")
    @ResponseStatus(HttpStatus.OK)
    public List<Dispensation> getDispenses(@CookieValue(name = "access-token") AccessToken
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
