package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;


public class Client {

    private final Scanner sc = new Scanner(System.in);

    private Socket socket;

    private final ObjectInputStream in;
    private final ObjectOutputStream out;

    private User user;


    public Client() throws IOException {

        CreateUser();

        StartConnection();

        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());

        SendInitialData();

        ListenMessages();
        StartChat();

    }

    public static void main(String[] args) throws IOException {

        new Client();

    }

    private void CreateUser() {

        System.out.println("Enter your nickname: ");
        this.user = new User(sc.nextLine());

    }

    private void SendInitialData() throws IOException {

        out.writeObject(user);
        out.flush();

        out.writeUTF(user.getName());
        out.flush();

    }

    private void StartConnection() throws IOException {

        this.socket = new Socket("localhost", 5000);

    }

    private void StartChat() {

        while (socket.isConnected()) {

            WriteMessages();

        }

    }

    private void WriteMessages() {

        String msg;
        try {

            msg = sc.nextLine()+"\r\n".strip();
            if (!msg.isBlank()) {

                out.writeUTF(msg);
                out.flush();

            }

        } catch (IOException e) {

            CloseConnection();

        }

    }

    private void ListenMessages() {

        new Thread(() -> {

            while (socket.isConnected()) {

                try {

                    System.out.println(in.readUTF());

                } catch (IOException e) {

                    CloseConnection();
                    break;

                }

            }

        }).start();

    }

    private void CloseConnection() {

        try {

            socket.close();
            in.close();
            out.close();

        } catch (IOException ignored) { }

    }

}
