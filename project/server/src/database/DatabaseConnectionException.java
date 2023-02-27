package database;

/**
 * Estende classe Exception, viene lanciata quando si verifica problema con connessione database.
 * @author Kristi Dashaj.
 * @author Giuseppe Grisolia.
 */
public class DatabaseConnectionException extends Exception {
	/**
     * costruisce {@code DatabaseConnectionException} con il messaggio di default.
     */
	DatabaseConnectionException(){
		super();
	}

	/**
     * costruisce {@code DatabaseConnectionException} con il messaggio passato in input {@code msg}.
     * @param msg messaggio di errore.
     */
	DatabaseConnectionException(String msg){
		super(msg);
	}
}