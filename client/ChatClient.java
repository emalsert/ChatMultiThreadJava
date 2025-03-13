package client;

import java.net.Socket;
import java.io.IOException;

/**
 * ChatClient is the main class for the chat client application.
 * It connects to the chat server and starts two threads:
 * one for reading messages from the server, and one for sending user input to the server.
 */
public class ChatClient {

    /** Default server host if none provided. */
    private static final String DEFAULT_HOST = "localhost";
    /** Default server port if none provided. */
    private static final int DEFAULT_PORT = 1234;

    /**
     * The main method to start the chat client.
     * Connects to the server at the specified host and port, then starts 
     * the ReadThread and WriteThread for message handling.
     * 
     * @param args Command-line arguments (optional host and port).
     */
    public static void main(String[] args) {
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            host = args[0];
        }
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port, using default port " + DEFAULT_PORT);
                port = DEFAULT_PORT;
            }
        }

        try {
            Socket socket = new Socket(host, port);
            System.out.println("Connected to chat server " + host + " on port " + port);

            // Start the thread to listen for server messages
            ReadThread readThread = new ReadThread(socket);
            readThread.start();

            // Start the thread to handle user input and send messages
            WriteThread writeThread = new WriteThread(socket);
            writeThread.start();
        } catch (IOException ex) {
            System.err.println("Unable to connect to server: " + ex.getMessage());
        }
    }
} 