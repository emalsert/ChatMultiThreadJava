#!/bin/bash

# Create a temporary directory with package structure
mkdir -p ~/chat-client/client
cd ~/chat-client

# Download the client files into the package directory
echo "Downloading chat client..."
curl -s https://raw.githubusercontent.com/emalsert/ChatMultiThreadJava/main/client/ChatClient.java > client/ChatClient.java
curl -s https://raw.githubusercontent.com/emalsert/ChatMultiThreadJava/main/client/ReadThread.java > client/ReadThread.java
curl -s https://raw.githubusercontent.com/emalsert/ChatMultiThreadJava/main/client/WriteThread.java > client/WriteThread.java

# Compile the client
echo "Compiling chat client..."
javac -source 11 -target 11 client/*.java

# Run the client from the root directory
echo "Starting chat client..."
cd ~/chat-client
java -Dfile.encoding=UTF-8 -Djava.awt.headless=true -cp . client.ChatClient 167.86.109.247 1234

# Clean up
cd ..
rm -rf ~/chat-client 