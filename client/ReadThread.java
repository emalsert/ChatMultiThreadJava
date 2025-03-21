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
 * - ReadThread gère la réception des messages (SERVEUR → CLIENT)
 * - WriteThread gère l'envoi des messages (CLIENT → SERVEUR)
 * 
 * Direction des flux :
 * 1. Le serveur envoie un message via son PrintWriter
 * 2. Ce message arrive sur le Socket du client
 * 3. ReadThread lit ce message via son BufferedReader (socket.getInputStream)
 * 4. Le message est affiché dans la console du client
 * 
 * Note : Ne pas confondre avec System.in (console) qui est géré par WriteThread
 */
public class ReadThread extends Thread {
    private Socket socket;
    private BufferedReader in;  // Pour lire les messages VENANT du serveur

    /**
     * Constructeur de ReadThread.
     * Initialise la connexion pour recevoir les messages du serveur.
     * 
     * @param socket le socket connecté au serveur, utilisé pour créer le flux d'entrée
     */
    public ReadThread(Socket socket) {
        this.socket = socket;
    }

    /**
     * Méthode principale du thread.
     * @Override car on redéfinit la méthode vide run() de la classe Thread.
     * C'est cette méthode qui sera exécutée dans un nouveau thread quand on appelle start().
     * 
     * Le processus est le suivant :
     * 1. Configure le BufferedReader sur le flux d'entrée du socket (messages du serveur)
     * 2. Entre dans une boucle infinie pour :
     *    - Attendre et lire les messages venant du serveur
     *    - Les afficher dans la console du client
     *    - Gérer la déconnexion si le serveur ferme la connexion
     */
    @Override
    public void run() {
        try {
            // Crée le lecteur pour recevoir les messages du serveur
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Boucle principale de lecture des messages venant du serveur
            while (true) {
                String message = in.readLine();
                if (message == null) {
                    System.out.println("Server disconnected.");
                    break;
                }
                // Affiche le message reçu dans la console du client
                System.out.println(message);
            }
        } catch (IOException e) {
            System.err.println("Error reading from server: " + e.getMessage());
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