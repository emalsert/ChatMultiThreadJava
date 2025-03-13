package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;

/**
 * ReadThread is a thread that listens for messages from the server and prints them to the console.
 * It runs in the background to constantly receive server messages.
 */
public class ReadThread extends Thread {
    private Socket socket;
    private BufferedReader in;

    /**
     * Constructs a ReadThread for a given client socket.
     * 
     * @param socket the socket connected to the chat server.
     */
    public ReadThread(Socket socket) {
        this.socket = socket;
    }

    /**
     * Continuously reads messages from the server and displays them.
     * If the server connection is lost or closed, it handles the termination of the client.
     */
    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                if (serverMessage.equals("You have been disconnected from the server.")) {
                    // Server is informing this client of disconnection
                    System.out.println(serverMessage);
                    break;
                }
                System.out.println(serverMessage);
            }
            if (serverMessage == null) {
                // Server connection closed unexpectedly
                System.out.println("Lost connection to server.");
            }
        } catch (IOException e) {
            System.out.println("Lost connection to server.");
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                // Ignore errors on close
            }
            // Exit the program if the server has disconnected
            System.exit(0);
        }
    }
} 