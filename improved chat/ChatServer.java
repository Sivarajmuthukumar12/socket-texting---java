import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ChatServer {
    private static final int PORT = 12345;  // Port for the server
    private static Set<ClientHandler> clientHandlers = new HashSet<>();  // Set to keep track of active clients

    public static void main(String[] args) {
        System.out.println("Chat Server Started...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                new ClientHandler(serverSocket.accept()).start();  // Accept client connection and start handler thread
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Broadcast message to all clients in the group chat
    public static void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clientHandlers) {
            if (client != sender) {  // Don't send message to the sender
                client.sendMessage(message);
            }
        }
    }

    // Send a private message to a specific client
    public static void sendPrivateMessage(String message, ClientHandler sender, String recipientName) {
        for (ClientHandler client : clientHandlers) {
            if (client.getClientName().equalsIgnoreCase(recipientName)) {
                if (client.isBlocked(sender.getClientName())) {
                    sender.sendMessage("You are blocked by " + recipientName + ". You cannot send private messages.");
                } else {
                    client.sendMessage("Private from " + sender.getClientName() + ": " + message);
                    sender.sendMessage("Private to " + recipientName + ": " + message);
                }
                return;
            }
        }
        sender.sendMessage("User " + recipientName + " not found.");
    }

    // List active users
    public static void listActiveUsers(ClientHandler requester) {
        StringBuilder userList = new StringBuilder("Active Users: ");
        for (ClientHandler client : clientHandlers) {
            userList.append(client.getClientName()).append(" ");
        }
        requester.sendMessage(userList.toString());
    }

    // Block a user from sending private messages
    public static void blockUser(ClientHandler blocker, String blockedName) {
        for (ClientHandler client : clientHandlers) {
            if (client.getClientName().equalsIgnoreCase(blockedName)) {
                blocker.addBlockedUser(blockedName);
                blocker.sendMessage(blockedName + " has been blocked from sending private messages.");
                return;
            }
        }
        blocker.sendMessage("User " + blockedName + " not found.");
    }

    // Unblock a user
    public static void unblockUser(ClientHandler unblocker, String unblockedName) {
        if (unblocker.removeBlockedUser(unblockedName)) {
            unblocker.sendMessage(unblockedName + " has been unblocked.");
        } else {
            unblocker.sendMessage("User " + unblockedName + " was not blocked.");
        }
    }

    // Client Handler for managing each individual client
    static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String clientName;
        private Set<String> blockedUsers = new HashSet<>();

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public String getClientName() {
            return clientName;
        }

        public boolean isBlocked(String user) {
            return blockedUsers.contains(user);
        }

        public void addBlockedUser(String user) {
            blockedUsers.add(user);
        }

        public boolean removeBlockedUser(String user) {
            return blockedUsers.remove(user);
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Welcome message with command guide
                out.println("Welcome to the Chat Application!");
                out.println("Please enter your name:");

                clientName = in.readLine();  // Get the name of the client
                synchronized (clientHandlers) {
                    clientHandlers.add(this);  // Add this client to the set of active clients
                }

                // Send welcome message and list of commands
                out.println("Hello, " + clientName + "! You are now in the group chat.");
                out.println("Here are the commands you can use:");
                out.println("/list  - View the list of active users in the chat");
                out.println("/private [username] [message] - Send a private message to a specific user");
                out.println("/block [username] - Block a user from sending you private messages");
                out.println("/unblock [username] - Unblock a previously blocked user");
                out.println("/exit - Exit the chat");
                out.println("To send a message to the group, simply type and press Enter.");

                // Notify all clients that a new user has joined
                broadcast(clientName + " has joined the chat!", this);

                String message;
                while ((message = in.readLine()) != null) {
                    // Handle user commands
                    if (message.startsWith("/private ")) {
                        String[] parts = message.split(" ", 3);
                        if (parts.length >= 3) {
                            sendPrivateMessage(parts[2], this, parts[1]);  // Format: /private [username] [message]
                        }
                    } else if (message.equalsIgnoreCase("/list")) {
                        listActiveUsers(this);  // Show list of active users
                    } else if (message.startsWith("/block ")) {
                        blockUser(this, message.split(" ")[1]);
                    } else if (message.startsWith("/unblock ")) {
                        unblockUser(this, message.split(" ")[1]);
                    } else if (message.equalsIgnoreCase("/exit")) {
                        break;  // Exit the chat
                    } else {
                        // Broadcast to the group chat
                        broadcast(clientName + ": " + message, this);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                    synchronized (clientHandlers) {
                        clientHandlers.remove(this);  // Remove client from active set
                    }
                    broadcast(clientName + " has left the chat.", this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }
    }
}
