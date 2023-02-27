package example;

/**
 * Estende classe RunTimeException, viene lanciata quando gli esempi hanno dimensioni diverse.
 * @author Kristi Dashaj.
 * @author Giuseppe Grisolia.
 */
public class ExampleSizeException extends RuntimeException{
    /**
     * costruisce {@code ExampleSizeException} con il messaggio di default.
     */
    public ExampleSizeException(){
        super();
    } 
    
    /**
     * costruisce {@code ExampleSizeException} con il messaggio passato in input {@code msg}.
     * @param msg messaggio di errore
     */
    public ExampleSizeException(String msg){
        super(msg);
    } 
}