package com.roboter5123.feeder.model;

import com.roboter5123.feeder.exception.GoneException;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Representation of a user
 * @author roboter5123
 */
@Entity
@Table(name = "user")
public class User implements Serializable {

    @Id
    private String email;
    private boolean activated;
    private String password;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "access_token_token")
    private AccessToken accessToken;
    private byte[] salt;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "email")
    private List<Feeder> feeders;
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "email")
    private List<Schedule> schedules;

    public void addSchedule(Schedule schedule) {

        schedules.add(schedule);
    }

    public Schedule getSchedule(String name) {

        try {

            return schedules.get(schedules.indexOf(new Schedule(name)));

        } catch (IndexOutOfBoundsException e) {

            return null;
        }
    }

    public Feeder getFeeder(UUID uuid) {

        try {

            return feeders.get(feeders.indexOf(new Feeder(uuid)));

        } catch (IndexOutOfBoundsException e) {

            throw new GoneException();
        }
    }

    public List<Schedule> getSchedules() {

        return schedules;
    }

    public void addFeeder(UUID uuid, Feeder feeder) {

        if (feeders.contains(feeder)){

            feeders.set(feeders.indexOf(new Feeder(uuid)), feeder);
        }else{

            feeders.add(feeder);
        }


    }

    public AccessToken getAccessToken() {

        return accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {

        this.accessToken = accessToken;
    }

    public byte[] getSalt() {

        return salt;
    }

    public void setSalt(byte[] salt) {

        this.salt = salt;
    }

    public String getEmail() {

        return email;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    public List<Feeder> getFeeders() {

        return feeders;
    }

    public void setFeeders(List<Feeder> feeders) {

        this.feeders = feeders;
    }

    public void removeFeeder(Feeder feeder) {

        feeders.remove(feeder);
    }

    public void setActivated(boolean b) {

        this.activated = b;
    }

    public boolean getActivated() {

        return activated;
    }

    @Override
    public String toString() {

        return "User{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + Arrays.toString(salt) + '\'' +
                ", feeders=" + feeders +
                '}';
    }

    public void setEmail(String email) {

        this.email = email;
    }
}
