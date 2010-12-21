/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema.types;

/**
 * Enumeration containing all the possible location referencing
 *  methods
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public enum LocationReferencingMethodType implements java.io.Serializable {


      //------------------/
     //- Enum Constants -/
    //------------------/

    /**
     * Constant VALUE_1
     */
    VALUE_1("1"),
    /**
     * Constant VALUE_2
     */
    VALUE_2("2"),
    /**
     * Constant VALUE_3
     */
    VALUE_3("3"),
    /**
     * Constant VALUE_4
     */
    VALUE_4("4"),
    /**
     * Constant VALUE_5
     */
    VALUE_5("5"),
    /**
     * Constant VALUE_6
     */
    VALUE_6("6"),
    /**
     * Constant VALUE_7
     */
    VALUE_7("7"),
    /**
     * Constant VALUE_8
     */
    VALUE_8("8"),
    /**
     * Constant VALUE_9
     */
    VALUE_9("9"),
    /**
     * Constant VALUE_10
     */
    VALUE_10("10"),
    /**
     * Constant VALUE_11
     */
    VALUE_11("11");

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

    private LocationReferencingMethodType(final java.lang.String value) {
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
    public static chouette.schema.types.LocationReferencingMethodType fromValue(
            final java.lang.String value) {
        for (LocationReferencingMethodType c: LocationReferencingMethodType.values()) {
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
