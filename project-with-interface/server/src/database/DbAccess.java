package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestisce l'accesso al DB per la lettura dei dati di training.
 * @author Kristi Dashaj.
 * @author Giuseppe Grisolia.
 */
public class DbAccess {
	/**
	 * connettore database.
	 */
	private final String DBMS = "jdbc:mysql";
	/**
	 * hostname in cui è installato il server mySQL.
	 */
	private final String SERVER = "localhost";
	/**
	 * numero porta per connessione.
	 */
	private final int PORT = 3306;
	/**
	 * nome database.
	 */
	private final String DATABASE = "Map";
	/**
	 * user database.
	 */
	private final String USER_ID = "root";
	/**
	 * password database.
	 */
	private final String PASSWORD = "root";
	/**
	 * connessione database
	 */
	private Connection conn;

	/**
	 * costruttore che inizializza una connessione al DB.
	 * @throws DatabaseConnectionException quando fallisce connessione database.
	 */
	public DbAccess() throws DatabaseConnectionException{
		String connectionString =  DBMS + "://" + SERVER + ":" + PORT + "/" + DATABASE
				+ "?user=" + USER_ID + "&password=" + PASSWORD + "&serverTimezone=UTC";
		try {
			conn = DriverManager.getConnection(connectionString, USER_ID, PASSWORD);
		} catch (SQLException e) {
			System.out.println("Impossibile connettersi al DB");
			throw new DatabaseConnectionException(e.toString());
		}
	}
	
	/**
	 * restituisce connessione al db.
	 * @return connesione db.
	 */
	Connection getConnection(){
		return conn;
	}

	/**
	 * chiude la connesione del db.
	 */
	public  void closeConnection() {
		try {
			conn.close();
		} catch (SQLException e) {
			System.out.println("Impossibile chiudere la connessione");
		}
	}
}