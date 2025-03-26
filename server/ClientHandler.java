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
 * 
 * Note sur l'utilisation de 'final' :
 * - clientSocket : garantit que la connexion socket reste la même
 * - server : garantit que la référence au serveur ne change pas
 */
public class ClientHandler extends Thread {
    private final Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String pseudo;
    private final ChatServer server;

    /**
     * Constructeur de ClientHandler.
     * Initialise la connexion avec le client via le socket fourni.
     * 
     * @param socket le socket du client connecté au serveur
     * @param server l'instance du serveur qui gère ce client
     */
    public ClientHandler(Socket socket, ChatServer server) {
        this.clientSocket = socket;
        this.server = server;
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
     * Cette méthode est marquée @Override car elle redéfinit la méthode run()
     * de la classe Thread dont ClientHandler hérite. Quand start() est appelé,
     * un nouveau thread est créé et cette méthode run() est exécutée dans ce thread.
     * Cela permet de gérer chaque client de manière indépendante et parallèle.
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
                return;
            }

            // Assure l'unicité du pseudonyme sur le serveur
            String originalPseudo = pseudo;
            pseudo = server.getUniquePseudo(originalPseudo);
            if (!pseudo.equals(originalPseudo)) {
                out.println("Pseudonym already taken, you have been renamed to " + pseudo);
            }

            // Ajoute ce client à la liste du serveur
            server.addClient(this);

            // Log dans la console du serveur uniquement
            System.out.println("Nouvel utilisateur connecté: " + pseudo);
            
            // Annonce à tous les autres clients (broadcast) qu'un nouveau client est arrivé
            server.broadcastMessage("Utilisateur " + pseudo + " a rejoint la conversation.", this);
            
            // Message de bienvenue envoyé uniquement au nouveau client
            out.println("Bienvenue dans le chat, " + pseudo + "!");

            // Afficher la liste des participants
            StringBuilder participants = new StringBuilder("Participants actuels : ");
            boolean first = true;
            for (ClientHandler client : server.getClientHandlers()) {
                if (!first) {
                    participants.append(", ");
                }
                participants.append(client.getPseudo());
                first = false;
            }
            out.println(participants.toString());

            String message;
            // Écoute les messages du client
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("exit")) {
                    out.println("Vous avez été déconnecté du serveur.");
                    server.broadcastMessage("Utilisateur " + pseudo + " a quitté la conversation.", this);
                    break;
                }
                server.broadcastMessage(pseudo + ": " + message, this);
            }

            // Si on sort de la boucle sans un "exit" du client (message == null signifie déconnexion)
            if (message == null) {
                server.broadcastMessage("Utilisateur " + pseudo + " a quitté la conversation.", this);
            }
        } catch (IOException e) {
            // Gère les erreurs inattendues (ex: client déconnecté de force)
            System.err.println("Erreur de communication avec " + pseudo + ": " + e.getMessage());
            server.broadcastMessage("Utilisateur " + pseudo + " a quitté la conversation.", this);
        } finally {
            // Retire ce client de la liste et ferme les ressources
            if (pseudo != null) {
                System.out.println(pseudo + " a été déconnecté.");
                server.removeClient(this);
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