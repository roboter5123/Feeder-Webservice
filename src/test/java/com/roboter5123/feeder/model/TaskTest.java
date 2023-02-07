package com.roboter5123.feeder.model;

import com.roboter5123.feeder.util.Weekday;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    Task testTask;
    Weekday WEEKDAY;
    int TEST_ID;
    int TEST_AMOUNT;
    LocalTime TEST_TIME;


    @BeforeEach
    void setUp(){

        TEST_ID = 5;
        TEST_AMOUNT = 10;
        WEEKDAY = Weekday.FRIDAY;
        TEST_TIME = LocalTime.now();
        testTask = new Task();
        testTask.setTaskId(TEST_ID);
        testTask.setAmount(TEST_AMOUNT);
        testTask.setWeekday(WEEKDAY);
        testTask.setTime(TEST_TIME);

    }

    @Test
    void testEquals() {
    }

    @Test
    void testToString() {
    }

    @Test
    void compareTo() {


    }
}