package com.roboter5123.feeder.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccessTokenTest {

    AccessToken testObject;
    String TEST_TOKEN;

    @BeforeEach
    void setUp(){

        TEST_TOKEN = "abc123";
    }

    @Test
    void AccessTokenWithString(){

        testObject = new AccessToken(TEST_TOKEN);
        assertEquals(TEST_TOKEN, testObject.getToken());
    }
}