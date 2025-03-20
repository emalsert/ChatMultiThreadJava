#!/bin/bash

# Compile the Java files
echo "Compiling Java files..."
javac -source 11 -target 11 server/*.java client/*.java

# Create a deployment directory
echo "Creating deployment directory..."
mkdir -p deploy/server deploy/client

# Copy compiled files and source files
echo "Copying files to deployment directory..."
cp server/*.class deploy/server/
cp client/*.class deploy/client/
cp README.md deploy/

# Create a tar archive
echo "Creating deployment archive..."
tar -czf deploy.tar.gz deploy/

# Copy to VPS
echo "Copying deployment archive to VPS..."
scp deploy.tar.gz root@167.86.109.247:/root/

# Clean up local files
echo "Cleaning up..."
rm -rf deploy deploy.tar.gz

echo "Deployment completed!" 