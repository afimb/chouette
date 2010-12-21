/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * Extension de zone d'arr�ts qui pr�cise notamment un code
 * tarifaire et un identifiant fonctionnel
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class StopAreaExtensionType extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _areaType.
     */
    private chouette.schema.types.ChouetteAreaType _areaType;

    /**
     * Field _nearestTopicName.
     */
    private java.lang.String _nearestTopicName;

    /**
     * Field _fareCode.
     */
    private int _fareCode;

    /**
     * keeps track of state for field: _fareCode
     */
    private boolean _has_fareCode;

    /**
     * Field _registration.
     */
    private chouette.schema.Registration _registration;

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

    /**
     * Field _stairsAvailability.
     */
    private boolean _stairsAvailability;

    /**
     * keeps track of state for field: _stairsAvailability
     */
    private boolean _has_stairsAvailability;

    /**
     * Field _liftAvailability.
     */
    private boolean _liftAvailability;

    /**
     * keeps track of state for field: _liftAvailability
     */
    private boolean _has_liftAvailability;


      //----------------/
     //- Constructors -/
    //----------------/

    public StopAreaExtensionType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     */
    public void deleteFareCode(
    ) {
        this._has_fareCode= false;
    }

    /**
     */
    public void deleteLiftAvailability(
    ) {
        this._has_liftAvailability= false;
    }

    /**
     */
    public void deleteMobilityRestrictedSuitability(
    ) {
        this._has_mobilityRestrictedSuitability= false;
    }

    /**
     */
    public void deleteStairsAvailability(
    ) {
        this._has_stairsAvailability= false;
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
     * Returns the value of field 'areaType'.
     * 
     * @return the value of field 'AreaType'.
     */
    public chouette.schema.types.ChouetteAreaType getAreaType(
    ) {
        return this._areaType;
    }

    /**
     * Returns the value of field 'fareCode'.
     * 
     * @return the value of field 'FareCode'.
     */
    public int getFareCode(
    ) {
        return this._fareCode;
    }

    /**
     * Returns the value of field 'liftAvailability'.
     * 
     * @return the value of field 'LiftAvailability'.
     */
    public boolean getLiftAvailability(
    ) {
        return this._liftAvailability;
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
     * Returns the value of field 'nearestTopicName'.
     * 
     * @return the value of field 'NearestTopicName'.
     */
    public java.lang.String getNearestTopicName(
    ) {
        return this._nearestTopicName;
    }

    /**
     * Returns the value of field 'registration'.
     * 
     * @return the value of field 'Registration'.
     */
    public chouette.schema.Registration getRegistration(
    ) {
        return this._registration;
    }

    /**
     * Returns the value of field 'stairsAvailability'.
     * 
     * @return the value of field 'StairsAvailability'.
     */
    public boolean getStairsAvailability(
    ) {
        return this._stairsAvailability;
    }

    /**
     * Method hasFareCode.
     * 
     * @return true if at least one FareCode has been added
     */
    public boolean hasFareCode(
    ) {
        return this._has_fareCode;
    }

    /**
     * Method hasLiftAvailability.
     * 
     * @return true if at least one LiftAvailability has been added
     */
    public boolean hasLiftAvailability(
    ) {
        return this._has_liftAvailability;
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
     * Method hasStairsAvailability.
     * 
     * @return true if at least one StairsAvailability has been adde
     */
    public boolean hasStairsAvailability(
    ) {
        return this._has_stairsAvailability;
    }

    /**
     * Returns the value of field 'liftAvailability'.
     * 
     * @return the value of field 'LiftAvailability'.
     */
    public boolean isLiftAvailability(
    ) {
        return this._liftAvailability;
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
     * Returns the value of field 'stairsAvailability'.
     * 
     * @return the value of field 'StairsAvailability'.
     */
    public boolean isStairsAvailability(
    ) {
        return this._stairsAvailability;
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
     * Sets the value of field 'areaType'.
     * 
     * @param areaType the value of field 'areaType'.
     */
    public void setAreaType(
            final chouette.schema.types.ChouetteAreaType areaType) {
        this._areaType = areaType;
    }

    /**
     * Sets the value of field 'fareCode'.
     * 
     * @param fareCode the value of field 'fareCode'.
     */
    public void setFareCode(
            final int fareCode) {
        this._fareCode = fareCode;
        this._has_fareCode = true;
    }

    /**
     * Sets the value of field 'liftAvailability'.
     * 
     * @param liftAvailability the value of field 'liftAvailability'
     */
    public void setLiftAvailability(
            final boolean liftAvailability) {
        this._liftAvailability = liftAvailability;
        this._has_liftAvailability = true;
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
     * Sets the value of field 'nearestTopicName'.
     * 
     * @param nearestTopicName the value of field 'nearestTopicName'
     */
    public void setNearestTopicName(
            final java.lang.String nearestTopicName) {
        this._nearestTopicName = nearestTopicName;
    }

    /**
     * Sets the value of field 'registration'.
     * 
     * @param registration the value of field 'registration'.
     */
    public void setRegistration(
            final chouette.schema.Registration registration) {
        this._registration = registration;
    }

    /**
     * Sets the value of field 'stairsAvailability'.
     * 
     * @param stairsAvailability the value of field
     * 'stairsAvailability'.
     */
    public void setStairsAvailability(
            final boolean stairsAvailability) {
        this._stairsAvailability = stairsAvailability;
        this._has_stairsAvailability = true;
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
