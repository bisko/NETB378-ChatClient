/*
 * Copyright (C) 2015 Biser Perchinkov F44307
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package netb378.chatclient.Client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import netb378.chatclient.Log;

/**
 * Handle socket communication for the chat client.
 * 
 * Manages the socket connection for the chat client.
 * 
 * @author Biser Perchinkov F44307
 */
public class ChatClientClientSocket implements Runnable {
    
    private Thread _thread = null;
    private Socket _socket = null;
    private ChatClientClient _client = null;
    
    private DataInputStream streamIn = null;
    private DataOutputStream streamOut = null;
    
    private String server = "";
    private Integer port = 0;
    
    /**
     * Initialize the socket by connecting to a certain server and port.
     * 
     * Connect to a server and port specified by the input parameters.
     * 
     * @param server The server to connect to
     * @param port The port to connect to
     * @param _client Client instance that will handle the socket events
     * @throws IOException In case of socket errors
     */
    public ChatClientClientSocket (String server, Integer port, ChatClientClient _client) throws IOException {
        if (this._socket != null) {
            try {
                this._socket.close();
            }
            catch (IOException ex) {}
        }
        
        this._socket = new Socket(server, port);
        
        this.initializeSocket(_client);
        
        this.server = server;
        this.port = port;
    }
    
    /**
     * Get the server connection string.
     * 
     * Returns a server connection string in the format host:port
     * 
     * @return Server connection string
     */
    public String getServerConnectString() {
        return this.server+":"+port;
    }
    
    /**
     * Initialize the socket when we have a successful connection.
     * 
     * Initializes the socket components when we successfully connect
     * to a server. 
     * 
     * Set up the data input streams and start the socket thread.
     * 
     * @param _client The client that handles the socket events
     */
    private void initializeSocket(ChatClientClient _client) {
        Log.log("initializing socket");
        this._client = _client;
        // add streams
        try {
            this.streamIn = new DataInputStream(new 
                        BufferedInputStream(this._socket.getInputStream()));
            
            this.streamOut = new DataOutputStream(new 
                        BufferedOutputStream(this._socket.getOutputStream()));
        }
        catch (IOException ex) {
            Log.log("Unable to acquire streams from the socket..");
            Log.log(""+ex);
        }        
        
        this.startThread();
    }
    
    /**
     * Socket loop.
     * 
     * Listens for socket events and data from the server.
     */
    public void run () {
        while (this._socket != null) {
            Log.log("Reading..");
            try {
                String line = this.streamIn.readUTF();
                this._client.handleServerMessage(line);
            } catch (IOException ex) {
                // mainly this happens when the server goes bye-bye
                // and we should show a message that we were disconnected
                Log.log("Problem reading info from socket :/");
                Log.log(""+this._socket);
                this._client.handleServerDisconnect();
                this.close();
            }   
        }
    }
    
    /**
     * Start a the socket thread.
     * 
     * Starts the socket thread if we haven't already started it.
     */
    public void startThread() {
        if (this._thread == null) {
            this._thread = new Thread(this);
            this._thread.start();
        }
    }
    
    /**
     * Send a message to the server.
     * 
     * Sends a message to the server. It is synchronized so we don't 
     * mess up the data if a race condition occurs in sending said data.
     * 
     * @param message The message to be sent to the server
     */
    public synchronized void send(String message) {
        if (this._socket != null) {
            try {
                this.streamOut.writeUTF(message.trim());
                this.streamOut.flush();
            }
            catch(IOException ex) {
                Log.log("Unable to write to socket: ");
                Log.log(ex.getMessage());
            }
        }
    }
    
    /**
     * Close the socket.
     * 
     * Close the socket and destroy the I/O streams, the socket and the thread.
     * 
     */
    public synchronized void close() {
        try{
            this.streamIn.close();
            this.streamOut.close();
            
            // close the socket
            this._socket.close();
        }
        catch (IOException ex) {
            Log.log("Unable to close client socket: "+ex.getMessage());
        }
        
        this._socket = null;
        this._thread = null;
    }
}
