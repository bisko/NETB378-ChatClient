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
        
        if (args.length > 0 || showHelp) {
            System.out.println(   "Simple Chat client help system\n"
                                + "-------------------------------------------\n"
                                + "Command line parameters for the application\n"
                                + "--server     start a chat server without any GUI\n"
                                + "--help       shows this screen\n");
            
            System.exit(0);
        }
       
        if (startServer) {
            // instance the server
        }
        else {
            // start the GUI 
        }
    }
}
