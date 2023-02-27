package server;

import java.io.*;
import java.net.*;

/**
 * classe che rimane in attesa di una connessione e quando la 
 * riceve da un client genera un thread.
 * @author Kristi Dashaj.
 * @author Giuseppe Grisolia.
 */
public class MultiServer {
    /**
     * porta per connessione.
     */
    private int PORT = 2025;

    /**
     * costruttore di classe, inizializza la porta e invoca run().
     * @param port porta connessione.
     */
    public MultiServer(int port) {
        PORT = port;
        run();
    }

    /**
     * istanzia un oggetto della classe server socket che pone in attesa di richiesta
     * di connesioni da parte del client.
     */
    private void run() {
        try {
            ServerSocket s = new ServerSocket(PORT);
            System.out.println("Server started on "+ PORT);
            try{
                while(true){
                    Socket socket = s.accept();
                    System.out.println("Connecting ... "+ socket);
                    try{
                        new ServerOneClient(socket);
                    }catch(IOException e) {
                        socket.close();
                    }
                }
            }finally {
                s.close();
                System.err.println("Server closing ... ");
            }
        }catch (IOException exc) {
            System.err.println(exc.toString());
        }
    }
    
    /**
     * metodo main.
     * @param args
     */
    public static void main (String args[])
    {
        MultiServer server = new MultiServer(2025);
    }
}