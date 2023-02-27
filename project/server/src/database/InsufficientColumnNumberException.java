package database;

/**
 * Estende classe Exception, viene lanciata quando la tabella del database è vuota 
 * o ha un numero insuficente di colonne.
 * @author Kristi Dashaj.
 * @author Giuseppe Grisolia.
 */
public class InsufficientColumnNumberException extends Exception {
	/**
     * costruisce {@code InsufficientColumnNumberException} con il messaggio di default.
     */
	public InsufficientColumnNumberException() {
		super();
	}

	/**
     * costruisce {@code InsufficientColumnNumberException} con il messaggio passato in input {@code msg}.
     * @param msg messaggio di errore
     */
	public InsufficientColumnNumberException(String msg) {
		super(msg);
	}

}