/**
 * 
 */
package fr.certu.chouette.echange.comparator;

/**
 * @author michel
 *
 */
public class ComparatorException extends RuntimeException
{
    public enum TYPE {DuplicateKey, UnvailableResource, UnbuildResource};
    
    private TYPE type;    
    /**g
     * @param type
     * @param arg0
     */

    public ComparatorException(TYPE type, String arg0)
    {
        super(arg0);
        this.type = type;
    }

    /**
     * @param type
     * @param arg0
     * @param arg1
     */
    public ComparatorException(TYPE type, String arg0, Throwable arg1)
    {
        super(arg0, arg1);
        this.type = type;
    }


    public TYPE getType()
    {
        return type;
    }
}
