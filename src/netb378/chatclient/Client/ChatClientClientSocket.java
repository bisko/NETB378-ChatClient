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
    
    public String getServerConnectString() {
        return this.server+":"+port;
    }
    
    
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
    
    public void startThread() {
        if (this._thread == null) {
            this._thread = new Thread(this);
            this._thread.start();
        }
    }
    
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
