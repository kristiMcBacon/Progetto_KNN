package database;

/**
 * Estende classe Exception, viene lanciata quando non è presente nessun valore in un campo di una colonna.
 * @author Kristi Dashaj.
 * @author Giuseppe Grisolia.
 */
public class NoValueException extends Exception {
	/**
     * costruisce {@code NoValueException} con il messaggio di default.
     */
	public NoValueException() {
		super();
	}

	/**
     * costruisce {@code NoValueException} con il messaggio passato in input {@code msg}.
     * @param msg messaggio di errore
     */
	public NoValueException(String msg) {
		super(msg);
	}
}