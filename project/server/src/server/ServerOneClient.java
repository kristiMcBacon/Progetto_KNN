package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.sql.SQLException;
import data.*;
import data.TrainingDataException;
import database.DatabaseConnectionException;
import database.DbAccess;
import database.InsufficientColumnNumberException;
import mining.KNN;

/**
 * definisce il thread che andrà a gestire separatamente le singole connessioni.
 * @author Kristi Dashaj.
 * @author Giuseppe Grisolia.
 */
public class ServerOneClient extends Thread {
    /**
	 * terminale per connessione tra macchine.
	 */
    private Socket socket;

	/**
	 * stream di oggetti in entrata dal quale leggere.
	 */
    private ObjectInputStream in;

	/**
	 * stream di oggetti in uscita sul quale scrivere.
	 */
    private ObjectOutputStream out;

    /**
	 * costruttore di classe, inizializza il socket, in e out e avvia il thread.
	 * @param s soket da inizializzare.
	 * @throws IOException quando avviene un I/O errore.
	 */
    public ServerOneClient(Socket s) throws IOException{
        socket = s;
        in = new ObjectInputStream(socket.getInputStream());
        out = new ObjectOutputStream(socket.getOutputStream());
		start();
    }

	/**
	 * riscrive il metodo run della superclasse thread al fine di gestire le richieste del client,
	 * in modo da rispondere alle richieste del client.
	 * @throws TrainingDataException quando non c'è un valore minimo o massimo.
	 * @throws InsufficientColumnNumberException controlla se la tabella è vuota.
	 * @throws SQLException gestisce errori in query SQL.
	 * @throws DatabaseConnectionException quando fallisce connessione database.
	 */
    public void run() {
        try {
			while (true) {
				String menu="";
				do {	
					//1(file)-2(binary file)-3(databse)
					int decision=(int) in.readObject();
					KNN knn=null;
					String risposta="@ERROR";		//@ERROR per ripetere il ciclo
					String tableName="";

					switch(decision) {
						//CASO 1(file)
					case 1:{
						do{
							Data trainingSet=null;
							String file="";
							//riceve nome tabella
							tableName= (String) in.readObject();
							file=tableName+".dat";
							try {
								trainingSet=new Data(file);
								knn=new KNN(trainingSet);
								System.out.println(trainingSet);
								//Salva il training set su un file binario
								try{
									knn.salva(file+".dmp");}
								catch(IOException exc) {System.out.println(exc.getMessage());}
							}
							catch(TrainingDataException exc){
								System.out.println(exc.getMessage());}

							//se il knn è stato caricato blocca la condizione del ciclo
							if(knn!=null)	
								risposta="";

							out.writeObject(risposta);
						}while(risposta.equals("@ERROR"));
					}
					break;
					//CASO 2(binary file)
					case 2:{
						do{
							String file="";
							//riceve nome tabella
							tableName= (String) in.readObject();
							file=tableName+".dmp";
							try {
								knn=KNN.carica(file);	
								System.out.println(knn);
							}
							catch(IOException | ClassNotFoundException exc){System.out.println(exc.getMessage());}

							//se il knn è stato caricato blocca la condizione del ciclo
							if(knn!=null)	
								risposta="";

							out.writeObject(risposta);
						}while(risposta.equals("@ERROR"));
					}
					break;
					//CASO 3(databse)
					case 3:{
						do{
							Data trainingSet=null;
							//riceve nome tabella
							tableName= (String) in.readObject();
							try {
								System.out.print("Connecting to DB...");
								DbAccess db=new DbAccess();
								System.out.println("done!");
								trainingSet= new Data(db, tableName);
								System.out.println(trainingSet);
								db.closeConnection();
								
								//Carico il trainingSet nel knn
								knn=new KNN(trainingSet);
								try{
									knn.salva(tableName+"DB.dmp");}
								catch(IOException exc) {System.out.println(exc.getMessage());}
								
							}
							//Gestione eccezione
							catch(InsufficientColumnNumberException | TrainingDataException exc1) {
								System.out.println("Numero di colonne insufficente o trainingset vuoto");}
							//Gestione eccezione connessione database assente
							catch(DatabaseConnectionException exc2) {
								System.out.println("Connessione Database assente");} 
							//Gestore eccezione
							catch (SQLException exc3) {
								System.out.println("Errore di sintassi nell'istruzione SQL");}
							
							
							//se il knn è stato caricato blocca la condizione del ciclo
							if(knn!=null)	
								risposta="";

							out.writeObject(risposta);
						}while(risposta.equals("@ERROR"));
					}
					break;
				}	//fine switch

				//legge esempio
				Double predizione=0.0;
				String c="";
				do {
					predizione=knn.predict(out, in);
					out.writeObject(predizione);
					
					c=(String) in.readObject();

				} while (c.toLowerCase().equals("y"));
				menu=(String) in.readObject();
			}
			while(menu.toLowerCase().equals("y"));
			
			break;
            }
            System.out.println("closing...");
        } catch(IOException | ClassNotFoundException e) {
            System.err.println("IO Exception");
        } finally {
            try {
                socket.close();
            } catch(IOException e) {
                System.err.println("Socket non chiuso");
            }
        }
    }
}