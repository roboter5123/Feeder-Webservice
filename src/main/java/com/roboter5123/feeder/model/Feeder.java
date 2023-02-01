package com.roboter5123.feeder.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * A representation of a feeder. Also used to set the settings of a feeder by sending this as a string
 * @author roboter5123
 */
@Entity
@Table(name = "feeder")
public class Feeder implements Serializable {

    @Id
    private UUID uuid;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "uuid")
    private List<Dispensation> dispensations;
    private String name;

    public Feeder() {

    }

    public Feeder(UUID uuid) {

        this.uuid = uuid;
        this.dispensations = new ArrayList<>();
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public List<Dispensation> getDispensations() {
        return dispensations;
    }

    public void setDispensations(List<Dispensation> dispensations) {

        this.dispensations= dispensations;
    }

    public boolean addDispensation(Dispensation dispensation) {

        if (this.dispensations == null){

            dispensations = new ArrayList<>();
        }

        if (dispensations.contains(dispensation)){

            return false;
        }

        dispensations.add(dispensation);
        return true;
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
        Feeder feeder = (Feeder) o;
        return Objects.equals(uuid, feeder.uuid);
    }

    @Override
    public int hashCode() {

        return Objects.hash(uuid);
    }

    @Override
    public String toString() {

        return "{" +
                "\"uuid\":\"" + uuid +
                "\", \"schedule\":" + schedule +
                ", \"name\":\"" + name +
                "\"}";
    }
}
