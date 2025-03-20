#!/bin/bash

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Java is not installed. Installing Java..."
    if [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS
        if ! command -v brew &> /dev/null; then
            echo "Homebrew is not installed. Installing Homebrew..."
            /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
        fi
        brew install openjdk@11
    elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
        # Linux
        if command -v apt-get &> /dev/null; then
            sudo apt-get update
            sudo apt-get install -y openjdk-11-jdk
        elif command -v yum &> /dev/null; then
            sudo yum install -y java-11-openjdk
        else
            echo "Unsupported package manager. Please install Java 11 manually."
            exit 1
        fi
    else
        echo "Unsupported operating system. Please install Java 11 manually."
        exit 1
    fi
fi

# Download and run the client
echo "Downloading chat client..."
curl -s https://raw.githubusercontent.com/emalsert/ChatMultiThreadJava/main/get-client.sh > get-client.sh
chmod +x get-client.sh
./get-client.sh 