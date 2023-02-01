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
    void testToString() {
    }
}