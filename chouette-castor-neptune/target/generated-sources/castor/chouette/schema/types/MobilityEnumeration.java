/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema.types;

/**
 * Identification of mobilityneeds
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public enum MobilityEnumeration implements java.io.Serializable {


      //------------------/
     //- Enum Constants -/
    //------------------/

    /**
     * Constant WHEELCHAIR
     */
    WHEELCHAIR("wheelchair"),
    /**
     * Constant ASSISTEDWHEELCHAIR
     */
    ASSISTEDWHEELCHAIR("assistedWheelchair"),
    /**
     * Constant MOTORIZEDWHEELCHAIR
     */
    MOTORIZEDWHEELCHAIR("motorizedWheelchair"),
    /**
     * Constant WALKINGFRAME
     */
    WALKINGFRAME("walkingFrame"),
    /**
     * Constant RESTRICTEDMOBILITY
     */
    RESTRICTEDMOBILITY("restrictedMobility"),
    /**
     * Constant OTHERMOBILITYNEED
     */
    OTHERMOBILITYNEED("otherMobilityNeed");

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

    private MobilityEnumeration(final java.lang.String value) {
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
    public static chouette.schema.types.MobilityEnumeration fromValue(
            final java.lang.String value) {
        for (MobilityEnumeration c: MobilityEnumeration.values()) {
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
