#!/bin/bash

# Compile the Java files
echo "Compiling Java files..."
javac server/ChatServer.java server/ClientHandler.java client/ChatClient.java client/ReadThread.java client/WriteThread.java

# Create a deployment directory
echo "Creating deployment directory..."
mkdir -p deploy

# Copy compiled files and source files
echo "Copying files to deployment directory..."
cp -r server deploy/
cp -r client deploy/
cp README.md deploy/

# Create a tar archive
echo "Creating deployment archive..."
tar -czf deploy.tar.gz deploy/

# Copy to VPS
echo "Copying to VPS..."
scp deploy.tar.gz root@167.86.109.247:/root/

# Clean up local files
echo "Cleaning up..."
rm -rf deploy deploy.tar.gz

echo "Deployment completed!" 