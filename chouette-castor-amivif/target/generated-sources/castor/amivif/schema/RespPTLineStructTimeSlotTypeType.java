/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema;

/**
 * Message type pour la structure de ligne et les frequences
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class RespPTLineStructTimeSlotTypeType extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _transportNetwork.
     */
    private amivif.schema.TransportNetwork _transportNetwork;

    /**
     * Field _companyList.
     */
    private java.util.List<amivif.schema.Company> _companyList;

    /**
     * Field _groupOfLineList.
     */
    private java.util.List<amivif.schema.GroupOfLine> _groupOfLineList;

    /**
     * Field _line.
     */
    private amivif.schema.Line _line;

    /**
     * Field _stopAreaList.
     */
    private java.util.List<amivif.schema.StopArea> _stopAreaList;

    /**
     * Field _stopPointList.
     */
    private java.util.List<amivif.schema.StopPoint> _stopPointList;

    /**
     * Field _PTLinkList.
     */
    private java.util.List<amivif.schema.PTLink> _PTLinkList;

    /**
     * Field _routeList.
     */
    private java.util.List<amivif.schema.Route> _routeList;

    /**
     * Field _subLineList.
     */
    private java.util.List<amivif.schema.SubLine> _subLineList;

    /**
     * Field _accessPointList.
     */
    private java.util.List<amivif.schema.AccessPoint> _accessPointList;

    /**
     * Field _PTAccessLinkList.
     */
    private java.util.List<amivif.schema.PTAccessLink> _PTAccessLinkList;

    /**
     * Field _stopPointInConnectionList.
     */
    private java.util.List<amivif.schema.StopPointInConnection> _stopPointInConnectionList;

    /**
     * Field _connectionLinkList.
     */
    private java.util.List<amivif.schema.ConnectionLink> _connectionLinkList;

    /**
     * Field _ICTList.
     */
    private java.util.List<amivif.schema.ICT> _ICTList;

    /**
     * Field _timetableList.
     */
    private java.util.List<amivif.schema.Timetable> _timetableList;

    /**
     * Field _journeyPatternList.
     */
    private java.util.List<amivif.schema.JourneyPattern> _journeyPatternList;

    /**
     * Field _timeSlotList.
     */
    private java.util.List<amivif.schema.TimeSlot> _timeSlotList;


      //----------------/
     //- Constructors -/
    //----------------/

    public RespPTLineStructTimeSlotTypeType() {
        super();
        this._companyList = new java.util.ArrayList<amivif.schema.Company>();
        this._groupOfLineList = new java.util.ArrayList<amivif.schema.GroupOfLine>();
        this._stopAreaList = new java.util.ArrayList<amivif.schema.StopArea>();
        this._stopPointList = new java.util.ArrayList<amivif.schema.StopPoint>();
        this._PTLinkList = new java.util.ArrayList<amivif.schema.PTLink>();
        this._routeList = new java.util.ArrayList<amivif.schema.Route>();
        this._subLineList = new java.util.ArrayList<amivif.schema.SubLine>();
        this._accessPointList = new java.util.ArrayList<amivif.schema.AccessPoint>();
        this._PTAccessLinkList = new java.util.ArrayList<amivif.schema.PTAccessLink>();
        this._stopPointInConnectionList = new java.util.ArrayList<amivif.schema.StopPointInConnection>();
        this._connectionLinkList = new java.util.ArrayList<amivif.schema.ConnectionLink>();
        this._ICTList = new java.util.ArrayList<amivif.schema.ICT>();
        this._timetableList = new java.util.ArrayList<amivif.schema.Timetable>();
        this._journeyPatternList = new java.util.ArrayList<amivif.schema.JourneyPattern>();
        this._timeSlotList = new java.util.ArrayList<amivif.schema.TimeSlot>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vAccessPoint
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addAccessPoint(
            final amivif.schema.AccessPoint vAccessPoint)
    throws java.lang.IndexOutOfBoundsException {
        this._accessPointList.add(vAccessPoint);
    }

    /**
     * 
     * 
     * @param index
     * @param vAccessPoint
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addAccessPoint(
            final int index,
            final amivif.schema.AccessPoint vAccessPoint)
    throws java.lang.IndexOutOfBoundsException {
        this._accessPointList.add(index, vAccessPoint);
    }

    /**
     * 
     * 
     * @param vCompany
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addCompany(
            final amivif.schema.Company vCompany)
    throws java.lang.IndexOutOfBoundsException {
        this._companyList.add(vCompany);
    }

    /**
     * 
     * 
     * @param index
     * @param vCompany
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addCompany(
            final int index,
            final amivif.schema.Company vCompany)
    throws java.lang.IndexOutOfBoundsException {
        this._companyList.add(index, vCompany);
    }

    /**
     * 
     * 
     * @param vConnectionLink
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addConnectionLink(
            final amivif.schema.ConnectionLink vConnectionLink)
    throws java.lang.IndexOutOfBoundsException {
        this._connectionLinkList.add(vConnectionLink);
    }

    /**
     * 
     * 
     * @param index
     * @param vConnectionLink
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addConnectionLink(
            final int index,
            final amivif.schema.ConnectionLink vConnectionLink)
    throws java.lang.IndexOutOfBoundsException {
        this._connectionLinkList.add(index, vConnectionLink);
    }

    /**
     * 
     * 
     * @param vGroupOfLine
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addGroupOfLine(
            final amivif.schema.GroupOfLine vGroupOfLine)
    throws java.lang.IndexOutOfBoundsException {
        this._groupOfLineList.add(vGroupOfLine);
    }

    /**
     * 
     * 
     * @param index
     * @param vGroupOfLine
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addGroupOfLine(
            final int index,
            final amivif.schema.GroupOfLine vGroupOfLine)
    throws java.lang.IndexOutOfBoundsException {
        this._groupOfLineList.add(index, vGroupOfLine);
    }

    /**
     * 
     * 
     * @param vICT
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addICT(
            final amivif.schema.ICT vICT)
    throws java.lang.IndexOutOfBoundsException {
        this._ICTList.add(vICT);
    }

    /**
     * 
     * 
     * @param index
     * @param vICT
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addICT(
            final int index,
            final amivif.schema.ICT vICT)
    throws java.lang.IndexOutOfBoundsException {
        this._ICTList.add(index, vICT);
    }

    /**
     * 
     * 
     * @param vJourneyPattern
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addJourneyPattern(
            final amivif.schema.JourneyPattern vJourneyPattern)
    throws java.lang.IndexOutOfBoundsException {
        this._journeyPatternList.add(vJourneyPattern);
    }

    /**
     * 
     * 
     * @param index
     * @param vJourneyPattern
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addJourneyPattern(
            final int index,
            final amivif.schema.JourneyPattern vJourneyPattern)
    throws java.lang.IndexOutOfBoundsException {
        this._journeyPatternList.add(index, vJourneyPattern);
    }

    /**
     * 
     * 
     * @param vPTAccessLink
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPTAccessLink(
            final amivif.schema.PTAccessLink vPTAccessLink)
    throws java.lang.IndexOutOfBoundsException {
        this._PTAccessLinkList.add(vPTAccessLink);
    }

    /**
     * 
     * 
     * @param index
     * @param vPTAccessLink
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPTAccessLink(
            final int index,
            final amivif.schema.PTAccessLink vPTAccessLink)
    throws java.lang.IndexOutOfBoundsException {
        this._PTAccessLinkList.add(index, vPTAccessLink);
    }

    /**
     * 
     * 
     * @param vPTLink
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPTLink(
            final amivif.schema.PTLink vPTLink)
    throws java.lang.IndexOutOfBoundsException {
        this._PTLinkList.add(vPTLink);
    }

    /**
     * 
     * 
     * @param index
     * @param vPTLink
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPTLink(
            final int index,
            final amivif.schema.PTLink vPTLink)
    throws java.lang.IndexOutOfBoundsException {
        this._PTLinkList.add(index, vPTLink);
    }

    /**
     * 
     * 
     * @param vRoute
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addRoute(
            final amivif.schema.Route vRoute)
    throws java.lang.IndexOutOfBoundsException {
        this._routeList.add(vRoute);
    }

    /**
     * 
     * 
     * @param index
     * @param vRoute
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addRoute(
            final int index,
            final amivif.schema.Route vRoute)
    throws java.lang.IndexOutOfBoundsException {
        this._routeList.add(index, vRoute);
    }

    /**
     * 
     * 
     * @param vStopArea
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addStopArea(
            final amivif.schema.StopArea vStopArea)
    throws java.lang.IndexOutOfBoundsException {
        this._stopAreaList.add(vStopArea);
    }

    /**
     * 
     * 
     * @param index
     * @param vStopArea
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addStopArea(
            final int index,
            final amivif.schema.StopArea vStopArea)
    throws java.lang.IndexOutOfBoundsException {
        this._stopAreaList.add(index, vStopArea);
    }

    /**
     * 
     * 
     * @param vStopPoint
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addStopPoint(
            final amivif.schema.StopPoint vStopPoint)
    throws java.lang.IndexOutOfBoundsException {
        this._stopPointList.add(vStopPoint);
    }

    /**
     * 
     * 
     * @param index
     * @param vStopPoint
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addStopPoint(
            final int index,
            final amivif.schema.StopPoint vStopPoint)
    throws java.lang.IndexOutOfBoundsException {
        this._stopPointList.add(index, vStopPoint);
    }

    /**
     * 
     * 
     * @param vStopPointInConnection
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addStopPointInConnection(
            final amivif.schema.StopPointInConnection vStopPointInConnection)
    throws java.lang.IndexOutOfBoundsException {
        this._stopPointInConnectionList.add(vStopPointInConnection);
    }

    /**
     * 
     * 
     * @param index
     * @param vStopPointInConnection
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addStopPointInConnection(
            final int index,
            final amivif.schema.StopPointInConnection vStopPointInConnection)
    throws java.lang.IndexOutOfBoundsException {
        this._stopPointInConnectionList.add(index, vStopPointInConnection);
    }

    /**
     * 
     * 
     * @param vSubLine
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addSubLine(
            final amivif.schema.SubLine vSubLine)
    throws java.lang.IndexOutOfBoundsException {
        this._subLineList.add(vSubLine);
    }

    /**
     * 
     * 
     * @param index
     * @param vSubLine
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addSubLine(
            final int index,
            final amivif.schema.SubLine vSubLine)
    throws java.lang.IndexOutOfBoundsException {
        this._subLineList.add(index, vSubLine);
    }

    /**
     * 
     * 
     * @param vTimeSlot
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addTimeSlot(
            final amivif.schema.TimeSlot vTimeSlot)
    throws java.lang.IndexOutOfBoundsException {
        this._timeSlotList.add(vTimeSlot);
    }

    /**
     * 
     * 
     * @param index
     * @param vTimeSlot
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addTimeSlot(
            final int index,
            final amivif.schema.TimeSlot vTimeSlot)
    throws java.lang.IndexOutOfBoundsException {
        this._timeSlotList.add(index, vTimeSlot);
    }

    /**
     * 
     * 
     * @param vTimetable
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addTimetable(
            final amivif.schema.Timetable vTimetable)
    throws java.lang.IndexOutOfBoundsException {
        this._timetableList.add(vTimetable);
    }

    /**
     * 
     * 
     * @param index
     * @param vTimetable
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addTimetable(
            final int index,
            final amivif.schema.Timetable vTimetable)
    throws java.lang.IndexOutOfBoundsException {
        this._timetableList.add(index, vTimetable);
    }

    /**
     * Method enumerateAccessPoint.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends amivif.schema.AccessPoint> enumerateAccessPoint(
    ) {
        return java.util.Collections.enumeration(this._accessPointList);
    }

    /**
     * Method enumerateCompany.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends amivif.schema.Company> enumerateCompany(
    ) {
        return java.util.Collections.enumeration(this._companyList);
    }

    /**
     * Method enumerateConnectionLink.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends amivif.schema.ConnectionLink> enumerateConnectionLink(
    ) {
        return java.util.Collections.enumeration(this._connectionLinkList);
    }

    /**
     * Method enumerateGroupOfLine.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends amivif.schema.GroupOfLine> enumerateGroupOfLine(
    ) {
        return java.util.Collections.enumeration(this._groupOfLineList);
    }

    /**
     * Method enumerateICT.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends amivif.schema.ICT> enumerateICT(
    ) {
        return java.util.Collections.enumeration(this._ICTList);
    }

    /**
     * Method enumerateJourneyPattern.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends amivif.schema.JourneyPattern> enumerateJourneyPattern(
    ) {
        return java.util.Collections.enumeration(this._journeyPatternList);
    }

    /**
     * Method enumeratePTAccessLink.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends amivif.schema.PTAccessLink> enumeratePTAccessLink(
    ) {
        return java.util.Collections.enumeration(this._PTAccessLinkList);
    }

    /**
     * Method enumeratePTLink.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends amivif.schema.PTLink> enumeratePTLink(
    ) {
        return java.util.Collections.enumeration(this._PTLinkList);
    }

    /**
     * Method enumerateRoute.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends amivif.schema.Route> enumerateRoute(
    ) {
        return java.util.Collections.enumeration(this._routeList);
    }

    /**
     * Method enumerateStopArea.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends amivif.schema.StopArea> enumerateStopArea(
    ) {
        return java.util.Collections.enumeration(this._stopAreaList);
    }

    /**
     * Method enumerateStopPoint.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends amivif.schema.StopPoint> enumerateStopPoint(
    ) {
        return java.util.Collections.enumeration(this._stopPointList);
    }

    /**
     * Method enumerateStopPointInConnection.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends amivif.schema.StopPointInConnection> enumerateStopPointInConnection(
    ) {
        return java.util.Collections.enumeration(this._stopPointInConnectionList);
    }

    /**
     * Method enumerateSubLine.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends amivif.schema.SubLine> enumerateSubLine(
    ) {
        return java.util.Collections.enumeration(this._subLineList);
    }

    /**
     * Method enumerateTimeSlot.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends amivif.schema.TimeSlot> enumerateTimeSlot(
    ) {
        return java.util.Collections.enumeration(this._timeSlotList);
    }

    /**
     * Method enumerateTimetable.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends amivif.schema.Timetable> enumerateTimetable(
    ) {
        return java.util.Collections.enumeration(this._timetableList);
    }

    /**
     * Method getAccessPoint.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the amivif.schema.AccessPoint at the
     * given index
     */
    public amivif.schema.AccessPoint getAccessPoint(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._accessPointList.size()) {
            throw new IndexOutOfBoundsException("getAccessPoint: Index value '" + index + "' not in range [0.." + (this._accessPointList.size() - 1) + "]");
        }

        return (amivif.schema.AccessPoint) _accessPointList.get(index);
    }

    /**
     * Method getAccessPoint.Returns the contents of the collection
     * in an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public amivif.schema.AccessPoint[] getAccessPoint(
    ) {
        amivif.schema.AccessPoint[] array = new amivif.schema.AccessPoint[0];
        return (amivif.schema.AccessPoint[]) this._accessPointList.toArray(array);
    }

    /**
     * Method getAccessPointAsReference.Returns a reference to
     * '_accessPointList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<amivif.schema.AccessPoint> getAccessPointAsReference(
    ) {
        return this._accessPointList;
    }

    /**
     * Method getAccessPointCount.
     * 
     * @return the size of this collection
     */
    public int getAccessPointCount(
    ) {
        return this._accessPointList.size();
    }

    /**
     * Method getCompany.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the amivif.schema.Company at the given
     * index
     */
    public amivif.schema.Company getCompany(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._companyList.size()) {
            throw new IndexOutOfBoundsException("getCompany: Index value '" + index + "' not in range [0.." + (this._companyList.size() - 1) + "]");
        }

        return (amivif.schema.Company) _companyList.get(index);
    }

    /**
     * Method getCompany.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public amivif.schema.Company[] getCompany(
    ) {
        amivif.schema.Company[] array = new amivif.schema.Company[0];
        return (amivif.schema.Company[]) this._companyList.toArray(array);
    }

    /**
     * Method getCompanyAsReference.Returns a reference to
     * '_companyList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<amivif.schema.Company> getCompanyAsReference(
    ) {
        return this._companyList;
    }

    /**
     * Method getCompanyCount.
     * 
     * @return the size of this collection
     */
    public int getCompanyCount(
    ) {
        return this._companyList.size();
    }

    /**
     * Method getConnectionLink.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the amivif.schema.ConnectionLink at the
     * given index
     */
    public amivif.schema.ConnectionLink getConnectionLink(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._connectionLinkList.size()) {
            throw new IndexOutOfBoundsException("getConnectionLink: Index value '" + index + "' not in range [0.." + (this._connectionLinkList.size() - 1) + "]");
        }

        return (amivif.schema.ConnectionLink) _connectionLinkList.get(index);
    }

    /**
     * Method getConnectionLink.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public amivif.schema.ConnectionLink[] getConnectionLink(
    ) {
        amivif.schema.ConnectionLink[] array = new amivif.schema.ConnectionLink[0];
        return (amivif.schema.ConnectionLink[]) this._connectionLinkList.toArray(array);
    }

    /**
     * Method getConnectionLinkAsReference.Returns a reference to
     * '_connectionLinkList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<amivif.schema.ConnectionLink> getConnectionLinkAsReference(
    ) {
        return this._connectionLinkList;
    }

    /**
     * Method getConnectionLinkCount.
     * 
     * @return the size of this collection
     */
    public int getConnectionLinkCount(
    ) {
        return this._connectionLinkList.size();
    }

    /**
     * Method getGroupOfLine.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the amivif.schema.GroupOfLine at the
     * given index
     */
    public amivif.schema.GroupOfLine getGroupOfLine(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._groupOfLineList.size()) {
            throw new IndexOutOfBoundsException("getGroupOfLine: Index value '" + index + "' not in range [0.." + (this._groupOfLineList.size() - 1) + "]");
        }

        return (amivif.schema.GroupOfLine) _groupOfLineList.get(index);
    }

    /**
     * Method getGroupOfLine.Returns the contents of the collection
     * in an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public amivif.schema.GroupOfLine[] getGroupOfLine(
    ) {
        amivif.schema.GroupOfLine[] array = new amivif.schema.GroupOfLine[0];
        return (amivif.schema.GroupOfLine[]) this._groupOfLineList.toArray(array);
    }

    /**
     * Method getGroupOfLineAsReference.Returns a reference to
     * '_groupOfLineList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<amivif.schema.GroupOfLine> getGroupOfLineAsReference(
    ) {
        return this._groupOfLineList;
    }

    /**
     * Method getGroupOfLineCount.
     * 
     * @return the size of this collection
     */
    public int getGroupOfLineCount(
    ) {
        return this._groupOfLineList.size();
    }

    /**
     * Method getICT.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the amivif.schema.ICT at the given index
     */
    public amivif.schema.ICT getICT(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._ICTList.size()) {
            throw new IndexOutOfBoundsException("getICT: Index value '" + index + "' not in range [0.." + (this._ICTList.size() - 1) + "]");
        }

        return (amivif.schema.ICT) _ICTList.get(index);
    }

    /**
     * Method getICT.Returns the contents of the collection in an
     * Array.  <p>Note:  Just in case the collection contents are
     * changing in another thread, we pass a 0-length Array of the
     * correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public amivif.schema.ICT[] getICT(
    ) {
        amivif.schema.ICT[] array = new amivif.schema.ICT[0];
        return (amivif.schema.ICT[]) this._ICTList.toArray(array);
    }

    /**
     * Method getICTAsReference.Returns a reference to '_ICTList'.
     * No type checking is performed on any modifications to the
     * Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<amivif.schema.ICT> getICTAsReference(
    ) {
        return this._ICTList;
    }

    /**
     * Method getICTCount.
     * 
     * @return the size of this collection
     */
    public int getICTCount(
    ) {
        return this._ICTList.size();
    }

    /**
     * Method getJourneyPattern.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the amivif.schema.JourneyPattern at the
     * given index
     */
    public amivif.schema.JourneyPattern getJourneyPattern(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._journeyPatternList.size()) {
            throw new IndexOutOfBoundsException("getJourneyPattern: Index value '" + index + "' not in range [0.." + (this._journeyPatternList.size() - 1) + "]");
        }

        return (amivif.schema.JourneyPattern) _journeyPatternList.get(index);
    }

    /**
     * Method getJourneyPattern.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public amivif.schema.JourneyPattern[] getJourneyPattern(
    ) {
        amivif.schema.JourneyPattern[] array = new amivif.schema.JourneyPattern[0];
        return (amivif.schema.JourneyPattern[]) this._journeyPatternList.toArray(array);
    }

    /**
     * Method getJourneyPatternAsReference.Returns a reference to
     * '_journeyPatternList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<amivif.schema.JourneyPattern> getJourneyPatternAsReference(
    ) {
        return this._journeyPatternList;
    }

    /**
     * Method getJourneyPatternCount.
     * 
     * @return the size of this collection
     */
    public int getJourneyPatternCount(
    ) {
        return this._journeyPatternList.size();
    }

    /**
     * Returns the value of field 'line'.
     * 
     * @return the value of field 'Line'.
     */
    public amivif.schema.Line getLine(
    ) {
        return this._line;
    }

    /**
     * Method getPTAccessLink.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the amivif.schema.PTAccessLink at the
     * given index
     */
    public amivif.schema.PTAccessLink getPTAccessLink(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._PTAccessLinkList.size()) {
            throw new IndexOutOfBoundsException("getPTAccessLink: Index value '" + index + "' not in range [0.." + (this._PTAccessLinkList.size() - 1) + "]");
        }

        return (amivif.schema.PTAccessLink) _PTAccessLinkList.get(index);
    }

    /**
     * Method getPTAccessLink.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public amivif.schema.PTAccessLink[] getPTAccessLink(
    ) {
        amivif.schema.PTAccessLink[] array = new amivif.schema.PTAccessLink[0];
        return (amivif.schema.PTAccessLink[]) this._PTAccessLinkList.toArray(array);
    }

    /**
     * Method getPTAccessLinkAsReference.Returns a reference to
     * '_PTAccessLinkList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<amivif.schema.PTAccessLink> getPTAccessLinkAsReference(
    ) {
        return this._PTAccessLinkList;
    }

    /**
     * Method getPTAccessLinkCount.
     * 
     * @return the size of this collection
     */
    public int getPTAccessLinkCount(
    ) {
        return this._PTAccessLinkList.size();
    }

    /**
     * Method getPTLink.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the amivif.schema.PTLink at the given
     * index
     */
    public amivif.schema.PTLink getPTLink(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._PTLinkList.size()) {
            throw new IndexOutOfBoundsException("getPTLink: Index value '" + index + "' not in range [0.." + (this._PTLinkList.size() - 1) + "]");
        }

        return (amivif.schema.PTLink) _PTLinkList.get(index);
    }

    /**
     * Method getPTLink.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public amivif.schema.PTLink[] getPTLink(
    ) {
        amivif.schema.PTLink[] array = new amivif.schema.PTLink[0];
        return (amivif.schema.PTLink[]) this._PTLinkList.toArray(array);
    }

    /**
     * Method getPTLinkAsReference.Returns a reference to
     * '_PTLinkList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<amivif.schema.PTLink> getPTLinkAsReference(
    ) {
        return this._PTLinkList;
    }

    /**
     * Method getPTLinkCount.
     * 
     * @return the size of this collection
     */
    public int getPTLinkCount(
    ) {
        return this._PTLinkList.size();
    }

    /**
     * Method getRoute.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the amivif.schema.Route at the given
     * index
     */
    public amivif.schema.Route getRoute(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._routeList.size()) {
            throw new IndexOutOfBoundsException("getRoute: Index value '" + index + "' not in range [0.." + (this._routeList.size() - 1) + "]");
        }

        return (amivif.schema.Route) _routeList.get(index);
    }

    /**
     * Method getRoute.Returns the contents of the collection in an
     * Array.  <p>Note:  Just in case the collection contents are
     * changing in another thread, we pass a 0-length Array of the
     * correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public amivif.schema.Route[] getRoute(
    ) {
        amivif.schema.Route[] array = new amivif.schema.Route[0];
        return (amivif.schema.Route[]) this._routeList.toArray(array);
    }

    /**
     * Method getRouteAsReference.Returns a reference to
     * '_routeList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<amivif.schema.Route> getRouteAsReference(
    ) {
        return this._routeList;
    }

    /**
     * Method getRouteCount.
     * 
     * @return the size of this collection
     */
    public int getRouteCount(
    ) {
        return this._routeList.size();
    }

    /**
     * Method getStopArea.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the amivif.schema.StopArea at the given
     * index
     */
    public amivif.schema.StopArea getStopArea(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._stopAreaList.size()) {
            throw new IndexOutOfBoundsException("getStopArea: Index value '" + index + "' not in range [0.." + (this._stopAreaList.size() - 1) + "]");
        }

        return (amivif.schema.StopArea) _stopAreaList.get(index);
    }

    /**
     * Method getStopArea.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public amivif.schema.StopArea[] getStopArea(
    ) {
        amivif.schema.StopArea[] array = new amivif.schema.StopArea[0];
        return (amivif.schema.StopArea[]) this._stopAreaList.toArray(array);
    }

    /**
     * Method getStopAreaAsReference.Returns a reference to
     * '_stopAreaList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<amivif.schema.StopArea> getStopAreaAsReference(
    ) {
        return this._stopAreaList;
    }

    /**
     * Method getStopAreaCount.
     * 
     * @return the size of this collection
     */
    public int getStopAreaCount(
    ) {
        return this._stopAreaList.size();
    }

    /**
     * Method getStopPoint.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the amivif.schema.StopPoint at the
     * given index
     */
    public amivif.schema.StopPoint getStopPoint(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._stopPointList.size()) {
            throw new IndexOutOfBoundsException("getStopPoint: Index value '" + index + "' not in range [0.." + (this._stopPointList.size() - 1) + "]");
        }

        return (amivif.schema.StopPoint) _stopPointList.get(index);
    }

    /**
     * Method getStopPoint.Returns the contents of the collection
     * in an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public amivif.schema.StopPoint[] getStopPoint(
    ) {
        amivif.schema.StopPoint[] array = new amivif.schema.StopPoint[0];
        return (amivif.schema.StopPoint[]) this._stopPointList.toArray(array);
    }

    /**
     * Method getStopPointAsReference.Returns a reference to
     * '_stopPointList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<amivif.schema.StopPoint> getStopPointAsReference(
    ) {
        return this._stopPointList;
    }

    /**
     * Method getStopPointCount.
     * 
     * @return the size of this collection
     */
    public int getStopPointCount(
    ) {
        return this._stopPointList.size();
    }

    /**
     * Method getStopPointInConnection.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the amivif.schema.StopPointInConnection
     * at the given index
     */
    public amivif.schema.StopPointInConnection getStopPointInConnection(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._stopPointInConnectionList.size()) {
            throw new IndexOutOfBoundsException("getStopPointInConnection: Index value '" + index + "' not in range [0.." + (this._stopPointInConnectionList.size() - 1) + "]");
        }

        return (amivif.schema.StopPointInConnection) _stopPointInConnectionList.get(index);
    }

    /**
     * Method getStopPointInConnection.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public amivif.schema.StopPointInConnection[] getStopPointInConnection(
    ) {
        amivif.schema.StopPointInConnection[] array = new amivif.schema.StopPointInConnection[0];
        return (amivif.schema.StopPointInConnection[]) this._stopPointInConnectionList.toArray(array);
    }

    /**
     * Method getStopPointInConnectionAsReference.Returns a
     * reference to '_stopPointInConnectionList'. No type checking
     * is performed on any modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<amivif.schema.StopPointInConnection> getStopPointInConnectionAsReference(
    ) {
        return this._stopPointInConnectionList;
    }

    /**
     * Method getStopPointInConnectionCount.
     * 
     * @return the size of this collection
     */
    public int getStopPointInConnectionCount(
    ) {
        return this._stopPointInConnectionList.size();
    }

    /**
     * Method getSubLine.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the amivif.schema.SubLine at the given
     * index
     */
    public amivif.schema.SubLine getSubLine(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._subLineList.size()) {
            throw new IndexOutOfBoundsException("getSubLine: Index value '" + index + "' not in range [0.." + (this._subLineList.size() - 1) + "]");
        }

        return (amivif.schema.SubLine) _subLineList.get(index);
    }

    /**
     * Method getSubLine.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public amivif.schema.SubLine[] getSubLine(
    ) {
        amivif.schema.SubLine[] array = new amivif.schema.SubLine[0];
        return (amivif.schema.SubLine[]) this._subLineList.toArray(array);
    }

    /**
     * Method getSubLineAsReference.Returns a reference to
     * '_subLineList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<amivif.schema.SubLine> getSubLineAsReference(
    ) {
        return this._subLineList;
    }

    /**
     * Method getSubLineCount.
     * 
     * @return the size of this collection
     */
    public int getSubLineCount(
    ) {
        return this._subLineList.size();
    }

    /**
     * Method getTimeSlot.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the amivif.schema.TimeSlot at the given
     * index
     */
    public amivif.schema.TimeSlot getTimeSlot(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._timeSlotList.size()) {
            throw new IndexOutOfBoundsException("getTimeSlot: Index value '" + index + "' not in range [0.." + (this._timeSlotList.size() - 1) + "]");
        }

        return (amivif.schema.TimeSlot) _timeSlotList.get(index);
    }

    /**
     * Method getTimeSlot.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public amivif.schema.TimeSlot[] getTimeSlot(
    ) {
        amivif.schema.TimeSlot[] array = new amivif.schema.TimeSlot[0];
        return (amivif.schema.TimeSlot[]) this._timeSlotList.toArray(array);
    }

    /**
     * Method getTimeSlotAsReference.Returns a reference to
     * '_timeSlotList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<amivif.schema.TimeSlot> getTimeSlotAsReference(
    ) {
        return this._timeSlotList;
    }

    /**
     * Method getTimeSlotCount.
     * 
     * @return the size of this collection
     */
    public int getTimeSlotCount(
    ) {
        return this._timeSlotList.size();
    }

    /**
     * Method getTimetable.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the amivif.schema.Timetable at the
     * given index
     */
    public amivif.schema.Timetable getTimetable(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._timetableList.size()) {
            throw new IndexOutOfBoundsException("getTimetable: Index value '" + index + "' not in range [0.." + (this._timetableList.size() - 1) + "]");
        }

        return (amivif.schema.Timetable) _timetableList.get(index);
    }

    /**
     * Method getTimetable.Returns the contents of the collection
     * in an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public amivif.schema.Timetable[] getTimetable(
    ) {
        amivif.schema.Timetable[] array = new amivif.schema.Timetable[0];
        return (amivif.schema.Timetable[]) this._timetableList.toArray(array);
    }

    /**
     * Method getTimetableAsReference.Returns a reference to
     * '_timetableList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<amivif.schema.Timetable> getTimetableAsReference(
    ) {
        return this._timetableList;
    }

    /**
     * Method getTimetableCount.
     * 
     * @return the size of this collection
     */
    public int getTimetableCount(
    ) {
        return this._timetableList.size();
    }

    /**
     * Returns the value of field 'transportNetwork'.
     * 
     * @return the value of field 'TransportNetwork'.
     */
    public amivif.schema.TransportNetwork getTransportNetwork(
    ) {
        return this._transportNetwork;
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
     * Method iterateAccessPoint.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends amivif.schema.AccessPoint> iterateAccessPoint(
    ) {
        return this._accessPointList.iterator();
    }

    /**
     * Method iterateCompany.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends amivif.schema.Company> iterateCompany(
    ) {
        return this._companyList.iterator();
    }

    /**
     * Method iterateConnectionLink.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends amivif.schema.ConnectionLink> iterateConnectionLink(
    ) {
        return this._connectionLinkList.iterator();
    }

    /**
     * Method iterateGroupOfLine.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends amivif.schema.GroupOfLine> iterateGroupOfLine(
    ) {
        return this._groupOfLineList.iterator();
    }

    /**
     * Method iterateICT.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends amivif.schema.ICT> iterateICT(
    ) {
        return this._ICTList.iterator();
    }

    /**
     * Method iterateJourneyPattern.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends amivif.schema.JourneyPattern> iterateJourneyPattern(
    ) {
        return this._journeyPatternList.iterator();
    }

    /**
     * Method iteratePTAccessLink.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends amivif.schema.PTAccessLink> iteratePTAccessLink(
    ) {
        return this._PTAccessLinkList.iterator();
    }

    /**
     * Method iteratePTLink.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends amivif.schema.PTLink> iteratePTLink(
    ) {
        return this._PTLinkList.iterator();
    }

    /**
     * Method iterateRoute.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends amivif.schema.Route> iterateRoute(
    ) {
        return this._routeList.iterator();
    }

    /**
     * Method iterateStopArea.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends amivif.schema.StopArea> iterateStopArea(
    ) {
        return this._stopAreaList.iterator();
    }

    /**
     * Method iterateStopPoint.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends amivif.schema.StopPoint> iterateStopPoint(
    ) {
        return this._stopPointList.iterator();
    }

    /**
     * Method iterateStopPointInConnection.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends amivif.schema.StopPointInConnection> iterateStopPointInConnection(
    ) {
        return this._stopPointInConnectionList.iterator();
    }

    /**
     * Method iterateSubLine.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends amivif.schema.SubLine> iterateSubLine(
    ) {
        return this._subLineList.iterator();
    }

    /**
     * Method iterateTimeSlot.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends amivif.schema.TimeSlot> iterateTimeSlot(
    ) {
        return this._timeSlotList.iterator();
    }

    /**
     * Method iterateTimetable.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends amivif.schema.Timetable> iterateTimetable(
    ) {
        return this._timetableList.iterator();
    }

    /**
     * Method removeAccessPoint.
     * 
     * @param vAccessPoint
     * @return true if the object was removed from the collection.
     */
    public boolean removeAccessPoint(
            final amivif.schema.AccessPoint vAccessPoint) {
        boolean removed = _accessPointList.remove(vAccessPoint);
        return removed;
    }

    /**
     * Method removeAccessPointAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public amivif.schema.AccessPoint removeAccessPointAt(
            final int index) {
        java.lang.Object obj = this._accessPointList.remove(index);
        return (amivif.schema.AccessPoint) obj;
    }

    /**
     */
    public void removeAllAccessPoint(
    ) {
        this._accessPointList.clear();
    }

    /**
     */
    public void removeAllCompany(
    ) {
        this._companyList.clear();
    }

    /**
     */
    public void removeAllConnectionLink(
    ) {
        this._connectionLinkList.clear();
    }

    /**
     */
    public void removeAllGroupOfLine(
    ) {
        this._groupOfLineList.clear();
    }

    /**
     */
    public void removeAllICT(
    ) {
        this._ICTList.clear();
    }

    /**
     */
    public void removeAllJourneyPattern(
    ) {
        this._journeyPatternList.clear();
    }

    /**
     */
    public void removeAllPTAccessLink(
    ) {
        this._PTAccessLinkList.clear();
    }

    /**
     */
    public void removeAllPTLink(
    ) {
        this._PTLinkList.clear();
    }

    /**
     */
    public void removeAllRoute(
    ) {
        this._routeList.clear();
    }

    /**
     */
    public void removeAllStopArea(
    ) {
        this._stopAreaList.clear();
    }

    /**
     */
    public void removeAllStopPoint(
    ) {
        this._stopPointList.clear();
    }

    /**
     */
    public void removeAllStopPointInConnection(
    ) {
        this._stopPointInConnectionList.clear();
    }

    /**
     */
    public void removeAllSubLine(
    ) {
        this._subLineList.clear();
    }

    /**
     */
    public void removeAllTimeSlot(
    ) {
        this._timeSlotList.clear();
    }

    /**
     */
    public void removeAllTimetable(
    ) {
        this._timetableList.clear();
    }

    /**
     * Method removeCompany.
     * 
     * @param vCompany
     * @return true if the object was removed from the collection.
     */
    public boolean removeCompany(
            final amivif.schema.Company vCompany) {
        boolean removed = _companyList.remove(vCompany);
        return removed;
    }

    /**
     * Method removeCompanyAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public amivif.schema.Company removeCompanyAt(
            final int index) {
        java.lang.Object obj = this._companyList.remove(index);
        return (amivif.schema.Company) obj;
    }

    /**
     * Method removeConnectionLink.
     * 
     * @param vConnectionLink
     * @return true if the object was removed from the collection.
     */
    public boolean removeConnectionLink(
            final amivif.schema.ConnectionLink vConnectionLink) {
        boolean removed = _connectionLinkList.remove(vConnectionLink);
        return removed;
    }

    /**
     * Method removeConnectionLinkAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public amivif.schema.ConnectionLink removeConnectionLinkAt(
            final int index) {
        java.lang.Object obj = this._connectionLinkList.remove(index);
        return (amivif.schema.ConnectionLink) obj;
    }

    /**
     * Method removeGroupOfLine.
     * 
     * @param vGroupOfLine
     * @return true if the object was removed from the collection.
     */
    public boolean removeGroupOfLine(
            final amivif.schema.GroupOfLine vGroupOfLine) {
        boolean removed = _groupOfLineList.remove(vGroupOfLine);
        return removed;
    }

    /**
     * Method removeGroupOfLineAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public amivif.schema.GroupOfLine removeGroupOfLineAt(
            final int index) {
        java.lang.Object obj = this._groupOfLineList.remove(index);
        return (amivif.schema.GroupOfLine) obj;
    }

    /**
     * Method removeICT.
     * 
     * @param vICT
     * @return true if the object was removed from the collection.
     */
    public boolean removeICT(
            final amivif.schema.ICT vICT) {
        boolean removed = _ICTList.remove(vICT);
        return removed;
    }

    /**
     * Method removeICTAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public amivif.schema.ICT removeICTAt(
            final int index) {
        java.lang.Object obj = this._ICTList.remove(index);
        return (amivif.schema.ICT) obj;
    }

    /**
     * Method removeJourneyPattern.
     * 
     * @param vJourneyPattern
     * @return true if the object was removed from the collection.
     */
    public boolean removeJourneyPattern(
            final amivif.schema.JourneyPattern vJourneyPattern) {
        boolean removed = _journeyPatternList.remove(vJourneyPattern);
        return removed;
    }

    /**
     * Method removeJourneyPatternAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public amivif.schema.JourneyPattern removeJourneyPatternAt(
            final int index) {
        java.lang.Object obj = this._journeyPatternList.remove(index);
        return (amivif.schema.JourneyPattern) obj;
    }

    /**
     * Method removePTAccessLink.
     * 
     * @param vPTAccessLink
     * @return true if the object was removed from the collection.
     */
    public boolean removePTAccessLink(
            final amivif.schema.PTAccessLink vPTAccessLink) {
        boolean removed = _PTAccessLinkList.remove(vPTAccessLink);
        return removed;
    }

    /**
     * Method removePTAccessLinkAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public amivif.schema.PTAccessLink removePTAccessLinkAt(
            final int index) {
        java.lang.Object obj = this._PTAccessLinkList.remove(index);
        return (amivif.schema.PTAccessLink) obj;
    }

    /**
     * Method removePTLink.
     * 
     * @param vPTLink
     * @return true if the object was removed from the collection.
     */
    public boolean removePTLink(
            final amivif.schema.PTLink vPTLink) {
        boolean removed = _PTLinkList.remove(vPTLink);
        return removed;
    }

    /**
     * Method removePTLinkAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public amivif.schema.PTLink removePTLinkAt(
            final int index) {
        java.lang.Object obj = this._PTLinkList.remove(index);
        return (amivif.schema.PTLink) obj;
    }

    /**
     * Method removeRoute.
     * 
     * @param vRoute
     * @return true if the object was removed from the collection.
     */
    public boolean removeRoute(
            final amivif.schema.Route vRoute) {
        boolean removed = _routeList.remove(vRoute);
        return removed;
    }

    /**
     * Method removeRouteAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public amivif.schema.Route removeRouteAt(
            final int index) {
        java.lang.Object obj = this._routeList.remove(index);
        return (amivif.schema.Route) obj;
    }

    /**
     * Method removeStopArea.
     * 
     * @param vStopArea
     * @return true if the object was removed from the collection.
     */
    public boolean removeStopArea(
            final amivif.schema.StopArea vStopArea) {
        boolean removed = _stopAreaList.remove(vStopArea);
        return removed;
    }

    /**
     * Method removeStopAreaAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public amivif.schema.StopArea removeStopAreaAt(
            final int index) {
        java.lang.Object obj = this._stopAreaList.remove(index);
        return (amivif.schema.StopArea) obj;
    }

    /**
     * Method removeStopPoint.
     * 
     * @param vStopPoint
     * @return true if the object was removed from the collection.
     */
    public boolean removeStopPoint(
            final amivif.schema.StopPoint vStopPoint) {
        boolean removed = _stopPointList.remove(vStopPoint);
        return removed;
    }

    /**
     * Method removeStopPointAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public amivif.schema.StopPoint removeStopPointAt(
            final int index) {
        java.lang.Object obj = this._stopPointList.remove(index);
        return (amivif.schema.StopPoint) obj;
    }

    /**
     * Method removeStopPointInConnection.
     * 
     * @param vStopPointInConnection
     * @return true if the object was removed from the collection.
     */
    public boolean removeStopPointInConnection(
            final amivif.schema.StopPointInConnection vStopPointInConnection) {
        boolean removed = _stopPointInConnectionList.remove(vStopPointInConnection);
        return removed;
    }

    /**
     * Method removeStopPointInConnectionAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public amivif.schema.StopPointInConnection removeStopPointInConnectionAt(
            final int index) {
        java.lang.Object obj = this._stopPointInConnectionList.remove(index);
        return (amivif.schema.StopPointInConnection) obj;
    }

    /**
     * Method removeSubLine.
     * 
     * @param vSubLine
     * @return true if the object was removed from the collection.
     */
    public boolean removeSubLine(
            final amivif.schema.SubLine vSubLine) {
        boolean removed = _subLineList.remove(vSubLine);
        return removed;
    }

    /**
     * Method removeSubLineAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public amivif.schema.SubLine removeSubLineAt(
            final int index) {
        java.lang.Object obj = this._subLineList.remove(index);
        return (amivif.schema.SubLine) obj;
    }

    /**
     * Method removeTimeSlot.
     * 
     * @param vTimeSlot
     * @return true if the object was removed from the collection.
     */
    public boolean removeTimeSlot(
            final amivif.schema.TimeSlot vTimeSlot) {
        boolean removed = _timeSlotList.remove(vTimeSlot);
        return removed;
    }

    /**
     * Method removeTimeSlotAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public amivif.schema.TimeSlot removeTimeSlotAt(
            final int index) {
        java.lang.Object obj = this._timeSlotList.remove(index);
        return (amivif.schema.TimeSlot) obj;
    }

    /**
     * Method removeTimetable.
     * 
     * @param vTimetable
     * @return true if the object was removed from the collection.
     */
    public boolean removeTimetable(
            final amivif.schema.Timetable vTimetable) {
        boolean removed = _timetableList.remove(vTimetable);
        return removed;
    }

    /**
     * Method removeTimetableAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public amivif.schema.Timetable removeTimetableAt(
            final int index) {
        java.lang.Object obj = this._timetableList.remove(index);
        return (amivif.schema.Timetable) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vAccessPoint
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setAccessPoint(
            final int index,
            final amivif.schema.AccessPoint vAccessPoint)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._accessPointList.size()) {
            throw new IndexOutOfBoundsException("setAccessPoint: Index value '" + index + "' not in range [0.." + (this._accessPointList.size() - 1) + "]");
        }

        this._accessPointList.set(index, vAccessPoint);
    }

    /**
     * 
     * 
     * @param vAccessPointArray
     */
    public void setAccessPoint(
            final amivif.schema.AccessPoint[] vAccessPointArray) {
        //-- copy array
        _accessPointList.clear();

        for (int i = 0; i < vAccessPointArray.length; i++) {
                this._accessPointList.add(vAccessPointArray[i]);
        }
    }

    /**
     * Sets the value of '_accessPointList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vAccessPointList the Vector to copy.
     */
    public void setAccessPoint(
            final java.util.List<amivif.schema.AccessPoint> vAccessPointList) {
        // copy vector
        this._accessPointList.clear();

        this._accessPointList.addAll(vAccessPointList);
    }

    /**
     * Sets the value of '_accessPointList' by setting it to the
     * given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param accessPointList the Vector to set.
     */
    public void setAccessPointAsReference(
            final java.util.List<amivif.schema.AccessPoint> accessPointList) {
        this._accessPointList = accessPointList;
    }

    /**
     * 
     * 
     * @param index
     * @param vCompany
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setCompany(
            final int index,
            final amivif.schema.Company vCompany)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._companyList.size()) {
            throw new IndexOutOfBoundsException("setCompany: Index value '" + index + "' not in range [0.." + (this._companyList.size() - 1) + "]");
        }

        this._companyList.set(index, vCompany);
    }

    /**
     * 
     * 
     * @param vCompanyArray
     */
    public void setCompany(
            final amivif.schema.Company[] vCompanyArray) {
        //-- copy array
        _companyList.clear();

        for (int i = 0; i < vCompanyArray.length; i++) {
                this._companyList.add(vCompanyArray[i]);
        }
    }

    /**
     * Sets the value of '_companyList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vCompanyList the Vector to copy.
     */
    public void setCompany(
            final java.util.List<amivif.schema.Company> vCompanyList) {
        // copy vector
        this._companyList.clear();

        this._companyList.addAll(vCompanyList);
    }

    /**
     * Sets the value of '_companyList' by setting it to the given
     * Vector. No type checking is performed.
     * @deprecated
     * 
     * @param companyList the Vector to set.
     */
    public void setCompanyAsReference(
            final java.util.List<amivif.schema.Company> companyList) {
        this._companyList = companyList;
    }

    /**
     * 
     * 
     * @param index
     * @param vConnectionLink
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setConnectionLink(
            final int index,
            final amivif.schema.ConnectionLink vConnectionLink)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._connectionLinkList.size()) {
            throw new IndexOutOfBoundsException("setConnectionLink: Index value '" + index + "' not in range [0.." + (this._connectionLinkList.size() - 1) + "]");
        }

        this._connectionLinkList.set(index, vConnectionLink);
    }

    /**
     * 
     * 
     * @param vConnectionLinkArray
     */
    public void setConnectionLink(
            final amivif.schema.ConnectionLink[] vConnectionLinkArray) {
        //-- copy array
        _connectionLinkList.clear();

        for (int i = 0; i < vConnectionLinkArray.length; i++) {
                this._connectionLinkList.add(vConnectionLinkArray[i]);
        }
    }

    /**
     * Sets the value of '_connectionLinkList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vConnectionLinkList the Vector to copy.
     */
    public void setConnectionLink(
            final java.util.List<amivif.schema.ConnectionLink> vConnectionLinkList) {
        // copy vector
        this._connectionLinkList.clear();

        this._connectionLinkList.addAll(vConnectionLinkList);
    }

    /**
     * Sets the value of '_connectionLinkList' by setting it to the
     * given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param connectionLinkList the Vector to set.
     */
    public void setConnectionLinkAsReference(
            final java.util.List<amivif.schema.ConnectionLink> connectionLinkList) {
        this._connectionLinkList = connectionLinkList;
    }

    /**
     * 
     * 
     * @param index
     * @param vGroupOfLine
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setGroupOfLine(
            final int index,
            final amivif.schema.GroupOfLine vGroupOfLine)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._groupOfLineList.size()) {
            throw new IndexOutOfBoundsException("setGroupOfLine: Index value '" + index + "' not in range [0.." + (this._groupOfLineList.size() - 1) + "]");
        }

        this._groupOfLineList.set(index, vGroupOfLine);
    }

    /**
     * 
     * 
     * @param vGroupOfLineArray
     */
    public void setGroupOfLine(
            final amivif.schema.GroupOfLine[] vGroupOfLineArray) {
        //-- copy array
        _groupOfLineList.clear();

        for (int i = 0; i < vGroupOfLineArray.length; i++) {
                this._groupOfLineList.add(vGroupOfLineArray[i]);
        }
    }

    /**
     * Sets the value of '_groupOfLineList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vGroupOfLineList the Vector to copy.
     */
    public void setGroupOfLine(
            final java.util.List<amivif.schema.GroupOfLine> vGroupOfLineList) {
        // copy vector
        this._groupOfLineList.clear();

        this._groupOfLineList.addAll(vGroupOfLineList);
    }

    /**
     * Sets the value of '_groupOfLineList' by setting it to the
     * given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param groupOfLineList the Vector to set.
     */
    public void setGroupOfLineAsReference(
            final java.util.List<amivif.schema.GroupOfLine> groupOfLineList) {
        this._groupOfLineList = groupOfLineList;
    }

    /**
     * 
     * 
     * @param index
     * @param vICT
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setICT(
            final int index,
            final amivif.schema.ICT vICT)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._ICTList.size()) {
            throw new IndexOutOfBoundsException("setICT: Index value '" + index + "' not in range [0.." + (this._ICTList.size() - 1) + "]");
        }

        this._ICTList.set(index, vICT);
    }

    /**
     * 
     * 
     * @param vICTArray
     */
    public void setICT(
            final amivif.schema.ICT[] vICTArray) {
        //-- copy array
        _ICTList.clear();

        for (int i = 0; i < vICTArray.length; i++) {
                this._ICTList.add(vICTArray[i]);
        }
    }

    /**
     * Sets the value of '_ICTList' by copying the given Vector.
     * All elements will be checked for type safety.
     * 
     * @param vICTList the Vector to copy.
     */
    public void setICT(
            final java.util.List<amivif.schema.ICT> vICTList) {
        // copy vector
        this._ICTList.clear();

        this._ICTList.addAll(vICTList);
    }

    /**
     * Sets the value of '_ICTList' by setting it to the given
     * Vector. No type checking is performed.
     * @deprecated
     * 
     * @param ICTList the Vector to set.
     */
    public void setICTAsReference(
            final java.util.List<amivif.schema.ICT> ICTList) {
        this._ICTList = ICTList;
    }

    /**
     * 
     * 
     * @param index
     * @param vJourneyPattern
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setJourneyPattern(
            final int index,
            final amivif.schema.JourneyPattern vJourneyPattern)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._journeyPatternList.size()) {
            throw new IndexOutOfBoundsException("setJourneyPattern: Index value '" + index + "' not in range [0.." + (this._journeyPatternList.size() - 1) + "]");
        }

        this._journeyPatternList.set(index, vJourneyPattern);
    }

    /**
     * 
     * 
     * @param vJourneyPatternArray
     */
    public void setJourneyPattern(
            final amivif.schema.JourneyPattern[] vJourneyPatternArray) {
        //-- copy array
        _journeyPatternList.clear();

        for (int i = 0; i < vJourneyPatternArray.length; i++) {
                this._journeyPatternList.add(vJourneyPatternArray[i]);
        }
    }

    /**
     * Sets the value of '_journeyPatternList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vJourneyPatternList the Vector to copy.
     */
    public void setJourneyPattern(
            final java.util.List<amivif.schema.JourneyPattern> vJourneyPatternList) {
        // copy vector
        this._journeyPatternList.clear();

        this._journeyPatternList.addAll(vJourneyPatternList);
    }

    /**
     * Sets the value of '_journeyPatternList' by setting it to the
     * given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param journeyPatternList the Vector to set.
     */
    public void setJourneyPatternAsReference(
            final java.util.List<amivif.schema.JourneyPattern> journeyPatternList) {
        this._journeyPatternList = journeyPatternList;
    }

    /**
     * Sets the value of field 'line'.
     * 
     * @param line the value of field 'line'.
     */
    public void setLine(
            final amivif.schema.Line line) {
        this._line = line;
    }

    /**
     * 
     * 
     * @param index
     * @param vPTAccessLink
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setPTAccessLink(
            final int index,
            final amivif.schema.PTAccessLink vPTAccessLink)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._PTAccessLinkList.size()) {
            throw new IndexOutOfBoundsException("setPTAccessLink: Index value '" + index + "' not in range [0.." + (this._PTAccessLinkList.size() - 1) + "]");
        }

        this._PTAccessLinkList.set(index, vPTAccessLink);
    }

    /**
     * 
     * 
     * @param vPTAccessLinkArray
     */
    public void setPTAccessLink(
            final amivif.schema.PTAccessLink[] vPTAccessLinkArray) {
        //-- copy array
        _PTAccessLinkList.clear();

        for (int i = 0; i < vPTAccessLinkArray.length; i++) {
                this._PTAccessLinkList.add(vPTAccessLinkArray[i]);
        }
    }

    /**
     * Sets the value of '_PTAccessLinkList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vPTAccessLinkList the Vector to copy.
     */
    public void setPTAccessLink(
            final java.util.List<amivif.schema.PTAccessLink> vPTAccessLinkList) {
        // copy vector
        this._PTAccessLinkList.clear();

        this._PTAccessLinkList.addAll(vPTAccessLinkList);
    }

    /**
     * Sets the value of '_PTAccessLinkList' by setting it to the
     * given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param PTAccessLinkList the Vector to set.
     */
    public void setPTAccessLinkAsReference(
            final java.util.List<amivif.schema.PTAccessLink> PTAccessLinkList) {
        this._PTAccessLinkList = PTAccessLinkList;
    }

    /**
     * 
     * 
     * @param index
     * @param vPTLink
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setPTLink(
            final int index,
            final amivif.schema.PTLink vPTLink)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._PTLinkList.size()) {
            throw new IndexOutOfBoundsException("setPTLink: Index value '" + index + "' not in range [0.." + (this._PTLinkList.size() - 1) + "]");
        }

        this._PTLinkList.set(index, vPTLink);
    }

    /**
     * 
     * 
     * @param vPTLinkArray
     */
    public void setPTLink(
            final amivif.schema.PTLink[] vPTLinkArray) {
        //-- copy array
        _PTLinkList.clear();

        for (int i = 0; i < vPTLinkArray.length; i++) {
                this._PTLinkList.add(vPTLinkArray[i]);
        }
    }

    /**
     * Sets the value of '_PTLinkList' by copying the given Vector.
     * All elements will be checked for type safety.
     * 
     * @param vPTLinkList the Vector to copy.
     */
    public void setPTLink(
            final java.util.List<amivif.schema.PTLink> vPTLinkList) {
        // copy vector
        this._PTLinkList.clear();

        this._PTLinkList.addAll(vPTLinkList);
    }

    /**
     * Sets the value of '_PTLinkList' by setting it to the given
     * Vector. No type checking is performed.
     * @deprecated
     * 
     * @param PTLinkList the Vector to set.
     */
    public void setPTLinkAsReference(
            final java.util.List<amivif.schema.PTLink> PTLinkList) {
        this._PTLinkList = PTLinkList;
    }

    /**
     * 
     * 
     * @param index
     * @param vRoute
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setRoute(
            final int index,
            final amivif.schema.Route vRoute)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._routeList.size()) {
            throw new IndexOutOfBoundsException("setRoute: Index value '" + index + "' not in range [0.." + (this._routeList.size() - 1) + "]");
        }

        this._routeList.set(index, vRoute);
    }

    /**
     * 
     * 
     * @param vRouteArray
     */
    public void setRoute(
            final amivif.schema.Route[] vRouteArray) {
        //-- copy array
        _routeList.clear();

        for (int i = 0; i < vRouteArray.length; i++) {
                this._routeList.add(vRouteArray[i]);
        }
    }

    /**
     * Sets the value of '_routeList' by copying the given Vector.
     * All elements will be checked for type safety.
     * 
     * @param vRouteList the Vector to copy.
     */
    public void setRoute(
            final java.util.List<amivif.schema.Route> vRouteList) {
        // copy vector
        this._routeList.clear();

        this._routeList.addAll(vRouteList);
    }

    /**
     * Sets the value of '_routeList' by setting it to the given
     * Vector. No type checking is performed.
     * @deprecated
     * 
     * @param routeList the Vector to set.
     */
    public void setRouteAsReference(
            final java.util.List<amivif.schema.Route> routeList) {
        this._routeList = routeList;
    }

    /**
     * 
     * 
     * @param index
     * @param vStopArea
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setStopArea(
            final int index,
            final amivif.schema.StopArea vStopArea)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._stopAreaList.size()) {
            throw new IndexOutOfBoundsException("setStopArea: Index value '" + index + "' not in range [0.." + (this._stopAreaList.size() - 1) + "]");
        }

        this._stopAreaList.set(index, vStopArea);
    }

    /**
     * 
     * 
     * @param vStopAreaArray
     */
    public void setStopArea(
            final amivif.schema.StopArea[] vStopAreaArray) {
        //-- copy array
        _stopAreaList.clear();

        for (int i = 0; i < vStopAreaArray.length; i++) {
                this._stopAreaList.add(vStopAreaArray[i]);
        }
    }

    /**
     * Sets the value of '_stopAreaList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vStopAreaList the Vector to copy.
     */
    public void setStopArea(
            final java.util.List<amivif.schema.StopArea> vStopAreaList) {
        // copy vector
        this._stopAreaList.clear();

        this._stopAreaList.addAll(vStopAreaList);
    }

    /**
     * Sets the value of '_stopAreaList' by setting it to the given
     * Vector. No type checking is performed.
     * @deprecated
     * 
     * @param stopAreaList the Vector to set.
     */
    public void setStopAreaAsReference(
            final java.util.List<amivif.schema.StopArea> stopAreaList) {
        this._stopAreaList = stopAreaList;
    }

    /**
     * 
     * 
     * @param index
     * @param vStopPoint
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setStopPoint(
            final int index,
            final amivif.schema.StopPoint vStopPoint)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._stopPointList.size()) {
            throw new IndexOutOfBoundsException("setStopPoint: Index value '" + index + "' not in range [0.." + (this._stopPointList.size() - 1) + "]");
        }

        this._stopPointList.set(index, vStopPoint);
    }

    /**
     * 
     * 
     * @param vStopPointArray
     */
    public void setStopPoint(
            final amivif.schema.StopPoint[] vStopPointArray) {
        //-- copy array
        _stopPointList.clear();

        for (int i = 0; i < vStopPointArray.length; i++) {
                this._stopPointList.add(vStopPointArray[i]);
        }
    }

    /**
     * Sets the value of '_stopPointList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vStopPointList the Vector to copy.
     */
    public void setStopPoint(
            final java.util.List<amivif.schema.StopPoint> vStopPointList) {
        // copy vector
        this._stopPointList.clear();

        this._stopPointList.addAll(vStopPointList);
    }

    /**
     * Sets the value of '_stopPointList' by setting it to the
     * given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param stopPointList the Vector to set.
     */
    public void setStopPointAsReference(
            final java.util.List<amivif.schema.StopPoint> stopPointList) {
        this._stopPointList = stopPointList;
    }

    /**
     * 
     * 
     * @param index
     * @param vStopPointInConnection
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setStopPointInConnection(
            final int index,
            final amivif.schema.StopPointInConnection vStopPointInConnection)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._stopPointInConnectionList.size()) {
            throw new IndexOutOfBoundsException("setStopPointInConnection: Index value '" + index + "' not in range [0.." + (this._stopPointInConnectionList.size() - 1) + "]");
        }

        this._stopPointInConnectionList.set(index, vStopPointInConnection);
    }

    /**
     * 
     * 
     * @param vStopPointInConnectionArray
     */
    public void setStopPointInConnection(
            final amivif.schema.StopPointInConnection[] vStopPointInConnectionArray) {
        //-- copy array
        _stopPointInConnectionList.clear();

        for (int i = 0; i < vStopPointInConnectionArray.length; i++) {
                this._stopPointInConnectionList.add(vStopPointInConnectionArray[i]);
        }
    }

    /**
     * Sets the value of '_stopPointInConnectionList' by copying
     * the given Vector. All elements will be checked for type
     * safety.
     * 
     * @param vStopPointInConnectionList the Vector to copy.
     */
    public void setStopPointInConnection(
            final java.util.List<amivif.schema.StopPointInConnection> vStopPointInConnectionList) {
        // copy vector
        this._stopPointInConnectionList.clear();

        this._stopPointInConnectionList.addAll(vStopPointInConnectionList);
    }

    /**
     * Sets the value of '_stopPointInConnectionList' by setting it
     * to the given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param stopPointInConnectionList the Vector to set.
     */
    public void setStopPointInConnectionAsReference(
            final java.util.List<amivif.schema.StopPointInConnection> stopPointInConnectionList) {
        this._stopPointInConnectionList = stopPointInConnectionList;
    }

    /**
     * 
     * 
     * @param index
     * @param vSubLine
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setSubLine(
            final int index,
            final amivif.schema.SubLine vSubLine)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._subLineList.size()) {
            throw new IndexOutOfBoundsException("setSubLine: Index value '" + index + "' not in range [0.." + (this._subLineList.size() - 1) + "]");
        }

        this._subLineList.set(index, vSubLine);
    }

    /**
     * 
     * 
     * @param vSubLineArray
     */
    public void setSubLine(
            final amivif.schema.SubLine[] vSubLineArray) {
        //-- copy array
        _subLineList.clear();

        for (int i = 0; i < vSubLineArray.length; i++) {
                this._subLineList.add(vSubLineArray[i]);
        }
    }

    /**
     * Sets the value of '_subLineList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vSubLineList the Vector to copy.
     */
    public void setSubLine(
            final java.util.List<amivif.schema.SubLine> vSubLineList) {
        // copy vector
        this._subLineList.clear();

        this._subLineList.addAll(vSubLineList);
    }

    /**
     * Sets the value of '_subLineList' by setting it to the given
     * Vector. No type checking is performed.
     * @deprecated
     * 
     * @param subLineList the Vector to set.
     */
    public void setSubLineAsReference(
            final java.util.List<amivif.schema.SubLine> subLineList) {
        this._subLineList = subLineList;
    }

    /**
     * 
     * 
     * @param index
     * @param vTimeSlot
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setTimeSlot(
            final int index,
            final amivif.schema.TimeSlot vTimeSlot)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._timeSlotList.size()) {
            throw new IndexOutOfBoundsException("setTimeSlot: Index value '" + index + "' not in range [0.." + (this._timeSlotList.size() - 1) + "]");
        }

        this._timeSlotList.set(index, vTimeSlot);
    }

    /**
     * 
     * 
     * @param vTimeSlotArray
     */
    public void setTimeSlot(
            final amivif.schema.TimeSlot[] vTimeSlotArray) {
        //-- copy array
        _timeSlotList.clear();

        for (int i = 0; i < vTimeSlotArray.length; i++) {
                this._timeSlotList.add(vTimeSlotArray[i]);
        }
    }

    /**
     * Sets the value of '_timeSlotList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vTimeSlotList the Vector to copy.
     */
    public void setTimeSlot(
            final java.util.List<amivif.schema.TimeSlot> vTimeSlotList) {
        // copy vector
        this._timeSlotList.clear();

        this._timeSlotList.addAll(vTimeSlotList);
    }

    /**
     * Sets the value of '_timeSlotList' by setting it to the given
     * Vector. No type checking is performed.
     * @deprecated
     * 
     * @param timeSlotList the Vector to set.
     */
    public void setTimeSlotAsReference(
            final java.util.List<amivif.schema.TimeSlot> timeSlotList) {
        this._timeSlotList = timeSlotList;
    }

    /**
     * 
     * 
     * @param index
     * @param vTimetable
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setTimetable(
            final int index,
            final amivif.schema.Timetable vTimetable)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._timetableList.size()) {
            throw new IndexOutOfBoundsException("setTimetable: Index value '" + index + "' not in range [0.." + (this._timetableList.size() - 1) + "]");
        }

        this._timetableList.set(index, vTimetable);
    }

    /**
     * 
     * 
     * @param vTimetableArray
     */
    public void setTimetable(
            final amivif.schema.Timetable[] vTimetableArray) {
        //-- copy array
        _timetableList.clear();

        for (int i = 0; i < vTimetableArray.length; i++) {
                this._timetableList.add(vTimetableArray[i]);
        }
    }

    /**
     * Sets the value of '_timetableList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vTimetableList the Vector to copy.
     */
    public void setTimetable(
            final java.util.List<amivif.schema.Timetable> vTimetableList) {
        // copy vector
        this._timetableList.clear();

        this._timetableList.addAll(vTimetableList);
    }

    /**
     * Sets the value of '_timetableList' by setting it to the
     * given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param timetableList the Vector to set.
     */
    public void setTimetableAsReference(
            final java.util.List<amivif.schema.Timetable> timetableList) {
        this._timetableList = timetableList;
    }

    /**
     * Sets the value of field 'transportNetwork'.
     * 
     * @param transportNetwork the value of field 'transportNetwork'
     */
    public void setTransportNetwork(
            final amivif.schema.TransportNetwork transportNetwork) {
        this._transportNetwork = transportNetwork;
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
