/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema;

/**
 * The path between two places covered by any "personal" mean of
 * transport 
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class ConnectionLinkTypeType extends amivif.schema.GeneralLinkTypeType 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _linkType.
     */
    private amivif.schema.types.ConnectionLinkTypeType _linkType;

    /**
     * Field _defaultDuration.
     */
    private org.exolab.castor.types.Duration _defaultDuration;

    /**
     * Field _frequentTravellerDuration.
     */
    private org.exolab.castor.types.Duration _frequentTravellerDuration;

    /**
     * Field _occasionalTravellerDuration.
     */
    private org.exolab.castor.types.Duration _occasionalTravellerDuration;

    /**
     * Field _mobilityRestrictedTravellerDuration.
     */
    private org.exolab.castor.types.Duration _mobilityRestrictedTravellerDuration;

    /**
     * Field _mobilityRestrictedSuitability.
     */
    private boolean _mobilityRestrictedSuitability;

    /**
     * keeps track of state for field: _mobilityRestrictedSuitabilit
     */
    private boolean _has_mobilityRestrictedSuitability;

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

    /**
     * Field _comment.
     */
    private java.lang.String _comment;

    /**
     * Extensions AMIVIF sur les correspondances
     */
    private amivif.schema.AMIVIF_ConnectionLink_Extension _AMIVIF_ConnectionLink_Extension;


      //----------------/
     //- Constructors -/
    //----------------/

    public ConnectionLinkTypeType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

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
     * 'AMIVIF_ConnectionLink_Extension'. The field
     * 'AMIVIF_ConnectionLink_Extension' has the following
     * description: Extensions AMIVIF sur les correspondances
     * 
     * @return the value of field 'AMIVIF_ConnectionLink_Extension'.
     */
    public amivif.schema.AMIVIF_ConnectionLink_Extension getAMIVIF_ConnectionLink_Extension(
    ) {
        return this._AMIVIF_ConnectionLink_Extension;
    }

    /**
     * Returns the value of field 'comment'.
     * 
     * @return the value of field 'Comment'.
     */
    public java.lang.String getComment(
    ) {
        return this._comment;
    }

    /**
     * Returns the value of field 'defaultDuration'.
     * 
     * @return the value of field 'DefaultDuration'.
     */
    public org.exolab.castor.types.Duration getDefaultDuration(
    ) {
        return this._defaultDuration;
    }

    /**
     * Returns the value of field 'frequentTravellerDuration'.
     * 
     * @return the value of field 'FrequentTravellerDuration'.
     */
    public org.exolab.castor.types.Duration getFrequentTravellerDuration(
    ) {
        return this._frequentTravellerDuration;
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
     * Returns the value of field 'linkType'.
     * 
     * @return the value of field 'LinkType'.
     */
    public amivif.schema.types.ConnectionLinkTypeType getLinkType(
    ) {
        return this._linkType;
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
     * Returns the value of field
     * 'mobilityRestrictedTravellerDuration'.
     * 
     * @return the value of field
     * 'MobilityRestrictedTravellerDuration'.
     */
    public org.exolab.castor.types.Duration getMobilityRestrictedTravellerDuration(
    ) {
        return this._mobilityRestrictedTravellerDuration;
    }

    /**
     * Returns the value of field 'occasionalTravellerDuration'.
     * 
     * @return the value of field 'OccasionalTravellerDuration'.
     */
    public org.exolab.castor.types.Duration getOccasionalTravellerDuration(
    ) {
        return this._occasionalTravellerDuration;
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
     * Sets the value of field 'AMIVIF_ConnectionLink_Extension'.
     * The field 'AMIVIF_ConnectionLink_Extension' has the
     * following description: Extensions AMIVIF sur les
     * correspondances
     * 
     * @param AMIVIF_ConnectionLink_Extension the value of field
     * 'AMIVIF_ConnectionLink_Extension'.
     */
    public void setAMIVIF_ConnectionLink_Extension(
            final amivif.schema.AMIVIF_ConnectionLink_Extension AMIVIF_ConnectionLink_Extension) {
        this._AMIVIF_ConnectionLink_Extension = AMIVIF_ConnectionLink_Extension;
    }

    /**
     * Sets the value of field 'comment'.
     * 
     * @param comment the value of field 'comment'.
     */
    public void setComment(
            final java.lang.String comment) {
        this._comment = comment;
    }

    /**
     * Sets the value of field 'defaultDuration'.
     * 
     * @param defaultDuration the value of field 'defaultDuration'.
     */
    public void setDefaultDuration(
            final org.exolab.castor.types.Duration defaultDuration) {
        this._defaultDuration = defaultDuration;
    }

    /**
     * Sets the value of field 'frequentTravellerDuration'.
     * 
     * @param frequentTravellerDuration the value of field
     * 'frequentTravellerDuration'.
     */
    public void setFrequentTravellerDuration(
            final org.exolab.castor.types.Duration frequentTravellerDuration) {
        this._frequentTravellerDuration = frequentTravellerDuration;
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
     * Sets the value of field 'linkType'.
     * 
     * @param linkType the value of field 'linkType'.
     */
    public void setLinkType(
            final amivif.schema.types.ConnectionLinkTypeType linkType) {
        this._linkType = linkType;
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
     * Sets the value of field
     * 'mobilityRestrictedTravellerDuration'.
     * 
     * @param mobilityRestrictedTravellerDuration the value of
     * field 'mobilityRestrictedTravellerDuration'.
     */
    public void setMobilityRestrictedTravellerDuration(
            final org.exolab.castor.types.Duration mobilityRestrictedTravellerDuration) {
        this._mobilityRestrictedTravellerDuration = mobilityRestrictedTravellerDuration;
    }

    /**
     * Sets the value of field 'occasionalTravellerDuration'.
     * 
     * @param occasionalTravellerDuration the value of field
     * 'occasionalTravellerDuration'.
     */
    public void setOccasionalTravellerDuration(
            final org.exolab.castor.types.Duration occasionalTravellerDuration) {
        this._occasionalTravellerDuration = occasionalTravellerDuration;
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
