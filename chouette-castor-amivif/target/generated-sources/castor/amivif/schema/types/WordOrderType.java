/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema.types;

/**
 * Order of words in a ILOC descriptor
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public enum WordOrderType implements java.io.Serializable {


      //------------------/
     //- Enum Constants -/
    //------------------/

    /**
     * Constant FROMTHEFIRSTTOTHELASTWORD
     */
    FROMTHEFIRSTTOTHELASTWORD("FromTheFirstToTheLastWord"),
    /**
     * Constant FROMTHELASTTOTHEFIRSTWORD
     */
    FROMTHELASTTOTHEFIRSTWORD("FromTheLastToTheFirstWord");

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

    private WordOrderType(final java.lang.String value) {
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
    public static amivif.schema.types.WordOrderType fromValue(
            final java.lang.String value) {
        for (WordOrderType c: WordOrderType.values()) {
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
