# Chat Multi-Thread Java

A simple multi-threaded chat application written in Java. The application consists of a server that can handle multiple clients simultaneously, and a client that can connect to the server and exchange messages.

## Features

- Multi-threaded server that can handle multiple clients
- Real-time message broadcasting to all connected clients
- Simple command-line interface
- Support for custom usernames
- Graceful disconnection handling

## Prerequisites

- Java 11 or higher
- A terminal/command prompt

## Installation

### Server

1. Clone the repository:
```bash
git clone https://github.com/emalsert/ChatMultiThreadJava.git
cd ChatMultiThreadJava
```

2. Compile the server:
```bash
javac -source 11 -target 11 server/*.java
```

3. Run the server:
```bash
java server.ChatServer 1234
```

### Client

The client can be installed and run with a single command:

```bash
curl -s https://raw.githubusercontent.com/emalsert/ChatMultiThreadJava/main/install-client.sh | bash
```

Or in two steps for better input handling:

```bash
# Download the installation script
curl -s https://raw.githubusercontent.com/emalsert/ChatMultiThreadJava/main/install-client.sh > install-client.sh

# Make it executable and run it
chmod +x install-client.sh && ./install-client.sh
```

The installation script will:
1. Check if Java is installed and install it if necessary
2. Download and compile the client code
3. Launch the chat client

## Usage

### Server

1. Start the server as described in the installation section
2. The server will listen on the specified port (default: 1234)
3. Wait for clients to connect

### Client

1. Run the client as described in the installation section
2. Enter your pseudonym when prompted
3. Start chatting! Type your messages and press Enter to send them
4. Type 'exit' to quit the chat

## Project Structure

- `server/` - Contains the server implementation
  - `ChatServer.java` - Main server class
  - `ClientHandler.java` - Handles individual client connections
- `client/` - Contains the client implementation
  - `ChatClient.java` - Main client class
  - `ReadThread.java` - Handles incoming messages
  - `WriteThread.java` - Handles outgoing messages
- `install-client.sh` - Client installation script
- `get-client.sh` - Client download and execution script

## License

This project is open source and available under the MIT License. 