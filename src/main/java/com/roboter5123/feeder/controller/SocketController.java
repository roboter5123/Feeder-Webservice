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

    public void run() {

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

    public Feeder addConnection(FeederConnection newConnection) {

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

    public FeederConnection getConnection(UUID uuid) throws NullPointerException {

        return this.connections.get(uuid);
    }

    public void dispense(UUID uuid, Dispensation dispensation) {

        try {

            String message = "dispense#" + dispensation.getAmount();
            sendMessage(uuid, message);

        } catch (IOException | NullPointerException e) {

            throw new GoneException();
        }
    }

    public void sendMessage(UUID uuid, String message) throws IOException, NullPointerException {

        FeederConnection connection = this.getConnection(uuid);

        if (connection == null){

            return;
        }
        connection.sendCommand(message);
    }

    public void updateFeeder(UUID uuid, Feeder feeder){

        String message = "set#" + feeder.toString();
        try {
            sendMessage(uuid, message);

        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }
}


