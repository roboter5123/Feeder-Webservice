package com.roboter5123.feeder.model;
import com.roboter5123.feeder.util.Weekday;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Representation of a planned dispensation that happens once every week or every day
 * @author roboter5123
 */
@Entity
@Table(name = "Task")
public class Task implements Serializable, Comparable<Task> {

    private Weekday weekday;
    private int amount;
    private LocalTime time;
    @Id
    @GeneratedValue
    private int taskId;

    public Task() {

    }
    public boolean isInvalid() {

        return weekday == null ;
    }

    public void setWeekday(Weekday weekday) {

        this.weekday = weekday;
    }

    public void setAmount(int amount) {

        this.amount = amount;
    }

    public void setTime(LocalTime time) {

        this.time = time;
    }

    public Weekday getWeekday() {

        return weekday;
    }

    public LocalTime getTime() {

        return time;
    }

    public int getAmount() {
        return amount;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return amount == task.amount && taskId == task.taskId && weekday == task.weekday && Objects.equals(time, task.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(weekday, amount, time, taskId);
    }

    @Override
    public String toString() {

        if (this.time == null || weekday == null || amount == 0){

            return "";
        }

        return "{" +
                "\"weekday\":" + weekday.ordinal() +
                ", \"amount\":" + amount +
                ", \"time\":\"" + time.format(DateTimeFormatter.ofPattern("HH:mm")) +
                "\", \"taskId\":" + taskId +
                '}';
    }

    @Override
    public int compareTo(Task task) {

        if (this.time == null && task.time != null ){

            return 1;

        }if (this.time == null && task.time == null){

            return 0;

        } else if (this.time != null && task.time == null) {

            return -1;
        }

        return this.time.compareTo(task.time);
    }
}
