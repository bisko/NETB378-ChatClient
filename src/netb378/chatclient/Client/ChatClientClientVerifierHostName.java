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

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 * Input verifier for a hostname.
 * 
 * Validates the input for a valid hostname.
 * 
 * @author Biser Perchinkov F44307
 */
public class ChatClientClientVerifierHostName extends InputVerifier {
    
    /**
     * Verify the input data.
     * 
     * Verifies the input data against a regexp for validity of a host.
     * 
     * @param input The string to check for a valid hostname
     * @return If we have a valid hostname or not.
     */
    @Override
    public boolean verify(JComponent input) {
       String text = ((JTextField) input).getText();
       
       if (text.isEmpty() || !text.matches("^[-.\\w]+$")) {
           return false;
       }
       
       return true;
    }
}