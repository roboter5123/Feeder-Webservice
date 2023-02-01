package com.roboter5123.feeder.service.api;

import com.roboter5123.feeder.controller.DatabaseController;
import com.roboter5123.feeder.controller.SocketController;
import com.roboter5123.feeder.exception.ConflictException;
import com.roboter5123.feeder.exception.UnauthorizedException;
import com.roboter5123.feeder.model.AccessToken;
import com.roboter5123.feeder.model.Feeder;
import com.roboter5123.feeder.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeederServiceTest {

    @Mock
    DatabaseController databaseController;
    @Mock
    SocketController socketController;
    @InjectMocks
    FeederService feederService;

    Feeder feeder;
    User user;
    AccessToken accessToken;
    UUID uuid;

    @BeforeEach
    void setUp(){

        uuid = UUID.randomUUID();
        accessToken = new AccessToken("abc123");
        accessToken.setExpires(LocalDateTime.now().plusDays(5));
        feeder = new Feeder(uuid);
        user = new User();
        user.setFeeders(new ArrayList<>());


    }

    @Test
    void registerFeederWorking() {
        when(databaseController.findByUuid(uuid)).thenReturn(feeder);
        when(databaseController.findByAccessToken(accessToken)).thenReturn(user);
        feederService.registerFeeder(accessToken, uuid);
        assertNotNull(user.getFeeder(uuid), "feeder wasn't added");
    }

    @Test
    void registerFeederAccesstokenInvalid() {

        when(databaseController.findByAccessToken(accessToken)).thenReturn(null);
        Exception exception = assertThrows(UnauthorizedException.class,()->feederService.registerFeeder(accessToken, uuid));
        assertInstanceOf(UnauthorizedException.class, exception);
    }

    @Test
    void registerFeederFeedersDoesntExist() {

        when(databaseController.findByAccessToken(accessToken)).thenReturn(user);
        when(databaseController.findByUuid(uuid)).thenReturn(null);
        feederService.registerFeeder(accessToken,uuid);
        assertNotNull(user.getFeeder(uuid), "feeder wasn't added");
    }

    @Test
    void registerFeederUserAlreadyOwnsFeeder() {

        user.addFeeder(uuid, feeder);
        when(databaseController.findByAccessToken(accessToken)).thenReturn(user);
        when(databaseController.findByUuid(uuid)).thenReturn(feeder);
        Exception exception = assertThrows(ConflictException.class,()->feederService.registerFeeder(accessToken, uuid));
        assertInstanceOf(ConflictException.class, exception);
    }

    @Test
    void getFeederWorking() {

//        fail("Not yet implemented");
    }

    @Test
    void getFeederUserDoesntExist() {

//        fail("Not yet implemented");
    }

    @Test
    void getFeederuserDoesntOwn() {

//        fail("Not yet implemented");
    }

    @Test
    void getFeeders() {

//        fail("Not yet implemented");
    }

    @Test
    void deleteFeeders() {

//        fail("Not yet implemented");
    }

    @Test
    void changeFeeder() {

//        fail("Not yet implemented");
    }
}