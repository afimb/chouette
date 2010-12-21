/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema;

/**
 * Instance of a Journey Pattern
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class VehicleJourneyTypeType extends amivif.schema.TridentObjectTypeType 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _routeId.
     */
    private java.lang.String _routeId;

    /**
     * Field _journeyPatternId.
     */
    private java.lang.String _journeyPatternId;

    /**
     * Field _publishedJourneyName.
     */
    private java.lang.String _publishedJourneyName;

    /**
     * Field _publishedJourneyIdentifier.
     */
    private java.lang.String _publishedJourneyIdentifier;

    /**
     * Field _transportMode.
     */
    private amivif.schema.types.TransportModeNameType _transportMode;

    /**
     * Field _vehicleTypeIdentifier.
     */
    private java.lang.String _vehicleTypeIdentifier;

    /**
     * Field _statusValue.
     */
    private amivif.schema.types.ServiceStatusValueType _statusValue;

    /**
     * Field _lineIdShortcut.
     */
    private java.lang.String _lineIdShortcut;

    /**
     * Field _routeIdShortcut.
     */
    private java.lang.String _routeIdShortcut;

    /**
     * Field _operatorId.
     */
    private java.lang.String _operatorId;

    /**
     * Field _facility.
     */
    private java.lang.String _facility;

    /**
     * Field _number.
     */
    private long _number;

    /**
     * keeps track of state for field: _number
     */
    private boolean _has_number;

    /**
     * Field _vehicleJourneyAtStopList.
     */
    private java.util.List<amivif.schema.VehicleJourneyAtStop> _vehicleJourneyAtStopList;

    /**
     * Field _comment.
     */
    private java.lang.String _comment;


      //----------------/
     //- Constructors -/
    //----------------/

    public VehicleJourneyTypeType() {
        super();
        this._vehicleJourneyAtStopList = new java.util.ArrayList<amivif.schema.VehicleJourneyAtStop>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vVehicleJourneyAtStop
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addVehicleJourneyAtStop(
            final amivif.schema.VehicleJourneyAtStop vVehicleJourneyAtStop)
    throws java.lang.IndexOutOfBoundsException {
        this._vehicleJourneyAtStopList.add(vVehicleJourneyAtStop);
    }

    /**
     * 
     * 
     * @param index
     * @param vVehicleJourneyAtStop
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addVehicleJourneyAtStop(
            final int index,
            final amivif.schema.VehicleJourneyAtStop vVehicleJourneyAtStop)
    throws java.lang.IndexOutOfBoundsException {
        this._vehicleJourneyAtStopList.add(index, vVehicleJourneyAtStop);
    }

    /**
     */
    public void deleteNumber(
    ) {
        this._has_number= false;
    }

    /**
     * Method enumerateVehicleJourneyAtStop.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends amivif.schema.VehicleJourneyAtStop> enumerateVehicleJourneyAtStop(
    ) {
        return java.util.Collections.enumeration(this._vehicleJourneyAtStopList);
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
     * Returns the value of field 'facility'.
     * 
     * @return the value of field 'Facility'.
     */
    public java.lang.String getFacility(
    ) {
        return this._facility;
    }

    /**
     * Returns the value of field 'journeyPatternId'.
     * 
     * @return the value of field 'JourneyPatternId'.
     */
    public java.lang.String getJourneyPatternId(
    ) {
        return this._journeyPatternId;
    }

    /**
     * Returns the value of field 'lineIdShortcut'.
     * 
     * @return the value of field 'LineIdShortcut'.
     */
    public java.lang.String getLineIdShortcut(
    ) {
        return this._lineIdShortcut;
    }

    /**
     * Returns the value of field 'number'.
     * 
     * @return the value of field 'Number'.
     */
    public long getNumber(
    ) {
        return this._number;
    }

    /**
     * Returns the value of field 'operatorId'.
     * 
     * @return the value of field 'OperatorId'.
     */
    public java.lang.String getOperatorId(
    ) {
        return this._operatorId;
    }

    /**
     * Returns the value of field 'publishedJourneyIdentifier'.
     * 
     * @return the value of field 'PublishedJourneyIdentifier'.
     */
    public java.lang.String getPublishedJourneyIdentifier(
    ) {
        return this._publishedJourneyIdentifier;
    }

    /**
     * Returns the value of field 'publishedJourneyName'.
     * 
     * @return the value of field 'PublishedJourneyName'.
     */
    public java.lang.String getPublishedJourneyName(
    ) {
        return this._publishedJourneyName;
    }

    /**
     * Returns the value of field 'routeId'.
     * 
     * @return the value of field 'RouteId'.
     */
    public java.lang.String getRouteId(
    ) {
        return this._routeId;
    }

    /**
     * Returns the value of field 'routeIdShortcut'.
     * 
     * @return the value of field 'RouteIdShortcut'.
     */
    public java.lang.String getRouteIdShortcut(
    ) {
        return this._routeIdShortcut;
    }

    /**
     * Returns the value of field 'statusValue'.
     * 
     * @return the value of field 'StatusValue'.
     */
    public amivif.schema.types.ServiceStatusValueType getStatusValue(
    ) {
        return this._statusValue;
    }

    /**
     * Returns the value of field 'transportMode'.
     * 
     * @return the value of field 'TransportMode'.
     */
    public amivif.schema.types.TransportModeNameType getTransportMode(
    ) {
        return this._transportMode;
    }

    /**
     * Method getVehicleJourneyAtStop.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the amivif.schema.VehicleJourneyAtStop
     * at the given index
     */
    public amivif.schema.VehicleJourneyAtStop getVehicleJourneyAtStop(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._vehicleJourneyAtStopList.size()) {
            throw new IndexOutOfBoundsException("getVehicleJourneyAtStop: Index value '" + index + "' not in range [0.." + (this._vehicleJourneyAtStopList.size() - 1) + "]");
        }

        return (amivif.schema.VehicleJourneyAtStop) _vehicleJourneyAtStopList.get(index);
    }

    /**
     * Method getVehicleJourneyAtStop.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public amivif.schema.VehicleJourneyAtStop[] getVehicleJourneyAtStop(
    ) {
        amivif.schema.VehicleJourneyAtStop[] array = new amivif.schema.VehicleJourneyAtStop[0];
        return (amivif.schema.VehicleJourneyAtStop[]) this._vehicleJourneyAtStopList.toArray(array);
    }

    /**
     * Method getVehicleJourneyAtStopAsReference.Returns a
     * reference to '_vehicleJourneyAtStopList'. No type checking
     * is performed on any modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<amivif.schema.VehicleJourneyAtStop> getVehicleJourneyAtStopAsReference(
    ) {
        return this._vehicleJourneyAtStopList;
    }

    /**
     * Method getVehicleJourneyAtStopCount.
     * 
     * @return the size of this collection
     */
    public int getVehicleJourneyAtStopCount(
    ) {
        return this._vehicleJourneyAtStopList.size();
    }

    /**
     * Returns the value of field 'vehicleTypeIdentifier'.
     * 
     * @return the value of field 'VehicleTypeIdentifier'.
     */
    public java.lang.String getVehicleTypeIdentifier(
    ) {
        return this._vehicleTypeIdentifier;
    }

    /**
     * Method hasNumber.
     * 
     * @return true if at least one Number has been added
     */
    public boolean hasNumber(
    ) {
        return this._has_number;
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
     * Method iterateVehicleJourneyAtStop.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends amivif.schema.VehicleJourneyAtStop> iterateVehicleJourneyAtStop(
    ) {
        return this._vehicleJourneyAtStopList.iterator();
    }

    /**
     */
    public void removeAllVehicleJourneyAtStop(
    ) {
        this._vehicleJourneyAtStopList.clear();
    }

    /**
     * Method removeVehicleJourneyAtStop.
     * 
     * @param vVehicleJourneyAtStop
     * @return true if the object was removed from the collection.
     */
    public boolean removeVehicleJourneyAtStop(
            final amivif.schema.VehicleJourneyAtStop vVehicleJourneyAtStop) {
        boolean removed = _vehicleJourneyAtStopList.remove(vVehicleJourneyAtStop);
        return removed;
    }

    /**
     * Method removeVehicleJourneyAtStopAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public amivif.schema.VehicleJourneyAtStop removeVehicleJourneyAtStopAt(
            final int index) {
        java.lang.Object obj = this._vehicleJourneyAtStopList.remove(index);
        return (amivif.schema.VehicleJourneyAtStop) obj;
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
     * Sets the value of field 'facility'.
     * 
     * @param facility the value of field 'facility'.
     */
    public void setFacility(
            final java.lang.String facility) {
        this._facility = facility;
    }

    /**
     * Sets the value of field 'journeyPatternId'.
     * 
     * @param journeyPatternId the value of field 'journeyPatternId'
     */
    public void setJourneyPatternId(
            final java.lang.String journeyPatternId) {
        this._journeyPatternId = journeyPatternId;
    }

    /**
     * Sets the value of field 'lineIdShortcut'.
     * 
     * @param lineIdShortcut the value of field 'lineIdShortcut'.
     */
    public void setLineIdShortcut(
            final java.lang.String lineIdShortcut) {
        this._lineIdShortcut = lineIdShortcut;
    }

    /**
     * Sets the value of field 'number'.
     * 
     * @param number the value of field 'number'.
     */
    public void setNumber(
            final long number) {
        this._number = number;
        this._has_number = true;
    }

    /**
     * Sets the value of field 'operatorId'.
     * 
     * @param operatorId the value of field 'operatorId'.
     */
    public void setOperatorId(
            final java.lang.String operatorId) {
        this._operatorId = operatorId;
    }

    /**
     * Sets the value of field 'publishedJourneyIdentifier'.
     * 
     * @param publishedJourneyIdentifier the value of field
     * 'publishedJourneyIdentifier'.
     */
    public void setPublishedJourneyIdentifier(
            final java.lang.String publishedJourneyIdentifier) {
        this._publishedJourneyIdentifier = publishedJourneyIdentifier;
    }

    /**
     * Sets the value of field 'publishedJourneyName'.
     * 
     * @param publishedJourneyName the value of field
     * 'publishedJourneyName'.
     */
    public void setPublishedJourneyName(
            final java.lang.String publishedJourneyName) {
        this._publishedJourneyName = publishedJourneyName;
    }

    /**
     * Sets the value of field 'routeId'.
     * 
     * @param routeId the value of field 'routeId'.
     */
    public void setRouteId(
            final java.lang.String routeId) {
        this._routeId = routeId;
    }

    /**
     * Sets the value of field 'routeIdShortcut'.
     * 
     * @param routeIdShortcut the value of field 'routeIdShortcut'.
     */
    public void setRouteIdShortcut(
            final java.lang.String routeIdShortcut) {
        this._routeIdShortcut = routeIdShortcut;
    }

    /**
     * Sets the value of field 'statusValue'.
     * 
     * @param statusValue the value of field 'statusValue'.
     */
    public void setStatusValue(
            final amivif.schema.types.ServiceStatusValueType statusValue) {
        this._statusValue = statusValue;
    }

    /**
     * Sets the value of field 'transportMode'.
     * 
     * @param transportMode the value of field 'transportMode'.
     */
    public void setTransportMode(
            final amivif.schema.types.TransportModeNameType transportMode) {
        this._transportMode = transportMode;
    }

    /**
     * 
     * 
     * @param index
     * @param vVehicleJourneyAtStop
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setVehicleJourneyAtStop(
            final int index,
            final amivif.schema.VehicleJourneyAtStop vVehicleJourneyAtStop)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._vehicleJourneyAtStopList.size()) {
            throw new IndexOutOfBoundsException("setVehicleJourneyAtStop: Index value '" + index + "' not in range [0.." + (this._vehicleJourneyAtStopList.size() - 1) + "]");
        }

        this._vehicleJourneyAtStopList.set(index, vVehicleJourneyAtStop);
    }

    /**
     * 
     * 
     * @param vVehicleJourneyAtStopArray
     */
    public void setVehicleJourneyAtStop(
            final amivif.schema.VehicleJourneyAtStop[] vVehicleJourneyAtStopArray) {
        //-- copy array
        _vehicleJourneyAtStopList.clear();

        for (int i = 0; i < vVehicleJourneyAtStopArray.length; i++) {
                this._vehicleJourneyAtStopList.add(vVehicleJourneyAtStopArray[i]);
        }
    }

    /**
     * Sets the value of '_vehicleJourneyAtStopList' by copying the
     * given Vector. All elements will be checked for type safety.
     * 
     * @param vVehicleJourneyAtStopList the Vector to copy.
     */
    public void setVehicleJourneyAtStop(
            final java.util.List<amivif.schema.VehicleJourneyAtStop> vVehicleJourneyAtStopList) {
        // copy vector
        this._vehicleJourneyAtStopList.clear();

        this._vehicleJourneyAtStopList.addAll(vVehicleJourneyAtStopList);
    }

    /**
     * Sets the value of '_vehicleJourneyAtStopList' by setting it
     * to the given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param vehicleJourneyAtStopList the Vector to set.
     */
    public void setVehicleJourneyAtStopAsReference(
            final java.util.List<amivif.schema.VehicleJourneyAtStop> vehicleJourneyAtStopList) {
        this._vehicleJourneyAtStopList = vehicleJourneyAtStopList;
    }

    /**
     * Sets the value of field 'vehicleTypeIdentifier'.
     * 
     * @param vehicleTypeIdentifier the value of field
     * 'vehicleTypeIdentifier'.
     */
    public void setVehicleTypeIdentifier(
            final java.lang.String vehicleTypeIdentifier) {
        this._vehicleTypeIdentifier = vehicleTypeIdentifier;
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
