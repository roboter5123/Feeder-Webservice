package com.roboter5123.feeder.service.api;

import com.roboter5123.feeder.controller.DatabaseController;
import com.roboter5123.feeder.controller.SocketController;
import com.roboter5123.feeder.exception.GoneException;
import com.roboter5123.feeder.exception.UnauthorizedException;
import com.roboter5123.feeder.model.AccessToken;
import com.roboter5123.feeder.model.Dispensation;
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
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DispensationServiceTest {

    @Mock
    DatabaseController databaseControllerMock;
    @Mock
    SocketController socketControllerMock;
    AccessToken accessToken;

    Feeder feeder;
    @Mock
    User user;
    UUID uuid;
    @InjectMocks
    DispensationService dispensationService;
    List<Dispensation> dispensations;

    @BeforeEach
    void setUp() {

        uuid = UUID.randomUUID();
        feeder = new Feeder();
        dispensations= new ArrayList<>();
        dispensations.add(new Dispensation(10, LocalDateTime.now()));
        dispensations.add(new Dispensation(50, LocalDateTime.now().minusDays(5)));
        dispensations.add(new Dispensation(0, LocalDateTime.now().minusDays(2)));
        feeder.setDispensations(dispensations);
    }

    @Test
    void dispenseWorking() {

        when(user.getFeeder(uuid)).thenReturn(feeder);
        when(databaseControllerMock.findByAccessToken(accessToken)).thenReturn(user);
        Dispensation dispensation = dispensationService.dispense(accessToken,5,uuid);
        assertTrue(feeder.getDispensations().contains(dispensation), "Dispensation wasn't added or done");
    }

    @Test
    void dispenseUserDoesntOwnThisFeeder(){

        when(user.getFeeder(uuid)).thenReturn(null);
        when(databaseControllerMock.findByAccessToken(accessToken)).thenReturn(user);
        Exception exception = assertThrows(GoneException.class,()->dispensationService.dispense(accessToken,10,uuid));
        assertInstanceOf(GoneException.class, exception);
    }

    @Test
    void dispenseAccessTokenDoesntHaveUser(){

        when(databaseControllerMock.findByAccessToken(accessToken)).thenReturn(null);
        Exception exception = assertThrows(UnauthorizedException.class,()->dispensationService.dispense(accessToken,10,uuid));
        assertInstanceOf(UnauthorizedException.class, exception);
    }

    @Test
    void piDispensedWorking() {

        when(databaseControllerMock.findByUuid(uuid)).thenReturn(feeder);
        Dispensation dispensation = dispensationService.piDispensed(5,uuid);
        assertTrue(feeder.getDispensations().contains(dispensation));
    }

    @Test
    void piDispensedFeederDoesntExist(){

        when(databaseControllerMock.findByUuid(uuid)).thenReturn(null);
        Exception exception = assertThrows(GoneException.class, ()->dispensationService.piDispensed(10,uuid));
        assertInstanceOf(GoneException.class, exception);
    }

    @Test
    void getDispensesWorking() {

        when(user.getFeeder(uuid)).thenReturn(feeder);
        when(databaseControllerMock.findByAccessToken(accessToken)).thenReturn(user);
        List<Dispensation> feederDispensations = dispensationService.getDispenses(accessToken,uuid);
        assertEquals(dispensations, feederDispensations);
    }

    @Test
    void getDispensesAccessTokenInvalid(){

        when(databaseControllerMock.findByAccessToken(accessToken)).thenReturn(null);
        Exception exception = assertThrows(UnauthorizedException.class, ()->dispensationService.getDispenses(accessToken,uuid));
        assertInstanceOf(UnauthorizedException.class, exception);
    }

    @Test
    void getDispensesUserDoesntOwnFeeder(){

        when(user.getFeeder(uuid)).thenReturn(null);
        when(databaseControllerMock.findByAccessToken(accessToken)).thenReturn(user);
        Exception exception = assertThrows(GoneException.class, ()->dispensationService.getDispenses(accessToken,uuid));
        assertInstanceOf(GoneException.class, exception);
    }

}