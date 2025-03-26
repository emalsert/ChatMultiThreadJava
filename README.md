# Chat Multi-Thread Java

Une application de chat multi-threadée simple écrite en Java. L'application se compose d'un serveur capable de gérer plusieurs clients simultanément et d'un client pouvant se connecter au serveur pour échanger des messages.

## Fonctionnalités

- Serveur multi-thread gérant plusieurs clients
- Diffusion des messages en temps réel
- Interface en ligne de commande simple
- Support des pseudonymes personnalisés
- Gestion élégante des déconnexions

## Architecture Détaillée

### Vue d'ensemble

L'application utilise une architecture client-serveur multi-threadée pour permettre une communication bidirectionnelle en temps réel.

### Côté Client

Le client utilise deux threads principaux :

1. **WriteThread (CLIENT → SERVEUR)**
   - Hérite de `Thread` pour l'exécution parallèle
   - Gère l'envoi des messages vers le serveur
   - Flux de données :
     * Lit l'entrée utilisateur via `System.in`
     * Envoie les messages au serveur via `socket.getOutputStream()`
   - Gère l'authentification initiale (pseudonyme)

2. **ReadThread (SERVEUR → CLIENT)**
   - Hérite de `Thread` pour l'exécution parallèle
   - Gère la réception des messages du serveur
   - Flux de données :
     * Reçoit les messages via `socket.getInputStream()`
     * Affiche les messages dans la console

### Côté Serveur

Le serveur utilise un thread principal et des threads clients :

1. **ChatServer (Thread Principal)**
   - Gère les connexions entrantes
   - Maintient une liste des clients connectés
   - Coordonne la diffusion des messages
   - Utilise un HashSet pour stocker les clients :
     * Permet une recherche rapide des clients
     * Évite les doublons automatiquement
     * Gestion efficace des ajouts/suppressions

2. **ClientHandler (Un thread par client)**
   - Hérite de `Thread` pour l'exécution parallèle
   - Gère la communication avec un client spécifique
   - Types de messages :
     * Messages console (logs serveur)
     * Messages broadcast (vers tous les autres clients)
     * Messages directs (vers un client spécifique)

### Flux de Communication

1. **Connexion Client**
   ```
   Client (nouveau) → ChatServer
   ├── Crée ClientHandler
   ├── Demande pseudonyme
   └── Broadcast "X a rejoint"
   ```

2. **Envoi de Message**
   ```
   Client (WriteThread) → Serveur (ClientHandler)
   └── Serveur broadcast → Autres Clients (ReadThread)
   ```

3. **Déconnexion**
   ```
   Client ("exit") → ClientHandler
   ├── Ferme les connexions
   └── Broadcast "X a quitté"
   ```

## Scénarios d'Utilisation

### Scénario 1 : Chat de Groupe
Alice, Bob et Charlie utilisent l'application pour discuter ensemble :

```
[Serveur] Démarrage sur le port 12345...

[Alice] Se connecte
> Entrez votre pseudonyme: Alice
> Bienvenue dans le chat, Alice!
[Tous] "Alice a rejoint la conversation"

[Bob] Se connecte
> Entrez votre pseudonyme: Bob
> Bienvenue dans le chat, Bob!
[Tous] "Bob a rejoint la conversation"

[Charlie] Se connecte
> Entrez votre pseudonyme: Charlie
> Bienvenue dans le chat, Charlie!
[Tous] "Charlie a rejoint la conversation"

[Alice] > Salut tout le monde !
[Bob/Charlie] "Alice: Salut tout le monde !"

[Bob] > Hey Alice et Charlie !
[Alice/Charlie] "Bob: Hey Alice et Charlie !"

[Charlie] > exit
[Tous] "Charlie a quitté la conversation"
```

### Scénario 2 : Gestion des Erreurs
Démonstration de la robustesse du système :

```
[Serveur] Démarrage sur le port 12345...

[David] Se connecte
> Entrez votre pseudonyme: Alice
> Erreur: Ce pseudonyme est déjà utilisé
> Entrez votre pseudonyme: David
> Bienvenue dans le chat, David!

[David] *perd sa connexion internet*
[Tous] "David a quitté la conversation"
[Serveur] "Client déconnecté: David"

[David] Se reconnecte
> Entrez votre pseudonyme: David
> Bienvenue dans le chat, David!
[Tous] "David a rejoint la conversation"
```

## Prérequis

- Java 11 ou supérieur
- Un terminal/invite de commande

## Installation

### Installation du Serveur

```bash
# 1. Cloner le dépôt
git clone https://github.com/emalsert/ChatMultiThreadJava.git
cd ChatMultiThreadJava

# 2. Compiler le serveur
javac --release 11 server/*.java

# 3. Lancer le serveur (utilise le port par défaut 12345)
java server.ChatServer
```

### Installation du Client

```bash
# 1. Dans le même répertoire que le serveur

# 2. Compiler le client
javac --release 11 client/*.java

# 3. Lancer le client
# Syntaxe : java client.ChatClient [hostname] [port]
#   - hostname : localhost ou adresse IP valide (ex: 192.168.1.100)
#   - port : numéro de port (1-65535, défaut: 12345)

# Exemples :
java client.ChatClient
java client.ChatClient localhost
java client.ChatClient 192.168.1.100 12345
```

## Utilisation

### Serveur

1. Démarrer le serveur selon les instructions d'installation
2. Le serveur écoute sur le port 12345 par défaut
3. Attendre les connexions des clients

### Client

1. Lancer le client selon les instructions d'installation
2. Spécifier l'adresse du serveur (localhost ou IP) et le port si nécessaire
3. Entrer un pseudonyme à l'invite
4. Commencer à chatter ! Taper les messages et appuyer sur Entrée pour envoyer
5. Taper 'exit' pour quitter

### Validation des Paramètres

Le client vérifie la validité des paramètres de connexion :
- L'adresse du serveur doit être :
  * "localhost" pour une connexion locale
  * Une adresse IP valide (ex: 192.168.1.100)
- Le port doit être un nombre compris entre 1 et 65535
- En cas d'erreur, un message explicatif est affiché avec des exemples d'utilisation

## Structure du Projet

- `server/` - Contient l'implémentation du serveur
  - `ChatServer.java` - Classe principale du serveur
  - `ClientHandler.java` - Gère les connexions individuelles des clients
- `client/` - Contient l'implémentation du client
  - `ChatClient.java` - Classe principale du client
  - `ReadThread.java` - Gère les messages entrants
  - `WriteThread.java` - Gère les messages sortants

## Licence

Ce projet est open source et disponible sous la licence MIT. 