# Chat Multi-Thread Java

Une application de chat simple et multi-threadée écrite en Java.

## Fonctionnalités

- Serveur multi-threadé capable de gérer plusieurs clients simultanément
- Diffusion des messages en temps réel
- Interface graphique simple avec Swing
- Support des pseudonymes personnalisés
- Gestion propre des déconnexions

## Prérequis

- Java 11 ou supérieur
- Terminal ou invite de commandes
- Pour le serveur : un serveur Linux (VPS recommandé)

## Installation

### Installation du serveur

1. Clonez le dépôt :
```bash
git clone https://github.com/emalsert/ChatMultiThreadJava.git
cd ChatMultiThreadJava
```

2. Compilez le serveur :
```bash
javac server/*.java
```

3. Lancez le serveur :
```bash
java server.ChatServer
```

### Installation du client

#### Sur macOS/Linux :
```bash
curl -s https://raw.githubusercontent.com/emalsert/ChatMultiThreadJava/main/install-client.sh | bash
```

#### Sur Windows :
1. Téléchargez le script d'installation :
```powershell
Invoke-WebRequest -Uri "https://raw.githubusercontent.com/emalsert/ChatMultiThreadJava/main/install-client.bat" -OutFile "install-client.bat"
```

2. Exécutez le script :
```cmd
install-client.bat
```

## Déploiement sur VPS

Pour déployer le serveur sur un VPS Linux :

1. Connectez-vous à votre VPS en SSH :
```bash
ssh root@votre-vps
```

2. Téléchargez le script de déploiement :
```bash
wget https://raw.githubusercontent.com/emalsert/ChatMultiThreadJava/main/deploy.sh
```

3. Rendez le script exécutable :
```bash
chmod +x deploy.sh
```

4. Exécutez le script :
```bash
./deploy.sh
```

Le script va :
- Installer Java si nécessaire
- Créer un répertoire pour le serveur
- Télécharger et compiler les fichiers source
- Configurer un service systemd pour le serveur
- Démarrer le serveur automatiquement

Pour vérifier le statut du serveur :
```bash
systemctl status chat-server
```

Pour voir les logs :
```bash
journalctl -u chat-server
```

## Utilisation

1. Démarrez le serveur (si en local) :
```bash
java server.ChatServer
```

2. Lancez le client :
```bash
java client.ChatGUI
```

3. Entrez votre pseudonyme dans la fenêtre de connexion

4. Commencez à chatter !

## Structure du projet

```
ChatMultiThreadJava/
├── server/
│   ├── ChatServer.java     # Classe principale du serveur
│   └── ClientHandler.java  # Gestion des connexions clients
├── client/
│   ├── ChatGUI.java       # Interface graphique du client
│   ├── ReadThread.java    # Thread de lecture des messages
│   └── WriteThread.java   # Thread d'envoi des messages
├── install-client.sh      # Script d'installation pour macOS/Linux
├── install-client.bat     # Script d'installation pour Windows
├── deploy.sh             # Script de déploiement pour VPS
└── README.md             # Documentation
```

## Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails. 