package client;

import java.io.PrintWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * WriteThread is a thread that handles user input from the console and sends it to the server.
 * It runs in parallel to allow the user to type messages while the ReadThread listens for server messages.
 */
public class WriteThread extends Thread {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader reader;

    /**
     * Constructs a WriteThread for a given client socket.
     * 
     * @param socket the socket connected to the chat server.
     */
    public WriteThread(Socket socket) {
        this.socket = socket;
    }

    /**
     * Reads user input from the console and sends it to the server.
     * It first prompts for a unique pseudonym, then continually reads messages to send.
     * If the user types "exit", it will inform the server and terminate the connection.
     */
    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(System.in));

            // Prompt for pseudonym and send it to the server
            System.out.print("Enter your pseudonym: ");
            System.out.flush();
            String pseudonyme = reader.readLine();
            out.println(pseudonyme);

            System.out.println("You can now send messages. Type 'exit' to quit.");

            // Read messages from the user and send them to the server
            while (true) {
                String message = reader.readLine();
                if (message == null || message.equalsIgnoreCase("exit")) {
                    out.println("exit");
                    break;
                }
                out.println(message);
            }
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing reader: " + e.getMessage());
            }
        }
    }
} 