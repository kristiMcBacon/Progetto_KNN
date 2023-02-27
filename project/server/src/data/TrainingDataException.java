package data;

/**
 * Estende classe RunTimeException, viene lanciata per gestire il caso di acquisizione errata del trainingSet,
 * per esempio: file inesistente, schema mancante trainingSet vuoto, trainingSet privo di variabile target numerica.
 * @author Kristi Dashaj.
 * @author Giuseppe Grisolia.
 */
public class TrainingDataException extends RuntimeException {
    /**
     * costruisce {@code TrainingDataException} con il messaggio di default.
     */
    public TrainingDataException(){
        super();
    } 
    
    /**
     * costruisce {@code TrainingDataException} con il messaggio passato in input {@code msg}.
     * @param msg messaggio di errore
     */
    public TrainingDataException(String msg){
        super(msg);
    } 
}