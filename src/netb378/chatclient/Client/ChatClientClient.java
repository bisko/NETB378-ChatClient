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
package netb378.chatclient.Client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import netb378.chatclient.Log;

/**
 *
 * @author bisko
 */
public final class ChatClientClient {
    
    private ChatClientServerConnectForm serverForm = null;
    private ChatClientClientSocket _clientSocket = null;
    
    
    public String username = "";
    
    public ChatClientClient() {
        ChatClientServerConnectForm.main(this);
        // show the server form
            // try to connect
            // if it fails
                // send error to the frontend
                // if unable to connect to a local address
                    // offer to start a server
            // if it succeeds 
                // hide server form 
                // show main chat form 
        
    }
    
    /**
     * 
     * @param server - the server to connect to
     * @param port - the port to connect to
     * @throws java.io.IOException 
     */
    public void connectToServer(String server, Integer port) throws IOException {
        ChatClientClientSocket _sck = new ChatClientClientSocket(server, port, this);
        this._clientSocket = _sck;
    }
    
    public void onConnectManagement() {
        // inform the server for our username
        this._clientSocket.send("NICK "+this.username);
        
        // run the form hiding/showing
    }
    
    public void startServer(Integer port) {
        // set local server instance
        // start the server
        // connect to the server when return true
    }
    
    /**
     * 
     * @param address - the Address to check for
     * @return If the address is local or not
     */
    public Boolean isThisALocalAddress(String address) {
        
        InetAddress addr = null;
        
        // check if the address is correct
        try {
            addr = InetAddress.getByName(address);
        }
        catch(UnknownHostException ex) {
            return false;
        }
        
        
        // Check if the address is loopback
        if (addr.isAnyLocalAddress() || addr.isLoopbackAddress())
            return true;

        // Check if any interface is registered for the device
        try {
            return NetworkInterface.getByInetAddress(addr) != null;
        } catch (SocketException e) {
            return false;
        }
        
    }
    
    // init socket
    // run main gui
        // if no server found and address in the local addresses - start server
        // kill server when closing the app

    void handleServerMessage(String line) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
