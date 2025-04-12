package client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.IOException;

public class ChatUI extends Application {
    private TextArea chatArea;
    private TextField messageField;
    private Client client;
    private String username;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        client = new Client();
        username = getUsername();
        client.sendMessage(username);

        // UI Components
        chatArea = new TextArea();
        chatArea.setEditable(false);

        messageField = new TextField();
        messageField.setPromptText("Type your message...");

        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendMessage());

        VBox layout = new VBox(10, chatArea, messageField, sendButton);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setTitle("Chat Client");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> client.closeConnection());
        primaryStage.show();

        // Start listening for messages
        new Thread(this::listenForMessages).start();
    }

    private String getUsername() {
        TextInputDialog dialog = new TextInputDialog("User");
        dialog.setTitle("Username");
        dialog.setHeaderText("Enter your username:");
        dialog.setContentText("Username:");
        return dialog.showAndWait().orElse("Anonymous");
    }

    private void listenForMessages() {
        try {
            BufferedReader in = client.getInputStream();
            String message;
            while ((message = in.readLine()) != null) {
                updateChatArea(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            client.sendMessage("[" + username + "]: " + message);
            messageField.clear();
        }
    }

    private void updateChatArea(String message) {
        Platform.runLater(() -> chatArea.appendText(message + "\n"));
    }
}
