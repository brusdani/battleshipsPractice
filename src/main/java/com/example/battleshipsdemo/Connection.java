package com.example.battleshipsdemo;

import com.example.battleshipsdemo.GameServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(Connection.class);

    private GameServer gameServer;

    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public Connection(Socket socket, GameServer gameServer) {
        this.gameServer = gameServer;
        this.socket = socket;

        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }


    @Override
    public void run() {


    }
}
