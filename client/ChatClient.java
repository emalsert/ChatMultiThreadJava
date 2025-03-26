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
            System.out.println("Connecté au serveur de chat " + hostname + " sur le port " + port);

            // Création et démarrage des threads
            new ReadThread(socket).start();
            new WriteThread(socket).start();

        } catch (IOException ex) {
            System.out.println("Erreur lors de la connexion au serveur : " + ex.getMessage());
        }
    }

    /**
     * Point d'entrée du programme.
     * Vérifie les arguments et lance le client.
     * 
     * @param args les arguments de la ligne de commande
     *             args[0] : hostname (localhost ou adresse IP valide)
     *             args[1] : port (optionnel, défaut: 12345)
     */
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 12345;

        try {
            if (args.length >= 1) {
                // Vérifie si l'hostname est valide
                if (!args[0].equals("localhost") && !isValidIPAddress(args[0])) {
                    System.err.println("Erreur: L'adresse du serveur doit être 'localhost' ou une adresse IP valide");
                    System.err.println("Exemple: java client.ChatClient localhost 12345");
                    System.err.println("        java client.ChatClient 192.168.1.100 12345");
                    System.exit(1);
                }
                hostname = args[0];
            }
            if (args.length >= 2) {
                port = Integer.parseInt(args[1]);
                if (port <= 0 || port > 65535) {
                    System.err.println("Erreur: Le port doit être compris entre 1 et 65535");
                    System.exit(1);
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("Erreur: Le numéro de port doit être un nombre valide");
            System.exit(1);
        }

        ChatClient client = new ChatClient(hostname, port);
        client.execute();
    }

    /**
     * Vérifie si une chaîne est une adresse IP valide.
     * 
     * @param ip l'adresse IP à vérifier
     * @return true si l'adresse IP est valide, false sinon
     */
    private static boolean isValidIPAddress(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }
        try {
            for (String part : parts) {
                int value = Integer.parseInt(part);
                if (value < 0 || value > 255) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
} 