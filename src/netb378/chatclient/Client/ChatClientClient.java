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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import netb378.chatclient.Log;
import netb378.chatclient.Server.ChatClientServer;

/**
 *
 * @author bisko
 */
public final class ChatClientClient {
    
    private ChatClientServerConnectForm serverForm = null;
    private ChatClientClientSocket _clientSocket = null;
    private ChatClientClientMainWindow clientForm = null;
    private ChatClientServer _serverInstance = null;
    
    public String username = "";
    
    public ChatClientClient() {
        ChatClientServerConnectForm.main(this);        
    }
    
    
    public String getServerInfo() {
        return this._clientSocket.getServerConnectString();
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
        // run the form hiding/showing
        ChatClientClientMainWindow.main(this);
    }
    
    public void startServer(Integer port) throws Exception {
        // set local server instance
        // start the server
        // connect to the server when return true
        
        try {
            this._serverInstance = new ChatClientServer(port);
        }
        catch(IOException ex) {
            throw new Exception("Unable to initialize server :/");
        }
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

    void handleServerMessage(String line) {
        Boolean goodBye = false;
        Log.log("Received message from server Saying: "+line);
        
        if (goodBye == false) {
            
            line = line.trim();
            
            // parse the message
            Pattern messagePattern = Pattern.compile("^(\\w+)\\s(.*)");
            Matcher messageMatches = messagePattern.matcher(line);

            if (messageMatches.matches()) {
                String command = messageMatches.group(1);
                String payload = messageMatches.group(2);

                command = command.trim();
                payload = payload.trim();

                if (payload.equals("")) {
                    // fail command from server!
                }

                switch(command) {

                    case "JOIN":
                        this.clientForm.protocolJoinUser(payload);
                        break;
                    case "NICK":

                        String[] payloadParts = payload.split(" ");

                        this.clientForm.protocolNickChange(payloadParts[0], payloadParts[1]);
                        break;
                    case "MSG":

                        this.clientForm.protocolMessage(payload);
                        break;

                    case "NAMES":

                        this.clientForm.protocolNamesList(payload);
                        break;
                    case "QUIT":
                        this.clientForm.protocolQuit(payload);
                        break;
                    default:
                        Log.log("Unsupported command");
                }
            }
            else {
                this.clientForm.invalidServerMessage("Invalid format for command: \""+line+"\"");
            }

        }
    }

    void formInitted(ChatClientClientMainWindow form) {
        this.clientForm = form;
        
        // inform the server for our username
        this._clientSocket.send("NICK "+this.username);
        this._clientSocket.send("NAMES server");
    }

    void handleUserInput(String payload) {
        
        payload = payload.trim();
        if (payload.length() == 0) {
            return;
        }
        
        String serverPayload = "";
        
        if (payload.charAt(0) != '/') {
            serverPayload = "MSG "+payload;
        }
        else {
            // tokenize 
            // parse the message
            Pattern messagePattern = Pattern.compile("^(\\w+)\\s(.*)");
            Matcher messageMatches = messagePattern.matcher(payload.substring(1));

            if (messageMatches.matches()) {
                String command = messageMatches.group(1);
                String cmdPayload = messageMatches.group(2);

                command = command.trim().toUpperCase();
                cmdPayload = cmdPayload.trim();
                
                switch(command) {
                    case "QUIT":
                        serverPayload = command + " " + cmdPayload;
                        
                        this._clientSocket.send(serverPayload);
                        this._clientSocket.close();
                        System.exit(0);
                        break;
                    default:
                        serverPayload = command + " " + cmdPayload;
                }
            }
        }
        
       
        this._clientSocket.send(serverPayload);
        this.clientForm.clearInputField();
    }

    void handleServerDisconnect() {
        this.clientForm.disableAndClearInputs();
        this.clientForm.appendTAMessage("We have been disconnected from the server!");
    }
}
