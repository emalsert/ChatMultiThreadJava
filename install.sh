#!/bin/bash

# Update system
echo "Updating system..."
apt-get update
apt-get upgrade -y

# Install Java
echo "Installing Java..."
apt-get install -y openjdk-11-jdk

# Create application directory
echo "Creating application directory..."
mkdir -p /opt/chat-app

# Extract deployment files
echo "Extracting deployment files..."
tar -xzf /root/deploy.tar.gz -C /opt/chat-app

# Clean up
echo "Cleaning up..."
rm /root/deploy.tar.gz

# Create systemd service file
echo "Creating systemd service..."
cat > /etc/systemd/system/chat-server.service << EOL
[Unit]
Description=Chat Server
After=network.target

[Service]
Type=simple
User=root
WorkingDirectory=/opt/chat-app/deploy
ExecStart=/usr/bin/java server.ChatServer 1234
Restart=always

[Install]
WantedBy=multi-user.target
EOL

# Reload systemd and start service
echo "Starting chat server service..."
systemctl daemon-reload
systemctl enable chat-server
systemctl start chat-server

echo "Installation completed!"
echo "Chat server is running on port 1234" 