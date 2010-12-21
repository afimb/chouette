/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * Structure qui regroupe les autres donn�es de la ligne.
 * 
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class ChouetteLineDescription extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * La ligne et ses r�f�rences vers ses itin�raires et son r�seau
     */
    private chouette.schema.Line _line;

    /**
     * La liste de tous les itin�raires de la ligne.
     */
    private java.util.List<chouette.schema.ChouetteRoute> _chouetteRouteList;

    /**
     * La liste de tous les arr�ts parcourus par les tron�ons des
     * itin�raires de ligne.
     * 
     */
    private java.util.List<chouette.schema.StopPoint> _stopPointList;

    /**
     * Liste des interdictions de trafic local auxquelles doivent
     * se conformer les voyageurs de la ligne
     */
    private java.util.List<chouette.schema.ITL> _ITLList;

    /**
     * La liste de tous les tron�ons des itin�raires de la ligne.
     */
    private java.util.List<chouette.schema.PtLink> _ptLinkList;

    /**
     * La liste de toutes les missions des courses de la ligne.
     */
    private java.util.List<chouette.schema.JourneyPattern> _journeyPatternList;

    /**
     * La liste de toutes les courses de la ligne qui sont
     * effectives pour au moins l'un des tableaux de marche de la
     * ligne.
     */
    private java.util.List<chouette.schema.VehicleJourney> _vehicleJourneyList;


      //----------------/
     //- Constructors -/
    //----------------/

    public ChouetteLineDescription() {
        super();
        this._chouetteRouteList = new java.util.ArrayList<chouette.schema.ChouetteRoute>();
        this._stopPointList = new java.util.ArrayList<chouette.schema.StopPoint>();
        this._ITLList = new java.util.ArrayList<chouette.schema.ITL>();
        this._ptLinkList = new java.util.ArrayList<chouette.schema.PtLink>();
        this._journeyPatternList = new java.util.ArrayList<chouette.schema.JourneyPattern>();
        this._vehicleJourneyList = new java.util.ArrayList<chouette.schema.VehicleJourney>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vChouetteRoute
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addChouetteRoute(
            final chouette.schema.ChouetteRoute vChouetteRoute)
    throws java.lang.IndexOutOfBoundsException {
        this._chouetteRouteList.add(vChouetteRoute);
    }

    /**
     * 
     * 
     * @param index
     * @param vChouetteRoute
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addChouetteRoute(
            final int index,
            final chouette.schema.ChouetteRoute vChouetteRoute)
    throws java.lang.IndexOutOfBoundsException {
        this._chouetteRouteList.add(index, vChouetteRoute);
    }

    /**
     * 
     * 
     * @param vITL
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addITL(
            final chouette.schema.ITL vITL)
    throws java.lang.IndexOutOfBoundsException {
        this._ITLList.add(vITL);
    }

    /**
     * 
     * 
     * @param index
     * @param vITL
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addITL(
            final int index,
            final chouette.schema.ITL vITL)
    throws java.lang.IndexOutOfBoundsException {
        this._ITLList.add(index, vITL);
    }

    /**
     * 
     * 
     * @param vJourneyPattern
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addJourneyPattern(
            final chouette.schema.JourneyPattern vJourneyPattern)
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
            final chouette.schema.JourneyPattern vJourneyPattern)
    throws java.lang.IndexOutOfBoundsException {
        this._journeyPatternList.add(index, vJourneyPattern);
    }

    /**
     * 
     * 
     * @param vPtLink
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPtLink(
            final chouette.schema.PtLink vPtLink)
    throws java.lang.IndexOutOfBoundsException {
        this._ptLinkList.add(vPtLink);
    }

    /**
     * 
     * 
     * @param index
     * @param vPtLink
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPtLink(
            final int index,
            final chouette.schema.PtLink vPtLink)
    throws java.lang.IndexOutOfBoundsException {
        this._ptLinkList.add(index, vPtLink);
    }

    /**
     * 
     * 
     * @param vStopPoint
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addStopPoint(
            final chouette.schema.StopPoint vStopPoint)
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
            final chouette.schema.StopPoint vStopPoint)
    throws java.lang.IndexOutOfBoundsException {
        this._stopPointList.add(index, vStopPoint);
    }

    /**
     * 
     * 
     * @param vVehicleJourney
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addVehicleJourney(
            final chouette.schema.VehicleJourney vVehicleJourney)
    throws java.lang.IndexOutOfBoundsException {
        this._vehicleJourneyList.add(vVehicleJourney);
    }

    /**
     * 
     * 
     * @param index
     * @param vVehicleJourney
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addVehicleJourney(
            final int index,
            final chouette.schema.VehicleJourney vVehicleJourney)
    throws java.lang.IndexOutOfBoundsException {
        this._vehicleJourneyList.add(index, vVehicleJourney);
    }

    /**
     * Method enumerateChouetteRoute.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends chouette.schema.ChouetteRoute> enumerateChouetteRoute(
    ) {
        return java.util.Collections.enumeration(this._chouetteRouteList);
    }

    /**
     * Method enumerateITL.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends chouette.schema.ITL> enumerateITL(
    ) {
        return java.util.Collections.enumeration(this._ITLList);
    }

    /**
     * Method enumerateJourneyPattern.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends chouette.schema.JourneyPattern> enumerateJourneyPattern(
    ) {
        return java.util.Collections.enumeration(this._journeyPatternList);
    }

    /**
     * Method enumeratePtLink.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends chouette.schema.PtLink> enumeratePtLink(
    ) {
        return java.util.Collections.enumeration(this._ptLinkList);
    }

    /**
     * Method enumerateStopPoint.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends chouette.schema.StopPoint> enumerateStopPoint(
    ) {
        return java.util.Collections.enumeration(this._stopPointList);
    }

    /**
     * Method enumerateVehicleJourney.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends chouette.schema.VehicleJourney> enumerateVehicleJourney(
    ) {
        return java.util.Collections.enumeration(this._vehicleJourneyList);
    }

    /**
     * Method getChouetteRoute.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the chouette.schema.ChouetteRoute at
     * the given index
     */
    public chouette.schema.ChouetteRoute getChouetteRoute(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._chouetteRouteList.size()) {
            throw new IndexOutOfBoundsException("getChouetteRoute: Index value '" + index + "' not in range [0.." + (this._chouetteRouteList.size() - 1) + "]");
        }

        return (chouette.schema.ChouetteRoute) _chouetteRouteList.get(index);
    }

    /**
     * Method getChouetteRoute.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public chouette.schema.ChouetteRoute[] getChouetteRoute(
    ) {
        chouette.schema.ChouetteRoute[] array = new chouette.schema.ChouetteRoute[0];
        return (chouette.schema.ChouetteRoute[]) this._chouetteRouteList.toArray(array);
    }

    /**
     * Method getChouetteRouteAsReference.Returns a reference to
     * '_chouetteRouteList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<chouette.schema.ChouetteRoute> getChouetteRouteAsReference(
    ) {
        return this._chouetteRouteList;
    }

    /**
     * Method getChouetteRouteCount.
     * 
     * @return the size of this collection
     */
    public int getChouetteRouteCount(
    ) {
        return this._chouetteRouteList.size();
    }

    /**
     * Method getITL.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the chouette.schema.ITL at the given
     * index
     */
    public chouette.schema.ITL getITL(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._ITLList.size()) {
            throw new IndexOutOfBoundsException("getITL: Index value '" + index + "' not in range [0.." + (this._ITLList.size() - 1) + "]");
        }

        return (chouette.schema.ITL) _ITLList.get(index);
    }

    /**
     * Method getITL.Returns the contents of the collection in an
     * Array.  <p>Note:  Just in case the collection contents are
     * changing in another thread, we pass a 0-length Array of the
     * correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public chouette.schema.ITL[] getITL(
    ) {
        chouette.schema.ITL[] array = new chouette.schema.ITL[0];
        return (chouette.schema.ITL[]) this._ITLList.toArray(array);
    }

    /**
     * Method getITLAsReference.Returns a reference to '_ITLList'.
     * No type checking is performed on any modifications to the
     * Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<chouette.schema.ITL> getITLAsReference(
    ) {
        return this._ITLList;
    }

    /**
     * Method getITLCount.
     * 
     * @return the size of this collection
     */
    public int getITLCount(
    ) {
        return this._ITLList.size();
    }

    /**
     * Method getJourneyPattern.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the chouette.schema.JourneyPattern at
     * the given index
     */
    public chouette.schema.JourneyPattern getJourneyPattern(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._journeyPatternList.size()) {
            throw new IndexOutOfBoundsException("getJourneyPattern: Index value '" + index + "' not in range [0.." + (this._journeyPatternList.size() - 1) + "]");
        }

        return (chouette.schema.JourneyPattern) _journeyPatternList.get(index);
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
    public chouette.schema.JourneyPattern[] getJourneyPattern(
    ) {
        chouette.schema.JourneyPattern[] array = new chouette.schema.JourneyPattern[0];
        return (chouette.schema.JourneyPattern[]) this._journeyPatternList.toArray(array);
    }

    /**
     * Method getJourneyPatternAsReference.Returns a reference to
     * '_journeyPatternList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<chouette.schema.JourneyPattern> getJourneyPatternAsReference(
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
     * Returns the value of field 'line'. The field 'line' has the
     * following description: La ligne et ses r�f�rences vers ses
     * itin�raires et son r�seau
     * 
     * @return the value of field 'Line'.
     */
    public chouette.schema.Line getLine(
    ) {
        return this._line;
    }

    /**
     * Method getPtLink.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the chouette.schema.PtLink at the given
     * index
     */
    public chouette.schema.PtLink getPtLink(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._ptLinkList.size()) {
            throw new IndexOutOfBoundsException("getPtLink: Index value '" + index + "' not in range [0.." + (this._ptLinkList.size() - 1) + "]");
        }

        return (chouette.schema.PtLink) _ptLinkList.get(index);
    }

    /**
     * Method getPtLink.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public chouette.schema.PtLink[] getPtLink(
    ) {
        chouette.schema.PtLink[] array = new chouette.schema.PtLink[0];
        return (chouette.schema.PtLink[]) this._ptLinkList.toArray(array);
    }

    /**
     * Method getPtLinkAsReference.Returns a reference to
     * '_ptLinkList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<chouette.schema.PtLink> getPtLinkAsReference(
    ) {
        return this._ptLinkList;
    }

    /**
     * Method getPtLinkCount.
     * 
     * @return the size of this collection
     */
    public int getPtLinkCount(
    ) {
        return this._ptLinkList.size();
    }

    /**
     * Method getStopPoint.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the chouette.schema.StopPoint at the
     * given index
     */
    public chouette.schema.StopPoint getStopPoint(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._stopPointList.size()) {
            throw new IndexOutOfBoundsException("getStopPoint: Index value '" + index + "' not in range [0.." + (this._stopPointList.size() - 1) + "]");
        }

        return (chouette.schema.StopPoint) _stopPointList.get(index);
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
    public chouette.schema.StopPoint[] getStopPoint(
    ) {
        chouette.schema.StopPoint[] array = new chouette.schema.StopPoint[0];
        return (chouette.schema.StopPoint[]) this._stopPointList.toArray(array);
    }

    /**
     * Method getStopPointAsReference.Returns a reference to
     * '_stopPointList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<chouette.schema.StopPoint> getStopPointAsReference(
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
     * Method getVehicleJourney.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the chouette.schema.VehicleJourney at
     * the given index
     */
    public chouette.schema.VehicleJourney getVehicleJourney(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._vehicleJourneyList.size()) {
            throw new IndexOutOfBoundsException("getVehicleJourney: Index value '" + index + "' not in range [0.." + (this._vehicleJourneyList.size() - 1) + "]");
        }

        return (chouette.schema.VehicleJourney) _vehicleJourneyList.get(index);
    }

    /**
     * Method getVehicleJourney.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public chouette.schema.VehicleJourney[] getVehicleJourney(
    ) {
        chouette.schema.VehicleJourney[] array = new chouette.schema.VehicleJourney[0];
        return (chouette.schema.VehicleJourney[]) this._vehicleJourneyList.toArray(array);
    }

    /**
     * Method getVehicleJourneyAsReference.Returns a reference to
     * '_vehicleJourneyList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<chouette.schema.VehicleJourney> getVehicleJourneyAsReference(
    ) {
        return this._vehicleJourneyList;
    }

    /**
     * Method getVehicleJourneyCount.
     * 
     * @return the size of this collection
     */
    public int getVehicleJourneyCount(
    ) {
        return this._vehicleJourneyList.size();
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
     * Method iterateChouetteRoute.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends chouette.schema.ChouetteRoute> iterateChouetteRoute(
    ) {
        return this._chouetteRouteList.iterator();
    }

    /**
     * Method iterateITL.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends chouette.schema.ITL> iterateITL(
    ) {
        return this._ITLList.iterator();
    }

    /**
     * Method iterateJourneyPattern.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends chouette.schema.JourneyPattern> iterateJourneyPattern(
    ) {
        return this._journeyPatternList.iterator();
    }

    /**
     * Method iteratePtLink.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends chouette.schema.PtLink> iteratePtLink(
    ) {
        return this._ptLinkList.iterator();
    }

    /**
     * Method iterateStopPoint.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends chouette.schema.StopPoint> iterateStopPoint(
    ) {
        return this._stopPointList.iterator();
    }

    /**
     * Method iterateVehicleJourney.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends chouette.schema.VehicleJourney> iterateVehicleJourney(
    ) {
        return this._vehicleJourneyList.iterator();
    }

    /**
     * 
     * 
     * @param out
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     */
    public void marshal(
            final java.io.Writer out)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        org.exolab.castor.xml.Marshaller.marshal(this, out);
    }

    /**
     * 
     * 
     * @param handler
     * @throws java.io.IOException if an IOException occurs during
     * marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     */
    public void marshal(
            final org.xml.sax.ContentHandler handler)
    throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        org.exolab.castor.xml.Marshaller.marshal(this, handler);
    }

    /**
     */
    public void removeAllChouetteRoute(
    ) {
        this._chouetteRouteList.clear();
    }

    /**
     */
    public void removeAllITL(
    ) {
        this._ITLList.clear();
    }

    /**
     */
    public void removeAllJourneyPattern(
    ) {
        this._journeyPatternList.clear();
    }

    /**
     */
    public void removeAllPtLink(
    ) {
        this._ptLinkList.clear();
    }

    /**
     */
    public void removeAllStopPoint(
    ) {
        this._stopPointList.clear();
    }

    /**
     */
    public void removeAllVehicleJourney(
    ) {
        this._vehicleJourneyList.clear();
    }

    /**
     * Method removeChouetteRoute.
     * 
     * @param vChouetteRoute
     * @return true if the object was removed from the collection.
     */
    public boolean removeChouetteRoute(
            final chouette.schema.ChouetteRoute vChouetteRoute) {
        boolean removed = _chouetteRouteList.remove(vChouetteRoute);
        return removed;
    }

    /**
     * Method removeChouetteRouteAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public chouette.schema.ChouetteRoute removeChouetteRouteAt(
            final int index) {
        java.lang.Object obj = this._chouetteRouteList.remove(index);
        return (chouette.schema.ChouetteRoute) obj;
    }

    /**
     * Method removeITL.
     * 
     * @param vITL
     * @return true if the object was removed from the collection.
     */
    public boolean removeITL(
            final chouette.schema.ITL vITL) {
        boolean removed = _ITLList.remove(vITL);
        return removed;
    }

    /**
     * Method removeITLAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public chouette.schema.ITL removeITLAt(
            final int index) {
        java.lang.Object obj = this._ITLList.remove(index);
        return (chouette.schema.ITL) obj;
    }

    /**
     * Method removeJourneyPattern.
     * 
     * @param vJourneyPattern
     * @return true if the object was removed from the collection.
     */
    public boolean removeJourneyPattern(
            final chouette.schema.JourneyPattern vJourneyPattern) {
        boolean removed = _journeyPatternList.remove(vJourneyPattern);
        return removed;
    }

    /**
     * Method removeJourneyPatternAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public chouette.schema.JourneyPattern removeJourneyPatternAt(
            final int index) {
        java.lang.Object obj = this._journeyPatternList.remove(index);
        return (chouette.schema.JourneyPattern) obj;
    }

    /**
     * Method removePtLink.
     * 
     * @param vPtLink
     * @return true if the object was removed from the collection.
     */
    public boolean removePtLink(
            final chouette.schema.PtLink vPtLink) {
        boolean removed = _ptLinkList.remove(vPtLink);
        return removed;
    }

    /**
     * Method removePtLinkAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public chouette.schema.PtLink removePtLinkAt(
            final int index) {
        java.lang.Object obj = this._ptLinkList.remove(index);
        return (chouette.schema.PtLink) obj;
    }

    /**
     * Method removeStopPoint.
     * 
     * @param vStopPoint
     * @return true if the object was removed from the collection.
     */
    public boolean removeStopPoint(
            final chouette.schema.StopPoint vStopPoint) {
        boolean removed = _stopPointList.remove(vStopPoint);
        return removed;
    }

    /**
     * Method removeStopPointAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public chouette.schema.StopPoint removeStopPointAt(
            final int index) {
        java.lang.Object obj = this._stopPointList.remove(index);
        return (chouette.schema.StopPoint) obj;
    }

    /**
     * Method removeVehicleJourney.
     * 
     * @param vVehicleJourney
     * @return true if the object was removed from the collection.
     */
    public boolean removeVehicleJourney(
            final chouette.schema.VehicleJourney vVehicleJourney) {
        boolean removed = _vehicleJourneyList.remove(vVehicleJourney);
        return removed;
    }

    /**
     * Method removeVehicleJourneyAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public chouette.schema.VehicleJourney removeVehicleJourneyAt(
            final int index) {
        java.lang.Object obj = this._vehicleJourneyList.remove(index);
        return (chouette.schema.VehicleJourney) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vChouetteRoute
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setChouetteRoute(
            final int index,
            final chouette.schema.ChouetteRoute vChouetteRoute)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._chouetteRouteList.size()) {
            throw new IndexOutOfBoundsException("setChouetteRoute: Index value '" + index + "' not in range [0.." + (this._chouetteRouteList.size() - 1) + "]");
        }

        this._chouetteRouteList.set(index, vChouetteRoute);
    }

    /**
     * 
     * 
     * @param vChouetteRouteArray
     */
    public void setChouetteRoute(
            final chouette.schema.ChouetteRoute[] vChouetteRouteArray) {
        //-- copy array
        _chouetteRouteList.clear();

        for (int i = 0; i < vChouetteRouteArray.length; i++) {
                this._chouetteRouteList.add(vChouetteRouteArray[i]);
        }
    }

    /**
     * Sets the value of '_chouetteRouteList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vChouetteRouteList the Vector to copy.
     */
    public void setChouetteRoute(
            final java.util.List<chouette.schema.ChouetteRoute> vChouetteRouteList) {
        // copy vector
        this._chouetteRouteList.clear();

        this._chouetteRouteList.addAll(vChouetteRouteList);
    }

    /**
     * Sets the value of '_chouetteRouteList' by setting it to the
     * given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param chouetteRouteList the Vector to set.
     */
    public void setChouetteRouteAsReference(
            final java.util.List<chouette.schema.ChouetteRoute> chouetteRouteList) {
        this._chouetteRouteList = chouetteRouteList;
    }

    /**
     * 
     * 
     * @param index
     * @param vITL
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setITL(
            final int index,
            final chouette.schema.ITL vITL)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._ITLList.size()) {
            throw new IndexOutOfBoundsException("setITL: Index value '" + index + "' not in range [0.." + (this._ITLList.size() - 1) + "]");
        }

        this._ITLList.set(index, vITL);
    }

    /**
     * 
     * 
     * @param vITLArray
     */
    public void setITL(
            final chouette.schema.ITL[] vITLArray) {
        //-- copy array
        _ITLList.clear();

        for (int i = 0; i < vITLArray.length; i++) {
                this._ITLList.add(vITLArray[i]);
        }
    }

    /**
     * Sets the value of '_ITLList' by copying the given Vector.
     * All elements will be checked for type safety.
     * 
     * @param vITLList the Vector to copy.
     */
    public void setITL(
            final java.util.List<chouette.schema.ITL> vITLList) {
        // copy vector
        this._ITLList.clear();

        this._ITLList.addAll(vITLList);
    }

    /**
     * Sets the value of '_ITLList' by setting it to the given
     * Vector. No type checking is performed.
     * @deprecated
     * 
     * @param ITLList the Vector to set.
     */
    public void setITLAsReference(
            final java.util.List<chouette.schema.ITL> ITLList) {
        this._ITLList = ITLList;
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
            final chouette.schema.JourneyPattern vJourneyPattern)
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
            final chouette.schema.JourneyPattern[] vJourneyPatternArray) {
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
            final java.util.List<chouette.schema.JourneyPattern> vJourneyPatternList) {
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
            final java.util.List<chouette.schema.JourneyPattern> journeyPatternList) {
        this._journeyPatternList = journeyPatternList;
    }

    /**
     * Sets the value of field 'line'. The field 'line' has the
     * following description: La ligne et ses r�f�rences vers ses
     * itin�raires et son r�seau
     * 
     * @param line the value of field 'line'.
     */
    public void setLine(
            final chouette.schema.Line line) {
        this._line = line;
    }

    /**
     * 
     * 
     * @param index
     * @param vPtLink
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setPtLink(
            final int index,
            final chouette.schema.PtLink vPtLink)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._ptLinkList.size()) {
            throw new IndexOutOfBoundsException("setPtLink: Index value '" + index + "' not in range [0.." + (this._ptLinkList.size() - 1) + "]");
        }

        this._ptLinkList.set(index, vPtLink);
    }

    /**
     * 
     * 
     * @param vPtLinkArray
     */
    public void setPtLink(
            final chouette.schema.PtLink[] vPtLinkArray) {
        //-- copy array
        _ptLinkList.clear();

        for (int i = 0; i < vPtLinkArray.length; i++) {
                this._ptLinkList.add(vPtLinkArray[i]);
        }
    }

    /**
     * Sets the value of '_ptLinkList' by copying the given Vector.
     * All elements will be checked for type safety.
     * 
     * @param vPtLinkList the Vector to copy.
     */
    public void setPtLink(
            final java.util.List<chouette.schema.PtLink> vPtLinkList) {
        // copy vector
        this._ptLinkList.clear();

        this._ptLinkList.addAll(vPtLinkList);
    }

    /**
     * Sets the value of '_ptLinkList' by setting it to the given
     * Vector. No type checking is performed.
     * @deprecated
     * 
     * @param ptLinkList the Vector to set.
     */
    public void setPtLinkAsReference(
            final java.util.List<chouette.schema.PtLink> ptLinkList) {
        this._ptLinkList = ptLinkList;
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
            final chouette.schema.StopPoint vStopPoint)
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
            final chouette.schema.StopPoint[] vStopPointArray) {
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
            final java.util.List<chouette.schema.StopPoint> vStopPointList) {
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
            final java.util.List<chouette.schema.StopPoint> stopPointList) {
        this._stopPointList = stopPointList;
    }

    /**
     * 
     * 
     * @param index
     * @param vVehicleJourney
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setVehicleJourney(
            final int index,
            final chouette.schema.VehicleJourney vVehicleJourney)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._vehicleJourneyList.size()) {
            throw new IndexOutOfBoundsException("setVehicleJourney: Index value '" + index + "' not in range [0.." + (this._vehicleJourneyList.size() - 1) + "]");
        }

        this._vehicleJourneyList.set(index, vVehicleJourney);
    }

    /**
     * 
     * 
     * @param vVehicleJourneyArray
     */
    public void setVehicleJourney(
            final chouette.schema.VehicleJourney[] vVehicleJourneyArray) {
        //-- copy array
        _vehicleJourneyList.clear();

        for (int i = 0; i < vVehicleJourneyArray.length; i++) {
                this._vehicleJourneyList.add(vVehicleJourneyArray[i]);
        }
    }

    /**
     * Sets the value of '_vehicleJourneyList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vVehicleJourneyList the Vector to copy.
     */
    public void setVehicleJourney(
            final java.util.List<chouette.schema.VehicleJourney> vVehicleJourneyList) {
        // copy vector
        this._vehicleJourneyList.clear();

        this._vehicleJourneyList.addAll(vVehicleJourneyList);
    }

    /**
     * Sets the value of '_vehicleJourneyList' by setting it to the
     * given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param vehicleJourneyList the Vector to set.
     */
    public void setVehicleJourneyAsReference(
            final java.util.List<chouette.schema.VehicleJourney> vehicleJourneyList) {
        this._vehicleJourneyList = vehicleJourneyList;
    }

    /**
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled
     * chouette.schema.ChouetteLineDescription
     */
    public static chouette.schema.ChouetteLineDescription unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (chouette.schema.ChouetteLineDescription) org.exolab.castor.xml.Unmarshaller.unmarshal(chouette.schema.ChouetteLineDescription.class, reader);
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
