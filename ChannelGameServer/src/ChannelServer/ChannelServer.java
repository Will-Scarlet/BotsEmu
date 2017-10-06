/*
 * RelayServer.java
 */

package ChannelServer;

import java.util.*;
import java.net.*;

/**
 * The RelayServer waits for client connections and uses PolicyServerConnections to handle policy requests.
 */
public class ChannelServer extends Thread {

    public static final byte[] PACKETS_HEADER = {(byte)0x01, (byte)0x00};

    public static final byte[] BOT_CREATION_HEADER = {(byte)0xE2, (byte)0x2E, (byte)0x02, (byte)0x00};
    public static final byte[] CREATE_BOT_USERNAME_TAKEN = {(byte)0x00, (byte)0x36};
    public static final byte[] CREATE_BOT_USERNAME_ERROR = {(byte)0x00, (byte)0x33};
    public static final byte[] CREATE_BOT_CREATED = {(byte)0x01, (byte)0x00};

    public static final byte[] CLIENT_NUMBER_HEADER= {(byte)0xE0, (byte)0x2E, (byte)0x04, (byte)0x00};
    public static final byte[] CHARACTER_INFORMATION_HEADER= {(byte)0xE1, (byte)0x2E, (byte)0x5E, (byte)0x05};


    public static final byte[] PLAYERS_HEADER= {(byte)0x27, (byte)0x27, (byte)0x13, (byte)0x00};

    public static final byte[] OK_HEADER = {(byte)0x46, (byte)0x2F, (byte)0x20, (byte)0x00};
    public static final byte[] OK_PACKET = {(byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};


    public static final byte[] SERVER_CLIENT_CHECK_1 = {(byte)0x01, (byte)0x00, (byte)0x01, (byte)0x00};
    public static final byte[] SERVER_CLIENT_CHECK_2 = {(byte)0xCC};
    public static final byte[] SERVER_CLIENT_CHECK_ANWSER = {(byte)0x02, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0xCC};

    public static final byte[] NULLBYTE = {(byte)0x00};
    public static String longnullbyte = "";

    public static int fake_i = 0;

    protected int port;
    protected ServerSocket serverSocket;
    protected boolean listening;
    protected Vector<ChannelServerConnection> clientConnections;
    

    /**
     * Creates a new instance of RelayServer.
     */
    public ChannelServer(int serverPort) {
        this.port = serverPort;
        this.listening = false;
        this.clientConnections = new Vector<ChannelServerConnection>();
    }
    
    /**
     * Gets the server's port.
     */
    public int getPort() {
        return this.port;
    }
    
    /**
     * Gets the server's listening status.
     */
    public boolean getListening() {
        return this.listening;
    }

    public int getClientCount() {
        return this.clientConnections.size();
    }

    
    /**
     * Roots a debug message to the main application.
     */
    protected void debug(String msg) {
        Main.debug("ChannelServer (" + this.port + ")", msg);
    }


    /**
     * Removes a client from the server (it's expected that the client closes its own connection).
     */
    public boolean remove(SocketAddress remoteAddress) {
        try {
            debug("IP : "+ remoteAddress);
            for (int i = 0; i < this.clientConnections.size(); i++) {
                ChannelServerConnection client = this.clientConnections.get(i);
                debug(""+client.getRemoteAddress());
                if (client.getRemoteAddress().equals(remoteAddress)) {
                    this.clientConnections.remove(i);
                    //debug("client " + remoteAddress + " removed");
                    return true;
                }
            }
        }
        catch (Exception e) {
            debug("Exception (remove): " + e.getMessage());
        }

        return false;
    }

    
    /**
     * Waits for clients' connections and handles them to a new RelayServerConnection.
     */
    public void run() {
        try {
            this.serverSocket = new ServerSocket(this.port);
            this.listening = true;
            debug("listening");
            Lobby lobby = new Lobby(this, Main.sql);

            while (this.listening) {
                Socket socket = this.serverSocket.accept();
                if(!Main.getip(socket).equals("5.73.69.243")){
                debug("client connection from " + socket.getRemoteSocketAddress());
                ChannelServerConnection socketConnection = new ChannelServerConnection(socket,this,lobby, Main.sql);
                clientConnections.add(socketConnection);
                socketConnection.start();
                }
            };
        }
        catch (Exception e) {
            debug("Exception (run): " + e.getMessage());
        }
    }
    
    /**
     * Closes the server's socket.
     */
    protected void finalize() {	 
        try {
            this.serverSocket.close();
            this.listening = false;
            debug("stopped");
        }
        catch (Exception e) {
            debug("Exception (finalize): " + e.getMessage());
        }
    }
}
