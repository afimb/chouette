/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * La strucutre d'�change d'une ligne de transport.
 * Cette strucuture contient la totalit� des donn�es qui d�crivent
 * la ligne.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class ChouettePTNetworkTypeType extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Le r�seau de la ligne
     */
    private chouette.schema.PTNetwork _PTNetwork;

    /**
     * Groupe de ligne auquel appartient la ligne d�crite
     */
    private chouette.schema.GroupOfLine _groupOfLine;

    /**
     * La compagnie de transport qui assure l'exploitation de la
     * ligne.
     */
    private java.util.List<chouette.schema.Company> _companyList;

    /**
     * Field _chouetteArea.
     */
    private chouette.schema.ChouetteArea _chouetteArea;

    /**
     * La liste de toutes les correspondances sur la ligne.
     * Les correspondances relient un arr�t de la ligne avec un
     * autre arr�t de cette m�me ligne ou d'une autre ligne.
     */
    private java.util.List<chouette.schema.ConnectionLink> _connectionLinkList;

    /**
     * La liste des tableaux de marche.
     */
    private java.util.List<chouette.schema.Timetable> _timetableList;

    /**
     * Field _timeSlotList.
     */
    private java.util.List<chouette.schema.TimeSlot> _timeSlotList;

    /**
     * Structure qui regroupe les autres donn�es de la ligne.
     */
    private chouette.schema.ChouetteLineDescription _chouetteLineDescription;

    /**
     * Field _facilityList.
     */
    private java.util.List<chouette.schema.Facility> _facilityList;

    /**
     * Field _accessPointList.
     */
    private java.util.List<chouette.schema.AccessPoint> _accessPointList;

    /**
     * La liste de toutes les correspondances sur la ligne.
     * Les correspondances relient un arr�t de la ligne avec un
     * autre arr�t de cette m�me ligne ou d'une autre ligne.
     */
    private java.util.List<chouette.schema.AccessLink> _accessLinkList;


      //----------------/
     //- Constructors -/
    //----------------/

    public ChouettePTNetworkTypeType() {
        super();
        this._companyList = new java.util.ArrayList<chouette.schema.Company>();
        this._connectionLinkList = new java.util.ArrayList<chouette.schema.ConnectionLink>();
        this._timetableList = new java.util.ArrayList<chouette.schema.Timetable>();
        this._timeSlotList = new java.util.ArrayList<chouette.schema.TimeSlot>();
        this._facilityList = new java.util.ArrayList<chouette.schema.Facility>();
        this._accessPointList = new java.util.ArrayList<chouette.schema.AccessPoint>();
        this._accessLinkList = new java.util.ArrayList<chouette.schema.AccessLink>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vAccessLink
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addAccessLink(
            final chouette.schema.AccessLink vAccessLink)
    throws java.lang.IndexOutOfBoundsException {
        this._accessLinkList.add(vAccessLink);
    }

    /**
     * 
     * 
     * @param index
     * @param vAccessLink
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addAccessLink(
            final int index,
            final chouette.schema.AccessLink vAccessLink)
    throws java.lang.IndexOutOfBoundsException {
        this._accessLinkList.add(index, vAccessLink);
    }

    /**
     * 
     * 
     * @param vAccessPoint
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addAccessPoint(
            final chouette.schema.AccessPoint vAccessPoint)
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
            final chouette.schema.AccessPoint vAccessPoint)
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
            final chouette.schema.Company vCompany)
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
            final chouette.schema.Company vCompany)
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
            final chouette.schema.ConnectionLink vConnectionLink)
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
            final chouette.schema.ConnectionLink vConnectionLink)
    throws java.lang.IndexOutOfBoundsException {
        this._connectionLinkList.add(index, vConnectionLink);
    }

    /**
     * 
     * 
     * @param vFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addFacility(
            final chouette.schema.Facility vFacility)
    throws java.lang.IndexOutOfBoundsException {
        this._facilityList.add(vFacility);
    }

    /**
     * 
     * 
     * @param index
     * @param vFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addFacility(
            final int index,
            final chouette.schema.Facility vFacility)
    throws java.lang.IndexOutOfBoundsException {
        this._facilityList.add(index, vFacility);
    }

    /**
     * 
     * 
     * @param vTimeSlot
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addTimeSlot(
            final chouette.schema.TimeSlot vTimeSlot)
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
            final chouette.schema.TimeSlot vTimeSlot)
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
            final chouette.schema.Timetable vTimetable)
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
            final chouette.schema.Timetable vTimetable)
    throws java.lang.IndexOutOfBoundsException {
        this._timetableList.add(index, vTimetable);
    }

    /**
     * Method enumerateAccessLink.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends chouette.schema.AccessLink> enumerateAccessLink(
    ) {
        return java.util.Collections.enumeration(this._accessLinkList);
    }

    /**
     * Method enumerateAccessPoint.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends chouette.schema.AccessPoint> enumerateAccessPoint(
    ) {
        return java.util.Collections.enumeration(this._accessPointList);
    }

    /**
     * Method enumerateCompany.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends chouette.schema.Company> enumerateCompany(
    ) {
        return java.util.Collections.enumeration(this._companyList);
    }

    /**
     * Method enumerateConnectionLink.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends chouette.schema.ConnectionLink> enumerateConnectionLink(
    ) {
        return java.util.Collections.enumeration(this._connectionLinkList);
    }

    /**
     * Method enumerateFacility.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends chouette.schema.Facility> enumerateFacility(
    ) {
        return java.util.Collections.enumeration(this._facilityList);
    }

    /**
     * Method enumerateTimeSlot.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends chouette.schema.TimeSlot> enumerateTimeSlot(
    ) {
        return java.util.Collections.enumeration(this._timeSlotList);
    }

    /**
     * Method enumerateTimetable.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends chouette.schema.Timetable> enumerateTimetable(
    ) {
        return java.util.Collections.enumeration(this._timetableList);
    }

    /**
     * Method getAccessLink.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the chouette.schema.AccessLink at the
     * given index
     */
    public chouette.schema.AccessLink getAccessLink(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._accessLinkList.size()) {
            throw new IndexOutOfBoundsException("getAccessLink: Index value '" + index + "' not in range [0.." + (this._accessLinkList.size() - 1) + "]");
        }

        return (chouette.schema.AccessLink) _accessLinkList.get(index);
    }

    /**
     * Method getAccessLink.Returns the contents of the collection
     * in an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public chouette.schema.AccessLink[] getAccessLink(
    ) {
        chouette.schema.AccessLink[] array = new chouette.schema.AccessLink[0];
        return (chouette.schema.AccessLink[]) this._accessLinkList.toArray(array);
    }

    /**
     * Method getAccessLinkAsReference.Returns a reference to
     * '_accessLinkList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<chouette.schema.AccessLink> getAccessLinkAsReference(
    ) {
        return this._accessLinkList;
    }

    /**
     * Method getAccessLinkCount.
     * 
     * @return the size of this collection
     */
    public int getAccessLinkCount(
    ) {
        return this._accessLinkList.size();
    }

    /**
     * Method getAccessPoint.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the chouette.schema.AccessPoint at the
     * given index
     */
    public chouette.schema.AccessPoint getAccessPoint(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._accessPointList.size()) {
            throw new IndexOutOfBoundsException("getAccessPoint: Index value '" + index + "' not in range [0.." + (this._accessPointList.size() - 1) + "]");
        }

        return (chouette.schema.AccessPoint) _accessPointList.get(index);
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
    public chouette.schema.AccessPoint[] getAccessPoint(
    ) {
        chouette.schema.AccessPoint[] array = new chouette.schema.AccessPoint[0];
        return (chouette.schema.AccessPoint[]) this._accessPointList.toArray(array);
    }

    /**
     * Method getAccessPointAsReference.Returns a reference to
     * '_accessPointList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<chouette.schema.AccessPoint> getAccessPointAsReference(
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
     * Returns the value of field 'chouetteArea'.
     * 
     * @return the value of field 'ChouetteArea'.
     */
    public chouette.schema.ChouetteArea getChouetteArea(
    ) {
        return this._chouetteArea;
    }

    /**
     * Returns the value of field 'chouetteLineDescription'. The
     * field 'chouetteLineDescription' has the following
     * description: Structure qui regroupe les autres donn�es de la
     * ligne.
     * 
     * 
     * @return the value of field 'ChouetteLineDescription'.
     */
    public chouette.schema.ChouetteLineDescription getChouetteLineDescription(
    ) {
        return this._chouetteLineDescription;
    }

    /**
     * Method getCompany.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the chouette.schema.Company at the
     * given index
     */
    public chouette.schema.Company getCompany(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._companyList.size()) {
            throw new IndexOutOfBoundsException("getCompany: Index value '" + index + "' not in range [0.." + (this._companyList.size() - 1) + "]");
        }

        return (chouette.schema.Company) _companyList.get(index);
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
    public chouette.schema.Company[] getCompany(
    ) {
        chouette.schema.Company[] array = new chouette.schema.Company[0];
        return (chouette.schema.Company[]) this._companyList.toArray(array);
    }

    /**
     * Method getCompanyAsReference.Returns a reference to
     * '_companyList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<chouette.schema.Company> getCompanyAsReference(
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
     * @return the value of the chouette.schema.ConnectionLink at
     * the given index
     */
    public chouette.schema.ConnectionLink getConnectionLink(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._connectionLinkList.size()) {
            throw new IndexOutOfBoundsException("getConnectionLink: Index value '" + index + "' not in range [0.." + (this._connectionLinkList.size() - 1) + "]");
        }

        return (chouette.schema.ConnectionLink) _connectionLinkList.get(index);
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
    public chouette.schema.ConnectionLink[] getConnectionLink(
    ) {
        chouette.schema.ConnectionLink[] array = new chouette.schema.ConnectionLink[0];
        return (chouette.schema.ConnectionLink[]) this._connectionLinkList.toArray(array);
    }

    /**
     * Method getConnectionLinkAsReference.Returns a reference to
     * '_connectionLinkList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<chouette.schema.ConnectionLink> getConnectionLinkAsReference(
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
     * Method getFacility.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the chouette.schema.Facility at the
     * given index
     */
    public chouette.schema.Facility getFacility(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._facilityList.size()) {
            throw new IndexOutOfBoundsException("getFacility: Index value '" + index + "' not in range [0.." + (this._facilityList.size() - 1) + "]");
        }

        return (chouette.schema.Facility) _facilityList.get(index);
    }

    /**
     * Method getFacility.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public chouette.schema.Facility[] getFacility(
    ) {
        chouette.schema.Facility[] array = new chouette.schema.Facility[0];
        return (chouette.schema.Facility[]) this._facilityList.toArray(array);
    }

    /**
     * Method getFacilityAsReference.Returns a reference to
     * '_facilityList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<chouette.schema.Facility> getFacilityAsReference(
    ) {
        return this._facilityList;
    }

    /**
     * Method getFacilityCount.
     * 
     * @return the size of this collection
     */
    public int getFacilityCount(
    ) {
        return this._facilityList.size();
    }

    /**
     * Returns the value of field 'groupOfLine'. The field
     * 'groupOfLine' has the following description: Groupe de ligne
     * auquel appartient la ligne d�crite
     * 
     * @return the value of field 'GroupOfLine'.
     */
    public chouette.schema.GroupOfLine getGroupOfLine(
    ) {
        return this._groupOfLine;
    }

    /**
     * Returns the value of field 'PTNetwork'. The field
     * 'PTNetwork' has the following description: Le r�seau de la
     * ligne
     * 
     * @return the value of field 'PTNetwork'.
     */
    public chouette.schema.PTNetwork getPTNetwork(
    ) {
        return this._PTNetwork;
    }

    /**
     * Method getTimeSlot.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the chouette.schema.TimeSlot at the
     * given index
     */
    public chouette.schema.TimeSlot getTimeSlot(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._timeSlotList.size()) {
            throw new IndexOutOfBoundsException("getTimeSlot: Index value '" + index + "' not in range [0.." + (this._timeSlotList.size() - 1) + "]");
        }

        return (chouette.schema.TimeSlot) _timeSlotList.get(index);
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
    public chouette.schema.TimeSlot[] getTimeSlot(
    ) {
        chouette.schema.TimeSlot[] array = new chouette.schema.TimeSlot[0];
        return (chouette.schema.TimeSlot[]) this._timeSlotList.toArray(array);
    }

    /**
     * Method getTimeSlotAsReference.Returns a reference to
     * '_timeSlotList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<chouette.schema.TimeSlot> getTimeSlotAsReference(
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
     * @return the value of the chouette.schema.Timetable at the
     * given index
     */
    public chouette.schema.Timetable getTimetable(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._timetableList.size()) {
            throw new IndexOutOfBoundsException("getTimetable: Index value '" + index + "' not in range [0.." + (this._timetableList.size() - 1) + "]");
        }

        return (chouette.schema.Timetable) _timetableList.get(index);
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
    public chouette.schema.Timetable[] getTimetable(
    ) {
        chouette.schema.Timetable[] array = new chouette.schema.Timetable[0];
        return (chouette.schema.Timetable[]) this._timetableList.toArray(array);
    }

    /**
     * Method getTimetableAsReference.Returns a reference to
     * '_timetableList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<chouette.schema.Timetable> getTimetableAsReference(
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
     * Method iterateAccessLink.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends chouette.schema.AccessLink> iterateAccessLink(
    ) {
        return this._accessLinkList.iterator();
    }

    /**
     * Method iterateAccessPoint.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends chouette.schema.AccessPoint> iterateAccessPoint(
    ) {
        return this._accessPointList.iterator();
    }

    /**
     * Method iterateCompany.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends chouette.schema.Company> iterateCompany(
    ) {
        return this._companyList.iterator();
    }

    /**
     * Method iterateConnectionLink.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends chouette.schema.ConnectionLink> iterateConnectionLink(
    ) {
        return this._connectionLinkList.iterator();
    }

    /**
     * Method iterateFacility.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends chouette.schema.Facility> iterateFacility(
    ) {
        return this._facilityList.iterator();
    }

    /**
     * Method iterateTimeSlot.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends chouette.schema.TimeSlot> iterateTimeSlot(
    ) {
        return this._timeSlotList.iterator();
    }

    /**
     * Method iterateTimetable.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends chouette.schema.Timetable> iterateTimetable(
    ) {
        return this._timetableList.iterator();
    }

    /**
     * Method removeAccessLink.
     * 
     * @param vAccessLink
     * @return true if the object was removed from the collection.
     */
    public boolean removeAccessLink(
            final chouette.schema.AccessLink vAccessLink) {
        boolean removed = _accessLinkList.remove(vAccessLink);
        return removed;
    }

    /**
     * Method removeAccessLinkAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public chouette.schema.AccessLink removeAccessLinkAt(
            final int index) {
        java.lang.Object obj = this._accessLinkList.remove(index);
        return (chouette.schema.AccessLink) obj;
    }

    /**
     * Method removeAccessPoint.
     * 
     * @param vAccessPoint
     * @return true if the object was removed from the collection.
     */
    public boolean removeAccessPoint(
            final chouette.schema.AccessPoint vAccessPoint) {
        boolean removed = _accessPointList.remove(vAccessPoint);
        return removed;
    }

    /**
     * Method removeAccessPointAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public chouette.schema.AccessPoint removeAccessPointAt(
            final int index) {
        java.lang.Object obj = this._accessPointList.remove(index);
        return (chouette.schema.AccessPoint) obj;
    }

    /**
     */
    public void removeAllAccessLink(
    ) {
        this._accessLinkList.clear();
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
    public void removeAllFacility(
    ) {
        this._facilityList.clear();
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
            final chouette.schema.Company vCompany) {
        boolean removed = _companyList.remove(vCompany);
        return removed;
    }

    /**
     * Method removeCompanyAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public chouette.schema.Company removeCompanyAt(
            final int index) {
        java.lang.Object obj = this._companyList.remove(index);
        return (chouette.schema.Company) obj;
    }

    /**
     * Method removeConnectionLink.
     * 
     * @param vConnectionLink
     * @return true if the object was removed from the collection.
     */
    public boolean removeConnectionLink(
            final chouette.schema.ConnectionLink vConnectionLink) {
        boolean removed = _connectionLinkList.remove(vConnectionLink);
        return removed;
    }

    /**
     * Method removeConnectionLinkAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public chouette.schema.ConnectionLink removeConnectionLinkAt(
            final int index) {
        java.lang.Object obj = this._connectionLinkList.remove(index);
        return (chouette.schema.ConnectionLink) obj;
    }

    /**
     * Method removeFacility.
     * 
     * @param vFacility
     * @return true if the object was removed from the collection.
     */
    public boolean removeFacility(
            final chouette.schema.Facility vFacility) {
        boolean removed = _facilityList.remove(vFacility);
        return removed;
    }

    /**
     * Method removeFacilityAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public chouette.schema.Facility removeFacilityAt(
            final int index) {
        java.lang.Object obj = this._facilityList.remove(index);
        return (chouette.schema.Facility) obj;
    }

    /**
     * Method removeTimeSlot.
     * 
     * @param vTimeSlot
     * @return true if the object was removed from the collection.
     */
    public boolean removeTimeSlot(
            final chouette.schema.TimeSlot vTimeSlot) {
        boolean removed = _timeSlotList.remove(vTimeSlot);
        return removed;
    }

    /**
     * Method removeTimeSlotAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public chouette.schema.TimeSlot removeTimeSlotAt(
            final int index) {
        java.lang.Object obj = this._timeSlotList.remove(index);
        return (chouette.schema.TimeSlot) obj;
    }

    /**
     * Method removeTimetable.
     * 
     * @param vTimetable
     * @return true if the object was removed from the collection.
     */
    public boolean removeTimetable(
            final chouette.schema.Timetable vTimetable) {
        boolean removed = _timetableList.remove(vTimetable);
        return removed;
    }

    /**
     * Method removeTimetableAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public chouette.schema.Timetable removeTimetableAt(
            final int index) {
        java.lang.Object obj = this._timetableList.remove(index);
        return (chouette.schema.Timetable) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vAccessLink
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setAccessLink(
            final int index,
            final chouette.schema.AccessLink vAccessLink)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._accessLinkList.size()) {
            throw new IndexOutOfBoundsException("setAccessLink: Index value '" + index + "' not in range [0.." + (this._accessLinkList.size() - 1) + "]");
        }

        this._accessLinkList.set(index, vAccessLink);
    }

    /**
     * 
     * 
     * @param vAccessLinkArray
     */
    public void setAccessLink(
            final chouette.schema.AccessLink[] vAccessLinkArray) {
        //-- copy array
        _accessLinkList.clear();

        for (int i = 0; i < vAccessLinkArray.length; i++) {
                this._accessLinkList.add(vAccessLinkArray[i]);
        }
    }

    /**
     * Sets the value of '_accessLinkList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vAccessLinkList the Vector to copy.
     */
    public void setAccessLink(
            final java.util.List<chouette.schema.AccessLink> vAccessLinkList) {
        // copy vector
        this._accessLinkList.clear();

        this._accessLinkList.addAll(vAccessLinkList);
    }

    /**
     * Sets the value of '_accessLinkList' by setting it to the
     * given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param accessLinkList the Vector to set.
     */
    public void setAccessLinkAsReference(
            final java.util.List<chouette.schema.AccessLink> accessLinkList) {
        this._accessLinkList = accessLinkList;
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
            final chouette.schema.AccessPoint vAccessPoint)
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
            final chouette.schema.AccessPoint[] vAccessPointArray) {
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
            final java.util.List<chouette.schema.AccessPoint> vAccessPointList) {
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
            final java.util.List<chouette.schema.AccessPoint> accessPointList) {
        this._accessPointList = accessPointList;
    }

    /**
     * Sets the value of field 'chouetteArea'.
     * 
     * @param chouetteArea the value of field 'chouetteArea'.
     */
    public void setChouetteArea(
            final chouette.schema.ChouetteArea chouetteArea) {
        this._chouetteArea = chouetteArea;
    }

    /**
     * Sets the value of field 'chouetteLineDescription'. The field
     * 'chouetteLineDescription' has the following description:
     * Structure qui regroupe les autres donn�es de la ligne.
     * 
     * 
     * @param chouetteLineDescription the value of field
     * 'chouetteLineDescription'.
     */
    public void setChouetteLineDescription(
            final chouette.schema.ChouetteLineDescription chouetteLineDescription) {
        this._chouetteLineDescription = chouetteLineDescription;
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
            final chouette.schema.Company vCompany)
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
            final chouette.schema.Company[] vCompanyArray) {
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
            final java.util.List<chouette.schema.Company> vCompanyList) {
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
            final java.util.List<chouette.schema.Company> companyList) {
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
            final chouette.schema.ConnectionLink vConnectionLink)
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
            final chouette.schema.ConnectionLink[] vConnectionLinkArray) {
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
            final java.util.List<chouette.schema.ConnectionLink> vConnectionLinkList) {
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
            final java.util.List<chouette.schema.ConnectionLink> connectionLinkList) {
        this._connectionLinkList = connectionLinkList;
    }

    /**
     * 
     * 
     * @param index
     * @param vFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setFacility(
            final int index,
            final chouette.schema.Facility vFacility)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._facilityList.size()) {
            throw new IndexOutOfBoundsException("setFacility: Index value '" + index + "' not in range [0.." + (this._facilityList.size() - 1) + "]");
        }

        this._facilityList.set(index, vFacility);
    }

    /**
     * 
     * 
     * @param vFacilityArray
     */
    public void setFacility(
            final chouette.schema.Facility[] vFacilityArray) {
        //-- copy array
        _facilityList.clear();

        for (int i = 0; i < vFacilityArray.length; i++) {
                this._facilityList.add(vFacilityArray[i]);
        }
    }

    /**
     * Sets the value of '_facilityList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vFacilityList the Vector to copy.
     */
    public void setFacility(
            final java.util.List<chouette.schema.Facility> vFacilityList) {
        // copy vector
        this._facilityList.clear();

        this._facilityList.addAll(vFacilityList);
    }

    /**
     * Sets the value of '_facilityList' by setting it to the given
     * Vector. No type checking is performed.
     * @deprecated
     * 
     * @param facilityList the Vector to set.
     */
    public void setFacilityAsReference(
            final java.util.List<chouette.schema.Facility> facilityList) {
        this._facilityList = facilityList;
    }

    /**
     * Sets the value of field 'groupOfLine'. The field
     * 'groupOfLine' has the following description: Groupe de ligne
     * auquel appartient la ligne d�crite
     * 
     * @param groupOfLine the value of field 'groupOfLine'.
     */
    public void setGroupOfLine(
            final chouette.schema.GroupOfLine groupOfLine) {
        this._groupOfLine = groupOfLine;
    }

    /**
     * Sets the value of field 'PTNetwork'. The field 'PTNetwork'
     * has the following description: Le r�seau de la ligne
     * 
     * @param PTNetwork the value of field 'PTNetwork'.
     */
    public void setPTNetwork(
            final chouette.schema.PTNetwork PTNetwork) {
        this._PTNetwork = PTNetwork;
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
            final chouette.schema.TimeSlot vTimeSlot)
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
            final chouette.schema.TimeSlot[] vTimeSlotArray) {
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
            final java.util.List<chouette.schema.TimeSlot> vTimeSlotList) {
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
            final java.util.List<chouette.schema.TimeSlot> timeSlotList) {
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
            final chouette.schema.Timetable vTimetable)
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
            final chouette.schema.Timetable[] vTimetableArray) {
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
            final java.util.List<chouette.schema.Timetable> vTimetableList) {
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
            final java.util.List<chouette.schema.Timetable> timetableList) {
        this._timetableList = timetableList;
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
