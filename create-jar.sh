#!/bin/bash

# Compile the client files
echo "Compiling client files..."
javac -source 11 -target 11 client/*.java

# Create the JAR file
echo "Creating JAR file..."
jar cfm client/chat-client.jar client/MANIFEST.MF client/*.class

echo "JAR file created successfully!" 