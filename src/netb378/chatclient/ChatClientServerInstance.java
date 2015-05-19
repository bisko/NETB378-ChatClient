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

package netb378.chatclient;

import java.io.IOException;
import netb378.chatclient.Server.ChatClientServer;

/**
 * Handles the creation of the chat client server instance
 * that is spawned from the main class.
 * 
 * @author Biser Perchinkov F44307
 */
public class ChatClientServerInstance {    
    
    /**
     * The local server instance that is run.
     */
    private ChatClientServer serverInstance = null;
    
    
    /**
     * Class constructor for the server instance.
     * 
     * This is where the actual initialization of the server happens.
     * After initializing the server, the thread sleeps indefinitely.
     * 
     */
    public ChatClientServerInstance() {
        // start the server instance
        try {
            this.serverInstance = new ChatClientServer(8082);
        }
        catch(IOException ex) {
            Log.log(ex.getMessage());
            System.exit(0);
        }
        
        /**
         * sleep indefinitely to keep the process alive, while the server does
         * it's thing
        */ 
        while(true) {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException ex) {
                Log.log("Interrupted sleep");
            }
        }
        
    }
}
