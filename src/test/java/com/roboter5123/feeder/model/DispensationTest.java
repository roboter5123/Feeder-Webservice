package com.roboter5123.feeder.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DispensationTest {

    int TEST_ID;
    int TEST_AMOUNT;
    LocalDateTime TEST_TIME;
    Dispensation testDispensation;

    @BeforeEach
    void setUp(){

    TEST_ID = 5;
    TEST_AMOUNT = 10;
    TEST_TIME = LocalDateTime.now();

    testDispensation = new Dispensation(TEST_AMOUNT, TEST_TIME);
    testDispensation.setDispensationId(TEST_ID);
    }

    @Test
    void compareToEqual() {

        Dispensation compareDispensation = new Dispensation(TEST_AMOUNT, TEST_TIME);
        compareDispensation.setDispensationId(TEST_ID);
        assertEquals(0,testDispensation.compareTo(compareDispensation));
    }

    @Test
    void compareToBefore() {

        Dispensation compareDispensation = new Dispensation(TEST_AMOUNT, TEST_TIME.minusDays(5));
        compareDispensation.setDispensationId(TEST_ID);
        assertEquals(1,testDispensation.compareTo(compareDispensation));
    }

    @Test
    void compareToAfter() {

        Dispensation compareDispensation = new Dispensation(TEST_AMOUNT, TEST_TIME.plusDays(5));
        compareDispensation.setDispensationId(TEST_ID);
        assertEquals(-1,testDispensation.compareTo(compareDispensation));
    }

    @Test
    void testEquals() {

        Dispensation comparisonDispensation = new Dispensation(TEST_AMOUNT, TEST_TIME);
        comparisonDispensation.setDispensationId(TEST_ID);
        assertEquals(comparisonDispensation,testDispensation);
    }

    @Test
    void testEqualsDifferentAmount() {

        Dispensation comparisonDispensation = new Dispensation(5, TEST_TIME);
        comparisonDispensation.setDispensationId(TEST_ID);
        assertEquals(comparisonDispensation,testDispensation);
    }

    @Test
    void testEqualsDifferentId() {

        Dispensation comparisonDispensation = new Dispensation(TEST_AMOUNT, TEST_TIME);
        assertNotEquals(comparisonDispensation,testDispensation);
    }

    @Test
    void testEqualsDifferentTime() {

        Dispensation comparisonDispensation = new Dispensation(TEST_AMOUNT, null);
        comparisonDispensation.setDispensationId(TEST_ID);
        assertNotEquals(comparisonDispensation,testDispensation);
    }

    @Test
    void testToString() {

        String testString = "{" +
                "\"dispensationId\":" + TEST_ID +
                ",\"amount\":" + TEST_AMOUNT +
                ",\"time\":\"" + TEST_TIME +
                "\"}";

        assertEquals(testString,testDispensation.toString());
    }
}