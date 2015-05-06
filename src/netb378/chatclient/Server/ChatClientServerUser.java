/*
 * Copyright (C) 2015 bisko
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
package netb378.chatclient.Server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import netb378.chatclient.Log;

/**
 *
 * @author bisko
 */
public class ChatClientServerUser implements Runnable {
    
    private Socket _socket = null;
    private ChatClientServer _server = null;
    private String username = "";
    private Integer id = 0;
    
    private DataInputStream streamIn = null;
    private DataOutputStream streamOut = null;
    
    private Thread _thread = null;
    
    public ChatClientServerUser(ChatClientServer _server, Socket _socket) {
        this._server = _server;
        this._socket = _socket;
        
        this.id = _socket.getPort();
        
        
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
        
        this.addClientToServerList();
        
        this.startThread();
    }
    
    
    private void addClientToServerList() {
        Log.log("Adding client to server list");
        this._server.addUser(this);
    }
    
    private void removeClientFromServerList() {
        Log.log("Removing client from server list");
        
        this._server.removeUser(this);
        
        try {
            // close the socket
            this._socket.close();
        } catch (IOException ex) {
            Log.log("Problem closing the socket: "+ex);
            Log.log(""+this._socket);
        }
    }
    
    public void startThread() {
        if (this._thread == null) {
            this._thread = new Thread(this);
            this._thread.start();
        }
    }
    
    public void run() {
        Boolean done = false;
        
        while (!done) {
            try {
                String line = this.streamIn.readUTF();
                
                done = this._server.handleMessage(this.id, line);
                
            } catch (IOException ex) {
                Log.log("Problem reading info from socket :/");
                Log.log(""+this._socket);
                done = true;
            }
        }
        
        this.removeClientFromServerList(); 
   }
    
}
