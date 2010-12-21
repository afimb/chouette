/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema;

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
     * Field _arrivalTime.
     */
    private org.exolab.castor.types.Time _arrivalTime;

    /**
     * Field _departureTime.
     */
    private org.exolab.castor.types.Time _departureTime;

    /**
     * Field _waitingTime.
     */
    private org.exolab.castor.types.Time _waitingTime;

    /**
     * Field _headwayFrequency.
     */
    private org.exolab.castor.types.Duration _headwayFrequency;

    /**
     * Field _boardingAlightingPossibility.
     */
    private amivif.schema.types.BoardingAlightingPossibilityType _boardingAlightingPossibility;

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
     * Returns the value of field 'arrivalTime'.
     * 
     * @return the value of field 'ArrivalTime'.
     */
    public org.exolab.castor.types.Time getArrivalTime(
    ) {
        return this._arrivalTime;
    }

    /**
     * Returns the value of field 'boardingAlightingPossibility'.
     * 
     * @return the value of field 'BoardingAlightingPossibility'.
     */
    public amivif.schema.types.BoardingAlightingPossibilityType getBoardingAlightingPossibility(
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
     * Returns the value of field 'departureTime'.
     * 
     * @return the value of field 'DepartureTime'.
     */
    public org.exolab.castor.types.Time getDepartureTime(
    ) {
        return this._departureTime;
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
     * Returns the value of field 'vehicleJourneyId'.
     * 
     * @return the value of field 'VehicleJourneyId'.
     */
    public java.lang.String getVehicleJourneyId(
    ) {
        return this._vehicleJourneyId;
    }

    /**
     * Returns the value of field 'waitingTime'.
     * 
     * @return the value of field 'WaitingTime'.
     */
    public org.exolab.castor.types.Time getWaitingTime(
    ) {
        return this._waitingTime;
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
     * Sets the value of field 'arrivalTime'.
     * 
     * @param arrivalTime the value of field 'arrivalTime'.
     */
    public void setArrivalTime(
            final org.exolab.castor.types.Time arrivalTime) {
        this._arrivalTime = arrivalTime;
    }

    /**
     * Sets the value of field 'boardingAlightingPossibility'.
     * 
     * @param boardingAlightingPossibility the value of field
     * 'boardingAlightingPossibility'.
     */
    public void setBoardingAlightingPossibility(
            final amivif.schema.types.BoardingAlightingPossibilityType boardingAlightingPossibility) {
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
     * Sets the value of field 'departureTime'.
     * 
     * @param departureTime the value of field 'departureTime'.
     */
    public void setDepartureTime(
            final org.exolab.castor.types.Time departureTime) {
        this._departureTime = departureTime;
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
     * Sets the value of field 'vehicleJourneyId'.
     * 
     * @param vehicleJourneyId the value of field 'vehicleJourneyId'
     */
    public void setVehicleJourneyId(
            final java.lang.String vehicleJourneyId) {
        this._vehicleJourneyId = vehicleJourneyId;
    }

    /**
     * Sets the value of field 'waitingTime'.
     * 
     * @param waitingTime the value of field 'waitingTime'.
     */
    public void setWaitingTime(
            final org.exolab.castor.types.Time waitingTime) {
        this._waitingTime = waitingTime;
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
