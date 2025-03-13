#!/bin/bash

# Create a temporary directory
mkdir -p ~/chat-client
cd ~/chat-client

# Download the client JAR
echo "Downloading chat client..."
curl -s https://raw.githubusercontent.com/emalsert/ChatMultiThreadJava/main/client/chat-client.jar > chat-client.jar

# Run the client
echo "Starting chat client..."
java -jar chat-client.jar 167.86.109.247 1234

# Clean up
cd ..
rm -rf ~/chat-client 