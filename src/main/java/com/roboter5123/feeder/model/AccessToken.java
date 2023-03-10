package com.roboter5123.feeder.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Used to authenticate and check authorization of a user
 * @author roboter5123
 */
@Entity
@Table(name = "access_token")
public class AccessToken {

    @Id
    @Column(name = "token", nullable = false)
    private String token;
    private LocalDateTime expires;

    public AccessToken() {}

    public AccessToken(String token) {

        this.token = token;
    }

    public String getToken() {

        return token;
    }

    public void setToken(String token) {

        this.token = token;
    }

    public LocalDateTime getExpires() {

        return expires;
    }

    public void setExpires(LocalDateTime expires) {

        this.expires = expires;
    }
}
