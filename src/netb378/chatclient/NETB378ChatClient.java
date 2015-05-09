/*
 * Copyright (C) 2015 Biser Perchinkov
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


/**
 * Variant 1. Simple chat system
 * Write a chat server and chat client. The server allows 
 * multiple clients to connect to the server. Use multi-threading 
 * to handle different clients that are connected to the server. 
 * Implement GUI for the client part.
 */
package netb378.chatclient;

/**
 *
 * @author Biser Perchinkov F44307
 */
public class NETB378ChatClient {

    static Boolean startServer = false;
    static Boolean showHelp = false;
    
    private static ChatClientServerInstance serverInstance = null;
    private static ChatClientClientInstance clientInstance = null;
    
    /**
     * @param args Arguments to the application
     */
    public static void main(String[] args) {
        System.out.println("Starting app");
        
   
        for (Integer i = 0; i < args.length; i++) {
            switch(args[i]) {
                case "--server":
                    startServer = true;
                    break;
                case "--help":
                    showHelp = true;
                    break;
            }
        }
        
        if (args.length > 1 || showHelp) {
            System.out.println(   "Simple Chat client help system\n"
                                + "-------------------------------------------\n"
                                + "Command line parameters for the application\n"
                                + "--server     start a chat server without any GUI\n"
                                + "--help       shows this screen\n");
            
            System.exit(0);
        }
       
        if (startServer) {
            // instance the server
            serverInstance = new ChatClientServerInstance();
        }
        else {
            clientInstance = new ChatClientClientInstance();
        }
    }
}
