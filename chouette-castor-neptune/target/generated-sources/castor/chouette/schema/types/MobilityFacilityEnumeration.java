/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema.types;

/**
 * Values for Mobility Facility: TPEG pti_table 23.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public enum MobilityFacilityEnumeration implements java.io.Serializable {


      //------------------/
     //- Enum Constants -/
    //------------------/

    /**
     * Constant PTI23_255_4
     */
    PTI23_255_4("pti23_255_4"),
    /**
     * Constant UNKNOWN
     */
    UNKNOWN("unknown"),
    /**
     * Constant PTI23_16
     */
    PTI23_16("pti23_16"),
    /**
     * Constant SUITABLEFORWHEELCHAIRS
     */
    SUITABLEFORWHEELCHAIRS("suitableForWheelChairs"),
    /**
     * Constant PTI23_16_1
     */
    PTI23_16_1("pti23_16_1"),
    /**
     * Constant LOWFLOOR
     */
    LOWFLOOR("lowFloor"),
    /**
     * Constant PTI23_16_2
     */
    PTI23_16_2("pti23_16_2"),
    /**
     * Constant BOARDINGASSISTANCE
     */
    BOARDINGASSISTANCE("boardingAssistance"),
    /**
     * Constant PTI23_16_3
     */
    PTI23_16_3("pti23_16_3"),
    /**
     * Constant STEPFREEACCESS
     */
    STEPFREEACCESS("stepFreeAccess"),
    /**
     * Constant TACTILEPATFORMEDGES
     */
    TACTILEPATFORMEDGES("tactilePatformEdges"),
    /**
     * Constant ONBOARDASSISTANCE
     */
    ONBOARDASSISTANCE("onboardAssistance"),
    /**
     * Constant UNACCOMPANIEDMINORASSISTANCE
     */
    UNACCOMPANIEDMINORASSISTANCE("unaccompaniedMinorAssistance"),
    /**
     * Constant AUDIOINFORMATION
     */
    AUDIOINFORMATION("audioInformation"),
    /**
     * Constant VISUALINFORMATION
     */
    VISUALINFORMATION("visualInformation"),
    /**
     * Constant DISPLAYSFORVISUALLYIMPAIRED
     */
    DISPLAYSFORVISUALLYIMPAIRED("displaysForVisuallyImpaired"),
    /**
     * Constant AUDIOFORHEARINGIMPAIRED
     */
    AUDIOFORHEARINGIMPAIRED("audioForHearingImpaired");

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

    private MobilityFacilityEnumeration(final java.lang.String value) {
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
    public static chouette.schema.types.MobilityFacilityEnumeration fromValue(
            final java.lang.String value) {
        for (MobilityFacilityEnumeration c: MobilityFacilityEnumeration.values()) {
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
