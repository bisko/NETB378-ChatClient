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
import java.net.Socket;
import java.util.List;
import netb378.chatclient.Log;

/**
 *
 * @author bisko
 */
public class ChatClientServer {
    
    int serverPort = 8082;
    ChatClientServerConnectionPool pool = null;
    
    public List<ChatClientServerUser> userList = null;
    
    //userlist
        // socket
        // user info
        // add/remove user
        // handle notification
    
    //api
        // notify all clients (except source)
        // add client
        // remove client
        // send userlist
        
    
    
    // init connection pool 
    // wait for connections from the connection pool
    // instance users
    // manage disconnects
    // manage messages
    
    public ChatClientServer(int port) {
        
        // store the port the server will run on
        if (port > 0) {
            this.serverPort = port;
        }
        
        try {
            this.pool = new ChatClientServerConnectionPool(this, port);
        }
        catch (IOException ex) {
            Log.log("Unable to initialize server");
            Log.log(ex.getMessage());
        }
    }
    
    
    public void addClientFromSocket(Socket socket) {
        Log.log("Adding a client to the server");
       
        // initialize the user object
        ChatClientServerUser user = new ChatClientServerUser(this, socket);
    }

    /**
     * 
     * @param id 
     * @param line
     * @return boolean If this is a goodbye message
     */
    public Boolean handleMessage(Integer id, String line) {
        
        Log.log("Received message from: "+id+" Saying: "+line);
        
        return false;
    }

    public void addUser(ChatClientServerUser aThis) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void removeUser(ChatClientServerUser aThis) {
        
        // notify clients
        // remove from list
     
    }
 }
