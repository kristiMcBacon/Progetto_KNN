package database;

/**
 * classe modella colonne tabella database.
 * @author Kristi Dashaj.
 * @author Giuseppe Grisolia.
 */
public class Column{
	/**
	 * nome colonna.
	 */
	private String name;
	/**
	 * identificativo type.
	 */
	private String type;

	/**
	 * costruttore colonna.
	 * @param name nome da assegnare alla colonna.
	 * @param type tipo della colonna.
	 */
	Column(String name,String type){
		this.name=name;
		this.type=type;
	}
	
	/**
	 * Restituisce nome della colonna.
	 * @return nome della colonna.
	 */
	public String getColumnName(){
		return name;
	}

	/**
	 * controlla se type è un numero
	 * @return true se il type è un numero false altrimenti
	 */
	public boolean isNumber(){
		return type.equals("number");
	}

	@Override
	public String toString(){
		return name+":"+type;
	}
}