package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * ChatServer is the main server class that listens for incoming client connections 
 * and broadcasts messages to all connected clients. It uses a separate thread 
 * (ClientHandler) for each connected client to handle communication.
 */
public class ChatServer {

    /** List of client handler threads for all connected clients. */
    private static final List<ClientHandler> clientHandlers = new ArrayList<>();

    /** Default port number for the server. */
    private static final int DEFAULT_PORT = 1234;

    /**
     * Adds a new client handler to the list of connected clients.
     * 
     * @param client The ClientHandler to add.
     */
    public static synchronized void addClient(ClientHandler client) {
        clientHandlers.add(client);
    }

    /**
     * Removes a client handler from the list of connected clients.
     * 
     * @param client The ClientHandler to remove.
     */
    public static synchronized void removeClient(ClientHandler client) {
        clientHandlers.remove(client);
    }

    /**
     * Generates a unique pseudonym for a new client by appending a number if needed
     * to avoid duplicates. This method checks existing connected clients' pseudonyms.
     * 
     * @param desiredPseudo The pseudonym requested by the new client.
     * @return A unique pseudonym (either the same as desiredPseudo if not taken, or modified with a number).
     */
    public static synchronized String getUniquePseudo(String desiredPseudo) {
        String newPseudo = desiredPseudo;
        int count = 1;
        boolean exists;
        do {
            exists = false;
            for (ClientHandler client : clientHandlers) {
                if (client.getPseudo() != null && client.getPseudo().equalsIgnoreCase(newPseudo)) {
                    exists = true;
                    newPseudo = desiredPseudo + count;
                    count++;
                    break;
                }
            }
        } while (exists);
        return newPseudo;
    }

    /**
     * Broadcasts a message to all connected clients except the sender.
     * This method is synchronized to prevent concurrent modifications to the clients list.
     * 
     * @param message The message to broadcast.
     * @param sender  The ClientHandler of the client sending the message (to be excluded).
     */
    public static synchronized void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : clientHandlers) {
            // Send the message to everyone except the sender
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    /**
     * The main method to start the chat server.
     * It listens on a specified port (or default port) for incoming client connections.
     * For each new connection, a ClientHandler thread is started.
     * 
     * @param args Command-line arguments (optional port number as first argument).
     */
    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port, using default port " + DEFAULT_PORT);
                port = DEFAULT_PORT;
            }
        }
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Chat server started on port " + port + ".");
            System.out.println("Waiting for client connections...");
            // Infinite loop to accept incoming client connections
            while (true) {
                Socket clientSocket = serverSocket.accept();
                // Create a new thread for the connected client
                ClientHandler clientThread = new ClientHandler(clientSocket);
                clientThread.start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 