/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * Extension de ligne pour l'accessibilit� aux PMR
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class LineExtensionTypeType extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Identifier of the line whose value will remain unchanged for
     * the all line life time 
     */
    private java.lang.String _stableId;

    /**
     * Field _mobilityRestrictedSuitability.
     */
    private boolean _mobilityRestrictedSuitability;

    /**
     * keeps track of state for field: _mobilityRestrictedSuitabilit
     */
    private boolean _has_mobilityRestrictedSuitability;

    /**
     * Field _accessibilitySuitabilityDetails.
     */
    private chouette.schema.AccessibilitySuitabilityDetails _accessibilitySuitabilityDetails;


      //----------------/
     //- Constructors -/
    //----------------/

    public LineExtensionTypeType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     */
    public void deleteMobilityRestrictedSuitability(
    ) {
        this._has_mobilityRestrictedSuitability= false;
    }

    /**
     * Returns the value of field
     * 'accessibilitySuitabilityDetails'.
     * 
     * @return the value of field 'AccessibilitySuitabilityDetails'.
     */
    public chouette.schema.AccessibilitySuitabilityDetails getAccessibilitySuitabilityDetails(
    ) {
        return this._accessibilitySuitabilityDetails;
    }

    /**
     * Returns the value of field 'mobilityRestrictedSuitability'.
     * 
     * @return the value of field 'MobilityRestrictedSuitability'.
     */
    public boolean getMobilityRestrictedSuitability(
    ) {
        return this._mobilityRestrictedSuitability;
    }

    /**
     * Returns the value of field 'stableId'. The field 'stableId'
     * has the following description: Identifier of the line whose
     * value will remain unchanged for the all line life time 
     * 
     * @return the value of field 'StableId'.
     */
    public java.lang.String getStableId(
    ) {
        return this._stableId;
    }

    /**
     * Method hasMobilityRestrictedSuitability.
     * 
     * @return true if at least one MobilityRestrictedSuitability
     * has been added
     */
    public boolean hasMobilityRestrictedSuitability(
    ) {
        return this._has_mobilityRestrictedSuitability;
    }

    /**
     * Returns the value of field 'mobilityRestrictedSuitability'.
     * 
     * @return the value of field 'MobilityRestrictedSuitability'.
     */
    public boolean isMobilityRestrictedSuitability(
    ) {
        return this._mobilityRestrictedSuitability;
    }

    /**
     * Method isValid.
     * 
     * @return true if this object is valid according to the schema
     */
    public boolean isValid(
    ) {
        try {
            validate();
        } catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    }

    /**
     * Sets the value of field 'accessibilitySuitabilityDetails'.
     * 
     * @param accessibilitySuitabilityDetails the value of field
     * 'accessibilitySuitabilityDetails'.
     */
    public void setAccessibilitySuitabilityDetails(
            final chouette.schema.AccessibilitySuitabilityDetails accessibilitySuitabilityDetails) {
        this._accessibilitySuitabilityDetails = accessibilitySuitabilityDetails;
    }

    /**
     * Sets the value of field 'mobilityRestrictedSuitability'.
     * 
     * @param mobilityRestrictedSuitability the value of field
     * 'mobilityRestrictedSuitability'.
     */
    public void setMobilityRestrictedSuitability(
            final boolean mobilityRestrictedSuitability) {
        this._mobilityRestrictedSuitability = mobilityRestrictedSuitability;
        this._has_mobilityRestrictedSuitability = true;
    }

    /**
     * Sets the value of field 'stableId'. The field 'stableId' has
     * the following description: Identifier of the line whose
     * value will remain unchanged for the all line life time 
     * 
     * @param stableId the value of field 'stableId'.
     */
    public void setStableId(
            final java.lang.String stableId) {
        this._stableId = stableId;
    }

    /**
     * 
     * 
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     */
    public void validate(
    )
    throws org.exolab.castor.xml.ValidationException {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    }

}
