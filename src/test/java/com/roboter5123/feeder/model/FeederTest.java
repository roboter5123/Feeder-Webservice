package com.roboter5123.feeder.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class FeederTest {

        UUID TEST_UUID;
        Feeder testObject;
        Feeder comparisonObject;
        Schedule TEST_SCHEDULE;
        String TEST_NAME;

    @BeforeEach
    void setUp() {

        TEST_UUID = UUID.fromString("60f86dac-aa95-4d6c-afcd-f2ad252dadec");
        TEST_SCHEDULE = new Schedule();
        TEST_NAME = "TEST_NAME";

        testObject = new Feeder();
        testObject.setUuid(TEST_UUID);
        testObject.setSchedule(TEST_SCHEDULE);
        testObject.setName(TEST_NAME);

        comparisonObject = new Feeder(TEST_UUID);
    }

    @Test
    void addDispensation() {

        Dispensation mockDispensation = mock(Dispensation.class);
        Dispensation mockDispensation2 = mock(Dispensation.class);
        mockDispensation2.setTime(LocalDateTime.now());
        List<Dispensation> mockDispensations = new ArrayList<>();
        mockDispensations.add(mockDispensation);
        mockDispensations.add(mockDispensation);
        testObject.addDispensation(mockDispensation);
        testObject.addDispensation(mockDispensation);

        assertNotEquals(mockDispensations, testObject.getDispensations());

        mockDispensations.remove(1);
        mockDispensations.add(mockDispensation2);
        testObject.addDispensation(mockDispensation2);

        assertEquals(mockDispensations, testObject.getDispensations());
    }

    @Test
    void testEqualsWithEqualObject() {

        assertEquals(testObject, comparisonObject);
    }

    @Test
    void testEqualsWithDifferentUuid() {

        comparisonObject.setUuid(UUID.randomUUID());
        assertNotEquals(comparisonObject, testObject);
    }

    @Test
    void testToString() {

        String testString = "{" +
                "\"uuid\":\"" + TEST_UUID + "\", \"schedule\":" + TEST_SCHEDULE +
                ", \"name\":\"" + TEST_NAME +
                "\"}";

        assertEquals(testString, testObject.toString());
    }
}