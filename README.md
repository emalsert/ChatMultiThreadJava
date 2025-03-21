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

### Option 1 : Installation Automatique du Serveur (Linux)

```bash
# Télécharger et exécuter le script d'installation
curl -s https://raw.githubusercontent.com/emalsert/ChatMultiThreadJava/main/install.sh | sudo bash
```

Le script va :
- Installer Java 11
- Créer le répertoire `/opt/chat-app`
- Configurer le service systemd
- Démarrer le serveur sur le port 12345

### Option 2 : Installation Manuelle du Serveur

```bash
# Cloner le dépôt
git clone https://github.com/emalsert/ChatMultiThreadJava.git
cd ChatMultiThreadJava

# Compiler
javac -source 11 -target 11 server/*.java

# Lancer (remplacer 12345 par le port souhaité)
java server.ChatServer 12345
```

### Installation du Client

#### Option 1 : Installation Rapide (macOS/Linux)

```bash
# Tout-en-un : télécharge, compile et lance le client
curl -s https://raw.githubusercontent.com/emalsert/ChatMultiThreadJava/main/install-client.sh | bash
```

#### Option 2 : Installation en Deux Étapes (macOS/Linux)

```bash
# 1. Télécharger le script
curl -s https://raw.githubusercontent.com/emalsert/ChatMultiThreadJava/main/install-client.sh > install-client.sh

# 2. Exécuter
chmod +x install-client.sh && ./install-client.sh
```

#### Option 3 : Installation Directe (tous OS)

```bash
# 1. Télécharger le script get-client
curl -s https://raw.githubusercontent.com/emalsert/ChatMultiThreadJava/main/get-client.sh > get-client.sh

# 2. Exécuter
chmod +x get-client.sh && ./get-client.sh
```

Le script `get-client.sh` va :
- Créer un dossier temporaire
- Télécharger les fichiers sources
- Compiler le code
- Lancer le client
- Nettoyer les fichiers temporaires

## Utilisation

### Serveur

1. Démarrer le serveur selon les instructions d'installation
2. Le serveur écoute sur le port spécifié (par défaut : 12345)
3. Attendre les connexions des clients

### Client

1. Lancer le client selon les instructions d'installation
2. Entrer un pseudonyme à l'invite
3. Commencer à chatter ! Taper les messages et appuyer sur Entrée pour envoyer
4. Taper 'exit' pour quitter

## Structure du Projet

- `server/` - Contient l'implémentation du serveur
  - `ChatServer.java` - Classe principale du serveur
  - `ClientHandler.java` - Gère les connexions individuelles des clients
- `client/` - Contient l'implémentation du client
  - `ChatClient.java` - Classe principale du client
  - `ReadThread.java` - Gère les messages entrants
  - `WriteThread.java` - Gère les messages sortants
- `install.sh` - Script d'installation du serveur
- `install-client.sh` - Script d'installation du client
- `get-client.sh` - Script de téléchargement et d'exécution

## Licence

Ce projet est open source et disponible sous la licence MIT. 