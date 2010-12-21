/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema;

/**
 * Type pour les fr�quences horaire
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class TimeSlotTypeType extends amivif.schema.TridentObjectTypeType 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _journeyPatternId.
     */
    private java.lang.String _journeyPatternId;

    /**
     * Field _timetableId.
     */
    private java.lang.String _timetableId;

    /**
     * Field _frequency.
     */
    private org.exolab.castor.types.Duration _frequency;

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

    /**
     * Field _journeyRunningTimeList.
     */
    private java.util.List<amivif.schema.JourneyRunningTime> _journeyRunningTimeList;


      //----------------/
     //- Constructors -/
    //----------------/

    public TimeSlotTypeType() {
        super();
        this._journeyRunningTimeList = new java.util.ArrayList<amivif.schema.JourneyRunningTime>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vJourneyRunningTime
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addJourneyRunningTime(
            final amivif.schema.JourneyRunningTime vJourneyRunningTime)
    throws java.lang.IndexOutOfBoundsException {
        this._journeyRunningTimeList.add(vJourneyRunningTime);
    }

    /**
     * 
     * 
     * @param index
     * @param vJourneyRunningTime
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addJourneyRunningTime(
            final int index,
            final amivif.schema.JourneyRunningTime vJourneyRunningTime)
    throws java.lang.IndexOutOfBoundsException {
        this._journeyRunningTimeList.add(index, vJourneyRunningTime);
    }

    /**
     * Method enumerateJourneyRunningTime.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends amivif.schema.JourneyRunningTime> enumerateJourneyRunningTime(
    ) {
        return java.util.Collections.enumeration(this._journeyRunningTimeList);
    }

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
     * Returns the value of field 'frequency'.
     * 
     * @return the value of field 'Frequency'.
     */
    public org.exolab.castor.types.Duration getFrequency(
    ) {
        return this._frequency;
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
     * Method getJourneyRunningTime.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the amivif.schema.JourneyRunningTime at
     * the given index
     */
    public amivif.schema.JourneyRunningTime getJourneyRunningTime(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._journeyRunningTimeList.size()) {
            throw new IndexOutOfBoundsException("getJourneyRunningTime: Index value '" + index + "' not in range [0.." + (this._journeyRunningTimeList.size() - 1) + "]");
        }

        return (amivif.schema.JourneyRunningTime) _journeyRunningTimeList.get(index);
    }

    /**
     * Method getJourneyRunningTime.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public amivif.schema.JourneyRunningTime[] getJourneyRunningTime(
    ) {
        amivif.schema.JourneyRunningTime[] array = new amivif.schema.JourneyRunningTime[0];
        return (amivif.schema.JourneyRunningTime[]) this._journeyRunningTimeList.toArray(array);
    }

    /**
     * Method getJourneyRunningTimeAsReference.Returns a reference
     * to '_journeyRunningTimeList'. No type checking is performed
     * on any modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<amivif.schema.JourneyRunningTime> getJourneyRunningTimeAsReference(
    ) {
        return this._journeyRunningTimeList;
    }

    /**
     * Method getJourneyRunningTimeCount.
     * 
     * @return the size of this collection
     */
    public int getJourneyRunningTimeCount(
    ) {
        return this._journeyRunningTimeList.size();
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
     * Returns the value of field 'timetableId'.
     * 
     * @return the value of field 'TimetableId'.
     */
    public java.lang.String getTimetableId(
    ) {
        return this._timetableId;
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
     * Method iterateJourneyRunningTime.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends amivif.schema.JourneyRunningTime> iterateJourneyRunningTime(
    ) {
        return this._journeyRunningTimeList.iterator();
    }

    /**
     */
    public void removeAllJourneyRunningTime(
    ) {
        this._journeyRunningTimeList.clear();
    }

    /**
     * Method removeJourneyRunningTime.
     * 
     * @param vJourneyRunningTime
     * @return true if the object was removed from the collection.
     */
    public boolean removeJourneyRunningTime(
            final amivif.schema.JourneyRunningTime vJourneyRunningTime) {
        boolean removed = _journeyRunningTimeList.remove(vJourneyRunningTime);
        return removed;
    }

    /**
     * Method removeJourneyRunningTimeAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public amivif.schema.JourneyRunningTime removeJourneyRunningTimeAt(
            final int index) {
        java.lang.Object obj = this._journeyRunningTimeList.remove(index);
        return (amivif.schema.JourneyRunningTime) obj;
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
     * Sets the value of field 'frequency'.
     * 
     * @param frequency the value of field 'frequency'.
     */
    public void setFrequency(
            final org.exolab.castor.types.Duration frequency) {
        this._frequency = frequency;
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
     * 
     * 
     * @param index
     * @param vJourneyRunningTime
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setJourneyRunningTime(
            final int index,
            final amivif.schema.JourneyRunningTime vJourneyRunningTime)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._journeyRunningTimeList.size()) {
            throw new IndexOutOfBoundsException("setJourneyRunningTime: Index value '" + index + "' not in range [0.." + (this._journeyRunningTimeList.size() - 1) + "]");
        }

        this._journeyRunningTimeList.set(index, vJourneyRunningTime);
    }

    /**
     * 
     * 
     * @param vJourneyRunningTimeArray
     */
    public void setJourneyRunningTime(
            final amivif.schema.JourneyRunningTime[] vJourneyRunningTimeArray) {
        //-- copy array
        _journeyRunningTimeList.clear();

        for (int i = 0; i < vJourneyRunningTimeArray.length; i++) {
                this._journeyRunningTimeList.add(vJourneyRunningTimeArray[i]);
        }
    }

    /**
     * Sets the value of '_journeyRunningTimeList' by copying the
     * given Vector. All elements will be checked for type safety.
     * 
     * @param vJourneyRunningTimeList the Vector to copy.
     */
    public void setJourneyRunningTime(
            final java.util.List<amivif.schema.JourneyRunningTime> vJourneyRunningTimeList) {
        // copy vector
        this._journeyRunningTimeList.clear();

        this._journeyRunningTimeList.addAll(vJourneyRunningTimeList);
    }

    /**
     * Sets the value of '_journeyRunningTimeList' by setting it to
     * the given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param journeyRunningTimeList the Vector to set.
     */
    public void setJourneyRunningTimeAsReference(
            final java.util.List<amivif.schema.JourneyRunningTime> journeyRunningTimeList) {
        this._journeyRunningTimeList = journeyRunningTimeList;
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
     * Sets the value of field 'timetableId'.
     * 
     * @param timetableId the value of field 'timetableId'.
     */
    public void setTimetableId(
            final java.lang.String timetableId) {
        this._timetableId = timetableId;
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
