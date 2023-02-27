package mining;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import data.*;
import example.*;
import utility.Keyboard;

/**
 * Modella il miner.
 * @author Kristi Dashaj.
 * @author Giuseppe Grisolia.
 */
public class KNN implements Serializable{      
    /**
     * data.
     */
    private Data data;      

    /**
     * costruttore trainingSet.
     * @param trainingSet trainingSet che forma data.
     */
    public KNN(Data trainingSet) {     
        data=trainingSet;
    }

    @Override
    public String toString() { 
        return data.toString();
    }

    /**
     * Predice il valore target dell’esempio passato come parametro.
     * @param e esempio.
     * @param k primi k esempi da considerare per il predict.
     * @return target predetto per l'esempio e.
     */
    public Double predict(Example e, int k) {           
        double val;
		val=data.avgClosest(e,k);
        return val;
    }

    /**
     * Predice il valore target dell’esempio e k inseriti da tastiera.
     * @return target predetto per l'esempio inserito da tastiera.
     */
    public double predict() {
        //inserimento esempio
        Example e =data.readExample();       
        //inserimento k
        int k=0;
        do {
            System.out.print("Inserisci valore K [0<k<"+data.getNumberOfExample()+"]: ");
            k=Keyboard.readInt();
        }while (k<1 || k>data.getNumberOfExample());
        //predizione in base all'esempio
        return data.avgClosest(e, k);
    }

    /**
     * Predice il valore target dell’esempio inserito da client.
	 * @param out stream di oggetti in uscita sul quale scrivere.
	 * @param in stream di oggetti in entrata dal quale leggere.
     * @return predizione valore target.
	 * @throws IOException quando avviene un I/O errore.
	 * @throws ClassNotFoundException se una classe serializzabile non puo esserew caricata.
	 * @throws ClassCastException quando si verifica errore durante il cast.
     */
    public double predict (ObjectOutputStream out, ObjectInputStream in) throws IOException, ClassNotFoundException, ClassCastException {
        Example e =data.readExample(out,in);
        int k=0;
        out.writeObject(data.getNumberOfExample());
        do {
            out.writeObject("Inserisci valore K [0<k<"+data.getNumberOfExample()+"]:");
            k=(Integer)(in.readObject());
        }while (k<1 || k>data.getNumberOfExample());
        
        return data.avgClosest(e, k);
    }

    /**
     * Predice il valore target dell’esempio inserito dall'interfaccia javaFX.
     * @param out stream di oggetti in uscita sul quale scrivere.
	 * @param in stream di oggetti in entrata dal quale leggere.
     * @return predizione valore target di tipo String.
	 * @throws IOException quando avviene un I/O errore.
	 * @throws ClassNotFoundException se una classe serializzabile non puo essere caricata.
	 * @throws ClassCastException quando si verifica errore durante il cast.
     */
    public String predictJFX (ObjectOutputStream out, ObjectInputStream in) throws IOException, ClassNotFoundException, ClassCastException {
        //prende l'esempio dagli input 
        Example e =data.readExampleJFX(out,in);
        //se null interrompi
        if(e==null) {
            return "ERRORE EXAMPLE";
        }
        Boolean correct=true;
        int k=0;
        String pString="";
        String t="";
        //riceve k
        t=(String) in.readObject();
		
        try {
            //converte k in Intero
            k = Integer.parseInt(t);
        }
        //Gestione eccezione se k non è un numero 
        catch (NumberFormatException exc) {
            correct=false;
            out.writeObject(correct);
            return "ERRORE K";
		}

        //controllo intervallo k
        if(k<1 || k>data.getNumberOfExample()) {
            correct=false;
            out.writeObject(correct);
            return "ERRORE K";
        }

        //Calcolo predizione
        Double p=data.avgClosest(e, k);
        //converto predizione in stringa
        pString=Double.toString(p);
        
        out.writeObject(correct);
        return pString;
    }
    
    /**
     * salvataggio knn.
     * @param nomeFile file sul quale salvare knn.
     * @throws IOException quando avviene un I/O errore.
     */
    public void salva(String nomeFile)throws IOException{
        FileOutputStream outFile = new FileOutputStream(nomeFile);
        ObjectOutputStream outStream = new ObjectOutputStream(outFile);
        outStream.writeObject(this);
        outStream.close();
        outFile.close();
    }

    /**
     * caricamento knn.
     * @param nomeFile file dal quale caricare knn.
     * @return trainingSet.
     * @throws IOException quando avviene un I/O errore.
	 * @throws ClassNotFoundException se una classe serializzabile non puo esserew caricata.
     */
    public static KNN carica(String nomeFile)throws IOException, ClassNotFoundException{
        FileInputStream inFile = new FileInputStream(nomeFile);
        ObjectInputStream inStream = new ObjectInputStream(inFile);
        KNN newTrainingSet=(KNN) inStream.readObject();
        inStream.close();
        inFile.close();
        return newTrainingSet;
    }
    
    /**
     * restituisce il trainingSet
     * @return trainingSet
     */
    public Data getData() {
        return data;
    }

    /**
     * Restituisce la lunghezza dell'array explanatorySet[] di {@code data}.
     * @return
     */
    public int getSizeExample() {
        return data.getNumberOfExplanatoryAttributes();
    }
}
