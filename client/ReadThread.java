package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * ReadThread est un thread qui gère la réception des messages du serveur.
 * Il hérite de la classe Thread de Java pour permettre l'exécution parallèle.
 * 
 * Cette classe travaille en tandem avec WriteThread :
 * - ReadThread gère la réception des messages (entrée)
 * - WriteThread gère l'envoi des messages (sortie)
 * 
 * Le flux de communication est le suivant :
 * 1. Le serveur envoie un message
 * 2. ReadThread le reçoit via BufferedReader
 * 3. Le message est affiché dans la console
 * 4. WriteThread continue à écouter les entrées utilisateur
 */
public class ReadThread extends Thread {
    private Socket socket;
    private BufferedReader in;

    /**
     * Constructeur de ReadThread.
     * Initialise la connexion avec le serveur via le socket fourni.
     * 
     * @param socket le socket connecté au serveur de chat
     */
    public ReadThread(Socket socket) {
        this.socket = socket;
    }

    /**
     * Méthode principale du thread.
     * Elle est appelée automatiquement lors du démarrage du thread.
     * 
     * Le processus est le suivant :
     * 1. Configure le BufferedReader pour lire les messages du serveur
     * 2. Entre dans une boucle infinie pour :
     *    - Lire les messages du serveur
     *    - Les afficher dans la console
     *    - Gérer la déconnexion si le serveur se déconnecte
     */
    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Boucle principale de lecture des messages
            while (true) {
                String message = in.readLine();
                if (message == null) {
                    System.out.println("Server disconnected.");
                    break;
                }
                System.out.println(message);
            }
        } catch (IOException e) {
            System.err.println("Error reading message: " + e.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing reader: " + e.getMessage());
            }
        }
    }
} 