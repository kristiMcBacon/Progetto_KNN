package data;
import java.io.Serializable;

/**
 * La classe modella un generico attributo discreto o continuo
 * @author Kristi Dashaj
 * @author Giuseppe Grisolia
 */
abstract class Attribute implements Serializable{   

    /**
     * nome simbolico dell'attributo
     */
    private String name;
    
    /**
     * identificativo numerico dell'attributo
     */
    private int index;

    /**
     * Costruttore dell'attributo
     * @param name nome dell'attributo
     * @param index identificativo numerico attributo
     */
    Attribute(String name, int index) {   
        this.name = new String(name);
        this.index = index;
    }

    @Override
    public String toString() { 
        return "Attribute["+index+"] = "+name;
    }

    /**
     * Restituisce il nome dell'attributo
     * @return nome dell'attributo
     */
    String getName() {     
        return name;
    }

    /**
     * Restituisce l'index dell'attributo
     * @return index dell'attributo
     */
    int getIndex() {
        return index;
    }
}