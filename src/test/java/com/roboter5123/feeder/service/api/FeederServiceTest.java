package com.roboter5123.feeder.service.api;

import com.roboter5123.feeder.controller.DatabaseController;
import com.roboter5123.feeder.controller.SocketController;
import com.roboter5123.feeder.exception.BadRequestException;
import com.roboter5123.feeder.exception.ConflictException;
import com.roboter5123.feeder.exception.GoneException;
import com.roboter5123.feeder.exception.UnauthorizedException;
import com.roboter5123.feeder.model.AccessToken;
import com.roboter5123.feeder.model.Feeder;
import com.roboter5123.feeder.model.Schedule;
import com.roboter5123.feeder.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    Schedule newSchedule;
    Schedule oldSchedule;

    @BeforeEach
    void setUp(){

        uuid = UUID.randomUUID();
        accessToken = new AccessToken("abc123");
        accessToken.setExpires(LocalDateTime.now().plusDays(5));
        feeder = new Feeder(uuid);
        user = new User();
        user.setFeeders(new ArrayList<>());
        newSchedule = new Schedule("newSchedule");
        oldSchedule = new Schedule("oldSchedule");
        user.addSchedule(newSchedule);
        user.addSchedule(oldSchedule);
    }

    @Test
    void registerFeederWorking() {

        when(databaseController.findByUuid(uuid)).thenReturn(feeder);
        when(databaseController.findByAccessToken(accessToken)).thenReturn(user);
        feederService.registerFeeder(accessToken, uuid);
        assertNotNull(user.getFeeder(uuid), "feeder wasn't added");
    }

    @Test
    void registerFeederAccessTokenInvalid() {

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

        user.addFeeder(uuid, feeder);
        when(databaseController.findByAccessToken(accessToken)).thenReturn(user);
        Feeder serviceFeeder = feederService.getFeeder(accessToken,uuid);
        assertEquals(feeder, serviceFeeder, "Get feeder doesn't get the correct feeder");
    }

    @Test
    void getFeederUserDoesntExist() {

        Exception exception = assertThrows(UnauthorizedException.class, ()->feederService.getFeeder(accessToken,uuid));
        assertInstanceOf(UnauthorizedException.class, exception);
    }

    @Test
    void getFeederUserDoesntOwn() {

        when(databaseController.findByAccessToken(accessToken)).thenReturn(user);
        Exception exception = assertThrows(GoneException.class, ()->feederService.getFeeder(accessToken,uuid));
        assertInstanceOf(GoneException.class, exception);
    }

    @Test
    void getFeedersWorking() {

        Feeder feeder1 = new Feeder(UUID.randomUUID());
        user.addFeeder(feeder1.getUuid(), feeder1);
        user.addFeeder(uuid, feeder);
        when(databaseController.findByAccessToken(accessToken)).thenReturn(user);
        List<Feeder> serviceFeeders = feederService.getFeeders(accessToken);
        assertEquals(user.getFeeders(), serviceFeeders);
    }

    @Test
    void getFeedersUserDoesntExist() {

        Exception exception = assertThrows(UnauthorizedException.class, ()->feederService.getFeeders(accessToken));
        assertInstanceOf(UnauthorizedException.class, exception);
    }

    @Test
    void getFeederUserDoesntOwnAnyFeeders() {

        when(databaseController.findByAccessToken(accessToken)).thenReturn(user);
        Exception exception = assertThrows(GoneException.class, ()->feederService.getFeeder(accessToken,uuid));
        assertInstanceOf(GoneException.class, exception);
    }

    @Test
    void deleteFeedersWorkingWith2FeedersInUserInitially() {

        Feeder feeder1 = new Feeder(UUID.randomUUID());
        List<Feeder> feeders = new ArrayList<>();
        feeders.add(feeder1);
        user.addFeeder(feeder1.getUuid(), feeder1);
        user.addFeeder(uuid, feeder);
        when(databaseController.findByAccessToken(accessToken)).thenReturn(user);
        feederService.deleteFeeder(accessToken, uuid);
        assertEquals(feeders, user.getFeeders());
    }
    @Test
    void deleteFeedersWorkingWith1FeedersInUserInitially() {

        List<Feeder> feeders = new ArrayList<>();
        user.addFeeder(uuid, feeder);
        when(databaseController.findByAccessToken(accessToken)).thenReturn(user);
        feederService.deleteFeeder(accessToken,uuid);
        assertEquals(feeders, user.getFeeders());
    }

    @Test
    void deleteFeedersUserDoesntExist() {

        Exception exception = assertThrows(UnauthorizedException.class, ()->feederService.deleteFeeder(accessToken, uuid));
        assertInstanceOf(UnauthorizedException.class, exception);
    }

    @Test
    void getFeederUserDoesntOwnTheFeeder() {

        when(databaseController.findByAccessToken(accessToken)).thenReturn(user);
        Exception exception = assertThrows(GoneException.class, ()->feederService.deleteFeeder(accessToken, uuid));
        assertInstanceOf(GoneException.class, exception);
    }

    @Test
    void changeFeederOnlyNameInputWorking(){

        when(databaseController.findByAccessToken(accessToken)).thenReturn(user);
        feeder.setName("oldName");
        user.addFeeder(uuid, feeder);
        Feeder serviceFeeder = feederService.changeFeeder(accessToken,uuid,"newFeederName",null);
        assertEquals("newFeederName", serviceFeeder.getName());
    }

    @Test
    void changeFeederOnlyScheduleInputWorking(){

        when(databaseController.findByScheduleName("newSchedule")).thenReturn(newSchedule);
        when(databaseController.findByAccessToken(accessToken)).thenReturn(user);
        user.addFeeder(uuid, feeder);
        Feeder serviceFeeder = feederService.changeFeeder(accessToken,uuid,null,"newSchedule");
        assertEquals(feeder, serviceFeeder);
        assertEquals(newSchedule, serviceFeeder.getSchedule());
    }

    @Test
    void changeFeederScheduleAndNameWorking(){

        when(databaseController.findByScheduleName("newSchedule")).thenReturn(newSchedule);
        when(databaseController.findByAccessToken(accessToken)).thenReturn(user);

        feeder.setName("oldName");
        user.addFeeder(uuid, feeder);
        Feeder serviceFeeder = feederService.changeFeeder(accessToken,uuid,"newFeederName","newSchedule");
        assertEquals(feeder, serviceFeeder);
        assertEquals("newFeederName", feeder.getName());
        assertEquals(newSchedule, serviceFeeder.getSchedule());

    }

    @Test
    void changeFeederNoNameAndNoSchedule() {

        Exception exception  = assertThrows(BadRequestException.class, ()-> feederService.changeFeeder(accessToken,uuid, null,null));
        assertInstanceOf(BadRequestException.class, exception);
    }

    @Test
    void changeFeederUserDoesntExist() {

        Exception exception  = assertThrows(UnauthorizedException.class, ()-> feederService.changeFeeder(accessToken,uuid, "null","null"));
        assertInstanceOf(UnauthorizedException.class, exception);
    }

    @Test
    void changeFeederUserDoesntOwnFeeder() {

        when(databaseController.findByAccessToken(accessToken)).thenReturn(user);
        Exception exception  = assertThrows(GoneException.class, ()-> feederService.changeFeeder(accessToken,uuid, "null","null"));
        assertInstanceOf(GoneException.class, exception);
    }

    @Test
    void changeFeederUserDoesntOwnSchedule() {

        user.addFeeder(uuid,feeder);
        when(databaseController.findByAccessToken(accessToken)).thenReturn(user);
        Exception exception  = assertThrows(BadRequestException.class, ()-> feederService.changeFeeder(accessToken,uuid, "null","null"));
        assertInstanceOf(BadRequestException.class, exception);
    }
}