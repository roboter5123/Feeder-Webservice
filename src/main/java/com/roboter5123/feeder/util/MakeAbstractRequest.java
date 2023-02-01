package com.roboter5123.feeder.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;

import java.io.IOException;

/**
 * Used to make a request to the Abstract™ API for checking if an email is valid.
 */
public class MakeAbstractRequest {

    private MakeAbstractRequest() {
        throw new IllegalStateException();
    }

    public static boolean checkEmail(String email, String apiKey) {

        try {

            Content content = Request.Get("https://emailvalidation.abstractapi.com/v1/?api_key=" + apiKey + "&email=" + email)

                    .execute().returnContent();

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(content.toString(), JsonObject.class);
            return jsonObject.get("deliverability").getAsString().equals("DELIVERABLE");

        } catch (IOException error) {

            error.printStackTrace();
        }

        return false;
    }
}
