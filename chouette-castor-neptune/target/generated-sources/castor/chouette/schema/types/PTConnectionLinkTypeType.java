/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema.types;

/**
 * Enumeration containing all the possible type of non PT access
 * link
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public enum PTConnectionLinkTypeType implements java.io.Serializable {


      //------------------/
     //- Enum Constants -/
    //------------------/

    /**
     * Constant UNDERGROUND
     */
    UNDERGROUND("Underground"),
    /**
     * Constant MIXED
     */
    MIXED("Mixed"),
    /**
     * Constant OVERGROUND
     */
    OVERGROUND("Overground");

      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field value.
     */
    private final java.lang.String value;


      //----------------/
     //- Constructors -/
    //----------------/

    private PTConnectionLinkTypeType(final java.lang.String value) {
        this.value = value;
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method fromValue.
     * 
     * @param value
     * @return the constant for this value
     */
    public static chouette.schema.types.PTConnectionLinkTypeType fromValue(
            final java.lang.String value) {
        for (PTConnectionLinkTypeType c: PTConnectionLinkTypeType.values()) {
            if (c.value.equals(value)) {
                return c;
            }
        }
        throw new IllegalArgumentException(value);
    }

    /**
     * 
     * 
     * @param value
     */
    public void setValue(
            final java.lang.String value) {
    }

    /**
     * Method toString.
     * 
     * @return the value of this constant
     */
    public java.lang.String toString(
    ) {
        return this.value;
    }

    /**
     * Method value.
     * 
     * @return the value of this constant
     */
    public java.lang.String value(
    ) {
        return this.value;
    }

}
