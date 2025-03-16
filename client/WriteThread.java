package client;

import java.io.PrintWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * WriteThread est un thread qui gère l'entrée utilisateur depuis la console et l'envoie au serveur.
 * Il hérite de la classe Thread de Java pour permettre l'exécution parallèle.
 * 
 * Cette classe travaille en tandem avec ReadThread :
 * - WriteThread gère l'envoi des messages (sortie)
 * - ReadThread gère la réception des messages (entrée)
 * 
 * Le flux de communication est le suivant :
 * 1. L'utilisateur tape un message dans la console
 * 2. WriteThread capture ce message via BufferedReader
 * 3. Le message est envoyé au serveur via PrintWriter
 * 4. ReadThread reçoit les messages du serveur et les affiche
 */
public class WriteThread extends Thread {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader reader;

    /**
     * Constructeur de WriteThread.
     * Initialise la connexion avec le serveur via le socket fourni.
     * 
     * @param socket le socket connecté au serveur de chat
     */
    public WriteThread(Socket socket) {
        this.socket = socket;
    }

    /**
     * Méthode principale du thread.
     * Elle est appelée automatiquement lors du démarrage du thread.
     * 
     * Le processus est le suivant :
     * 1. Demande un pseudonyme à l'utilisateur
     * 2. Envoie ce pseudonyme au serveur
     * 3. Entre dans une boucle infinie pour :
     *    - Lire les messages de l'utilisateur
     *    - Les envoyer au serveur
     *    - Gérer la commande de sortie ("exit")
     */
    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(System.in));

            // Demande et envoie le pseudonyme
            System.out.print("Enter your pseudonym: ");
            System.out.flush();
            String pseudonyme = reader.readLine();
            out.println(pseudonyme);

            System.out.println("You can now send messages. Type 'exit' to quit.");

            // Boucle principale de lecture et envoi des messages
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