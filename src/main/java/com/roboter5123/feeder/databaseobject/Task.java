package com.roboter5123.feeder.databaseobject;
import com.roboter5123.feeder.util.Weekday;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
@Table(name = "Task")
public class Task implements Serializable, Comparable {

    private Weekday weekday;
    private int amount;
    private LocalTime time;
    @Id
    @GeneratedValue
    private int taskId;

    public Task() {

    }
    public boolean isInValid() {

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

    public void setTaskId(int taskId) {

        this.taskId = taskId;
    }

    public Weekday getWeekday() {

        return weekday;
    }

    public int getAmount() {

        return amount;
    }

    public LocalTime getTime() {

        return time;
    }

    public int getTaskId() {

        return taskId;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;

        if (amount == task.amount && taskId == task.taskId && weekday == task.weekday && Objects.equals(time, task.time)) {

            return true;

        } else if (taskId == task.taskId && !(amount == task.amount && weekday == task.weekday && Objects.equals(time, task.time))) {

            return true;

        } else {

            return (taskId != task.taskId && (amount == task.amount && weekday == task.weekday && Objects.equals(time, task.time)));
        }
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
    public int compareTo(Object o) {
        Task task = (Task) o;

        if (this.time == null && task.time != null ){

            return 1;
        }if (this.time == null && task.time == null){

            return 0;

        } else if (task == null) {

            return -1;
        }
        return this.time.compareTo(task.time);
    }
}
