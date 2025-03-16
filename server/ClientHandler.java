package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;

/**
 * ClientHandler est un thread qui gère la communication avec un client unique.
 * Il hérite de la classe Thread de Java pour permettre l'exécution parallèle.
 * 
 * Cette classe travaille en tandem avec ChatServer :
 * - ClientHandler gère la communication individuelle avec un client
 * - ChatServer gère la liste des clients et la diffusion des messages
 * 
 * Le flux de communication est le suivant :
 * 1. ClientHandler reçoit un message du client
 * 2. Le message est transmis à ChatServer pour diffusion
 * 3. ChatServer diffuse le message à tous les autres clients
 * 4. Chaque ClientHandler envoie le message à son client respectif
 */
public class ClientHandler extends Thread {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String pseudo;

    /**
     * Constructeur de ClientHandler.
     * Initialise la connexion avec le client via le socket fourni.
     * 
     * @param socket le socket du client connecté au serveur
     */
    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    /**
     * Récupère le pseudonyme (surnom) du client.
     * 
     * @return Le pseudonyme du client
     */
    public String getPseudo() {
        return pseudo;
    }

    /**
     * Envoie un message au client associé à ce gestionnaire.
     * Utilise le flux de sortie du client pour envoyer les données.
     * 
     * @param message Le message à envoyer
     */
    public void sendMessage(String message) {
        out.println(message);
    }

    /**
     * Méthode principale du thread qui gère la communication avec le client.
     * Elle est appelée automatiquement lors du démarrage du thread.
     * 
     * Le processus est le suivant :
     * 1. Initialise les flux d'entrée/sortie pour ce client
     * 2. Lit le pseudonyme envoyé par le client
     * 3. Vérifie et assure l'unicité du pseudonyme
     * 4. Ajoute ce client à la liste du serveur
     * 5. Entre dans une boucle pour :
     *    - Lire les messages du client
     *    - Les diffuser aux autres clients
     *    - Gérer la déconnexion
     */
    @Override
    public void run() {
        try {
            // Initialise les flux d'entrée/sortie pour ce client
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Lit le pseudonyme envoyé par le client
            pseudo = in.readLine();
            if (pseudo == null) {
                // Le client s'est déconnecté immédiatement
                return;
            }

            // Assure l'unicité du pseudonyme sur le serveur
            String originalPseudo = pseudo;
            pseudo = ChatServer.getUniquePseudo(originalPseudo);
            if (!pseudo.equals(originalPseudo)) {
                // Informe le client du changement de pseudonyme
                out.println("Pseudonym already taken, you have been renamed to " + pseudo);
            }

            // Ajoute ce client à la liste du serveur et annonce aux autres
            ChatServer.addClient(this);
            System.out.println("New user connected: " + pseudo);
            ChatServer.broadcastMessage("User " + pseudo + " has joined the conversation.", this);
            // Message de bienvenue au nouveau client
            out.println("Welcome to the chat, " + pseudo + "!");

            String message;
            // Écoute les messages du client
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("exit")) {
                    // Le client a initié la déconnexion
                    out.println("You have been disconnected from the server.");
                    // Notifie les autres clients de la déconnexion
                    ChatServer.broadcastMessage("User " + pseudo + " has left the conversation.", this);
                    break;
                }
                // Diffuse le message reçu aux autres clients
                ChatServer.broadcastMessage(pseudo + ": " + message, this);
            }

            // Si on sort de la boucle sans un "exit" du client (message == null signifie déconnexion)
            if (message == null) {
                // Notifie les autres que le client est parti de manière inattendue
                ChatServer.broadcastMessage("User " + pseudo + " has left the conversation.", this);
            }
        } catch (IOException e) {
            // Gère les erreurs inattendues (ex: client déconnecté de force)
            System.err.println("Communication error with " + pseudo + ": " + e.getMessage());
            ChatServer.broadcastMessage("User " + pseudo + " has left the conversation.", this);
        } finally {
            // Retire ce client de la liste et ferme les ressources
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