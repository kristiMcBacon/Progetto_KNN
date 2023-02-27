package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import example.Example;

/**
 * classe che modella la tabella di un database sql.
 * @author Kristi Dashaj.
 * @author Giuseppe Grisolia.
 */
public class TableData {
	/**
	 * accesso al database.
	 */
	private DbAccess db;
	/**
	 * nome tabella.
	 */
	private String table;
	/**
	 * contiene lo schema della tabella.
	 */
	private TableSchema tSchema;
	/**
	 * Lista di esempi.
	 */
	private List<Example> transSet;
	/**
	 * nome e tipo target.
	 */
	private List<Object> target;
	
	/**
	 * costruttore di classe.
	 * @param db database.
	 * @param tSchema contiene lo schema della tabella
	 * @throws SQLException gestisce errori in query SQL.
	 * @throws InsufficientColumnNumberException controlla se la tabella è vuota.
	 */
	public TableData(DbAccess db, TableSchema tSchema) throws SQLException,InsufficientColumnNumberException{
		this.db=db;
		this.tSchema=tSchema;
		this.table=tSchema.getTableName();		
		transSet = new ArrayList<Example>();
		target= new ArrayList<Object>();	
		init();
	}
	
	/**
	 * carica gli esempi, i target dal database.
	 * @throws SQLException gestisce errori in query SQL.
	 */
	private void init() throws SQLException{		
		String query="select ";
		
		for(Column c:tSchema){			
			query += c.getColumnName();
			query+=",";
		}

		query +=tSchema.target().getColumnName();
		query += (" FROM "+table);
		
		Statement statement = db.getConnection().createStatement();
		ResultSet rs = statement.executeQuery(query);
		while (rs.next()) {
			Example currentTuple=new Example(tSchema.getNumberOfAttributes());
			int i=0;
			for(Column c:tSchema) {
				if(c.isNumber())
					currentTuple.set(rs.getDouble(i+1),i);
				else
					currentTuple.set(rs.getString(i+1),i);
				i++;
			}
			transSet.add(currentTuple);
			
			if(tSchema.target().isNumber())
				target.add(rs.getDouble(tSchema.target().getColumnName()));
			else
				target.add(rs.getString(tSchema.target().getColumnName()));
		}
		rs.close();
		statement.close();	
	}
	
	/**
	 * formula ed esegue un interogazione SQL per estrarre il valore MIN o MAX di column.
	 * @param column colonna da cui estrarre valore max o min.
	 * @param aggregate enumerativo per indicare min o max
	 * @return valore min o max estratto.
	 * @throws SQLException gestisce errori in query SQL.
	 * @throws NoValueException quando non è presente nessun valore in un campo di una colonna.
	 */
	public Object getAggregateColumnValue(Column column,QUERY_TYPE aggregate) throws SQLException, NoValueException	{

		String query="SELECT ";											//Creazione stringa con SELECT..
		if(aggregate == QUERY_TYPE.MIN){								//se in input passiamo QUERY_TYPE.MIN aggiunge alla stringa la parola "min"
			query+="MIN";													
		}
		else if (aggregate == QUERY_TYPE.MAX)	{						//se in input passiamo QUERY_TYPE.Max aggiunge alla stringa la parola "max"
			query+="MAX";
		}
		query+="("+column.getColumnName()+") FROM "+table;				//poi aggiungiamo "FROM" e il nome della tabella----> risultato si ha la query completa...
  		
		Statement statement = db.getConnection().createStatement();		//creazione satatemant per operare sul database
		ResultSet rs = statement.executeQuery(query);					//caricamento query in result set
		Object val=null;							
		if(rs.next())	{
			if(column.isNumber())
				val = rs.getDouble(1);
			else
				val = rs.getString(1);
		}
		rs.close();
		statement.close();
		if(val==null)
			throw new NoValueException("No "+aggregate+" on "+column.getColumnName());

		return val;
	}

	/**
	 * restituisce la lista di esempi.
	 * @return lista esempi.
	 */
	public List<Example> getExamples(){
		return transSet; 
	}

	/**
	 * restituisce la lista di target.
	 * @return lista target.
	 */
	public List<Object> getTargetValues(){
		return target; 
	}

	/**
	 * restituisce lo schema della tabella.
	 * @return schema tabella.
	 */
	public TableSchema getTSchema(){
		return tSchema; 
	}
	
	
	@Override
	public String toString() { 
		String stampa= new String("\nSTAMPA Tabella---------------------------\n"+tSchema+"\n\nVALORI TABELLA:");
		
		Iterator<Object> itTarget=target.iterator();
		Iterator<Example> itT=transSet.iterator();
				   while(itT.hasNext()){
					   stampa+="\n" + itT.next()+" "+itTarget.next();   
				   }
	
		return stampa+"\nFINE STAMPA Tabella----------------------\n";
	}
}