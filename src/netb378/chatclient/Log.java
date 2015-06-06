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

/**
 * Handles all the logging functions for the chat client and server.
 * 
 * It is basically a simple class that outputs things to the stdout.
 * @author Biser Perchinkov F44307
 */
public class Log {
    
    /**
     * Log a message to the standard output.
     * 
     * Logs a message to stdout.
     * 
     * @param message the message to log to stdout.
     */
    static public void log(String message) {
        System.out.print("LOG: ");
        System.out.println(message);
    }
}
