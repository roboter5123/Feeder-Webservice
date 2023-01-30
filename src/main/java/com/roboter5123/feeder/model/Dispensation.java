package com.roboter5123.feeder.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Used as a representation of a dispense action by the feeder
 * @author roboter5123
 */
@Entity
@Table(name = "dispensation")
public class Dispensation implements Serializable, Comparable<Dispensation> {

    @Id
    @GeneratedValue
    private int dispensationId;
    private int amount;
    private LocalDateTime time;

    public Dispensation() {}

    public Dispensation(int amount, LocalDateTime time) {

        this.amount = amount;
        this.time = time;
    }

    public int getAmount() {
        return amount;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public void setDispensationId(int id) {
        this.dispensationId = id;
    }

    public int getDispensationId() {

        return this.dispensationId;
    }

    @Override
    public int compareTo(Dispensation o) {

        if (this.time.isBefore(o.getTime())) {

            return -1;

        } else if (this.time.isEqual(o.getTime())) {

            return 0;

        } else {

            return 1;
        }
    }

    @Override
    public String toString() {
        return "{" +
                "\"dispensationId\":" + dispensationId +
                ",\"amount\":" + amount +
                ",\"time\":\"" + time +
                "\"}";
    }
}

