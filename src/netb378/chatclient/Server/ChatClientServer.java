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
package netb378.chatclient.Server;

import java.io.IOException;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import netb378.chatclient.Log;

/**
 *
 * @author Biser Perchinkov F44307
 */
public class ChatClientServer {
    
    int serverPort = 8082;
    ChatClientServerConnectionPool pool = null;
    
    public Hashtable<Integer, ChatClientServerUser> userList = new Hashtable<Integer, ChatClientServerUser>();
       
    public ChatClientServer(int port) throws IOException {
        
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
            throw ex;
        }
    }
    
    
    public void addClientFromSocket(Socket socket) {
        Log.log("Adding a client to the server");
       
        // initialize the user object
        ChatClientServerUser user = new ChatClientServerUser(this, socket);
        this.addUser(user);
    }

    /**
     * 
     * @param id the Client Id
     * @param line
     * @return boolean If this is a goodbye message
     */
    public Boolean handleMessage(Integer id, String line) {
        Boolean goodBye = false;
        Log.log("Received message from: "+id+" Saying: "+line);
        
        // happens when dropping the connection
        if (line == null) {
            // if the user has authenticated, then we send a quit message
            try {
                if (!this.userList.get(id).username.isEmpty()) {
                    line = "QUIT " + this.userList.get(id).username + " Disconnected from server";
                }
                else {
                    goodBye = true;
                }
            }
            catch (NullPointerException ex) {
                goodBye = true;
            }
            Log.log("Line is null!");
        }
        
        if (goodBye == false) {
            // parse the message
            Pattern messagePattern = Pattern.compile("^(\\w+)\\s(.*)");
            Matcher messageMatches = messagePattern.matcher(line);
            try {
                if (messageMatches.matches()) {
                    String command = messageMatches.group(1);
                    String payload = messageMatches.group(2);

                    command = command.trim();
                    payload = payload.trim();

                    if (payload.equals("")) {
                        throw new InvalidCommandException(command, "empty payload");
                    }
                    
                    String othersNotification = command+" "+this.userList.get(id).username+" "+payload;

                    switch(command) {
                        case "NICK":                         
                            // if we have an existing user with that username
                            // we change the username and send it back to the user
                            if (this.checkExistingUser(payload)) {
                                payload = this.getNonCollidingUsername(payload);
                                this.notifySpecificUser(id, "FORCENICK "+payload);
                            }
                            
                            if (this.userList.get(id).username.equals("")) {
                                // the user is now setting the username
                                // first join
                                this.notifyAll("JOIN "+payload);
                            }
                            else {
                                // just a nick change
                                this.notifyAll(othersNotification);
                            }

                            this.userList.get(id).username = payload;
                           
                            break;
                        case "MSG":
                            if (this.userList.get(id).username.equals("")) {
                                throw new InvalidCommandException(command, payload);
                            }
                            
                            this.notifyAll(othersNotification);
                            break;
                            
                        case "NAMES":
                            this.notifySpecificUser(id, this.getUserList());
                            break;
                        case "QUIT":
                            this.notifyAllExcept(id, othersNotification);
                            goodBye = true;
                            break;
                        default:
                            throw new InvalidCommandException(command, payload);
                    }
                }
                else {
                    this.notifySpecificUser(id, "Invalid format for command: \""+line+"\"");
                }
                
            }
            catch(InvalidCommandException ex) {
                this.notifySpecificUser(id, ex.getFormattedError());
            }
        }
        return goodBye;
    }

    public void addUser(ChatClientServerUser user) {        
        Log.log("Adding client to server list");
        this.userList.put(user.id, user);
        
        Log.log("New size: "+this.userList.size());
    }

    public void removeUser(ChatClientServerUser user) {      
        // remove from list
        Log.log("Removed user from list");
        
        this.notifyAllExcept(user.id, "QUIT " + this.userList.get(user.id).username + " Connection reset by peer");
        this.userList.remove(user.id);
    }

    private void notifyAllExcept(Integer id, String payload) {
        
        Log.log("Notify users for message");
        
        for(Integer idx : this.userList.keySet()) {
            if (idx != id) {
                Log.log("Notifying: "+idx);
                this.userList.get(idx).send(payload);
            }
        }
    }
    
    private void notifySpecificUser(Integer id, String payload) {
        if (this.userList.containsKey(id)) {
            this.userList.get(id).send(payload);
        }
    }

    private void notifyAll(String payload) {
        Log.log("Notify users for message");
        
        for(Integer idx : this.userList.keySet()) {
            Log.log("Notifying(all): "+idx);
            this.userList.get(idx).send(payload);
            
        }
    }

    private String getUserList() {
        
        String returnValue = "NAMES";
        
        for(Integer idx : this.userList.keySet()) {
            if (!this.userList.get(idx).username.isEmpty()) {
                returnValue += " "+this.userList.get(idx).username;
            }    
        }
        
        return returnValue;
    }

    private boolean checkExistingUser(String payload) {
        for(Integer idx : this.userList.keySet()) {
            if (!this.userList.get(idx).username.isEmpty()) {
                if(this.userList.get(idx).username.equals(payload)) {
                    return true;
                }
            }    
        }
        
        return false;
    }

    private String getNonCollidingUsername(String payload) {
        Random rnd = new Random();
        
        String resultingUsername = payload;
        
        while (this.checkExistingUser(resultingUsername)) {
            resultingUsername = payload + "-"+ rnd.nextInt(999999);;
        }
               
        return resultingUsername;
    }

    private static class InvalidCommandException extends Exception {

        private String command = null;
        private String payload = null;
        
        public InvalidCommandException(String command, String payload) {
            super(command);
            this.command = command;
            this.payload = payload;
        }
        
        public String getCmd() {
            return this.command;
        }
        
        public String getPayload() {
            return this.payload;
        }
        
        public String getFormattedError() {
            return "Invalid command " + this.getCmd() + " " + this.getPayload();
        }
    }
 }
