package client;

import java.net.Socket;
import java.io.IOException;

/**
 * ChatClient est la classe principale du client de chat.
 * Elle ne fait pas d'héritage mais coordonne les deux threads principaux :
 * - ReadThread : pour la réception des messages
 * - WriteThread : pour l'envoi des messages
 * 
 * Le flux de fonctionnement est le suivant :
 * 1. ChatClient établit la connexion avec le serveur
 * 2. Crée et démarre ReadThread pour écouter les messages
 * 3. Crée et démarre WriteThread pour gérer les entrées utilisateur
 * 4. Les deux threads s'exécutent en parallèle
 * 
 * Cette architecture multi-thread permet :
 * - De recevoir des messages même pendant qu'on en écrit
 * - Une interface utilisateur réactive
 * - Une gestion efficace des entrées/sorties
 */
public class ChatClient {
    private String hostname;
    private int port;
    private String username;

    /**
     * Constructeur de ChatClient.
     * Initialise les paramètres de connexion.
     * 
     * @param hostname l'adresse du serveur
     * @param port le port du serveur
     */
    public ChatClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    /**
     * Méthode principale qui établit la connexion et démarre les threads.
     * 
     * Le processus est le suivant :
     * 1. Crée un socket pour se connecter au serveur
     * 2. Crée et démarre ReadThread pour la réception
     * 3. Crée et démarre WriteThread pour l'envoi
     * 4. Les threads s'exécutent en parallèle
     */
    public void execute() {
        try {
            Socket socket = new Socket(hostname, port);
            System.out.println("Connected to chat server " + hostname + " on port " + port);

            // Création et démarrage des threads
            new ReadThread(socket).start();
            new WriteThread(socket).start();

        } catch (IOException ex) {
            System.out.println("Error connecting to server: " + ex.getMessage());
        }
    }

    /**
     * Point d'entrée du programme.
     * Vérifie les arguments et lance le client.
     * 
     * @param args les arguments de la ligne de commande
     *             args[0] : hostname (optionnel, défaut: localhost)
     *             args[1] : port (optionnel, défaut: 1234)
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Syntax: java ChatClient <hostname> <port>");
            System.exit(0);
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        ChatClient client = new ChatClient(hostname, port);
        client.execute();
    }
} 