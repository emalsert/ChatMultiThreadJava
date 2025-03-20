#!/bin/bash

# Couleurs pour les messages
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${GREEN}Déploiement du serveur de chat...${NC}"

# Vérifier si Java est installé
if ! command -v java &> /dev/null; then
    echo -e "${RED}Java n'est pas installé. Installation de Java...${NC}"
    sudo apt-get update
    sudo apt-get install -y openjdk-11-jdk
fi

# Créer le répertoire du serveur
echo "Création du répertoire du serveur..."
mkdir -p /opt/chat-server
cd /opt/chat-server

# Télécharger les fichiers source
echo "Téléchargement des fichiers source..."
wget -q https://raw.githubusercontent.com/emalsert/ChatMultiThreadJava/main/server/ChatServer.java
wget -q https://raw.githubusercontent.com/emalsert/ChatMultiThreadJava/main/server/ClientHandler.java

# Compiler le serveur
echo "Compilation du serveur..."
javac *.java

# Créer le service systemd
echo "Configuration du service systemd..."
cat > /etc/systemd/system/chat-server.service << EOL
[Unit]
Description=Chat Server
After=network.target

[Service]
Type=simple
User=root
WorkingDirectory=/opt/chat-server
ExecStart=/usr/bin/java ChatServer
Restart=always
RestartSec=3

[Install]
WantedBy=multi-user.target
EOL

# Recharger systemd et démarrer le service
echo "Démarrage du service..."
sudo systemctl daemon-reload
sudo systemctl enable chat-server
sudo systemctl start chat-server

# Vérifier le statut
if sudo systemctl is-active --quiet chat-server; then
    echo -e "${GREEN}Le serveur de chat est maintenant en cours d'exécution sur le port 1234${NC}"
    echo -e "${GREEN}Pour voir les logs: journalctl -u chat-server${NC}"
else
    echo -e "${RED}Erreur lors du démarrage du serveur${NC}"
    echo -e "${RED}Vérifiez les logs: journalctl -u chat-server${NC}"
fi 