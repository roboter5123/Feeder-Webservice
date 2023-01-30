package com.roboter5123.feeder.model;
import com.roboter5123.feeder.exception.ConflictException;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A representation of a Schedule. Including all tasks. Owned by a user and set on a feeder
 * @author roboter5123
 */
@Entity
@Table(name = "Schedule")
public class Schedule implements Serializable {

    @Id
    @GeneratedValue
    private int scheduleId;
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "schedule_id")
    private List<Task> tasks;
    private String name;

    public Schedule() {

    }

    public Schedule(String name) {

        this.name = name;
    }

    public Schedule(int scheduleId) {

        this.scheduleId = scheduleId;
    }

    public void addTask(Task task){

        if (tasks == null){

            tasks = new ArrayList<>();
        }

        if (tasks.contains(task)) {

            throw new ConflictException();
        }

        tasks.add(task);
    }

    public void removeTask(Task task) {

        if (tasks == null){

            return;
        }

        tasks.remove(task);
    }

    public Task getTask(Task task) {

        if (!tasks.contains(task)){

            return null;
        }

        return tasks.get(tasks.indexOf(task));
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Schedule schedule = (Schedule) o;

        if (scheduleId == schedule.getScheduleId() && Objects.equals(name, schedule.getName())){

            return true;
        }else if(scheduleId == schedule.getScheduleId() && !Objects.equals(name, schedule.getName())){

            return true;

        }else return scheduleId != schedule.getScheduleId() && Objects.equals(name, schedule.getName());
    }

    @Override
    public int hashCode() {

        return Objects.hash(scheduleId, name);
    }

    @Override
    public String toString() throws NullPointerException{

        return "{" +
                "\"scheduleId\":" + scheduleId +
                ", \"tasks\":" + tasks +
                ", \"name\":\"" + name +
                "\"}";
    }


}
