package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectionHandler implements Runnable, Serializable {

    private final Socket socket;

    private final ObjectInputStream in;
    private final ObjectOutputStream out;

    private Object user;
    private String name;

    static ArrayList<ConnectionHandler> clientSocketList = new ArrayList<>();


    public ConnectionHandler(Socket socket) throws IOException {

        this.socket = socket;

        this.in = new ObjectInputStream(socket.getInputStream());
        this.out = new ObjectOutputStream(socket.getOutputStream());

        SetNewUser();

    }

    public void SetNewUser() {

        try {

            user = in.readObject();
            name = in.readUTF();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        Server.clientMap.put(user, name);
        clientSocketList.add(this);

        SendMessageToGroup(user + " connected");

        Server.LogMessage("Client.User: " + user + " name: " + name);

    }

    private String ShowUsersConnected() {

        String listOfUsers = "";

        for (ConnectionHandler c : clientSocketList)
            listOfUsers = listOfUsers.concat("Client.User: " + c.user + "\n" +
                    "Name: " + c.name + "\n");

        listOfUsers = listOfUsers.concat("Total users connected: " + clientSocketList.size());

        return listOfUsers;

    }

    private void Chat() {

        String msg;

        do {

            try {

                msg = in.readUTF();

            } catch (IOException s) {

                CloseConnection();
                break;

            }

            Server.LogMessage(name + ": " + msg);
            SendMessageToGroup(name + ": " + msg);

        } while (socket.isConnected());

    }

    public void SendMessageToGroup(String message) {

        try {

            for (ConnectionHandler client : clientSocketList) {

                if (!client.user.equals(this.user)) {

                    client.out.writeUTF(message);
                    client.out.flush();

                }

            }

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    @Override
    public void run() {

        Chat();

    }

    private void CloseConnection() {

        if (!socket.isConnected()) {

            try {

                socket.close();
                in.close();
                out.close();

            } catch (IOException ignored) {

            }

        }

    }

}
