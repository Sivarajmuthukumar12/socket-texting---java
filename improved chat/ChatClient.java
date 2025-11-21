import java.io.*;
import java.net.*;

public class ChatClient {
    private static final String SERVER_ADDRESS = "172.16.23.170";  // Change to your server's local IP address
    private static final int SERVER_PORT = 12345;  // Port to connect to

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
             BufferedReader serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter serverOutput = new PrintWriter(socket.getOutputStream(), true)) {

            // Handle incoming messages from the server in a separate thread
            Thread incomingMessages = new Thread(() -> {
                String message;
                try {
                    while ((message = serverInput.readLine()) != null) {
                        System.out.println(message);  // Print received messages
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            incomingMessages.start();

            // Send messages to the server
            String name = consoleInput.readLine();  // User enters name
            serverOutput.println(name);  // Send name to the server

            String message;
            while ((message = consoleInput.readLine()) != null) {
                if (message.equalsIgnoreCase("/exit")) {
                    serverOutput.println("/exit");  // Notify the server to exit the chat
                    break;  // Exit the chat
                } else if (message.equalsIgnoreCase("/list")) {
                    serverOutput.println("/list");  // Request the list of users
                } else if (message.startsWith("/block ")) {
                    serverOutput.println(message);  // Block a user
                } else if (message.startsWith("/unblock ")) {
                    serverOutput.println(message);  // Unblock a user
                } else if (message.startsWith("/private ")) {
                    serverOutput.println(message);  // Send private message
                } else {
                    serverOutput.println(message);  // Send general message
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
