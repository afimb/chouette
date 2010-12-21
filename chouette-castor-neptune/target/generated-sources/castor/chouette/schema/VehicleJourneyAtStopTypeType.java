/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * Passing time on a stop point
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class VehicleJourneyAtStopTypeType extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _stopPointId.
     */
    private java.lang.String _stopPointId;

    /**
     * Field _vehicleJourneyId.
     */
    private java.lang.String _vehicleJourneyId;

    /**
     * Field _connectingServiceId.
     */
    private java.lang.String _connectingServiceId;

    /**
     * Field _vehicleJourneyAtStopTypeChoice.
     */
    private chouette.schema.VehicleJourneyAtStopTypeChoice _vehicleJourneyAtStopTypeChoice;

    /**
     * Field _headwayFrequency.
     */
    private org.exolab.castor.types.Duration _headwayFrequency;

    /**
     * Field _boardingAlightingPossibility.
     */
    private chouette.schema.types.BoardingAlightingPossibilityType _boardingAlightingPossibility;

    /**
     * Field _order.
     */
    private long _order;

    /**
     * keeps track of state for field: _order
     */
    private boolean _has_order;


      //----------------/
     //- Constructors -/
    //----------------/

    public VehicleJourneyAtStopTypeType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     */
    public void deleteOrder(
    ) {
        this._has_order= false;
    }

    /**
     * Returns the value of field 'boardingAlightingPossibility'.
     * 
     * @return the value of field 'BoardingAlightingPossibility'.
     */
    public chouette.schema.types.BoardingAlightingPossibilityType getBoardingAlightingPossibility(
    ) {
        return this._boardingAlightingPossibility;
    }

    /**
     * Returns the value of field 'connectingServiceId'.
     * 
     * @return the value of field 'ConnectingServiceId'.
     */
    public java.lang.String getConnectingServiceId(
    ) {
        return this._connectingServiceId;
    }

    /**
     * Returns the value of field 'headwayFrequency'.
     * 
     * @return the value of field 'HeadwayFrequency'.
     */
    public org.exolab.castor.types.Duration getHeadwayFrequency(
    ) {
        return this._headwayFrequency;
    }

    /**
     * Returns the value of field 'order'.
     * 
     * @return the value of field 'Order'.
     */
    public long getOrder(
    ) {
        return this._order;
    }

    /**
     * Returns the value of field 'stopPointId'.
     * 
     * @return the value of field 'StopPointId'.
     */
    public java.lang.String getStopPointId(
    ) {
        return this._stopPointId;
    }

    /**
     * Returns the value of field 'vehicleJourneyAtStopTypeChoice'.
     * 
     * @return the value of field 'VehicleJourneyAtStopTypeChoice'.
     */
    public chouette.schema.VehicleJourneyAtStopTypeChoice getVehicleJourneyAtStopTypeChoice(
    ) {
        return this._vehicleJourneyAtStopTypeChoice;
    }

    /**
     * Returns the value of field 'vehicleJourneyId'.
     * 
     * @return the value of field 'VehicleJourneyId'.
     */
    public java.lang.String getVehicleJourneyId(
    ) {
        return this._vehicleJourneyId;
    }

    /**
     * Method hasOrder.
     * 
     * @return true if at least one Order has been added
     */
    public boolean hasOrder(
    ) {
        return this._has_order;
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
     * Sets the value of field 'boardingAlightingPossibility'.
     * 
     * @param boardingAlightingPossibility the value of field
     * 'boardingAlightingPossibility'.
     */
    public void setBoardingAlightingPossibility(
            final chouette.schema.types.BoardingAlightingPossibilityType boardingAlightingPossibility) {
        this._boardingAlightingPossibility = boardingAlightingPossibility;
    }

    /**
     * Sets the value of field 'connectingServiceId'.
     * 
     * @param connectingServiceId the value of field
     * 'connectingServiceId'.
     */
    public void setConnectingServiceId(
            final java.lang.String connectingServiceId) {
        this._connectingServiceId = connectingServiceId;
    }

    /**
     * Sets the value of field 'headwayFrequency'.
     * 
     * @param headwayFrequency the value of field 'headwayFrequency'
     */
    public void setHeadwayFrequency(
            final org.exolab.castor.types.Duration headwayFrequency) {
        this._headwayFrequency = headwayFrequency;
    }

    /**
     * Sets the value of field 'order'.
     * 
     * @param order the value of field 'order'.
     */
    public void setOrder(
            final long order) {
        this._order = order;
        this._has_order = true;
    }

    /**
     * Sets the value of field 'stopPointId'.
     * 
     * @param stopPointId the value of field 'stopPointId'.
     */
    public void setStopPointId(
            final java.lang.String stopPointId) {
        this._stopPointId = stopPointId;
    }

    /**
     * Sets the value of field 'vehicleJourneyAtStopTypeChoice'.
     * 
     * @param vehicleJourneyAtStopTypeChoice the value of field
     * 'vehicleJourneyAtStopTypeChoice'.
     */
    public void setVehicleJourneyAtStopTypeChoice(
            final chouette.schema.VehicleJourneyAtStopTypeChoice vehicleJourneyAtStopTypeChoice) {
        this._vehicleJourneyAtStopTypeChoice = vehicleJourneyAtStopTypeChoice;
    }

    /**
     * Sets the value of field 'vehicleJourneyId'.
     * 
     * @param vehicleJourneyId the value of field 'vehicleJourneyId'
     */
    public void setVehicleJourneyId(
            final java.lang.String vehicleJourneyId) {
        this._vehicleJourneyId = vehicleJourneyId;
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
