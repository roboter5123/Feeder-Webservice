package com.roboter5123.feeder.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DispensationTest {

    Dispensation testObject;
    Dispensation wrongObject;

    @BeforeEach
    void setUp(){

        LocalDateTime dateTime = LocalDateTime.now();
        testObject = new Dispensation(10, dateTime);
        testObject.setDispensationId(5);
        wrongObject = new Dispensation(5, dateTime);
    }

    @Test
    void compareTo() {

        assertEquals( 0,testObject.compareTo(testObject), "Test with same object");
        wrongObject.setTime(wrongObject.getTime().minusDays(5));
        assertEquals(1, testObject.compareTo(wrongObject),"Test with lower date");
        wrongObject.setTime(wrongObject.getTime().plusDays(10));
        assertEquals(-1, testObject.compareTo(wrongObject), "test with higher date");
    }

    @Test
    void testToString() {

        String testString = "{\"dispensationId\":"+testObject.getDispensationId()+",\"amount\":"+ testObject.getAmount()+",\"time\":\""+testObject.getTime()+"\"}";
        assertEquals(testString, testObject.toString());
    }
}