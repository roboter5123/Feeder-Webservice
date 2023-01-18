package com.roboter5123.feeder.databaseobject;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "dispensation")
public class Dispensation implements Serializable, Comparable {

    @Id
    @GeneratedValue
    private int dispensationId;
    private int amount;
    private LocalDateTime time;

    public Dispensation() {

    }

    public Dispensation(int amount, LocalDateTime time) {

        this.amount = amount;
        this.time = time;
    }

    public LocalDateTime getTime() {

        return time;
    }

    public int getAmount() {

        return amount;
    }

    @Override
    public String toString() {
        return "{" +
                "\"dispensationId\":" + dispensationId +
                ", \"amount\":" + amount +
                ", \"time\":\"" + time +
                "\"}";
    }

    @Override
    public int compareTo(Object o) {

        Dispensation dispensation = (Dispensation) o;

        if (this.time.isBefore(dispensation.getTime())){

            return 1;
        } else if (this.time.isEqual(dispensation.getTime())) {

            return 0;
        }else{

            return +1;
        }
    }
}
