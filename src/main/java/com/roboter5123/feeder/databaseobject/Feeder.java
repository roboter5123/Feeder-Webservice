package com.roboter5123.feeder.databaseobject;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
    }

    public void addDispensation(Dispensation dispensation) {

        dispensations.add(dispensation);
    }

    public void setName(String name) {

        this.name = name;
    }

    public Schedule getSchedule() {

        return schedule;
    }

    public void setSchedule(Schedule schedule) {

        this.schedule = schedule;
    }

    public UUID getUuid() {

        return uuid;
    }

    public void setUuid(UUID uuid) {

        this.uuid = uuid;
    }

    public String getName() {

        return name;
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

    public List<Dispensation> getDispensations() {

        return dispensations;
    }
}
