package data;

/**
 * Estende la classe Attribute e rappresenta un attributo continuo.
 * @author Kristi Dashaj.
 * @author Giuseppe Grisolia.
 */
class ContinuousAttribute extends Attribute{         
    /**
     * valore minimo.
     */
    private double min = Double.POSITIVE_INFINITY;

    /**
     * valore massimo.
     */
    private double max = Double.NEGATIVE_INFINITY;

    /**
     * Costruttore attributo continuo.
     * @param name nome attributo continuo.
     * @param index indice attributo continuo.
     */
    ContinuousAttribute(String name, int index)   {   
        super(name, index);
    }

    @Override
    public String toString() { 
        return super.toString()+" |MIN: "+min+"|MAX: "+max+"|";
    }

    /**
     * aggiorna min in base al valore v passato come parametro (if v<min then min=v).
     * @param v variabile da impostare come minimo .
     */
    void setMin(Double v){
        if (v<min)
            min = v;
    }

    
    /**
     * aggiorna max in base al valore v passato come parametro (if v>max then max=v).
     * @param v variabile da impostare come massimo.
     */
    void setMax(Double v){
        if (v>max)
            max = v;
    }

    
    /**
     * restitusice il valore (value-min)/(max-min).
     * @param value valore da scalare.
     * @return valore che rappresente la divisione tra (value-min) e (max-min).
     */
    double scale(double value){
        double result = 0.0;
        result = (value-min)/(max-min);
        return result;
    }
}