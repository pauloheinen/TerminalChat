package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Objects;

public class Server {

    private ServerSocket serverSocket;

    static HashMap<Object, String> clientMap = new HashMap<>();


    public static void main(String[] args) {

        new Server();

    }

    public Server() {

        ServerStartup();
        AcceptConnection();

    }

    private void ServerStartup() {

        try {

            this.serverSocket = new ServerSocket(5000);
            LogMessage("Server initiated.");

        } catch (IOException e) {

            LogMessage("Server Could not be started");

        }

    }

    private void AcceptConnection() {

        Socket clientSocket = null;

        do {

            try {

                clientSocket = serverSocket.accept();

                Runnable connectionHandler = new ConnectionHandler(Objects.requireNonNull(clientSocket));
                new Thread(connectionHandler).start();

            } catch (IOException e) {

                LogMessage("An error occurred when trying to connect an user!");

            }

        } while (Objects.requireNonNull(clientSocket).isConnected());

    }

    public static void LogMessage(String message) {

        System.out.println("Server log: " + message);

    }

}

