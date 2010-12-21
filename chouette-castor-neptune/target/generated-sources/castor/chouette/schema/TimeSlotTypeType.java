/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * Type pour les fr�quences horaire
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class TimeSlotTypeType extends chouette.schema.TridentObjectTypeType 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _beginningSlotTime.
     */
    private org.exolab.castor.types.Time _beginningSlotTime;

    /**
     * Field _endSlotTime.
     */
    private org.exolab.castor.types.Time _endSlotTime;

    /**
     * Field _firstDepartureTimeInSlot.
     */
    private org.exolab.castor.types.Time _firstDepartureTimeInSlot;

    /**
     * Field _lastDepartureTimeInSlot.
     */
    private org.exolab.castor.types.Time _lastDepartureTimeInSlot;


      //----------------/
     //- Constructors -/
    //----------------/

    public TimeSlotTypeType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'beginningSlotTime'.
     * 
     * @return the value of field 'BeginningSlotTime'.
     */
    public org.exolab.castor.types.Time getBeginningSlotTime(
    ) {
        return this._beginningSlotTime;
    }

    /**
     * Returns the value of field 'endSlotTime'.
     * 
     * @return the value of field 'EndSlotTime'.
     */
    public org.exolab.castor.types.Time getEndSlotTime(
    ) {
        return this._endSlotTime;
    }

    /**
     * Returns the value of field 'firstDepartureTimeInSlot'.
     * 
     * @return the value of field 'FirstDepartureTimeInSlot'.
     */
    public org.exolab.castor.types.Time getFirstDepartureTimeInSlot(
    ) {
        return this._firstDepartureTimeInSlot;
    }

    /**
     * Returns the value of field 'lastDepartureTimeInSlot'.
     * 
     * @return the value of field 'LastDepartureTimeInSlot'.
     */
    public org.exolab.castor.types.Time getLastDepartureTimeInSlot(
    ) {
        return this._lastDepartureTimeInSlot;
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
     * Sets the value of field 'beginningSlotTime'.
     * 
     * @param beginningSlotTime the value of field
     * 'beginningSlotTime'.
     */
    public void setBeginningSlotTime(
            final org.exolab.castor.types.Time beginningSlotTime) {
        this._beginningSlotTime = beginningSlotTime;
    }

    /**
     * Sets the value of field 'endSlotTime'.
     * 
     * @param endSlotTime the value of field 'endSlotTime'.
     */
    public void setEndSlotTime(
            final org.exolab.castor.types.Time endSlotTime) {
        this._endSlotTime = endSlotTime;
    }

    /**
     * Sets the value of field 'firstDepartureTimeInSlot'.
     * 
     * @param firstDepartureTimeInSlot the value of field
     * 'firstDepartureTimeInSlot'.
     */
    public void setFirstDepartureTimeInSlot(
            final org.exolab.castor.types.Time firstDepartureTimeInSlot) {
        this._firstDepartureTimeInSlot = firstDepartureTimeInSlot;
    }

    /**
     * Sets the value of field 'lastDepartureTimeInSlot'.
     * 
     * @param lastDepartureTimeInSlot the value of field
     * 'lastDepartureTimeInSlot'.
     */
    public void setLastDepartureTimeInSlot(
            final org.exolab.castor.types.Time lastDepartureTimeInSlot) {
        this._lastDepartureTimeInSlot = lastDepartureTimeInSlot;
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
