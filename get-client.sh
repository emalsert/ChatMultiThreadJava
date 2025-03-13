#!/bin/bash

# Create a temporary directory
mkdir -p ~/chat-client
cd ~/chat-client

# Download the client files
echo "Downloading chat client..."
wget https://raw.githubusercontent.com/emalsert/ChatMultiThreadJava/main/client/ChatClient.java
wget https://raw.githubusercontent.com/emalsert/ChatMultiThreadJava/main/client/ReadThread.java
wget https://raw.githubusercontent.com/emalsert/ChatMultiThreadJava/main/client/WriteThread.java

# Compile the client
echo "Compiling chat client..."
javac -source 11 -target 11 *.java

# Run the client
echo "Starting chat client..."
java ChatClient 167.86.109.247 1234

# Clean up
cd ..
rm -rf ~/chat-client 