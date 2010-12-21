/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema;

/**
 * TimeTable informations (merge of LineTimeTable and
 * PointTimeTable)
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class TimetableTypeType extends amivif.schema.TridentObjectTypeType 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _version.
     */
    private java.lang.String _version;

    /**
     * Field _periodList.
     */
    private java.util.List<amivif.schema.Period> _periodList;

    /**
     * Field _calendarDayList.
     */
    private java.util.List<org.exolab.castor.types.Date> _calendarDayList;

    /**
     * Field _dayTypeList.
     */
    private java.util.List<amivif.schema.types.DayTypeType> _dayTypeList;

    /**
     * Field _stopPointIdList.
     */
    private java.util.List<java.lang.String> _stopPointIdList;

    /**
     * Field _vehicleJourneyIdList.
     */
    private java.util.List<java.lang.String> _vehicleJourneyIdList;

    /**
     * Field _comment.
     */
    private java.lang.String _comment;


      //----------------/
     //- Constructors -/
    //----------------/

    public TimetableTypeType() {
        super();
        this._periodList = new java.util.ArrayList<amivif.schema.Period>();
        this._calendarDayList = new java.util.ArrayList<org.exolab.castor.types.Date>();
        this._dayTypeList = new java.util.ArrayList<amivif.schema.types.DayTypeType>();
        this._stopPointIdList = new java.util.ArrayList<java.lang.String>();
        this._vehicleJourneyIdList = new java.util.ArrayList<java.lang.String>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vCalendarDay
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addCalendarDay(
            final org.exolab.castor.types.Date vCalendarDay)
    throws java.lang.IndexOutOfBoundsException {
        this._calendarDayList.add(vCalendarDay);
    }

    /**
     * 
     * 
     * @param index
     * @param vCalendarDay
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addCalendarDay(
            final int index,
            final org.exolab.castor.types.Date vCalendarDay)
    throws java.lang.IndexOutOfBoundsException {
        this._calendarDayList.add(index, vCalendarDay);
    }

    /**
     * 
     * 
     * @param vDayType
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addDayType(
            final amivif.schema.types.DayTypeType vDayType)
    throws java.lang.IndexOutOfBoundsException {
        this._dayTypeList.add(vDayType);
    }

    /**
     * 
     * 
     * @param index
     * @param vDayType
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addDayType(
            final int index,
            final amivif.schema.types.DayTypeType vDayType)
    throws java.lang.IndexOutOfBoundsException {
        this._dayTypeList.add(index, vDayType);
    }

    /**
     * 
     * 
     * @param vPeriod
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPeriod(
            final amivif.schema.Period vPeriod)
    throws java.lang.IndexOutOfBoundsException {
        this._periodList.add(vPeriod);
    }

    /**
     * 
     * 
     * @param index
     * @param vPeriod
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPeriod(
            final int index,
            final amivif.schema.Period vPeriod)
    throws java.lang.IndexOutOfBoundsException {
        this._periodList.add(index, vPeriod);
    }

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
        this._stopPointIdList.add(index, vStopPointId);
    }

    /**
     * 
     * 
     * @param vVehicleJourneyId
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addVehicleJourneyId(
            final java.lang.String vVehicleJourneyId)
    throws java.lang.IndexOutOfBoundsException {
        this._vehicleJourneyIdList.add(vVehicleJourneyId);
    }

    /**
     * 
     * 
     * @param index
     * @param vVehicleJourneyId
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addVehicleJourneyId(
            final int index,
            final java.lang.String vVehicleJourneyId)
    throws java.lang.IndexOutOfBoundsException {
        this._vehicleJourneyIdList.add(index, vVehicleJourneyId);
    }

    /**
     * Method enumerateCalendarDay.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends org.exolab.castor.types.Date> enumerateCalendarDay(
    ) {
        return java.util.Collections.enumeration(this._calendarDayList);
    }

    /**
     * Method enumerateDayType.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends amivif.schema.types.DayTypeType> enumerateDayType(
    ) {
        return java.util.Collections.enumeration(this._dayTypeList);
    }

    /**
     * Method enumeratePeriod.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends amivif.schema.Period> enumeratePeriod(
    ) {
        return java.util.Collections.enumeration(this._periodList);
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
     * Method enumerateVehicleJourneyId.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends java.lang.String> enumerateVehicleJourneyId(
    ) {
        return java.util.Collections.enumeration(this._vehicleJourneyIdList);
    }

    /**
     * Method getCalendarDay.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the org.exolab.castor.types.Date at the
     * given index
     */
    public org.exolab.castor.types.Date getCalendarDay(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._calendarDayList.size()) {
            throw new IndexOutOfBoundsException("getCalendarDay: Index value '" + index + "' not in range [0.." + (this._calendarDayList.size() - 1) + "]");
        }

        return (org.exolab.castor.types.Date) _calendarDayList.get(index);
    }

    /**
     * Method getCalendarDay.Returns the contents of the collection
     * in an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public org.exolab.castor.types.Date[] getCalendarDay(
    ) {
        org.exolab.castor.types.Date[] array = new org.exolab.castor.types.Date[0];
        return (org.exolab.castor.types.Date[]) this._calendarDayList.toArray(array);
    }

    /**
     * Method getCalendarDayAsReference.Returns a reference to
     * '_calendarDayList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<org.exolab.castor.types.Date> getCalendarDayAsReference(
    ) {
        return this._calendarDayList;
    }

    /**
     * Method getCalendarDayCount.
     * 
     * @return the size of this collection
     */
    public int getCalendarDayCount(
    ) {
        return this._calendarDayList.size();
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
     * Method getDayType.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the amivif.schema.types.DayTypeType at
     * the given index
     */
    public amivif.schema.types.DayTypeType getDayType(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._dayTypeList.size()) {
            throw new IndexOutOfBoundsException("getDayType: Index value '" + index + "' not in range [0.." + (this._dayTypeList.size() - 1) + "]");
        }

        return (amivif.schema.types.DayTypeType) _dayTypeList.get(index);
    }

    /**
     * Method getDayType.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public amivif.schema.types.DayTypeType[] getDayType(
    ) {
        amivif.schema.types.DayTypeType[] array = new amivif.schema.types.DayTypeType[0];
        return (amivif.schema.types.DayTypeType[]) this._dayTypeList.toArray(array);
    }

    /**
     * Method getDayTypeAsReference.Returns a reference to
     * '_dayTypeList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<amivif.schema.types.DayTypeType> getDayTypeAsReference(
    ) {
        return this._dayTypeList;
    }

    /**
     * Method getDayTypeCount.
     * 
     * @return the size of this collection
     */
    public int getDayTypeCount(
    ) {
        return this._dayTypeList.size();
    }

    /**
     * Method getPeriod.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the amivif.schema.Period at the given
     * index
     */
    public amivif.schema.Period getPeriod(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._periodList.size()) {
            throw new IndexOutOfBoundsException("getPeriod: Index value '" + index + "' not in range [0.." + (this._periodList.size() - 1) + "]");
        }

        return (amivif.schema.Period) _periodList.get(index);
    }

    /**
     * Method getPeriod.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public amivif.schema.Period[] getPeriod(
    ) {
        amivif.schema.Period[] array = new amivif.schema.Period[0];
        return (amivif.schema.Period[]) this._periodList.toArray(array);
    }

    /**
     * Method getPeriodAsReference.Returns a reference to
     * '_periodList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<amivif.schema.Period> getPeriodAsReference(
    ) {
        return this._periodList;
    }

    /**
     * Method getPeriodCount.
     * 
     * @return the size of this collection
     */
    public int getPeriodCount(
    ) {
        return this._periodList.size();
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
     * Method getVehicleJourneyId.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the java.lang.String at the given index
     */
    public java.lang.String getVehicleJourneyId(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._vehicleJourneyIdList.size()) {
            throw new IndexOutOfBoundsException("getVehicleJourneyId: Index value '" + index + "' not in range [0.." + (this._vehicleJourneyIdList.size() - 1) + "]");
        }

        return (java.lang.String) _vehicleJourneyIdList.get(index);
    }

    /**
     * Method getVehicleJourneyId.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public java.lang.String[] getVehicleJourneyId(
    ) {
        java.lang.String[] array = new java.lang.String[0];
        return (java.lang.String[]) this._vehicleJourneyIdList.toArray(array);
    }

    /**
     * Method getVehicleJourneyIdAsReference.Returns a reference to
     * '_vehicleJourneyIdList'. No type checking is performed on
     * any modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<java.lang.String> getVehicleJourneyIdAsReference(
    ) {
        return this._vehicleJourneyIdList;
    }

    /**
     * Method getVehicleJourneyIdCount.
     * 
     * @return the size of this collection
     */
    public int getVehicleJourneyIdCount(
    ) {
        return this._vehicleJourneyIdList.size();
    }

    /**
     * Returns the value of field 'version'.
     * 
     * @return the value of field 'Version'.
     */
    public java.lang.String getVersion(
    ) {
        return this._version;
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
     * Method iterateCalendarDay.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends org.exolab.castor.types.Date> iterateCalendarDay(
    ) {
        return this._calendarDayList.iterator();
    }

    /**
     * Method iterateDayType.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends amivif.schema.types.DayTypeType> iterateDayType(
    ) {
        return this._dayTypeList.iterator();
    }

    /**
     * Method iteratePeriod.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends amivif.schema.Period> iteratePeriod(
    ) {
        return this._periodList.iterator();
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
     * Method iterateVehicleJourneyId.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends java.lang.String> iterateVehicleJourneyId(
    ) {
        return this._vehicleJourneyIdList.iterator();
    }

    /**
     */
    public void removeAllCalendarDay(
    ) {
        this._calendarDayList.clear();
    }

    /**
     */
    public void removeAllDayType(
    ) {
        this._dayTypeList.clear();
    }

    /**
     */
    public void removeAllPeriod(
    ) {
        this._periodList.clear();
    }

    /**
     */
    public void removeAllStopPointId(
    ) {
        this._stopPointIdList.clear();
    }

    /**
     */
    public void removeAllVehicleJourneyId(
    ) {
        this._vehicleJourneyIdList.clear();
    }

    /**
     * Method removeCalendarDay.
     * 
     * @param vCalendarDay
     * @return true if the object was removed from the collection.
     */
    public boolean removeCalendarDay(
            final org.exolab.castor.types.Date vCalendarDay) {
        boolean removed = _calendarDayList.remove(vCalendarDay);
        return removed;
    }

    /**
     * Method removeCalendarDayAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public org.exolab.castor.types.Date removeCalendarDayAt(
            final int index) {
        java.lang.Object obj = this._calendarDayList.remove(index);
        return (org.exolab.castor.types.Date) obj;
    }

    /**
     * Method removeDayType.
     * 
     * @param vDayType
     * @return true if the object was removed from the collection.
     */
    public boolean removeDayType(
            final amivif.schema.types.DayTypeType vDayType) {
        boolean removed = _dayTypeList.remove(vDayType);
        return removed;
    }

    /**
     * Method removeDayTypeAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public amivif.schema.types.DayTypeType removeDayTypeAt(
            final int index) {
        java.lang.Object obj = this._dayTypeList.remove(index);
        return (amivif.schema.types.DayTypeType) obj;
    }

    /**
     * Method removePeriod.
     * 
     * @param vPeriod
     * @return true if the object was removed from the collection.
     */
    public boolean removePeriod(
            final amivif.schema.Period vPeriod) {
        boolean removed = _periodList.remove(vPeriod);
        return removed;
    }

    /**
     * Method removePeriodAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public amivif.schema.Period removePeriodAt(
            final int index) {
        java.lang.Object obj = this._periodList.remove(index);
        return (amivif.schema.Period) obj;
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
     * Method removeVehicleJourneyId.
     * 
     * @param vVehicleJourneyId
     * @return true if the object was removed from the collection.
     */
    public boolean removeVehicleJourneyId(
            final java.lang.String vVehicleJourneyId) {
        boolean removed = _vehicleJourneyIdList.remove(vVehicleJourneyId);
        return removed;
    }

    /**
     * Method removeVehicleJourneyIdAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public java.lang.String removeVehicleJourneyIdAt(
            final int index) {
        java.lang.Object obj = this._vehicleJourneyIdList.remove(index);
        return (java.lang.String) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vCalendarDay
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setCalendarDay(
            final int index,
            final org.exolab.castor.types.Date vCalendarDay)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._calendarDayList.size()) {
            throw new IndexOutOfBoundsException("setCalendarDay: Index value '" + index + "' not in range [0.." + (this._calendarDayList.size() - 1) + "]");
        }

        this._calendarDayList.set(index, vCalendarDay);
    }

    /**
     * 
     * 
     * @param vCalendarDayArray
     */
    public void setCalendarDay(
            final org.exolab.castor.types.Date[] vCalendarDayArray) {
        //-- copy array
        _calendarDayList.clear();

        for (int i = 0; i < vCalendarDayArray.length; i++) {
                this._calendarDayList.add(vCalendarDayArray[i]);
        }
    }

    /**
     * Sets the value of '_calendarDayList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vCalendarDayList the Vector to copy.
     */
    public void setCalendarDay(
            final java.util.List<org.exolab.castor.types.Date> vCalendarDayList) {
        // copy vector
        this._calendarDayList.clear();

        this._calendarDayList.addAll(vCalendarDayList);
    }

    /**
     * Sets the value of '_calendarDayList' by setting it to the
     * given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param calendarDayList the Vector to set.
     */
    public void setCalendarDayAsReference(
            final java.util.List<org.exolab.castor.types.Date> calendarDayList) {
        this._calendarDayList = calendarDayList;
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
     * 
     * 
     * @param index
     * @param vDayType
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setDayType(
            final int index,
            final amivif.schema.types.DayTypeType vDayType)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._dayTypeList.size()) {
            throw new IndexOutOfBoundsException("setDayType: Index value '" + index + "' not in range [0.." + (this._dayTypeList.size() - 1) + "]");
        }

        this._dayTypeList.set(index, vDayType);
    }

    /**
     * 
     * 
     * @param vDayTypeArray
     */
    public void setDayType(
            final amivif.schema.types.DayTypeType[] vDayTypeArray) {
        //-- copy array
        _dayTypeList.clear();

        for (int i = 0; i < vDayTypeArray.length; i++) {
                this._dayTypeList.add(vDayTypeArray[i]);
        }
    }

    /**
     * Sets the value of '_dayTypeList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vDayTypeList the Vector to copy.
     */
    public void setDayType(
            final java.util.List<amivif.schema.types.DayTypeType> vDayTypeList) {
        // copy vector
        this._dayTypeList.clear();

        this._dayTypeList.addAll(vDayTypeList);
    }

    /**
     * Sets the value of '_dayTypeList' by setting it to the given
     * Vector. No type checking is performed.
     * @deprecated
     * 
     * @param dayTypeList the Vector to set.
     */
    public void setDayTypeAsReference(
            final java.util.List<amivif.schema.types.DayTypeType> dayTypeList) {
        this._dayTypeList = dayTypeList;
    }

    /**
     * 
     * 
     * @param index
     * @param vPeriod
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setPeriod(
            final int index,
            final amivif.schema.Period vPeriod)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._periodList.size()) {
            throw new IndexOutOfBoundsException("setPeriod: Index value '" + index + "' not in range [0.." + (this._periodList.size() - 1) + "]");
        }

        this._periodList.set(index, vPeriod);
    }

    /**
     * 
     * 
     * @param vPeriodArray
     */
    public void setPeriod(
            final amivif.schema.Period[] vPeriodArray) {
        //-- copy array
        _periodList.clear();

        for (int i = 0; i < vPeriodArray.length; i++) {
                this._periodList.add(vPeriodArray[i]);
        }
    }

    /**
     * Sets the value of '_periodList' by copying the given Vector.
     * All elements will be checked for type safety.
     * 
     * @param vPeriodList the Vector to copy.
     */
    public void setPeriod(
            final java.util.List<amivif.schema.Period> vPeriodList) {
        // copy vector
        this._periodList.clear();

        this._periodList.addAll(vPeriodList);
    }

    /**
     * Sets the value of '_periodList' by setting it to the given
     * Vector. No type checking is performed.
     * @deprecated
     * 
     * @param periodList the Vector to set.
     */
    public void setPeriodAsReference(
            final java.util.List<amivif.schema.Period> periodList) {
        this._periodList = periodList;
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
     * 
     * 
     * @param index
     * @param vVehicleJourneyId
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setVehicleJourneyId(
            final int index,
            final java.lang.String vVehicleJourneyId)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._vehicleJourneyIdList.size()) {
            throw new IndexOutOfBoundsException("setVehicleJourneyId: Index value '" + index + "' not in range [0.." + (this._vehicleJourneyIdList.size() - 1) + "]");
        }

        this._vehicleJourneyIdList.set(index, vVehicleJourneyId);
    }

    /**
     * 
     * 
     * @param vVehicleJourneyIdArray
     */
    public void setVehicleJourneyId(
            final java.lang.String[] vVehicleJourneyIdArray) {
        //-- copy array
        _vehicleJourneyIdList.clear();

        for (int i = 0; i < vVehicleJourneyIdArray.length; i++) {
                this._vehicleJourneyIdList.add(vVehicleJourneyIdArray[i]);
        }
    }

    /**
     * Sets the value of '_vehicleJourneyIdList' by copying the
     * given Vector. All elements will be checked for type safety.
     * 
     * @param vVehicleJourneyIdList the Vector to copy.
     */
    public void setVehicleJourneyId(
            final java.util.List<java.lang.String> vVehicleJourneyIdList) {
        // copy vector
        this._vehicleJourneyIdList.clear();

        this._vehicleJourneyIdList.addAll(vVehicleJourneyIdList);
    }

    /**
     * Sets the value of '_vehicleJourneyIdList' by setting it to
     * the given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param vehicleJourneyIdList the Vector to set.
     */
    public void setVehicleJourneyIdAsReference(
            final java.util.List<java.lang.String> vehicleJourneyIdList) {
        this._vehicleJourneyIdList = vehicleJourneyIdList;
    }

    /**
     * Sets the value of field 'version'.
     * 
     * @param version the value of field 'version'.
     */
    public void setVersion(
            final java.lang.String version) {
        this._version = version;
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
