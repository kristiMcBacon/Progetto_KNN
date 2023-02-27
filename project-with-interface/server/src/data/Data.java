package data;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import example.*;
import database.*;

import utility.Keyboard;

/**
 * Modella il training set.
 * @author Kristi Dashaj.
 * @author Giuseppe Grisolia.
 */
public class Data implements Serializable{		
	
	/**
	 * LinkedList di oggetti istanza della classe Example.
	 */
	private List<Example> data;	
	/**
	 * LinkedList di valori della variabile target.
	 */					
	private List<Double> target;	
	/**
	 * numero di esempi memorizzato in data.
	 */				
	private int numberOfExamples;
	/**
	 * arrayList delle variabili indipendenti che definiscono lo schema degli oggetti istanza di Example.
	 */					
	private List<Attribute> explanatorySet;		
	/**
	 * attributo target.
	 */	
	private ContinuousAttribute classAttribute;		 
	
	
	/**
	 * costruttore di data che prende in input il nome di un file.
	 * @param fileName nome file da cui caricare trainingSet.
	 * @throws TrainingDataException quando il file è vuoto o inesistente, 
	 * 								 quando manca lo schema nel trainingSet, 
	 * 								 quando L'attributo non è definito, quando target non è presente.
	 * @throws FileNotFoundException se file non trovato.
	 */
	public Data(String fileName)throws TrainingDataException, FileNotFoundException{
		File inFile = new File (fileName);
		
		//ECCEZIONE
		if(!inFile.exists())
			throw new TrainingDataException("File inesistemte");
		//ECCEZIONE
		if(inFile.length()==0)
			throw new TrainingDataException("File vuoto");

		try (Scanner sc = new Scanner (inFile)) {
			String line = sc.nextLine();
			
			//ECCEZIONE
			if(!line.contains("@schema"))
				throw new TrainingDataException("Schema mancante");
			
			String[] s=line.split(" ");

			//popolare explanatory Set 
			try{
				explanatorySet = new ArrayList<Attribute>(Integer.valueOf(s[1]));		//prende il valore di @schema
			} catch(NumberFormatException ex){
				System.out.println("FORMATO NON VALIDO: La stringa non è un intero");
			} 
					
			short iAttribute=0;    		//indice attributi
			line = sc.nextLine();
			
			while(!line.contains("@data")) {
				s=line.split(" ");

				if(s[0].equals("@desc"))	{ 
					if(s[2].equals("discrete")){
						DiscreteAttribute c=new DiscreteAttribute(s[1],iAttribute);			//aggiungo l'attributo DISCRETO all'explanatorySet    		 
			    	    explanatorySet.add(iAttribute, c);
					}		
					else if(s[2].equals("continuous")){
						ContinuousAttribute c=new ContinuousAttribute(s[1],iAttribute);		//aggiungo l'attributo CONTINUO all'explanatorySet 		 
			    	    explanatorySet.add(iAttribute, c);
					}			
			    }
				else if(s[0].equals("@target"))
					classAttribute=new ContinuousAttribute(s[1], iAttribute);
					else	//ECCEZIONE
					throw new TrainingDataException("Target non presente");
				
			iAttribute++;
			line = sc.nextLine();	//prende riga successiva
			}
			
			//avvalorare numero di esempi
			try{
				numberOfExamples = Integer.valueOf(line.split(" ")[1]);
			} catch(NumberFormatException ex){System.out.println("FORMATO NON VALIDO: La stringa non è un intero");}		
			
			//ECCEZIONE
			if(numberOfExamples==0)
				throw new TrainingDataException("TraingSet vuoto");

			//popolare data e target
			data=new LinkedList<Example>();
			target=new LinkedList<Double>();
			Double cA=0.0;
			
			while (sc.hasNextLine())	{
				Example e=new Example(explanatorySet.size());		//crea vettore con dimensione numero di attributi discreti letti da file
				line = sc.nextLine();
				// assumo che attributi siano tutti discreti
				s=line.split(",");

				//inserisco nell'esempio "e" i valori della linea
				for(short jColumn=0;jColumn<s.length-1;jColumn++) {
					//Se è una stringa
					if(explanatorySet.get(jColumn) instanceof DiscreteAttribute) {
						e.set(s[jColumn],jColumn);	
					}
					//Se è una Double
					else if(explanatorySet.get(jColumn) instanceof ContinuousAttribute) {
						try{
							cA = Double.valueOf(s[jColumn]);
							e.set(cA,jColumn);	
							((ContinuousAttribute)explanatorySet.get(jColumn)).setMin(cA);
							((ContinuousAttribute)explanatorySet.get(jColumn)).setMax(cA);
						} catch(NumberFormatException ex){
							System.out.println("FORMATO NON VALIDO: La stringa non è un double");
						}
					} 
					else	//ECCEZIONE
						throw new TrainingDataException("L'attributo non è definito");
				}
				//AGGIUNGO GLI ESEMPI NELLA LISTA Data			
				data.add(e);	//copia l'esempio 'e' dentro data

				//Avvaloro il target "classAttribute"
				for(Double val:target) {
					classAttribute.setMin(val);
					classAttribute.setMax(val);
				}

				//AGGIUNGO IL TARGET NELLA LISTA Target
				try{
					target.add(Double.valueOf(s[s.length - 1]));
				} catch(NumberFormatException ex){System.out.println("FORMATO NON VALIDO: La stringa non è un double");}

				}
		sc.close();
		}
	}
	
	/**
	 * 2° costruttore di Data attraverso Databse MySQL.
	 * @param db database da cui caricare il trainingSet.
	 * @param tableName Nome tabella del database.
	 * @throws TrainingDataException quando non c'è un valore minimo o massimo.
	 * @throws InsufficientColumnNumberException controlla se la tabella è vuota.
	 * @throws SQLException gestisce errori in query SQL.
	 */
	public Data(DbAccess db, String tableName)throws TrainingDataException, InsufficientColumnNumberException, SQLException{

		TableSchema ts = new TableSchema(tableName, db);
		TableData td = new TableData(db, ts);

		//Inizializzo l'explanatorySet
		explanatorySet= new ArrayList<Attribute>();
		
		//Avvaloramento ExplenetorySet
		Iterator<Column> itTabS=td.getTSchema().getTableSchema().iterator();

		int i=0;
		Column col;
		while(itTabS.hasNext())	{
			col=itTabS.next();
			if(col.isNumber()){
				ContinuousAttribute a=new ContinuousAttribute(col.getColumnName() ,i);		//aggiungo l'attributo CONTINUO all'explanatorySet 		 
				explanatorySet.add(i, a);
				try {
					((ContinuousAttribute) explanatorySet.get(i)).setMin((Double)td.getAggregateColumnValue(col, QUERY_TYPE.MIN));
					} catch (NoValueException e)	{
						throw new TrainingDataException("Non c'è un valore minimo in :"+col.getColumnName());
						}	
				try {
					((ContinuousAttribute) explanatorySet.get(i)).setMax((Double)td.getAggregateColumnValue(col, QUERY_TYPE.MAX));
					} catch (NoValueException e)	{
						throw new TrainingDataException("Non c'è un valore massimo in :"+col.getColumnName());
						}	
			}				
			else {
				DiscreteAttribute a=new DiscreteAttribute(col.getColumnName(),i);		//aggiungo l'attributo DISCRETO all'explanatorySet    		 
				explanatorySet.add(i, a);
			}
			i++;
		}

		//Avvaloro il target "classAttribute"
		classAttribute=new ContinuousAttribute(ts.target().getColumnName() , i);
		try {
			classAttribute.setMin((Double)td.getAggregateColumnValue(ts.target(), QUERY_TYPE.MIN));
		}	catch (NoValueException e)	{
				throw new TrainingDataException("Non c'è un valore minimo in :"+ts.target().getColumnName());
		}
		
		try {
			classAttribute.setMax((Double)td.getAggregateColumnValue(ts.target(), QUERY_TYPE.MAX));
		}	catch (NoValueException e)	{
				throw new TrainingDataException("Non c'è un valore massimo in :"+ts.target().getColumnName());
		}
		
		//popolare data e target
		data=new LinkedList<Example>();
		target=new LinkedList<Double>();

		int index=0;
		Iterator<Object> itT=td.getTargetValues().iterator();
		Iterator<Example> itTset=td.getExamples().iterator();

		while(itTset.hasNext())	{
			data.add(index, itTset.next());
			target.add(index,(Double) itT.next());
			index++;
		}
		
		//Avvaloro 'numberOfExamples'
		numberOfExamples=data.size();
	}

	@Override
    public String toString() {
		Iterator<Example> itD=data.iterator();
		Iterator<Double> itT=target.iterator();
		Iterator<Attribute> itExS=explanatorySet.iterator();
		String temp= new String();
		String desc= new String();
		int i=1;

		while(itExS.hasNext()) {
			desc+="@desc: "+itExS.next()+"\n";
		}   

		while(itD.hasNext()) {
			temp+="\n"+i+") "+itD.next()+itT.next();
			i++;
		}        
	return "@schema: "+explanatorySet.size()+
		   "\n"+desc+
		   "@target: "+classAttribute+
		   "\n\nVALORI DATA[size: "+numberOfExamples+"]: "+temp;
	}

	/**
	 * Restituisce la lunghezza dell'array explanatorySet[].
	 * @return lunghezza explanatorySet.
	 */
	public int getNumberOfExplanatoryAttributes() {		
		return explanatorySet.size();
	}
	
	/**
	 * Restituisce il numero di esempi
	 * @return numero esempi
	 */
	public int getNumberOfExample() {		
		return numberOfExamples;
	}
	
	/**
	 * Partiziona data rispetto all'elemento x di key e restiutisce il punto di separazione.
	 * @param key arrayList contenente distanze.
	 * @param inf estremo inferiore della lista.
	 * @param sup estremo superiore della lista.
	 * @return punto di separazione.
	 */
	private  int partition(ArrayList<Double> key, int inf, int sup){
		int i,j;
	
		i=inf; 
		j=sup; 
		int	med=(inf+sup)/2;
		
		Double x=key.get(med);

		data.get(inf).swap(data.get(med));

		double temp=target.get(inf);
		target.set(inf, target.get(med));
		target.set(med, temp);
		
		temp=key.get(inf);
		key.set(inf, key.get(med));
		key.set(med, temp);
		
		while (true) {		
			while(i<=sup && key.get(i)<=x) { 
				i++; 	
			}
			while(key.get(j)>x) {
				j--;
			}
			
			if(i<j) { 
				data.get(i).swap(data.get(j));
				temp=target.get(i);
				target.set(i, target.get(j));
				target.set(j, temp);
				
				temp=key.get(i);
				key.set(i, key.get(j));
				key.set(j, temp);	
			}
			else break;
		}

		data.get(inf).swap(data.get(j));
	
		temp=target.get(inf);
		target.set(inf, target.get(j));
		target.set(j, temp);
		
		temp=key.get(inf);
		key.set(inf, key.get(j));
		key.set(j,temp);
		
		return j;
	}
	
	/**
	 * Algoritmo quicksort per l'ordinamento di data, usando come relazione d'ordine totale "<=" definita su key. 
	 * @param key arrayList contenente distanze.
	 * @param inf estremo inferiore della lista.
	 * @param sup estremo superiore della lista.
	 */
	private void quicksort(ArrayList<Double> key, int inf, int sup) {	
		if(sup>=inf){
			
		int pos;
			
		pos=partition(key, inf, sup);
					
		if ((pos-inf) < (sup-pos+1)) {
			quicksort(key, inf, pos-1); 
			quicksort(key, pos+1,sup);
			}
		else {
			quicksort(key, pos+1, sup); 
			quicksort(key, inf, pos-1);
		}						
		}	
	}
	
	/**
	 * 1) Avvalora key con le distanze calcolate tra ciascuna istanza di Example memorizzata in data ed e (usare il metodo distance di Example)
	 * 2) ordina data, target e key in accordo ai valori contenuti in key (usare quicksort)
	 * 3) identifica gli esempi di data che sono associati alle k distanze più piccole in key
	 * 4) calcola e restituisce la media dei valori memorizzati in target in corrispondenza degli esempi isolati al punto 3.
	 * @param e esempio.
	 * @param k intero che rappresenta i primi k valori con cui fare la media.
	 * @return media dei valori memorizzati in target in corrispondenza degli esempi isolati.
	 */
	public double avgClosest(Example e, int k) {
		double media = 0.0;
		Example scaled= new Example(e.getSize());

		//1. avvaloriamo key dove mettiamo le distanze calcolate tra gli esempi contenuti in data
		ArrayList<Double> key = new ArrayList<Double>(numberOfExamples); 

		e=scaledExample(e);				//scalo l'esempio "e" 

		for(int i=0; i<numberOfExamples; i++) {
			scaled=scaledExample(data.get(i));		//scalo esempi trainingSet
			key.add(i, e.distance(scaled));			//contiene distanze
		}
		
		//2. ordina data, target e key
		quicksort(key, 0, (key.size()-1));
	
		//3. isoliamo le prime 'k' distanze minori
		ArrayList<Double> minDistance= new ArrayList<Double>(k);
	
		for(int i=0; i<k; i++){
			minDistance.add(i, key.get(i));
		}

		//4. media dei k target minori
		double somma=0.0;

		for(int i=0; i<minDistance.size(); i++){
			somma += target.get(i);
		}
		media = somma/k;

		return media;
	}

	/**
	 * Legge un Example da tastiera.
	 * @return esempio letto da tastiera.
	 */
	public Example readExample() {
		Example e =new Example(getNumberOfExplanatoryAttributes());
		int i=0;
		
		for(Attribute a:explanatorySet) {
			if(a instanceof DiscreteAttribute) {
				String t;
				do {
					System.out.print("Inserisci valore discreto (String) X["+i+"]: ");
					t=Keyboard.readString();
				} while(!Pattern.matches("[a-zA-Z]+", t));
				e.set(t,i);
			}
			else {
				double x=0.0;
				do {
					System.out.print("Inserisci valore continuo X["+i+"]: ");
					x=Keyboard.readDouble();
				} while(new Double(x).equals(Double.NaN));
				e.set(x,i);
			}
			i++;
		}
		return e;
	}

	/**
	 * Legge un Example da file.
	 * @param out stream di oggetti in uscita sul quale scrivere.
	 * @param in stream di oggetti in entrata dal quale leggere.
	 * @return esempio letto da file.
	 * @throws IOException quando avviene un I/O errore.
	 * @throws ClassNotFoundException se una classe serializzabile non puo esserew caricata.
	 * @throws ClassCastException quando si verifica errore durante il cast.
	 */
	public Example readExample(ObjectOutputStream out, ObjectInputStream in) throws IOException, ClassNotFoundException, ClassCastException {
		Example e=new Example(this.getNumberOfExplanatoryAttributes());
		int i=0;
		String t="";
		Double x=0.0;
		
		for(Attribute a:explanatorySet){
			if(a instanceof DiscreteAttribute) {
			//se è una stringa 
				do {
					out.writeObject("@READSTRING");	
					out.writeObject("Inserisci valore discreto (String) X["+i+"]: ");
					t=(String) in.readObject();
				} while(!Pattern.matches("[a-zA-Z]+", t));
				e.set(t,i);
			}
			else {
				do {
					out.writeObject("");
					out.writeObject("Inserisci valore continuo X["+i+"]: ");
					x= (Double) in.readObject();
				} while(new Double(x).equals(Double.NaN));
				e.set(x,i);
			}
			i++;	
		}
		//quando carica tutto l'esempio termina
		out.writeObject("@ENDEXAMPLE");
		return e;
	}

	/**
	 * Legge un Example da interfaccia javaFX.
	 * @param out stream di oggetti in uscita sul quale scrivere.
	 * @param in stream di oggetti in entrata dal quale leggere.
	 * @return esempio letto da file.
	 * @throws IOException quando avviene un I/O errore.
	 * @throws ClassNotFoundException se una classe serializzabile non puo essere caricata.
	 * @throws ClassCastException quando si verifica errore durante il cast.
	 */
	public Example readExampleJFX(ObjectOutputStream out, ObjectInputStream in) throws IOException, ClassNotFoundException, ClassCastException {
		Example e=new Example(this.getNumberOfExplanatoryAttributes());
		int i=0;
		String t="";
		Double x=0.0;
		Boolean correct=true;
		
		for(Attribute a:explanatorySet){
			if(a instanceof DiscreteAttribute) { 	//se è una stringa 
				t=(String) in.readObject();
				if(!Pattern.matches("[a-zA-Z]+", t)) {
					correct=false;
				} else {e.set(t, i);}		
			} 
			else {									//se è un Double
				t=(String) in.readObject();
				try {
					x= Double.parseDouble(t);
					e.set(x, i);
				} catch (NumberFormatException exc) {
					correct=false;
				}
			}
			i++;	
		}
		
		out.writeObject(correct);
		if(!correct) e=null;
		return e;
	}

	/**
	 * Imposta Label su interfaccia javaFX.
	 * @param out stream di oggetti in uscita sul quale scrivere.
	 * @throws IOException quando avviene un I/O errore.
	 * @throws ClassNotFoundException se una classe serializzabile non puo essere caricata.
	 * @throws ClassCastException quando si verifica errore durante il cast.
	 */
	public void setLabel(ObjectOutputStream out) throws IOException, ClassNotFoundException, ClassCastException {
		int i=0;
		
		for(Attribute a:explanatorySet){
			if(a instanceof DiscreteAttribute) {	
					out.writeObject("String X["+i+"]: ");
			}
			else {
					out.writeObject("Double X["+i+"]: ");
			}
			i++;	
		}
	}

	/**
	 * Restituisce una nuova istanza di Example che contiene i valori discreti di e senza modifiche e i valori
 	 * continui di e scalati tra 0 e 1 (fare uso di RTTI e del metodo scale di ContinuousAttribute).
	 * @param e esempio.
	 * @return esempio scalato.
	 */
	private Example scaledExample(Example e) {
        Example f =new Example(e.getSize());
		int i=0;
		for(Attribute a:explanatorySet) {
			if(a instanceof DiscreteAttribute) {
				f.set(e.get(i), i);
			}
			else {
                Double sc=((ContinuousAttribute) a).scale((Double) e.get(i));	//valore scalato
                f.set(sc,i);
			}
		i++;
		}
	return f;
    }
}