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
 * Input verifier for numeric values.
 * 
 * Verify that the input has a numeric value.
 * 
 * @author Biser Perchinkov F44307
 */
public class ChatClientClientVerifierNumeric extends InputVerifier {
    
    /**
     * Verify the input for a numeric value.
     * 
     * Checks if the input has a valid numeric value.
     * 
     * @param input The input to check 
     * @return If the value is numeric or not
     */
    @Override
    public boolean verify(JComponent input) {
       String text = ((JTextField) input).getText();
       try {
          Integer.parseInt(text);
       } catch (NumberFormatException e) {
          return false;
       }

       return true;
    }
}