# Multi-threaded Chat Application in Java

This is a simple multi-threaded chat application implemented in Java. The application consists of a server that can handle multiple clients simultaneously, and client applications that can connect to the server to exchange messages.

## Features

- Multi-threaded server that can handle multiple clients simultaneously
- Unique pseudonym (nickname) system for clients
- Real-time message broadcasting to all connected clients
- Graceful connection handling and disconnection
- Command-line interface for both server and clients

## Project Structure

```
.
├── server/
│   ├── ChatServer.java    # Main server class
│   └── ClientHandler.java # Thread handler for each client
└── client/
    ├── ChatClient.java    # Main client class
    ├── ReadThread.java    # Thread for reading server messages
    └── WriteThread.java   # Thread for sending messages to server
```

## Compilation

Make sure you have the JDK (Java Development Kit) installed on your system.

To compile the project, navigate to the root directory containing the `server` and `client` folders and run:

```bash
javac server/ChatServer.java server/ClientHandler.java client/ChatClient.java client/ReadThread.java client/WriteThread.java
```

This will compile all the Java classes and generate the corresponding `.class` files.

## Running the Application

### Starting the Server

To start the chat server, run:

```bash
java server.ChatServer [port]
```

- `[port]` is optional. If not specified, the default port **1234** will be used.
- The server will display a message indicating it's listening on the specified port and waiting for client connections.

### Starting a Client

To start a chat client, open a new terminal and run:

```bash
java client.ChatClient [host] [port]
```

- `[host]` is optional. If not specified, `localhost` will be used.
- `[port]` is optional. If not specified, the default port **1234** will be used.

For example:
```bash
java client.ChatClient localhost 1234
```

### Using the Chat Client

1. When you start a client, you'll be prompted to enter a pseudonym (nickname).
2. If the pseudonym is already taken, the server will automatically assign you a unique one.
3. Once connected, you can start sending messages that will be broadcast to all other connected clients.
4. To quit the chat, type `exit` and press Enter.

## Internal Functioning

- The server uses a separate thread (ClientHandler) for each connected client to handle communication.
- The server maintains a list of all connected clients and broadcasts messages to all clients except the sender.
- The special message `"exit"` triggers client disconnection:
  - When a client sends `"exit"`, the server sends a disconnection confirmation and notifies other users.
  - If a client disconnects unexpectedly, the server detects the disconnection, removes the client from the list, and notifies others.
- The client uses two threads to improve responsiveness:
  - A read thread that listens for server messages (displays messages immediately when received).
  - A write thread that reads user input and sends it to the server without blocking message reception.
- If the server is stopped while clients are connected, the clients will display a connection loss message and close automatically.

## Error Handling

The application includes error handling for various scenarios:
- Invalid port numbers
- Connection failures
- Unexpected disconnections
- Resource cleanup on exit

## Requirements

- Java Development Kit (JDK) 8 or higher
- Network connectivity between server and clients 