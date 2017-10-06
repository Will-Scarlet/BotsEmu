/*
 * LoginServer.java
 */

package LoginServer;

import java.net.*;
import java.util.*;

/**
 * A LoginServer for communication between the connected clients.
 */
public class LoginServer extends Thread {

    public static final byte[] LOGIN_SUCCESSBYTE = {(byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
    public static final byte[] LOGIN_INCUSERBYTE = {(byte)0x01, (byte)0x00, (byte)0x02, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
    public static final byte[] LOGIN_INCPASSBYTE = {(byte)0x01, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
    public static final byte[] LOGIN_BANUSERBYTE = {(byte)0x01, (byte)0x00, (byte)0x03, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
    public static final byte[] LOGIN_ALREADYLOGGEDIN = {(byte)0x01, (byte)0x00, (byte)0x06, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
    public static final byte[] LOGINHEADER = {(byte)0xEC, (byte)0x2C, (byte)0x4A, (byte)0x00};
    protected ServerSocket socketServer;
    protected int port;
    protected boolean listening;
    protected Vector<LoginServerConnection> clientConnections;

    
    /**
     * Creates a new instance of LoginServer.
     */
    public LoginServer(int serverPort) {
        this.port = serverPort;
        this.clientConnections = new Vector<LoginServerConnection>();
        this.listening = false;
    }
    
    /**
     * Gets the server's port.
     */
    public int getPort() {
        return this.port;
    }
    
    /**
     * Gets the number of clients.
     */
    public int getClientCount() {
        return this.clientConnections.size();
    }
    
    /**
     * Roots a debug message to the main application.
     */
    protected void debug(String msg) {
        Main.debug("LoginServer (" + this.port + ")", msg);
    }
    
 
    
    /**
     * Removes a client from the server (it's expected that the client closes its own connection).
     */
    public boolean remove(SocketAddress remoteAddress) {
        try {
            for (int i = 0; i < this.clientConnections.size(); i++) {
                LoginServerConnection client = this.clientConnections.get(i);

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
     * Listens for client conections and handles them to ChatServerConnections.
     */
    public void run() {
        try {
            Main.debug("[Server]", "LoginServer (" + this.port + ") started and listening");
            this.socketServer = new ServerSocket(this.port);
            this.listening = true;

            while (listening) {
                Socket socket = this.socketServer.accept();
                if(!Main.getip(socket).equals("5.73.69.243")){
                debug("Client connection from " + Main.getip(socket));
                LoginServerConnection socketConnection = new LoginServerConnection(socket, this);
                socketConnection.start();
                this.clientConnections.add(socketConnection);
                }
            };
        }
        catch (Exception e) {
            debug(e.getMessage());
        }
    }

    /**
     * Closes the server's socket.
     */
    protected void finalize() {	 
        try {
            this.socketServer.close();
            this.listening = false;
            debug("Stopped.");
        }
        catch (Exception e) {
            debug("Exception (finalize): " + e.getMessage());
        }
    }
}
