/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema;

/**
 * The physical (spatial) possibility for a passenger
 *  to access or leave the PT network.
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class PTAccessPointTypeType extends amivif.schema.PointTypeType 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _name.
     */
    private java.lang.String _name;

    /**
     * Field _type.
     */
    private amivif.schema.types.TypeType _type;

    /**
     * Field _openingTime.
     */
    private org.exolab.castor.types.Time _openingTime;

    /**
     * Field _closingTime.
     */
    private org.exolab.castor.types.Time _closingTime;

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
     * Extensions AMIVIF sur les points d'acc�s
     */
    private amivif.schema.AMIVIF_AccessPoint_Extension _AMIVIF_AccessPoint_Extension;


      //----------------/
     //- Constructors -/
    //----------------/

    public PTAccessPointTypeType() {
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
     * Returns the value of field 'AMIVIF_AccessPoint_Extension'.
     * The field 'AMIVIF_AccessPoint_Extension' has the following
     * description: Extensions AMIVIF sur les points d'acc�s
     * 
     * @return the value of field 'AMIVIF_AccessPoint_Extension'.
     */
    public amivif.schema.AMIVIF_AccessPoint_Extension getAMIVIF_AccessPoint_Extension(
    ) {
        return this._AMIVIF_AccessPoint_Extension;
    }

    /**
     * Returns the value of field 'closingTime'.
     * 
     * @return the value of field 'ClosingTime'.
     */
    public org.exolab.castor.types.Time getClosingTime(
    ) {
        return this._closingTime;
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
     * Returns the value of field 'name'.
     * 
     * @return the value of field 'Name'.
     */
    public java.lang.String getName(
    ) {
        return this._name;
    }

    /**
     * Returns the value of field 'openingTime'.
     * 
     * @return the value of field 'OpeningTime'.
     */
    public org.exolab.castor.types.Time getOpeningTime(
    ) {
        return this._openingTime;
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
     * Returns the value of field 'type'.
     * 
     * @return the value of field 'Type'.
     */
    public amivif.schema.types.TypeType getType(
    ) {
        return this._type;
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
     * Sets the value of field 'AMIVIF_AccessPoint_Extension'. The
     * field 'AMIVIF_AccessPoint_Extension' has the following
     * description: Extensions AMIVIF sur les points d'acc�s
     * 
     * @param AMIVIF_AccessPoint_Extension the value of field
     * 'AMIVIF_AccessPoint_Extension'.
     */
    public void setAMIVIF_AccessPoint_Extension(
            final amivif.schema.AMIVIF_AccessPoint_Extension AMIVIF_AccessPoint_Extension) {
        this._AMIVIF_AccessPoint_Extension = AMIVIF_AccessPoint_Extension;
    }

    /**
     * Sets the value of field 'closingTime'.
     * 
     * @param closingTime the value of field 'closingTime'.
     */
    public void setClosingTime(
            final org.exolab.castor.types.Time closingTime) {
        this._closingTime = closingTime;
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
     * Sets the value of field 'name'.
     * 
     * @param name the value of field 'name'.
     */
    public void setName(
            final java.lang.String name) {
        this._name = name;
    }

    /**
     * Sets the value of field 'openingTime'.
     * 
     * @param openingTime the value of field 'openingTime'.
     */
    public void setOpeningTime(
            final org.exolab.castor.types.Time openingTime) {
        this._openingTime = openingTime;
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
     * Sets the value of field 'type'.
     * 
     * @param type the value of field 'type'.
     */
    public void setType(
            final amivif.schema.types.TypeType type) {
        this._type = type;
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
