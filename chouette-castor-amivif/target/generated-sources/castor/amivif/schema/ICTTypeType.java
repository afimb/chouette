/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema;

/**
 * Information sur les interdictions de trafic local ou le
 * sectionnement des lignes
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class ICTTypeType extends amivif.schema.TridentObjectTypeType 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _typeICT.
     */
    private amivif.schema.types.TypeICTType _typeICT;

    /**
     * Field _section.
     */
    private long _section;

    /**
     * keeps track of state for field: _section
     */
    private boolean _has_section;

    /**
     * Field _routeId.
     */
    private java.lang.String _routeId;

    /**
     * Field _stopPointIdList.
     */
    private java.util.List<java.lang.String> _stopPointIdList;

    /**
     * Field _vehicleJourneyId.
     */
    private java.lang.String _vehicleJourneyId;


      //----------------/
     //- Constructors -/
    //----------------/

    public ICTTypeType() {
        super();
        this._stopPointIdList = new java.util.ArrayList<java.lang.String>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStopPointId
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addStopPointId(
            final java.lang.String vStopPointId)
    throws java.lang.IndexOutOfBoundsException {
        // check for the maximum size
        if (this._stopPointIdList.size() >= 2) {
            throw new IndexOutOfBoundsException("addStopPointId has a maximum of 2");
        }

        this._stopPointIdList.add(vStopPointId);
    }

    /**
     * 
     * 
     * @param index
     * @param vStopPointId
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addStopPointId(
            final int index,
            final java.lang.String vStopPointId)
    throws java.lang.IndexOutOfBoundsException {
        // check for the maximum size
        if (this._stopPointIdList.size() >= 2) {
            throw new IndexOutOfBoundsException("addStopPointId has a maximum of 2");
        }

        this._stopPointIdList.add(index, vStopPointId);
    }

    /**
     */
    public void deleteSection(
    ) {
        this._has_section= false;
    }

    /**
     * Method enumerateStopPointId.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends java.lang.String> enumerateStopPointId(
    ) {
        return java.util.Collections.enumeration(this._stopPointIdList);
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
     * Returns the value of field 'section'.
     * 
     * @return the value of field 'Section'.
     */
    public long getSection(
    ) {
        return this._section;
    }

    /**
     * Method getStopPointId.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the java.lang.String at the given index
     */
    public java.lang.String getStopPointId(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._stopPointIdList.size()) {
            throw new IndexOutOfBoundsException("getStopPointId: Index value '" + index + "' not in range [0.." + (this._stopPointIdList.size() - 1) + "]");
        }

        return (java.lang.String) _stopPointIdList.get(index);
    }

    /**
     * Method getStopPointId.Returns the contents of the collection
     * in an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public java.lang.String[] getStopPointId(
    ) {
        java.lang.String[] array = new java.lang.String[0];
        return (java.lang.String[]) this._stopPointIdList.toArray(array);
    }

    /**
     * Method getStopPointIdAsReference.Returns a reference to
     * '_stopPointIdList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<java.lang.String> getStopPointIdAsReference(
    ) {
        return this._stopPointIdList;
    }

    /**
     * Method getStopPointIdCount.
     * 
     * @return the size of this collection
     */
    public int getStopPointIdCount(
    ) {
        return this._stopPointIdList.size();
    }

    /**
     * Returns the value of field 'typeICT'.
     * 
     * @return the value of field 'TypeICT'.
     */
    public amivif.schema.types.TypeICTType getTypeICT(
    ) {
        return this._typeICT;
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
     * Method hasSection.
     * 
     * @return true if at least one Section has been added
     */
    public boolean hasSection(
    ) {
        return this._has_section;
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
     * Method iterateStopPointId.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends java.lang.String> iterateStopPointId(
    ) {
        return this._stopPointIdList.iterator();
    }

    /**
     */
    public void removeAllStopPointId(
    ) {
        this._stopPointIdList.clear();
    }

    /**
     * Method removeStopPointId.
     * 
     * @param vStopPointId
     * @return true if the object was removed from the collection.
     */
    public boolean removeStopPointId(
            final java.lang.String vStopPointId) {
        boolean removed = _stopPointIdList.remove(vStopPointId);
        return removed;
    }

    /**
     * Method removeStopPointIdAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public java.lang.String removeStopPointIdAt(
            final int index) {
        java.lang.Object obj = this._stopPointIdList.remove(index);
        return (java.lang.String) obj;
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
     * Sets the value of field 'section'.
     * 
     * @param section the value of field 'section'.
     */
    public void setSection(
            final long section) {
        this._section = section;
        this._has_section = true;
    }

    /**
     * 
     * 
     * @param index
     * @param vStopPointId
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setStopPointId(
            final int index,
            final java.lang.String vStopPointId)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._stopPointIdList.size()) {
            throw new IndexOutOfBoundsException("setStopPointId: Index value '" + index + "' not in range [0.." + (this._stopPointIdList.size() - 1) + "]");
        }

        this._stopPointIdList.set(index, vStopPointId);
    }

    /**
     * 
     * 
     * @param vStopPointIdArray
     */
    public void setStopPointId(
            final java.lang.String[] vStopPointIdArray) {
        //-- copy array
        _stopPointIdList.clear();

        for (int i = 0; i < vStopPointIdArray.length; i++) {
                this._stopPointIdList.add(vStopPointIdArray[i]);
        }
    }

    /**
     * Sets the value of '_stopPointIdList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vStopPointIdList the Vector to copy.
     */
    public void setStopPointId(
            final java.util.List<java.lang.String> vStopPointIdList) {
        // copy vector
        this._stopPointIdList.clear();

        this._stopPointIdList.addAll(vStopPointIdList);
    }

    /**
     * Sets the value of '_stopPointIdList' by setting it to the
     * given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param stopPointIdList the Vector to set.
     */
    public void setStopPointIdAsReference(
            final java.util.List<java.lang.String> stopPointIdList) {
        this._stopPointIdList = stopPointIdList;
    }

    /**
     * Sets the value of field 'typeICT'.
     * 
     * @param typeICT the value of field 'typeICT'.
     */
    public void setTypeICT(
            final amivif.schema.types.TypeICTType typeICT) {
        this._typeICT = typeICT;
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
