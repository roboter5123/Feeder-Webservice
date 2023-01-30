package com.roboter5123.feeder.controller;
import com.roboter5123.feeder.model.Dispensation;
import com.roboter5123.feeder.model.Feeder;
import com.roboter5123.feeder.exception.GoneException;
import com.roboter5123.feeder.util.FeederConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.UUID;

/**
 * SocketController is a separate thread which takes a undetermined amount of socket connections from feeders and sends messages to them.
 * @author roboter5123
 */
@Controller
public class SocketController extends Thread {

    private final DatabaseController databaseController;
    private final ServerSocket server;
    private final HashMap<UUID, FeederConnection> connections;

    @Autowired
    public SocketController(DatabaseController databaseController) {

        this.databaseController = databaseController;
        int port = 8058;
        this.connections = new HashMap<>();

        try {

            this.server = new ServerSocket(port);

        } catch (IOException e) {

            throw new RuntimeException(e);
        }

        this.start();
    }

    /**
     * Main Method starts the socket server for accepting connections and manages them.
     */
    public void run(){

        boolean running = true;

        while (running) {

            FeederConnection newConnection;

            try {

                newConnection = new FeederConnection(server.accept());

            } catch (IOException e) {

                continue;
            }

            Feeder feeder = addConnection(newConnection);

            if (feeder.getSchedule() != null) {

                try {

                    newConnection.sendCommand("set#{\"schedule\":" + feeder.getSchedule() + ",\"name\":\"" +feeder.getName() + "\"}");

                }catch (NullPointerException e){

                    e.printStackTrace();
                }
            }
        }
    }

    private Feeder addConnection(FeederConnection newConnection) {

        Feeder feeder = databaseController.findByUuid(newConnection.getUuid());

        if (feeder == null) {

            feeder = new Feeder();
            feeder.setUuid(newConnection.getUuid());
            databaseController.save(feeder);
        }

        this.connections.put(newConnection.getUuid(), newConnection);
        System.out.println("connected with " + newConnection.getUuid().toString());
        return feeder;
    }

    /**
     * @param uuid of the feeder that is connected
     * @return The feeder connection for the corresponding feeder
     * @throws NullPointerException thrown if the feeder hasn't connected yet.
     */
    private FeederConnection getConnection(UUID uuid) throws NullPointerException {

        return this.connections.get(uuid);
    }

    /**
     * Sends a dispense Command to the specified feeder over the server socket
     * @param uuid Used to find the feeder in the connection hashmap
     * @param dispensation Is sent to the feeder to dispense
     */
    public void dispense(UUID uuid, Dispensation dispensation) {

        try {

            String message = "dispense#" + dispensation.getAmount();
            sendMessage(uuid, message);

        } catch (IOException | NullPointerException e) {

            throw new GoneException();
        }
    }

    /**
     * Sends an arbitrary message to the feeder specified.
     * @param uuid Used to find the feeder in the connection hashmap
     * @param message Should include a command and args. Seperated by #. Example: "set#" + feeder.toString()
     * @throws IOException Thrown when the feeders connection is lost during sending
     * @throws NullPointerException thrown if the feeder hasn't connected yet.
     */
    private void sendMessage(UUID uuid, String message) throws IOException, NullPointerException {

        FeederConnection connection = this.getConnection(uuid);

        if (connection == null){

            return;
        }
        connection.sendCommand(message);
    }

    /**
     * Called when a feeder connects to the server and when changes are made to its representation in the database.
     * And updates the feeder to its current status in the database
     * @param uuid Used to find the feeder in the connection hashmap
     * @param feeder All the settings to set on the feeder
     */
    public void updateFeeder(UUID uuid, Feeder feeder){

        String message = "set#" + feeder.toString();
        try {
            sendMessage(uuid, message);

        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }
}


