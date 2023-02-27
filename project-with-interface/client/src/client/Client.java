package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * client che manda e riceve messaggi dal server, il client manda la richiesta di comunicazione al server che deve essere attivo.
 * @author Kristi Dashaj.
 * @author Giuseppe Grisolia.
 */
public class Client {
	/**
	 * terminale per connessione tra macchine.
	 */
	private Socket socket=null;

	/**
	 * stream di oggetti in entrata dal quale leggere.
	 */
	private ObjectOutputStream out=null;

	/**
	 * stream di oggetti in uscita sul quale scrivere.
	 */
	private ObjectInputStream in=null;
	
	/**
	 * costruttore di classe.
	 * @param address indirizzo riservato al localhost.
	 * @param port porta per connessione.
	 * @throws IOException quando avviene un I/O errore.
	 * @throws ClassNotFoundException se una classe serializzabile non puo essere caricata.
	 */
	public Client (String address, int port) throws IOException, ClassNotFoundException{
			socket = new Socket(address, port);
			System.out.println(socket);		
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());	
	}
	
	/**
	 * invia un messaggio al server.
	 * @param mex messaggio che il client invia al server.
	 */
	public void invia(Object mex) {
        try {
            out.writeObject(mex);
        } catch (IOException e) {
			System.out.println("ERRORE invio messaggio");}
    }

	/**
	 * rimane in attesa di un messaggio dal server.
	 * @return messaggio che riceve del server.
	 */
    public Object ricevi() {
        Object mex="";
        try {
            mex = in.readObject();
        } catch (ClassNotFoundException | IOException e) {
            System.out.println("ERRORE ricevimento messaggio");}

    return mex;
	}
}