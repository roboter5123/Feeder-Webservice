package com.roboter5123.feeder.model;

/**
 * A class to simplify user input
 */
public class LoginData {

    private String email;
    private String password;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Turns this object into a user. Might be the same as casting. Just want to make sure it's the correct user.
     * @return this object as a user
     */
    public User toUser() {

        User user = new User();
        user.setEmail(this.email);
        user.setPassword(this.password);
        return user;
    }
}
