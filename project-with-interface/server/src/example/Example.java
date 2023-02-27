package example;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Modella i valori degli attributi indipendenti di un esempio
 * @author Kristi Dashaj
 * @author Giuseppe Grisolia
 */

public class Example implements Serializable {              
    /**
     * Array di Object che contiene un valore per ciascun attributo indipendente
     */
    private LinkedList<Object> example;      

    
    /**
     * costruttore di classe.
     * @param size dimensione esempio.
     */
    public Example(int size) {     
        this.example = new LinkedList<Object>();
        for(int i=0; i<size; i++)
            example.add(new Object());
    }

    @Override
    public String toString() {
            Iterator<Object> e=example.iterator();
            String temp= new String();

            while(e.hasNext()) {
                temp+=e.next()+", ";
            }        
        return "Example= "+temp;
    }

    /**
     * setta l'oggetto o nella posizione index.
     * @param o oggetto da inserire.
     * @param index posizoine dove inserire o.
     */
    public void set(Object o, int index) {     
        this.example.set(index, o);

    }

    /**
     * restituisce l'oggetto nella posizione index
     * @param index posizione dell'oggetto da restituire.
     * @return oggetto in posizione index.
     */
    public Object get(int index) {     
        return example.get(index);
    }

    
    /**
     * Restituisce in valore della lunghezza dell'exemple.
     * @return lunghezza example.
     */ 
    public int getSize() {     
        return example.size();
    }

    /**
     * scambia due esempi.
     * @param e esempio da scambiare.
     * @throws ExampleSizeException quando gli esempi hanno dimensioni diverse.
     */
    public void swap(Example e) throws ExampleSizeException{
        //ECCEZIONE
        if(e.example.size()!=this.example.size())
          throw new ExampleSizeException("Gli esempi hanno dimensioni diverse");

        Example tmp = new Example(e.example.size());
        
        for(int i=0; i<e.example.size(); i++) {
            tmp.set(this.get(i), i);
            this.set(e.get(i), i);
            e.set(tmp.get(i), i);  
        }        
    }

    
    /**
     * Calcola e restituisce la distanza di Hamming calcolata tra l’istanza di Example passata come parametro e quella corrente
     * @param e esempio del quale calcolare distanza di hamming.
     * @return distanza di hamming.
     * @throws ExampleSizeException quando gli esempi hanno dimensioni diverse.
     */
    public double distance(Example e) throws ExampleSizeException{        
        double hamming=0.0;
        
        //ECCEZIONE
        if(e.example.size()!=this.example.size())
          throw new ExampleSizeException("Gli esempi hanno dimensioni diverse");

        if(e.example.size()==this.example.size() && e.example.size()!=0 && this.example.size()!=0){
            for(int i=0; i<(e.getSize()); i++) {
                if(e.get(i) instanceof String) {
                    if(!e.get(i).equals(this.get(i))) {     //distanza + 1 se è diverso
                        hamming++;
                    }                       
                } 
                else if (e.get(i) instanceof Double){     //distanza = valore assoluto della sottrazione dei valori
                    Double a=(Double)e.get(i);
                    Double b=(Double)this.get(i);
                    hamming+=Math.abs(a-b);
                }    
            }
        }else hamming=-1.0;
    return hamming;
    }
}