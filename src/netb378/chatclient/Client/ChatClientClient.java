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

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import netb378.chatclient.Log;
import netb378.chatclient.Server.ChatClientServer;

/**
 * Chat client manager.
 * 
 * Handles the client actions and GUI.
 * 
 * @author Biser Perchinkov F44307
 */
public final class ChatClientClient {
    
    private final ChatClientServerConnectForm serverForm = null;
    private ChatClientClientSocket _clientSocket = null;
    private ChatClientClientMainWindow clientForm = null;
    private ChatClientServer _serverInstance = null;
    
    public String username = "";
    
    /**
     * Class constructor.
     * 
     * When instantiating the class the Connect to server form
     * is started, so the user can input the data where to connect to.
     */
    public ChatClientClient() {
        ChatClientServerConnectForm.main(this);        
    }
    
    /**
     * Get the server connection string from the socket.
     * 
     * @return server connection string in the format server:host
     */
    public String getServerInfo() {
        return this._clientSocket.getServerConnectString();
    }
    
    
    /**
     * Is the client also a server host
     * 
     * @return if the client is also a server host
     */
    public Boolean isThisClientAHost() {
        return this._serverInstance != null;
    }
    /**
     * Connect to a server.
     * 
     * Connect to a server at the specified port and host.
     * 
     * @param server - the server to connect to
     * @param port - the port to connect to
     * @throws java.io.IOException 
     */
    public void connectToServer(String server, Integer port) throws IOException {
        ChatClientClientSocket _sck = new ChatClientClientSocket(server, port, this);
        this._clientSocket = _sck;
    }
    
    /**
     * Called when we have a connection.
     * 
     * Starts the main chat window.
     */
    public void onConnectManagement() {
        // run the form hiding/showing
        ChatClientClientMainWindow.main(this);
    }
    
    
    /**
     * Start a server instance from the client.
     * 
     * Instantiate a server from the client interface. 
     * 
     * @param port the port to be used when starting the server
     * @throws Exception General exception if something goes wrong in the server instance
     */
    public void startServer(Integer port) throws Exception {       
        try {
            this._serverInstance = new ChatClientServer(port);
        }
        catch(IOException ex) {
            throw new Exception("Unable to initialize server :/");
        }
    }
    
    /**
     * Check if a given address is local.
     * 
     * Check the address to see if it is local to the machine.
     * 
     * @param address the address to check for
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

    /**
     * Parse and handle protocol message.
     * 
     * Prepare and parse the string line from the socket.
     * 
     * The line is trimmed and split in the format:
     *  Command<space>Payload
     * 
     * If the line doesn't follow this format, an Invalid Command is sent
     * to the client.
     * 
     * Otherwise we check the format of the commands and handle them appropriately.
     * 
     * @param line the string line sent by the server
     */
    void handleServerMessage(String line) {
        Log.log("Received message from server Saying: "+line);
       
        // trim the line before parsing
        line = line.trim();

        // split the line in command/payload
        Pattern messagePattern = Pattern.compile("^(\\w+)\\s(.*)");
        Matcher messageMatches = messagePattern.matcher(line);

        // if the line follows the specified format we run it through
        if (messageMatches.matches()) {
            String command = messageMatches.group(1);
            String payload = messageMatches.group(2);

            // trim the payload and command just in case we missed something
            // above
            command = command.trim();
            payload = payload.trim();

            switch(command) {

                case "JOIN":
                    this.clientForm.protocolJoinUser(payload);
                    break;
                case "NICK":
                    String[] payloadParts = payload.split(" ");
                    this.clientForm.protocolNickChange(payloadParts[0], payloadParts[1]);
                    break;
                case "FORCENICK":
                    this.username = payload;
                    this.clientForm.protocolForceNick(payload);
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
            // invalid command from the server, we show it to the user
            this.clientForm.invalidServerMessage("Invalid format for command: \""+line+"\"");
        }
    }

    /**
     * Called when we have connected successfully to the server.
     * 
     * Sets the pointer to the form which we pass to the function.
     * Sends initializing commands when connecting to the server.
     * 
     * @param form the form which called the function
     */
    void formInitted(ChatClientClientMainWindow form) {
        this.clientForm = form;
        
        // inform the server for our username
        this._clientSocket.send("NICK "+this.username);
        
        // get the current user list from the server
        this._clientSocket.send("NAMES server");
    }

    
    /**
     * Handle user input.
     * 
     * Handles the text inputted in the main text field in the form.
     * 
     * Checks if we are sending a command to the server or just sending
     * a text message.
     * 
     * A command is defined by a forward-slash ( / ) at the beginning of
     * the string. If the payload is a command, we strip the slash, capitalize
     * the first word and send it to the server.
     * 
     * If it is a text message, we prepend MSG at the beginning and send it 
     * to the server.
     *
     * 
     * @param payload the string to parse and handle
     */
    void handleUserInput(String payload) {
        
        payload = payload.trim();
        if (payload.length() == 0) {
            return;
        }
        
        String serverPayload = "";
        
        // just a text message
        if (payload.charAt(0) != '/') {
            serverPayload = "MSG "+payload;
        }
        else {
            // we got a command
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
        
       
        // after we have defined the server payload - send it
        this._clientSocket.send(serverPayload);
    }

    /**
     * Handle a hard disconnect by the server.
     * 
     * Usually fired when the server drops the connection.
     * Clears and disables the necessary fields from the form.
     * Inform the user of the disconnection with a message.
     * 
     */
    void handleServerDisconnect() {
        this.clientForm.disableAndClearInputs();
        this.clientForm.appendTAMessage("You have been disconnected from the server!");
    }
}
