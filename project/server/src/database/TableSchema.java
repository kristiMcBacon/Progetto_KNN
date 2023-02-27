package database;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * classe che modella lo schema.
 * @author Kristi Dashaj.
 * @author Giuseppe Grisolia.
 */
public class TableSchema implements Iterable<Column>{
	/**
	 * nome e tipo colonne che compongono la tabella.
	 */
	private List<Column> tableSchema;
	/**
	 * nome e tipo target.
	 */	
	private Column target;	
	/**
	 * nome della tabella.
	 */										
	private String tableName;										
	
	/**
	 * costruttore di classe.
	 * @param tableName nome tabella con cui creare schema.
	 * @param db database da cui prendere tabella.
	 * @throws SQLException gestisce errori in query SQL.
	 * @throws InsufficientColumnNumberException quando la tabella del database è vuota o ha un numero insuficente di colonne.
	 */
	public TableSchema(String tableName, DbAccess db) throws SQLException,InsufficientColumnNumberException{

		tableSchema=new ArrayList<Column>();	
		this.tableName=tableName;		
		
		//crea un HashMap con  i tipi e le corrispondenze (tipo SQL & tipo Java)
		HashMap<String,String> mapSQL_JAVATypes=new HashMap<String, String>();
		mapSQL_JAVATypes.put("CHAR","string");
		mapSQL_JAVATypes.put("VARCHAR","string");
		mapSQL_JAVATypes.put("LONGVARCHAR","string");
		mapSQL_JAVATypes.put("BIT","string");
		mapSQL_JAVATypes.put("SHORT","number");
		mapSQL_JAVATypes.put("INT","number");
		mapSQL_JAVATypes.put("LONG","number");
		mapSQL_JAVATypes.put("FLOAT","number");
		mapSQL_JAVATypes.put("DOUBLE","number");
		
		DatabaseMetaData meta = db.getConnection().getMetaData();
	    ResultSet res = meta.getColumns(null, null, tableName, null);
	     		   
	    while (res.next()) {
	        if(mapSQL_JAVATypes.containsKey(res.getString("TYPE_NAME")))
	        	if(res.isLast()) 	
	        		target=new Column(		
	        				 res.getString("COLUMN_NAME"),
	        				 mapSQL_JAVATypes.get(res.getString("TYPE_NAME")));
	        	else
	        		 tableSchema.add(
						 new Column(res.getString("COLUMN_NAME"),
	        				 mapSQL_JAVATypes.get(res.getString("TYPE_NAME")))
	        				 );	         
	      }
	      res.close();
		  //Eccezione se non vengono avvalorate target & tableSchema
	      if(target==null || tableSchema.size()==0) throw new InsufficientColumnNumberException("La tabella selezionata contiene meno di due colonne");
		}

		/**
		 * restituisce il target di colonna.
		 * @return valore target.
		 */
		public Column target() {
			return target;
		}
		
		/**
		 * restituisce la dimensione della tabella.
		 * @return Dimensione tableSchema.
		 */
		int getNumberOfAttributes() {
			return tableSchema.size();
		}
		
		/**
		 * restituisce il nome della tabella.
		 * @return Nome tabella
		 */
		String getTableName() {
			return tableName;
		}

		/**
		 * restituisce colonna della tabella.
		 * @param pos posizione della colonna da restituire.
		 * @return colonna della tabella.
		 */
		Column getColumn(int pos) {
			Column c=tableSchema.get(pos);
			return c;
		}

		/**
		 * restituisce il table schema.
		 * @return Schema della tabella.
		 */
		public List<Column> getTableSchema() {
			return tableSchema;
		}

		@Override
		public Iterator<Column> iterator() {
			return tableSchema.iterator();
		}

		@Override
		public String toString() { 
			String stampa= new String("NOME TABELLA: "+tableName+"\nSCHEMA DELLA TABELLA: ");
				Iterator<Column> w=tableSchema.iterator();
				   while(w.hasNext()){
					   stampa+="\n" + w.next();   
				   }
				stampa+="\nTARGET: "+target;

			return stampa;
		}
}