package server;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Ask for username
            out.println("Enter your username:");
            username = in.readLine();
            System.out.println(username + " joined the chat.");
            Server.broadcast(username + " has joined the chat!", this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("[" + username + "]: " + message);
                Server.broadcast("[" + username + "]: " + message, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                Server.removeClient(this);
                Server.broadcast(username + " has left the chat!", this);
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}
