package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import utility.Keyboard;


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
	 * costruttore di classe e richiama il metodo talking.
	 * @param address indirizzo riservato al localhost.
	 * @param port porta per connessione.
	 * @throws IOException quando avviene un I/O errore.
	 * @throws ClassNotFoundException se una classe serializzabile non puo essere caricata.
	 */
	Client (String address, int port) throws IOException, ClassNotFoundException{
			socket = new Socket(address, port);
			System.out.println(socket);		
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());	
			talking();
	}
	
	/**
	 * metodo che gestisce il dialogo con il server.
	 * @throws IOException quando avviene un I/O errore.
	 * @throws ClassNotFoundException se una classe serializzabile non puo essere caricata.
	 */
	private void talking() throws IOException, ClassNotFoundException {
		int decision=0;
		String menu="";
		
		do {	
			do{			
				System.out.println("Load KNN from file [1]");
				System.out.println("Load KNN from binary file  [2]");
				System.out.println("Load KNN from database  [3]");
				decision=Keyboard.readInt();		//chiede di scegliere tipo caricamento
			}while(decision <0 || decision >3);
			String risposta="";
			out.writeObject(decision);				//invia variabile decision al server

			//cicla: chiede e invia il nome tabella, e aspetta la risposta
			do {
				String tableName="";
				System.out.println("Table name (without estensione):");
				tableName=Keyboard.readString();	//chiede nome tabella
				out.writeObject(tableName);     	//invia tableName al server
				risposta=(String)in.readObject();
			}while(risposta.contains("@ERROR"));
			
			System.out.println("KNN loaded on the server");

			// predict
			String c;
			do {
				boolean flag=true; //legge example
				do {
					risposta=(String)(in.readObject());
					if(!risposta.equals("@ENDEXAMPLE")) {
						//sto leggendo l'esempio
						String msg=(String)(in.readObject());
						if(risposta.equals("@READSTRING")) {	//leggo una stringa
							System.out.println(msg);
							out.writeObject(Keyboard.readString());
						}
						else {											//leggo un numero
							double x=0.0;
							do {
								System.out.println(msg);								
								x=Keyboard.readDouble();
							}
							while(new Double(x).equals(Double.NaN));
							out.writeObject(x);
						}
					}
					else flag=false;
				}while(flag);
				
				//sto leggendo  k
				int max=(int)(in.readObject());
				risposta=(String)(in.readObject());
				int k=0;
				do {
					System.out.print(risposta);
					k=Keyboard.readInt();
				}while (k<1 || k>max);
				out.writeObject(k);

				//aspetto la predizione 
				System.out.println("Prediction:"+in.readObject());
	
				System.out.println("Vuoi ripetere predizione? Y/N");
				c=Keyboard.readString();
				out.writeObject(c);	
				
			}while (c.toLowerCase().equals("y"));	
			System.out.println("Vuoi ripetere una nuova esecuzione con un nuovo oggetto KNN? (Y/N)");
			menu=Keyboard.readString();
			out.writeObject(menu);
		}
		while(menu.toLowerCase().equals("y"));
	}


	/**
	 * main che Inizializza la connessione con il server.
	 * @param args
	 */
	public static void main(String[] args){
	
		args = new String[2];
    	args[0] = "127.0.0.1";
    	args[1] = "2025";

		InetAddress addr;
		try {
			addr = InetAddress.getByName(args[0]);
		} catch (UnknownHostException e) {
			System.out.println(e.toString());
			return;
		}
		
		Client c;
		try {
			c=new Client(args[0], new Integer(args[1]));
			
		}  catch (IOException e) {
			System.out.println(e.toString());
			return;
		} catch (NumberFormatException e) {
			System.out.println(e.toString());
			return;
		} catch (ClassNotFoundException e) {
			System.out.println(e.toString());
			return;
		}
	}
}