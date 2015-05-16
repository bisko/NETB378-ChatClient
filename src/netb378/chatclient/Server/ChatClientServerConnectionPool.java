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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import netb378.chatclient.Log;

/**
 * Connection pool to carry out the socket handling
 * It threads for every socket that happens
 *
 * @author bisko
 */
public class ChatClientServerConnectionPool implements Runnable {
    
    private ChatClientServer _server = null;
    private ServerSocket _serverSocket = null;
    private Thread      _thread = null;
        
    public ChatClientServerConnectionPool(ChatClientServer _server, Integer port) throws IOException {
        
        this._server = _server;
        Log.log("Initializing connection pool");
        
        this._serverSocket = new ServerSocket(port);
        
        Log.log("Server initialized on port: " + port);
        Log.log("Server info: " + this._serverSocket);
        
        this.startThread();
        
        // temporary
        //Log.log("Exiting...");
        //System.exit(0);
        
    }
    
    public void startThread() {
        if (this._thread == null) {
            this._thread = new Thread(this);
            this._thread.start();
        }
    }
    
    public void run() {
        
        while (this._thread != null) {
            try {
                Log.log("Waiting for a client...");
                Socket tmpSock = this._serverSocket.accept();
                
                Log.log("Client connected!");
               
                
                this._server.addClientFromSocket(tmpSock);
                
                // free the socket, so we can
                tmpSock = null;
            }
            catch (IOException ex) {
                Log.log("Something went woo-hoo when listening for connections");
                Log.log(""+ex);
            }            
        }
    }
}
