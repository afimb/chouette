/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema.types;

/**
 * Values for Nuisance Facility: TPEG pti_table 23.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public enum NuisanceFacilityEnumeration implements java.io.Serializable {


      //------------------/
     //- Enum Constants -/
    //------------------/

    /**
     * Constant UNKNOWN
     */
    UNKNOWN("unknown"),
    /**
     * Constant SMOKING
     */
    SMOKING("smoking"),
    /**
     * Constant NOSMOKING
     */
    NOSMOKING("noSmoking"),
    /**
     * Constant MOBILEPHONEUSEZONE
     */
    MOBILEPHONEUSEZONE("mobilePhoneUseZone"),
    /**
     * Constant MOBILEPHONEFREEZONE
     */
    MOBILEPHONEFREEZONE("mobilePhoneFreeZone");

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

    private NuisanceFacilityEnumeration(final java.lang.String value) {
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
    public static chouette.schema.types.NuisanceFacilityEnumeration fromValue(
            final java.lang.String value) {
        for (NuisanceFacilityEnumeration c: NuisanceFacilityEnumeration.values()) {
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
