package com.roboter5123.feeder.model;

import com.roboter5123.feeder.exception.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleTest {

    Schedule testSchedule;
    String TEST_NAME;

    int TEST_ID;
    @BeforeEach
    void setUp(){

        TEST_NAME = "TEST_NAME";
        TEST_ID = 5;

        testSchedule = new Schedule();
        testSchedule.setName(TEST_NAME);
        testSchedule.setScheduleId(TEST_ID);
    }

    @Test
    void addTask() {

        Task mockTask = new Task();
        List<Task> mockTasks = new ArrayList<>();
        mockTasks.add(mockTask);

        testSchedule.addTask(mockTask);
        assertEquals(mockTasks, testSchedule.getTasks());

        Exception exception = assertThrows(ConflictException.class, ()-> testSchedule.addTask(mockTask));
        assertInstanceOf(ConflictException.class, exception);

        Task secondMockTask = new Task();
        secondMockTask.setTaskId(5);

        mockTasks.add(secondMockTask);
        testSchedule.addTask(secondMockTask);

        assertEquals(mockTasks, testSchedule.getTasks());
    }

    @Test
    void removeTask() {

        Task mockTask = new Task();
        List<Task> mockTasks = new ArrayList<>();
        mockTasks.add(mockTask);
        Task secondMockTask = new Task();
        secondMockTask.setTaskId(5);
        mockTasks.add(secondMockTask);

        testSchedule.addTask(mockTask);
        testSchedule.addTask(secondMockTask);
        assertEquals(mockTasks, testSchedule.getTasks());

        testSchedule.removeTask(secondMockTask);
        mockTasks.remove(secondMockTask);
        assertEquals(mockTasks, testSchedule.getTasks());

        testSchedule.removeTask(mockTask);
        mockTasks.remove(mockTask);
        assertEquals(mockTasks, testSchedule.getTasks());

        testSchedule.removeTask(null);
        assertEquals(mockTasks, testSchedule.getTasks());
    }

    @Test
    void equalsSameObject(){

        assertEquals(testSchedule,testSchedule);
    }

    @Test
    void equalsDifferentObject(){

        Schedule compareSchedule = new Schedule(TEST_NAME);
        compareSchedule.setScheduleId(TEST_ID);
        assertEquals(compareSchedule, testSchedule);
    }

    @Test
    void equalsWithDifferentScheduleId(){

        Schedule compareSchedule = new Schedule(TEST_ID);
        assertEquals(compareSchedule, testSchedule);
    }
    @Test
    void equalsWithSameScheduleNameDifferentId(){

        Schedule compareSchedule = new Schedule(TEST_NAME);
        assertEquals(compareSchedule, testSchedule);
    }

    @Test
    void equalsWithDifferentScheduleNameSameScheduleId(){

        Schedule compareSchedule = new Schedule(TEST_ID);
        assertEquals(compareSchedule, testSchedule);
    }

    @Test
    void equalsWithDifferentSchedule(){

        Schedule compareSchedule = new Schedule();
        assertNotEquals(compareSchedule, testSchedule);
    }

    @Test
    void testToString() {

        String testString = "{\"scheduleId\":" + TEST_ID + ",\"tasks\":" + null +
                ",\"name\":\"" + TEST_NAME + "\"}";
        assertEquals(testString,testSchedule.toString());
    }
}