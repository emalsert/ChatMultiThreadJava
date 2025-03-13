package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;

/**
 * ClientHandler is a thread that handles communication with a single client.
 * It receives messages from the client and broadcasts them to others via the ChatServer.
 */
public class ClientHandler extends Thread {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String pseudo;

    /**
     * Constructs a new ClientHandler for a given client socket.
     * 
     * @param socket the client socket connected to the server.
     */
    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    /**
     * Gets the pseudonym (nickname) of the client.
     * 
     * @return The client's pseudonym.
     */
    public String getPseudo() {
        return pseudo;
    }

    /**
     * Sends a message to the client associated with this handler.
     * This uses the client's output stream to send data.
     * 
     * @param message The message to send.
     */
    public void sendMessage(String message) {
        out.println(message);
    }

    /**
     * The run method of the thread which handles the client's communication.
     * It listens for incoming messages from the client and broadcasts them to other clients.
     * It also handles client connection setup (unique pseudonym assignment) and disconnection.
     */
    @Override
    public void run() {
        try {
            // Initialize input and output streams for this client
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Read the pseudonym sent by the client
            pseudo = in.readLine();
            if (pseudo == null) {
                // Client disconnected immediately
                return;
            }

            // Ensure the pseudonym is unique on the server
            String originalPseudo = pseudo;
            pseudo = ChatServer.getUniquePseudo(originalPseudo);
            if (!pseudo.equals(originalPseudo)) {
                // Inform client of pseudonym change
                out.println("Pseudonym already taken, you have been renamed to " + pseudo);
            }

            // Add this client to the server's list and announce to others
            ChatServer.addClient(this);
            System.out.println("New user connected: " + pseudo);
            ChatServer.broadcastMessage("User " + pseudo + " has joined the conversation.", this);
            // Welcome message to the new client
            out.println("Welcome to the chat, " + pseudo + "!");

            String message;
            // Listen for messages from the client
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("exit")) {
                    // Client initiated disconnection
                    out.println("You have been disconnected from the server.");
                    // Notify other clients about the disconnection
                    ChatServer.broadcastMessage("User " + pseudo + " has left the conversation.", this);
                    break;
                }
                // Broadcast the received message to other clients
                ChatServer.broadcastMessage(pseudo + ": " + message, this);
            }

            // If we exit the loop without a client-initiated "exit" (message == null means disconnect)
            if (message == null) {
                // Notify others that the client has left unexpectedly
                ChatServer.broadcastMessage("User " + pseudo + " has left the conversation.", this);
            }
        } catch (IOException e) {
            // Handle unexpected errors (e.g., client forcibly disconnected)
            System.err.println("Communication error with " + pseudo + ": " + e.getMessage());
            ChatServer.broadcastMessage("User " + pseudo + " has left the conversation.", this);
        } finally {
            // Remove this client from the list and close resources
            if (pseudo != null) {
                System.out.println(pseudo + " has disconnected.");
                ChatServer.removeClient(this);
            }
            try {
                if (in != null) in.close();
            } catch (IOException ex) { /* Ignore close errors */ }
            if (out != null) out.close();
            try {
                if (clientSocket != null) clientSocket.close();
            } catch (IOException ex) { /* Ignore close errors */ }
        }
    }
} 