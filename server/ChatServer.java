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
 * ChatServer est la classe principale du serveur de chat.
 * Elle ne fait pas d'héritage mais gère la liste des clients connectés et coordonne
 * les communications entre eux via les ClientHandler.
 * 
 * Cette classe travaille en tandem avec ClientHandler :
 * - ChatServer gère la liste des clients et la diffusion des messages
 * - ClientHandler gère la communication individuelle avec chaque client
 * 
 * Le flux de fonctionnement est le suivant :
 * 1. ChatServer démarre et écoute les connexions entrantes
 * 2. Pour chaque nouveau client, un ClientHandler est créé
 * 3. Le ClientHandler est ajouté à la liste des clients actifs
 * 4. Les messages sont diffusés à tous les clients via leurs ClientHandler
 */
public class ChatServer {

    /** Liste des gestionnaires de clients pour tous les clients connectés. */
    private static final List<ClientHandler> clientHandlers = new ArrayList<>();

    /** Port par défaut du serveur. */
    private static final int DEFAULT_PORT = 1234;

    /**
     * Ajoute un nouveau gestionnaire de client à la liste des clients connectés.
     * Cette méthode est synchronisée pour éviter les modifications concurrentes.
     * 
     * @param client Le ClientHandler à ajouter
     */
    public static synchronized void addClient(ClientHandler client) {
        clientHandlers.add(client);
    }

    /**
     * Retire un gestionnaire de client de la liste des clients connectés.
     * Cette méthode est synchronisée pour éviter les modifications concurrentes.
     * 
     * @param client Le ClientHandler à retirer
     */
    public static synchronized void removeClient(ClientHandler client) {
        clientHandlers.remove(client);
    }

    /**
     * Génère un pseudonyme unique pour un nouveau client en ajoutant un nombre
     * si nécessaire pour éviter les doublons. Cette méthode vérifie les pseudonymes
     * existants des clients connectés.
     * 
     * @param desiredPseudo Le pseudonyme demandé par le nouveau client
     * @return Un pseudonyme unique (soit le même que desiredPseudo si non pris, soit modifié avec un nombre)
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
     * Diffuse un message à tous les clients connectés sauf l'expéditeur.
     * Cette méthode est synchronisée pour éviter les modifications concurrentes
     * de la liste des clients.
     * 
     * @param message Le message à diffuser
     * @param sender Le ClientHandler du client envoyant le message (à exclure)
     */
    public static synchronized void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : clientHandlers) {
            // Envoie le message à tous sauf l'expéditeur
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    /**
     * Point d'entrée du programme serveur.
     * Il écoute sur un port spécifié (ou le port par défaut) pour les connexions
     * entrantes. Pour chaque nouvelle connexion, un thread ClientHandler est démarré.
     * 
     * @param args Arguments de la ligne de commande (port optionnel comme premier argument)
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
            // Boucle infinie pour accepter les connexions entrantes
            while (true) {
                Socket clientSocket = serverSocket.accept();
                // Crée un nouveau thread pour le client connecté
                ClientHandler clientThread = new ClientHandler(clientSocket);
                clientThread.start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 