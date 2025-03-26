package client;

import java.io.PrintWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * WriteThread est un thread qui gère l'envoi des messages vers le serveur.
 * Il hérite de la classe Thread de Java pour permettre l'exécution parallèle.
 * 
 * Cette classe travaille en tandem avec ReadThread :
 * - WriteThread gère l'envoi des messages (CLIENT → SERVEUR)
 * - ReadThread gère la réception des messages (SERVEUR → CLIENT)
 * 
 * Direction des flux :
 * 1. L'utilisateur tape un message dans la console
 * 2. WriteThread lit ce message via BufferedReader (System.in)
 * 3. Le message est envoyé au serveur via PrintWriter (socket.getOutputStream)
 * 4. Le serveur reçoit et traite le message
 * 
 * Note : Ne pas confondre les deux BufferedReader utilisés :
 * - reader : lit l'entrée utilisateur (System.in, console)
 * - ReadThread utilise un autre BufferedReader pour les messages du serveur
 */
public class WriteThread extends Thread {
    private Socket socket;
    private PrintWriter out;       // Pour envoyer des messages VERS le serveur
    private BufferedReader reader; // Pour lire l'entrée de l'utilisateur (console)

    /**
     * Constructeur de WriteThread.
     * Initialise la connexion pour envoyer des messages au serveur.
     * 
     * @param socket le socket connecté au serveur, utilisé pour créer le flux de sortie
     */
    public WriteThread(Socket socket) {
        this.socket = socket;
    }

    /**
     * Méthode principale du thread.
     * @Override car on redéfinit la méthode vide run() de la classe Thread.
     * C'est cette méthode qui sera exécutée dans un nouveau thread quand on appelle start().
     * 
     * Le processus est le suivant :
     * 1. Configure :
     *    - PrintWriter sur le flux de sortie du socket (vers serveur)
     *    - BufferedReader sur System.in (entrée console)
     * 2. Demande et envoie le pseudonyme au serveur
     * 3. Entre dans une boucle pour :
     *    - Lire les messages tapés par l'utilisateur
     *    - Les envoyer au serveur
     *    - Gérer la commande de sortie ("exit")
     */
    @Override
    public void run() {
        try {
            // Configure le flux de sortie vers le serveur
            out = new PrintWriter(socket.getOutputStream(), true);
            // Configure la lecture de l'entrée utilisateur (console)
            reader = new BufferedReader(new InputStreamReader(System.in));

            // Demande et envoie le pseudonyme
            System.out.print("Entrez votre pseudonyme: ");
            System.out.flush();
            String pseudonyme = reader.readLine(); 
            out.println(pseudonyme);              

            System.out.println("Vous pouvez maintenant envoyer des messages. Tapez 'exit' pour quitter.");

            // Boucle principale de lecture des messages utilisateur et envoi au serveur
            while (true) {
                String message = reader.readLine();  
                if (message == null || message.equalsIgnoreCase("exit")) {
                    out.println("exit");            
                    break;
                }
                out.println(message);             
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de l'envoi au serveur : " + e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                System.err.println("Erreur lors de la fermeture du lecteur ou de l'envoi : " + e.getMessage());
            }
        }
    }
}