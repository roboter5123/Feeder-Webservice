package com.roboter5123.feeder.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

/**
 * Represents a feeder that is currently connected.
 * Can send and receive Commands to the feeder.
 */
public class FeederConnection {

    private final BufferedReader in;
    private final PrintWriter out;
    private final UUID uuid;

    public FeederConnection(Socket client) throws IOException {

        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(), true);
        String sentUUID = in.readLine();
        uuid = UUID.fromString(sentUUID);
    }

    public void sendCommand(String command) {

        out.println(command);
    }

    public String receiveResponse() throws IOException {

        return in.readLine();
    }

    public UUID getUuid() {

        return uuid;
    }
}
