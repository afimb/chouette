/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema.types;

/**
 * Enumeration of specific psychosensory needs
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public enum PyschosensoryNeedEnumeration implements java.io.Serializable {


      //------------------/
     //- Enum Constants -/
    //------------------/

    /**
     * Constant VISUALIMPAIRMENT
     */
    VISUALIMPAIRMENT("visualImpairment"),
    /**
     * Constant AUDITORYIMPAIRMENT
     */
    AUDITORYIMPAIRMENT("auditoryImpairment"),
    /**
     * Constant COGNITIVEINPUTIMPAIRMENT
     */
    COGNITIVEINPUTIMPAIRMENT("cognitiveInputImpairment"),
    /**
     * Constant AVERSETOLIFTS
     */
    AVERSETOLIFTS("averseToLifts"),
    /**
     * Constant AVERSETOESCALATORS
     */
    AVERSETOESCALATORS("averseToEscalators"),
    /**
     * Constant AVERSETOCONFINEDSPACES
     */
    AVERSETOCONFINEDSPACES("averseToConfinedSpaces"),
    /**
     * Constant AVERSETOCROWDS
     */
    AVERSETOCROWDS("averseToCrowds"),
    /**
     * Constant OTHERPSYCHOSENSORYNEED
     */
    OTHERPSYCHOSENSORYNEED("otherPsychosensoryNeed");

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

    private PyschosensoryNeedEnumeration(final java.lang.String value) {
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
    public static chouette.schema.types.PyschosensoryNeedEnumeration fromValue(
            final java.lang.String value) {
        for (PyschosensoryNeedEnumeration c: PyschosensoryNeedEnumeration.values()) {
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
