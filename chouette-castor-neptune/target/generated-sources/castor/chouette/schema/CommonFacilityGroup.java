/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * Structured Classification Elements. Corresponds to TPEG 18 Event
 * Reason
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class CommonFacilityGroup extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Classification of FareClass Facility type - Tpeg pti23.
     */
    private java.util.List<chouette.schema.types.FareClassFacilityEnumeration> _fareClassFacilityList;

    /**
     * Classification of Ticketing Facility type - Tpeg pti23.
     */
    private java.util.List<chouette.schema.types.TicketingFacilityEnumeration> _ticketingFacilityList;

    /**
     * Classification of Nuisance Facility type - Tpeg pti23.
     */
    private java.util.List<chouette.schema.types.NuisanceFacilityEnumeration> _nuisanceFacilityList;

    /**
     * Classification of Mobility Facility type - Tpeg pti23.
     */
    private java.util.List<chouette.schema.types.MobilityFacilityEnumeration> _mobilityFacilityList;

    /**
     * Classification of PassengerInfo Facility type - Tpeg pti23.
     */
    private java.util.List<chouette.schema.types.PassengerInformationFacilityEnumeration> _passengerInformationFacilityList;

    /**
     * Classification of PassengerComms Facility type - Tpeg pti23.
     */
    private java.util.List<chouette.schema.types.PassengerCommsFacilityEnumeration> _passengerCommsFacilityList;

    /**
     * Classification of Refreshment Facility type - Tpeg pti23.
     */
    private java.util.List<chouette.schema.types.RefreshmentFacilityEnumeration> _refreshmentFacilityList;

    /**
     * Classification of Access Facility
     */
    private java.util.List<chouette.schema.types.AccessFacilityEnumeration> _accessFacilityList;

    /**
     * Classification of Sanitary Facility type - Tpeg pti23.
     */
    private java.util.List<chouette.schema.types.SanitaryFacilityEnumeration> _sanitaryFacilityList;

    /**
     * Classification of Luggage Facility type - Tpeg pti23.
     */
    private java.util.List<chouette.schema.types.LuggageFacilityEnumeration> _luggageFacilityList;


      //----------------/
     //- Constructors -/
    //----------------/

    public CommonFacilityGroup() {
        super();
        this._fareClassFacilityList = new java.util.ArrayList<chouette.schema.types.FareClassFacilityEnumeration>();
        this._ticketingFacilityList = new java.util.ArrayList<chouette.schema.types.TicketingFacilityEnumeration>();
        this._nuisanceFacilityList = new java.util.ArrayList<chouette.schema.types.NuisanceFacilityEnumeration>();
        this._mobilityFacilityList = new java.util.ArrayList<chouette.schema.types.MobilityFacilityEnumeration>();
        this._passengerInformationFacilityList = new java.util.ArrayList<chouette.schema.types.PassengerInformationFacilityEnumeration>();
        this._passengerCommsFacilityList = new java.util.ArrayList<chouette.schema.types.PassengerCommsFacilityEnumeration>();
        this._refreshmentFacilityList = new java.util.ArrayList<chouette.schema.types.RefreshmentFacilityEnumeration>();
        this._accessFacilityList = new java.util.ArrayList<chouette.schema.types.AccessFacilityEnumeration>();
        this._sanitaryFacilityList = new java.util.ArrayList<chouette.schema.types.SanitaryFacilityEnumeration>();
        this._luggageFacilityList = new java.util.ArrayList<chouette.schema.types.LuggageFacilityEnumeration>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vAccessFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addAccessFacility(
            final chouette.schema.types.AccessFacilityEnumeration vAccessFacility)
    throws java.lang.IndexOutOfBoundsException {
        this._accessFacilityList.add(vAccessFacility);
    }

    /**
     * 
     * 
     * @param index
     * @param vAccessFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addAccessFacility(
            final int index,
            final chouette.schema.types.AccessFacilityEnumeration vAccessFacility)
    throws java.lang.IndexOutOfBoundsException {
        this._accessFacilityList.add(index, vAccessFacility);
    }

    /**
     * 
     * 
     * @param vFareClassFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addFareClassFacility(
            final chouette.schema.types.FareClassFacilityEnumeration vFareClassFacility)
    throws java.lang.IndexOutOfBoundsException {
        this._fareClassFacilityList.add(vFareClassFacility);
    }

    /**
     * 
     * 
     * @param index
     * @param vFareClassFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addFareClassFacility(
            final int index,
            final chouette.schema.types.FareClassFacilityEnumeration vFareClassFacility)
    throws java.lang.IndexOutOfBoundsException {
        this._fareClassFacilityList.add(index, vFareClassFacility);
    }

    /**
     * 
     * 
     * @param vLuggageFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addLuggageFacility(
            final chouette.schema.types.LuggageFacilityEnumeration vLuggageFacility)
    throws java.lang.IndexOutOfBoundsException {
        this._luggageFacilityList.add(vLuggageFacility);
    }

    /**
     * 
     * 
     * @param index
     * @param vLuggageFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addLuggageFacility(
            final int index,
            final chouette.schema.types.LuggageFacilityEnumeration vLuggageFacility)
    throws java.lang.IndexOutOfBoundsException {
        this._luggageFacilityList.add(index, vLuggageFacility);
    }

    /**
     * 
     * 
     * @param vMobilityFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addMobilityFacility(
            final chouette.schema.types.MobilityFacilityEnumeration vMobilityFacility)
    throws java.lang.IndexOutOfBoundsException {
        this._mobilityFacilityList.add(vMobilityFacility);
    }

    /**
     * 
     * 
     * @param index
     * @param vMobilityFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addMobilityFacility(
            final int index,
            final chouette.schema.types.MobilityFacilityEnumeration vMobilityFacility)
    throws java.lang.IndexOutOfBoundsException {
        this._mobilityFacilityList.add(index, vMobilityFacility);
    }

    /**
     * 
     * 
     * @param vNuisanceFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addNuisanceFacility(
            final chouette.schema.types.NuisanceFacilityEnumeration vNuisanceFacility)
    throws java.lang.IndexOutOfBoundsException {
        this._nuisanceFacilityList.add(vNuisanceFacility);
    }

    /**
     * 
     * 
     * @param index
     * @param vNuisanceFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addNuisanceFacility(
            final int index,
            final chouette.schema.types.NuisanceFacilityEnumeration vNuisanceFacility)
    throws java.lang.IndexOutOfBoundsException {
        this._nuisanceFacilityList.add(index, vNuisanceFacility);
    }

    /**
     * 
     * 
     * @param vPassengerCommsFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPassengerCommsFacility(
            final chouette.schema.types.PassengerCommsFacilityEnumeration vPassengerCommsFacility)
    throws java.lang.IndexOutOfBoundsException {
        this._passengerCommsFacilityList.add(vPassengerCommsFacility);
    }

    /**
     * 
     * 
     * @param index
     * @param vPassengerCommsFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPassengerCommsFacility(
            final int index,
            final chouette.schema.types.PassengerCommsFacilityEnumeration vPassengerCommsFacility)
    throws java.lang.IndexOutOfBoundsException {
        this._passengerCommsFacilityList.add(index, vPassengerCommsFacility);
    }

    /**
     * 
     * 
     * @param vPassengerInformationFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPassengerInformationFacility(
            final chouette.schema.types.PassengerInformationFacilityEnumeration vPassengerInformationFacility)
    throws java.lang.IndexOutOfBoundsException {
        this._passengerInformationFacilityList.add(vPassengerInformationFacility);
    }

    /**
     * 
     * 
     * @param index
     * @param vPassengerInformationFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPassengerInformationFacility(
            final int index,
            final chouette.schema.types.PassengerInformationFacilityEnumeration vPassengerInformationFacility)
    throws java.lang.IndexOutOfBoundsException {
        this._passengerInformationFacilityList.add(index, vPassengerInformationFacility);
    }

    /**
     * 
     * 
     * @param vRefreshmentFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addRefreshmentFacility(
            final chouette.schema.types.RefreshmentFacilityEnumeration vRefreshmentFacility)
    throws java.lang.IndexOutOfBoundsException {
        this._refreshmentFacilityList.add(vRefreshmentFacility);
    }

    /**
     * 
     * 
     * @param index
     * @param vRefreshmentFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addRefreshmentFacility(
            final int index,
            final chouette.schema.types.RefreshmentFacilityEnumeration vRefreshmentFacility)
    throws java.lang.IndexOutOfBoundsException {
        this._refreshmentFacilityList.add(index, vRefreshmentFacility);
    }

    /**
     * 
     * 
     * @param vSanitaryFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addSanitaryFacility(
            final chouette.schema.types.SanitaryFacilityEnumeration vSanitaryFacility)
    throws java.lang.IndexOutOfBoundsException {
        this._sanitaryFacilityList.add(vSanitaryFacility);
    }

    /**
     * 
     * 
     * @param index
     * @param vSanitaryFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addSanitaryFacility(
            final int index,
            final chouette.schema.types.SanitaryFacilityEnumeration vSanitaryFacility)
    throws java.lang.IndexOutOfBoundsException {
        this._sanitaryFacilityList.add(index, vSanitaryFacility);
    }

    /**
     * 
     * 
     * @param vTicketingFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addTicketingFacility(
            final chouette.schema.types.TicketingFacilityEnumeration vTicketingFacility)
    throws java.lang.IndexOutOfBoundsException {
        this._ticketingFacilityList.add(vTicketingFacility);
    }

    /**
     * 
     * 
     * @param index
     * @param vTicketingFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addTicketingFacility(
            final int index,
            final chouette.schema.types.TicketingFacilityEnumeration vTicketingFacility)
    throws java.lang.IndexOutOfBoundsException {
        this._ticketingFacilityList.add(index, vTicketingFacility);
    }

    /**
     * Method enumerateAccessFacility.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends chouette.schema.types.AccessFacilityEnumeration> enumerateAccessFacility(
    ) {
        return java.util.Collections.enumeration(this._accessFacilityList);
    }

    /**
     * Method enumerateFareClassFacility.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends chouette.schema.types.FareClassFacilityEnumeration> enumerateFareClassFacility(
    ) {
        return java.util.Collections.enumeration(this._fareClassFacilityList);
    }

    /**
     * Method enumerateLuggageFacility.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends chouette.schema.types.LuggageFacilityEnumeration> enumerateLuggageFacility(
    ) {
        return java.util.Collections.enumeration(this._luggageFacilityList);
    }

    /**
     * Method enumerateMobilityFacility.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends chouette.schema.types.MobilityFacilityEnumeration> enumerateMobilityFacility(
    ) {
        return java.util.Collections.enumeration(this._mobilityFacilityList);
    }

    /**
     * Method enumerateNuisanceFacility.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends chouette.schema.types.NuisanceFacilityEnumeration> enumerateNuisanceFacility(
    ) {
        return java.util.Collections.enumeration(this._nuisanceFacilityList);
    }

    /**
     * Method enumeratePassengerCommsFacility.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends chouette.schema.types.PassengerCommsFacilityEnumeration> enumeratePassengerCommsFacility(
    ) {
        return java.util.Collections.enumeration(this._passengerCommsFacilityList);
    }

    /**
     * Method enumeratePassengerInformationFacility.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends chouette.schema.types.PassengerInformationFacilityEnumeration> enumeratePassengerInformationFacility(
    ) {
        return java.util.Collections.enumeration(this._passengerInformationFacilityList);
    }

    /**
     * Method enumerateRefreshmentFacility.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends chouette.schema.types.RefreshmentFacilityEnumeration> enumerateRefreshmentFacility(
    ) {
        return java.util.Collections.enumeration(this._refreshmentFacilityList);
    }

    /**
     * Method enumerateSanitaryFacility.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends chouette.schema.types.SanitaryFacilityEnumeration> enumerateSanitaryFacility(
    ) {
        return java.util.Collections.enumeration(this._sanitaryFacilityList);
    }

    /**
     * Method enumerateTicketingFacility.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends chouette.schema.types.TicketingFacilityEnumeration> enumerateTicketingFacility(
    ) {
        return java.util.Collections.enumeration(this._ticketingFacilityList);
    }

    /**
     * Method getAccessFacility.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * chouette.schema.types.AccessFacilityEnumeration at the given
     * index
     */
    public chouette.schema.types.AccessFacilityEnumeration getAccessFacility(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._accessFacilityList.size()) {
            throw new IndexOutOfBoundsException("getAccessFacility: Index value '" + index + "' not in range [0.." + (this._accessFacilityList.size() - 1) + "]");
        }

        return (chouette.schema.types.AccessFacilityEnumeration) _accessFacilityList.get(index);
    }

    /**
     * Method getAccessFacility.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public chouette.schema.types.AccessFacilityEnumeration[] getAccessFacility(
    ) {
        chouette.schema.types.AccessFacilityEnumeration[] array = new chouette.schema.types.AccessFacilityEnumeration[0];
        return (chouette.schema.types.AccessFacilityEnumeration[]) this._accessFacilityList.toArray(array);
    }

    /**
     * Method getAccessFacilityAsReference.Returns a reference to
     * '_accessFacilityList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<chouette.schema.types.AccessFacilityEnumeration> getAccessFacilityAsReference(
    ) {
        return this._accessFacilityList;
    }

    /**
     * Method getAccessFacilityCount.
     * 
     * @return the size of this collection
     */
    public int getAccessFacilityCount(
    ) {
        return this._accessFacilityList.size();
    }

    /**
     * Method getFareClassFacility.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * chouette.schema.types.FareClassFacilityEnumeration at the
     * given index
     */
    public chouette.schema.types.FareClassFacilityEnumeration getFareClassFacility(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._fareClassFacilityList.size()) {
            throw new IndexOutOfBoundsException("getFareClassFacility: Index value '" + index + "' not in range [0.." + (this._fareClassFacilityList.size() - 1) + "]");
        }

        return (chouette.schema.types.FareClassFacilityEnumeration) _fareClassFacilityList.get(index);
    }

    /**
     * Method getFareClassFacility.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public chouette.schema.types.FareClassFacilityEnumeration[] getFareClassFacility(
    ) {
        chouette.schema.types.FareClassFacilityEnumeration[] array = new chouette.schema.types.FareClassFacilityEnumeration[0];
        return (chouette.schema.types.FareClassFacilityEnumeration[]) this._fareClassFacilityList.toArray(array);
    }

    /**
     * Method getFareClassFacilityAsReference.Returns a reference
     * to '_fareClassFacilityList'. No type checking is performed
     * on any modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<chouette.schema.types.FareClassFacilityEnumeration> getFareClassFacilityAsReference(
    ) {
        return this._fareClassFacilityList;
    }

    /**
     * Method getFareClassFacilityCount.
     * 
     * @return the size of this collection
     */
    public int getFareClassFacilityCount(
    ) {
        return this._fareClassFacilityList.size();
    }

    /**
     * Method getLuggageFacility.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * chouette.schema.types.LuggageFacilityEnumeration at the
     * given index
     */
    public chouette.schema.types.LuggageFacilityEnumeration getLuggageFacility(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._luggageFacilityList.size()) {
            throw new IndexOutOfBoundsException("getLuggageFacility: Index value '" + index + "' not in range [0.." + (this._luggageFacilityList.size() - 1) + "]");
        }

        return (chouette.schema.types.LuggageFacilityEnumeration) _luggageFacilityList.get(index);
    }

    /**
     * Method getLuggageFacility.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public chouette.schema.types.LuggageFacilityEnumeration[] getLuggageFacility(
    ) {
        chouette.schema.types.LuggageFacilityEnumeration[] array = new chouette.schema.types.LuggageFacilityEnumeration[0];
        return (chouette.schema.types.LuggageFacilityEnumeration[]) this._luggageFacilityList.toArray(array);
    }

    /**
     * Method getLuggageFacilityAsReference.Returns a reference to
     * '_luggageFacilityList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<chouette.schema.types.LuggageFacilityEnumeration> getLuggageFacilityAsReference(
    ) {
        return this._luggageFacilityList;
    }

    /**
     * Method getLuggageFacilityCount.
     * 
     * @return the size of this collection
     */
    public int getLuggageFacilityCount(
    ) {
        return this._luggageFacilityList.size();
    }

    /**
     * Method getMobilityFacility.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * chouette.schema.types.MobilityFacilityEnumeration at the
     * given index
     */
    public chouette.schema.types.MobilityFacilityEnumeration getMobilityFacility(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._mobilityFacilityList.size()) {
            throw new IndexOutOfBoundsException("getMobilityFacility: Index value '" + index + "' not in range [0.." + (this._mobilityFacilityList.size() - 1) + "]");
        }

        return (chouette.schema.types.MobilityFacilityEnumeration) _mobilityFacilityList.get(index);
    }

    /**
     * Method getMobilityFacility.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public chouette.schema.types.MobilityFacilityEnumeration[] getMobilityFacility(
    ) {
        chouette.schema.types.MobilityFacilityEnumeration[] array = new chouette.schema.types.MobilityFacilityEnumeration[0];
        return (chouette.schema.types.MobilityFacilityEnumeration[]) this._mobilityFacilityList.toArray(array);
    }

    /**
     * Method getMobilityFacilityAsReference.Returns a reference to
     * '_mobilityFacilityList'. No type checking is performed on
     * any modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<chouette.schema.types.MobilityFacilityEnumeration> getMobilityFacilityAsReference(
    ) {
        return this._mobilityFacilityList;
    }

    /**
     * Method getMobilityFacilityCount.
     * 
     * @return the size of this collection
     */
    public int getMobilityFacilityCount(
    ) {
        return this._mobilityFacilityList.size();
    }

    /**
     * Method getNuisanceFacility.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * chouette.schema.types.NuisanceFacilityEnumeration at the
     * given index
     */
    public chouette.schema.types.NuisanceFacilityEnumeration getNuisanceFacility(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._nuisanceFacilityList.size()) {
            throw new IndexOutOfBoundsException("getNuisanceFacility: Index value '" + index + "' not in range [0.." + (this._nuisanceFacilityList.size() - 1) + "]");
        }

        return (chouette.schema.types.NuisanceFacilityEnumeration) _nuisanceFacilityList.get(index);
    }

    /**
     * Method getNuisanceFacility.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public chouette.schema.types.NuisanceFacilityEnumeration[] getNuisanceFacility(
    ) {
        chouette.schema.types.NuisanceFacilityEnumeration[] array = new chouette.schema.types.NuisanceFacilityEnumeration[0];
        return (chouette.schema.types.NuisanceFacilityEnumeration[]) this._nuisanceFacilityList.toArray(array);
    }

    /**
     * Method getNuisanceFacilityAsReference.Returns a reference to
     * '_nuisanceFacilityList'. No type checking is performed on
     * any modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<chouette.schema.types.NuisanceFacilityEnumeration> getNuisanceFacilityAsReference(
    ) {
        return this._nuisanceFacilityList;
    }

    /**
     * Method getNuisanceFacilityCount.
     * 
     * @return the size of this collection
     */
    public int getNuisanceFacilityCount(
    ) {
        return this._nuisanceFacilityList.size();
    }

    /**
     * Method getPassengerCommsFacility.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * chouette.schema.types.PassengerCommsFacilityEnumeration at
     * the given index
     */
    public chouette.schema.types.PassengerCommsFacilityEnumeration getPassengerCommsFacility(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._passengerCommsFacilityList.size()) {
            throw new IndexOutOfBoundsException("getPassengerCommsFacility: Index value '" + index + "' not in range [0.." + (this._passengerCommsFacilityList.size() - 1) + "]");
        }

        return (chouette.schema.types.PassengerCommsFacilityEnumeration) _passengerCommsFacilityList.get(index);
    }

    /**
     * Method getPassengerCommsFacility.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public chouette.schema.types.PassengerCommsFacilityEnumeration[] getPassengerCommsFacility(
    ) {
        chouette.schema.types.PassengerCommsFacilityEnumeration[] array = new chouette.schema.types.PassengerCommsFacilityEnumeration[0];
        return (chouette.schema.types.PassengerCommsFacilityEnumeration[]) this._passengerCommsFacilityList.toArray(array);
    }

    /**
     * Method getPassengerCommsFacilityAsReference.Returns a
     * reference to '_passengerCommsFacilityList'. No type checking
     * is performed on any modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<chouette.schema.types.PassengerCommsFacilityEnumeration> getPassengerCommsFacilityAsReference(
    ) {
        return this._passengerCommsFacilityList;
    }

    /**
     * Method getPassengerCommsFacilityCount.
     * 
     * @return the size of this collection
     */
    public int getPassengerCommsFacilityCount(
    ) {
        return this._passengerCommsFacilityList.size();
    }

    /**
     * Method getPassengerInformationFacility.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * chouette.schema.types.PassengerInformationFacilityEnumeration
     * at the given index
     */
    public chouette.schema.types.PassengerInformationFacilityEnumeration getPassengerInformationFacility(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._passengerInformationFacilityList.size()) {
            throw new IndexOutOfBoundsException("getPassengerInformationFacility: Index value '" + index + "' not in range [0.." + (this._passengerInformationFacilityList.size() - 1) + "]");
        }

        return (chouette.schema.types.PassengerInformationFacilityEnumeration) _passengerInformationFacilityList.get(index);
    }

    /**
     * Method getPassengerInformationFacility.Returns the contents
     * of the collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public chouette.schema.types.PassengerInformationFacilityEnumeration[] getPassengerInformationFacility(
    ) {
        chouette.schema.types.PassengerInformationFacilityEnumeration[] array = new chouette.schema.types.PassengerInformationFacilityEnumeration[0];
        return (chouette.schema.types.PassengerInformationFacilityEnumeration[]) this._passengerInformationFacilityList.toArray(array);
    }

    /**
     * Method getPassengerInformationFacilityAsReference.Returns a
     * reference to '_passengerInformationFacilityList'. No type
     * checking is performed on any modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<chouette.schema.types.PassengerInformationFacilityEnumeration> getPassengerInformationFacilityAsReference(
    ) {
        return this._passengerInformationFacilityList;
    }

    /**
     * Method getPassengerInformationFacilityCount.
     * 
     * @return the size of this collection
     */
    public int getPassengerInformationFacilityCount(
    ) {
        return this._passengerInformationFacilityList.size();
    }

    /**
     * Method getRefreshmentFacility.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * chouette.schema.types.RefreshmentFacilityEnumeration at the
     * given index
     */
    public chouette.schema.types.RefreshmentFacilityEnumeration getRefreshmentFacility(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._refreshmentFacilityList.size()) {
            throw new IndexOutOfBoundsException("getRefreshmentFacility: Index value '" + index + "' not in range [0.." + (this._refreshmentFacilityList.size() - 1) + "]");
        }

        return (chouette.schema.types.RefreshmentFacilityEnumeration) _refreshmentFacilityList.get(index);
    }

    /**
     * Method getRefreshmentFacility.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public chouette.schema.types.RefreshmentFacilityEnumeration[] getRefreshmentFacility(
    ) {
        chouette.schema.types.RefreshmentFacilityEnumeration[] array = new chouette.schema.types.RefreshmentFacilityEnumeration[0];
        return (chouette.schema.types.RefreshmentFacilityEnumeration[]) this._refreshmentFacilityList.toArray(array);
    }

    /**
     * Method getRefreshmentFacilityAsReference.Returns a reference
     * to '_refreshmentFacilityList'. No type checking is performed
     * on any modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<chouette.schema.types.RefreshmentFacilityEnumeration> getRefreshmentFacilityAsReference(
    ) {
        return this._refreshmentFacilityList;
    }

    /**
     * Method getRefreshmentFacilityCount.
     * 
     * @return the size of this collection
     */
    public int getRefreshmentFacilityCount(
    ) {
        return this._refreshmentFacilityList.size();
    }

    /**
     * Method getSanitaryFacility.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * chouette.schema.types.SanitaryFacilityEnumeration at the
     * given index
     */
    public chouette.schema.types.SanitaryFacilityEnumeration getSanitaryFacility(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._sanitaryFacilityList.size()) {
            throw new IndexOutOfBoundsException("getSanitaryFacility: Index value '" + index + "' not in range [0.." + (this._sanitaryFacilityList.size() - 1) + "]");
        }

        return (chouette.schema.types.SanitaryFacilityEnumeration) _sanitaryFacilityList.get(index);
    }

    /**
     * Method getSanitaryFacility.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public chouette.schema.types.SanitaryFacilityEnumeration[] getSanitaryFacility(
    ) {
        chouette.schema.types.SanitaryFacilityEnumeration[] array = new chouette.schema.types.SanitaryFacilityEnumeration[0];
        return (chouette.schema.types.SanitaryFacilityEnumeration[]) this._sanitaryFacilityList.toArray(array);
    }

    /**
     * Method getSanitaryFacilityAsReference.Returns a reference to
     * '_sanitaryFacilityList'. No type checking is performed on
     * any modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<chouette.schema.types.SanitaryFacilityEnumeration> getSanitaryFacilityAsReference(
    ) {
        return this._sanitaryFacilityList;
    }

    /**
     * Method getSanitaryFacilityCount.
     * 
     * @return the size of this collection
     */
    public int getSanitaryFacilityCount(
    ) {
        return this._sanitaryFacilityList.size();
    }

    /**
     * Method getTicketingFacility.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * chouette.schema.types.TicketingFacilityEnumeration at the
     * given index
     */
    public chouette.schema.types.TicketingFacilityEnumeration getTicketingFacility(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._ticketingFacilityList.size()) {
            throw new IndexOutOfBoundsException("getTicketingFacility: Index value '" + index + "' not in range [0.." + (this._ticketingFacilityList.size() - 1) + "]");
        }

        return (chouette.schema.types.TicketingFacilityEnumeration) _ticketingFacilityList.get(index);
    }

    /**
     * Method getTicketingFacility.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public chouette.schema.types.TicketingFacilityEnumeration[] getTicketingFacility(
    ) {
        chouette.schema.types.TicketingFacilityEnumeration[] array = new chouette.schema.types.TicketingFacilityEnumeration[0];
        return (chouette.schema.types.TicketingFacilityEnumeration[]) this._ticketingFacilityList.toArray(array);
    }

    /**
     * Method getTicketingFacilityAsReference.Returns a reference
     * to '_ticketingFacilityList'. No type checking is performed
     * on any modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<chouette.schema.types.TicketingFacilityEnumeration> getTicketingFacilityAsReference(
    ) {
        return this._ticketingFacilityList;
    }

    /**
     * Method getTicketingFacilityCount.
     * 
     * @return the size of this collection
     */
    public int getTicketingFacilityCount(
    ) {
        return this._ticketingFacilityList.size();
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
     * Method iterateAccessFacility.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends chouette.schema.types.AccessFacilityEnumeration> iterateAccessFacility(
    ) {
        return this._accessFacilityList.iterator();
    }

    /**
     * Method iterateFareClassFacility.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends chouette.schema.types.FareClassFacilityEnumeration> iterateFareClassFacility(
    ) {
        return this._fareClassFacilityList.iterator();
    }

    /**
     * Method iterateLuggageFacility.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends chouette.schema.types.LuggageFacilityEnumeration> iterateLuggageFacility(
    ) {
        return this._luggageFacilityList.iterator();
    }

    /**
     * Method iterateMobilityFacility.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends chouette.schema.types.MobilityFacilityEnumeration> iterateMobilityFacility(
    ) {
        return this._mobilityFacilityList.iterator();
    }

    /**
     * Method iterateNuisanceFacility.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends chouette.schema.types.NuisanceFacilityEnumeration> iterateNuisanceFacility(
    ) {
        return this._nuisanceFacilityList.iterator();
    }

    /**
     * Method iteratePassengerCommsFacility.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends chouette.schema.types.PassengerCommsFacilityEnumeration> iteratePassengerCommsFacility(
    ) {
        return this._passengerCommsFacilityList.iterator();
    }

    /**
     * Method iteratePassengerInformationFacility.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends chouette.schema.types.PassengerInformationFacilityEnumeration> iteratePassengerInformationFacility(
    ) {
        return this._passengerInformationFacilityList.iterator();
    }

    /**
     * Method iterateRefreshmentFacility.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends chouette.schema.types.RefreshmentFacilityEnumeration> iterateRefreshmentFacility(
    ) {
        return this._refreshmentFacilityList.iterator();
    }

    /**
     * Method iterateSanitaryFacility.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends chouette.schema.types.SanitaryFacilityEnumeration> iterateSanitaryFacility(
    ) {
        return this._sanitaryFacilityList.iterator();
    }

    /**
     * Method iterateTicketingFacility.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends chouette.schema.types.TicketingFacilityEnumeration> iterateTicketingFacility(
    ) {
        return this._ticketingFacilityList.iterator();
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
     * Method removeAccessFacility.
     * 
     * @param vAccessFacility
     * @return true if the object was removed from the collection.
     */
    public boolean removeAccessFacility(
            final chouette.schema.types.AccessFacilityEnumeration vAccessFacility) {
        boolean removed = _accessFacilityList.remove(vAccessFacility);
        return removed;
    }

    /**
     * Method removeAccessFacilityAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public chouette.schema.types.AccessFacilityEnumeration removeAccessFacilityAt(
            final int index) {
        java.lang.Object obj = this._accessFacilityList.remove(index);
        return (chouette.schema.types.AccessFacilityEnumeration) obj;
    }

    /**
     */
    public void removeAllAccessFacility(
    ) {
        this._accessFacilityList.clear();
    }

    /**
     */
    public void removeAllFareClassFacility(
    ) {
        this._fareClassFacilityList.clear();
    }

    /**
     */
    public void removeAllLuggageFacility(
    ) {
        this._luggageFacilityList.clear();
    }

    /**
     */
    public void removeAllMobilityFacility(
    ) {
        this._mobilityFacilityList.clear();
    }

    /**
     */
    public void removeAllNuisanceFacility(
    ) {
        this._nuisanceFacilityList.clear();
    }

    /**
     */
    public void removeAllPassengerCommsFacility(
    ) {
        this._passengerCommsFacilityList.clear();
    }

    /**
     */
    public void removeAllPassengerInformationFacility(
    ) {
        this._passengerInformationFacilityList.clear();
    }

    /**
     */
    public void removeAllRefreshmentFacility(
    ) {
        this._refreshmentFacilityList.clear();
    }

    /**
     */
    public void removeAllSanitaryFacility(
    ) {
        this._sanitaryFacilityList.clear();
    }

    /**
     */
    public void removeAllTicketingFacility(
    ) {
        this._ticketingFacilityList.clear();
    }

    /**
     * Method removeFareClassFacility.
     * 
     * @param vFareClassFacility
     * @return true if the object was removed from the collection.
     */
    public boolean removeFareClassFacility(
            final chouette.schema.types.FareClassFacilityEnumeration vFareClassFacility) {
        boolean removed = _fareClassFacilityList.remove(vFareClassFacility);
        return removed;
    }

    /**
     * Method removeFareClassFacilityAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public chouette.schema.types.FareClassFacilityEnumeration removeFareClassFacilityAt(
            final int index) {
        java.lang.Object obj = this._fareClassFacilityList.remove(index);
        return (chouette.schema.types.FareClassFacilityEnumeration) obj;
    }

    /**
     * Method removeLuggageFacility.
     * 
     * @param vLuggageFacility
     * @return true if the object was removed from the collection.
     */
    public boolean removeLuggageFacility(
            final chouette.schema.types.LuggageFacilityEnumeration vLuggageFacility) {
        boolean removed = _luggageFacilityList.remove(vLuggageFacility);
        return removed;
    }

    /**
     * Method removeLuggageFacilityAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public chouette.schema.types.LuggageFacilityEnumeration removeLuggageFacilityAt(
            final int index) {
        java.lang.Object obj = this._luggageFacilityList.remove(index);
        return (chouette.schema.types.LuggageFacilityEnumeration) obj;
    }

    /**
     * Method removeMobilityFacility.
     * 
     * @param vMobilityFacility
     * @return true if the object was removed from the collection.
     */
    public boolean removeMobilityFacility(
            final chouette.schema.types.MobilityFacilityEnumeration vMobilityFacility) {
        boolean removed = _mobilityFacilityList.remove(vMobilityFacility);
        return removed;
    }

    /**
     * Method removeMobilityFacilityAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public chouette.schema.types.MobilityFacilityEnumeration removeMobilityFacilityAt(
            final int index) {
        java.lang.Object obj = this._mobilityFacilityList.remove(index);
        return (chouette.schema.types.MobilityFacilityEnumeration) obj;
    }

    /**
     * Method removeNuisanceFacility.
     * 
     * @param vNuisanceFacility
     * @return true if the object was removed from the collection.
     */
    public boolean removeNuisanceFacility(
            final chouette.schema.types.NuisanceFacilityEnumeration vNuisanceFacility) {
        boolean removed = _nuisanceFacilityList.remove(vNuisanceFacility);
        return removed;
    }

    /**
     * Method removeNuisanceFacilityAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public chouette.schema.types.NuisanceFacilityEnumeration removeNuisanceFacilityAt(
            final int index) {
        java.lang.Object obj = this._nuisanceFacilityList.remove(index);
        return (chouette.schema.types.NuisanceFacilityEnumeration) obj;
    }

    /**
     * Method removePassengerCommsFacility.
     * 
     * @param vPassengerCommsFacility
     * @return true if the object was removed from the collection.
     */
    public boolean removePassengerCommsFacility(
            final chouette.schema.types.PassengerCommsFacilityEnumeration vPassengerCommsFacility) {
        boolean removed = _passengerCommsFacilityList.remove(vPassengerCommsFacility);
        return removed;
    }

    /**
     * Method removePassengerCommsFacilityAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public chouette.schema.types.PassengerCommsFacilityEnumeration removePassengerCommsFacilityAt(
            final int index) {
        java.lang.Object obj = this._passengerCommsFacilityList.remove(index);
        return (chouette.schema.types.PassengerCommsFacilityEnumeration) obj;
    }

    /**
     * Method removePassengerInformationFacility.
     * 
     * @param vPassengerInformationFacility
     * @return true if the object was removed from the collection.
     */
    public boolean removePassengerInformationFacility(
            final chouette.schema.types.PassengerInformationFacilityEnumeration vPassengerInformationFacility) {
        boolean removed = _passengerInformationFacilityList.remove(vPassengerInformationFacility);
        return removed;
    }

    /**
     * Method removePassengerInformationFacilityAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public chouette.schema.types.PassengerInformationFacilityEnumeration removePassengerInformationFacilityAt(
            final int index) {
        java.lang.Object obj = this._passengerInformationFacilityList.remove(index);
        return (chouette.schema.types.PassengerInformationFacilityEnumeration) obj;
    }

    /**
     * Method removeRefreshmentFacility.
     * 
     * @param vRefreshmentFacility
     * @return true if the object was removed from the collection.
     */
    public boolean removeRefreshmentFacility(
            final chouette.schema.types.RefreshmentFacilityEnumeration vRefreshmentFacility) {
        boolean removed = _refreshmentFacilityList.remove(vRefreshmentFacility);
        return removed;
    }

    /**
     * Method removeRefreshmentFacilityAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public chouette.schema.types.RefreshmentFacilityEnumeration removeRefreshmentFacilityAt(
            final int index) {
        java.lang.Object obj = this._refreshmentFacilityList.remove(index);
        return (chouette.schema.types.RefreshmentFacilityEnumeration) obj;
    }

    /**
     * Method removeSanitaryFacility.
     * 
     * @param vSanitaryFacility
     * @return true if the object was removed from the collection.
     */
    public boolean removeSanitaryFacility(
            final chouette.schema.types.SanitaryFacilityEnumeration vSanitaryFacility) {
        boolean removed = _sanitaryFacilityList.remove(vSanitaryFacility);
        return removed;
    }

    /**
     * Method removeSanitaryFacilityAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public chouette.schema.types.SanitaryFacilityEnumeration removeSanitaryFacilityAt(
            final int index) {
        java.lang.Object obj = this._sanitaryFacilityList.remove(index);
        return (chouette.schema.types.SanitaryFacilityEnumeration) obj;
    }

    /**
     * Method removeTicketingFacility.
     * 
     * @param vTicketingFacility
     * @return true if the object was removed from the collection.
     */
    public boolean removeTicketingFacility(
            final chouette.schema.types.TicketingFacilityEnumeration vTicketingFacility) {
        boolean removed = _ticketingFacilityList.remove(vTicketingFacility);
        return removed;
    }

    /**
     * Method removeTicketingFacilityAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public chouette.schema.types.TicketingFacilityEnumeration removeTicketingFacilityAt(
            final int index) {
        java.lang.Object obj = this._ticketingFacilityList.remove(index);
        return (chouette.schema.types.TicketingFacilityEnumeration) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vAccessFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setAccessFacility(
            final int index,
            final chouette.schema.types.AccessFacilityEnumeration vAccessFacility)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._accessFacilityList.size()) {
            throw new IndexOutOfBoundsException("setAccessFacility: Index value '" + index + "' not in range [0.." + (this._accessFacilityList.size() - 1) + "]");
        }

        this._accessFacilityList.set(index, vAccessFacility);
    }

    /**
     * 
     * 
     * @param vAccessFacilityArray
     */
    public void setAccessFacility(
            final chouette.schema.types.AccessFacilityEnumeration[] vAccessFacilityArray) {
        //-- copy array
        _accessFacilityList.clear();

        for (int i = 0; i < vAccessFacilityArray.length; i++) {
                this._accessFacilityList.add(vAccessFacilityArray[i]);
        }
    }

    /**
     * Sets the value of '_accessFacilityList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vAccessFacilityList the Vector to copy.
     */
    public void setAccessFacility(
            final java.util.List<chouette.schema.types.AccessFacilityEnumeration> vAccessFacilityList) {
        // copy vector
        this._accessFacilityList.clear();

        this._accessFacilityList.addAll(vAccessFacilityList);
    }

    /**
     * Sets the value of '_accessFacilityList' by setting it to the
     * given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param accessFacilityList the Vector to set.
     */
    public void setAccessFacilityAsReference(
            final java.util.List<chouette.schema.types.AccessFacilityEnumeration> accessFacilityList) {
        this._accessFacilityList = accessFacilityList;
    }

    /**
     * 
     * 
     * @param index
     * @param vFareClassFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setFareClassFacility(
            final int index,
            final chouette.schema.types.FareClassFacilityEnumeration vFareClassFacility)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._fareClassFacilityList.size()) {
            throw new IndexOutOfBoundsException("setFareClassFacility: Index value '" + index + "' not in range [0.." + (this._fareClassFacilityList.size() - 1) + "]");
        }

        this._fareClassFacilityList.set(index, vFareClassFacility);
    }

    /**
     * 
     * 
     * @param vFareClassFacilityArray
     */
    public void setFareClassFacility(
            final chouette.schema.types.FareClassFacilityEnumeration[] vFareClassFacilityArray) {
        //-- copy array
        _fareClassFacilityList.clear();

        for (int i = 0; i < vFareClassFacilityArray.length; i++) {
                this._fareClassFacilityList.add(vFareClassFacilityArray[i]);
        }
    }

    /**
     * Sets the value of '_fareClassFacilityList' by copying the
     * given Vector. All elements will be checked for type safety.
     * 
     * @param vFareClassFacilityList the Vector to copy.
     */
    public void setFareClassFacility(
            final java.util.List<chouette.schema.types.FareClassFacilityEnumeration> vFareClassFacilityList) {
        // copy vector
        this._fareClassFacilityList.clear();

        this._fareClassFacilityList.addAll(vFareClassFacilityList);
    }

    /**
     * Sets the value of '_fareClassFacilityList' by setting it to
     * the given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param fareClassFacilityList the Vector to set.
     */
    public void setFareClassFacilityAsReference(
            final java.util.List<chouette.schema.types.FareClassFacilityEnumeration> fareClassFacilityList) {
        this._fareClassFacilityList = fareClassFacilityList;
    }

    /**
     * 
     * 
     * @param index
     * @param vLuggageFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setLuggageFacility(
            final int index,
            final chouette.schema.types.LuggageFacilityEnumeration vLuggageFacility)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._luggageFacilityList.size()) {
            throw new IndexOutOfBoundsException("setLuggageFacility: Index value '" + index + "' not in range [0.." + (this._luggageFacilityList.size() - 1) + "]");
        }

        this._luggageFacilityList.set(index, vLuggageFacility);
    }

    /**
     * 
     * 
     * @param vLuggageFacilityArray
     */
    public void setLuggageFacility(
            final chouette.schema.types.LuggageFacilityEnumeration[] vLuggageFacilityArray) {
        //-- copy array
        _luggageFacilityList.clear();

        for (int i = 0; i < vLuggageFacilityArray.length; i++) {
                this._luggageFacilityList.add(vLuggageFacilityArray[i]);
        }
    }

    /**
     * Sets the value of '_luggageFacilityList' by copying the
     * given Vector. All elements will be checked for type safety.
     * 
     * @param vLuggageFacilityList the Vector to copy.
     */
    public void setLuggageFacility(
            final java.util.List<chouette.schema.types.LuggageFacilityEnumeration> vLuggageFacilityList) {
        // copy vector
        this._luggageFacilityList.clear();

        this._luggageFacilityList.addAll(vLuggageFacilityList);
    }

    /**
     * Sets the value of '_luggageFacilityList' by setting it to
     * the given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param luggageFacilityList the Vector to set.
     */
    public void setLuggageFacilityAsReference(
            final java.util.List<chouette.schema.types.LuggageFacilityEnumeration> luggageFacilityList) {
        this._luggageFacilityList = luggageFacilityList;
    }

    /**
     * 
     * 
     * @param index
     * @param vMobilityFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setMobilityFacility(
            final int index,
            final chouette.schema.types.MobilityFacilityEnumeration vMobilityFacility)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._mobilityFacilityList.size()) {
            throw new IndexOutOfBoundsException("setMobilityFacility: Index value '" + index + "' not in range [0.." + (this._mobilityFacilityList.size() - 1) + "]");
        }

        this._mobilityFacilityList.set(index, vMobilityFacility);
    }

    /**
     * 
     * 
     * @param vMobilityFacilityArray
     */
    public void setMobilityFacility(
            final chouette.schema.types.MobilityFacilityEnumeration[] vMobilityFacilityArray) {
        //-- copy array
        _mobilityFacilityList.clear();

        for (int i = 0; i < vMobilityFacilityArray.length; i++) {
                this._mobilityFacilityList.add(vMobilityFacilityArray[i]);
        }
    }

    /**
     * Sets the value of '_mobilityFacilityList' by copying the
     * given Vector. All elements will be checked for type safety.
     * 
     * @param vMobilityFacilityList the Vector to copy.
     */
    public void setMobilityFacility(
            final java.util.List<chouette.schema.types.MobilityFacilityEnumeration> vMobilityFacilityList) {
        // copy vector
        this._mobilityFacilityList.clear();

        this._mobilityFacilityList.addAll(vMobilityFacilityList);
    }

    /**
     * Sets the value of '_mobilityFacilityList' by setting it to
     * the given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param mobilityFacilityList the Vector to set.
     */
    public void setMobilityFacilityAsReference(
            final java.util.List<chouette.schema.types.MobilityFacilityEnumeration> mobilityFacilityList) {
        this._mobilityFacilityList = mobilityFacilityList;
    }

    /**
     * 
     * 
     * @param index
     * @param vNuisanceFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setNuisanceFacility(
            final int index,
            final chouette.schema.types.NuisanceFacilityEnumeration vNuisanceFacility)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._nuisanceFacilityList.size()) {
            throw new IndexOutOfBoundsException("setNuisanceFacility: Index value '" + index + "' not in range [0.." + (this._nuisanceFacilityList.size() - 1) + "]");
        }

        this._nuisanceFacilityList.set(index, vNuisanceFacility);
    }

    /**
     * 
     * 
     * @param vNuisanceFacilityArray
     */
    public void setNuisanceFacility(
            final chouette.schema.types.NuisanceFacilityEnumeration[] vNuisanceFacilityArray) {
        //-- copy array
        _nuisanceFacilityList.clear();

        for (int i = 0; i < vNuisanceFacilityArray.length; i++) {
                this._nuisanceFacilityList.add(vNuisanceFacilityArray[i]);
        }
    }

    /**
     * Sets the value of '_nuisanceFacilityList' by copying the
     * given Vector. All elements will be checked for type safety.
     * 
     * @param vNuisanceFacilityList the Vector to copy.
     */
    public void setNuisanceFacility(
            final java.util.List<chouette.schema.types.NuisanceFacilityEnumeration> vNuisanceFacilityList) {
        // copy vector
        this._nuisanceFacilityList.clear();

        this._nuisanceFacilityList.addAll(vNuisanceFacilityList);
    }

    /**
     * Sets the value of '_nuisanceFacilityList' by setting it to
     * the given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param nuisanceFacilityList the Vector to set.
     */
    public void setNuisanceFacilityAsReference(
            final java.util.List<chouette.schema.types.NuisanceFacilityEnumeration> nuisanceFacilityList) {
        this._nuisanceFacilityList = nuisanceFacilityList;
    }

    /**
     * 
     * 
     * @param index
     * @param vPassengerCommsFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setPassengerCommsFacility(
            final int index,
            final chouette.schema.types.PassengerCommsFacilityEnumeration vPassengerCommsFacility)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._passengerCommsFacilityList.size()) {
            throw new IndexOutOfBoundsException("setPassengerCommsFacility: Index value '" + index + "' not in range [0.." + (this._passengerCommsFacilityList.size() - 1) + "]");
        }

        this._passengerCommsFacilityList.set(index, vPassengerCommsFacility);
    }

    /**
     * 
     * 
     * @param vPassengerCommsFacilityArray
     */
    public void setPassengerCommsFacility(
            final chouette.schema.types.PassengerCommsFacilityEnumeration[] vPassengerCommsFacilityArray) {
        //-- copy array
        _passengerCommsFacilityList.clear();

        for (int i = 0; i < vPassengerCommsFacilityArray.length; i++) {
                this._passengerCommsFacilityList.add(vPassengerCommsFacilityArray[i]);
        }
    }

    /**
     * Sets the value of '_passengerCommsFacilityList' by copying
     * the given Vector. All elements will be checked for type
     * safety.
     * 
     * @param vPassengerCommsFacilityList the Vector to copy.
     */
    public void setPassengerCommsFacility(
            final java.util.List<chouette.schema.types.PassengerCommsFacilityEnumeration> vPassengerCommsFacilityList) {
        // copy vector
        this._passengerCommsFacilityList.clear();

        this._passengerCommsFacilityList.addAll(vPassengerCommsFacilityList);
    }

    /**
     * Sets the value of '_passengerCommsFacilityList' by setting
     * it to the given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param passengerCommsFacilityList the Vector to set.
     */
    public void setPassengerCommsFacilityAsReference(
            final java.util.List<chouette.schema.types.PassengerCommsFacilityEnumeration> passengerCommsFacilityList) {
        this._passengerCommsFacilityList = passengerCommsFacilityList;
    }

    /**
     * 
     * 
     * @param index
     * @param vPassengerInformationFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setPassengerInformationFacility(
            final int index,
            final chouette.schema.types.PassengerInformationFacilityEnumeration vPassengerInformationFacility)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._passengerInformationFacilityList.size()) {
            throw new IndexOutOfBoundsException("setPassengerInformationFacility: Index value '" + index + "' not in range [0.." + (this._passengerInformationFacilityList.size() - 1) + "]");
        }

        this._passengerInformationFacilityList.set(index, vPassengerInformationFacility);
    }

    /**
     * 
     * 
     * @param vPassengerInformationFacilityArray
     */
    public void setPassengerInformationFacility(
            final chouette.schema.types.PassengerInformationFacilityEnumeration[] vPassengerInformationFacilityArray) {
        //-- copy array
        _passengerInformationFacilityList.clear();

        for (int i = 0; i < vPassengerInformationFacilityArray.length; i++) {
                this._passengerInformationFacilityList.add(vPassengerInformationFacilityArray[i]);
        }
    }

    /**
     * Sets the value of '_passengerInformationFacilityList' by
     * copying the given Vector. All elements will be checked for
     * type safety.
     * 
     * @param vPassengerInformationFacilityList the Vector to copy.
     */
    public void setPassengerInformationFacility(
            final java.util.List<chouette.schema.types.PassengerInformationFacilityEnumeration> vPassengerInformationFacilityList) {
        // copy vector
        this._passengerInformationFacilityList.clear();

        this._passengerInformationFacilityList.addAll(vPassengerInformationFacilityList);
    }

    /**
     * Sets the value of '_passengerInformationFacilityList' by
     * setting it to the given Vector. No type checking is
     * performed.
     * @deprecated
     * 
     * @param passengerInformationFacilityList the Vector to set.
     */
    public void setPassengerInformationFacilityAsReference(
            final java.util.List<chouette.schema.types.PassengerInformationFacilityEnumeration> passengerInformationFacilityList) {
        this._passengerInformationFacilityList = passengerInformationFacilityList;
    }

    /**
     * 
     * 
     * @param index
     * @param vRefreshmentFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setRefreshmentFacility(
            final int index,
            final chouette.schema.types.RefreshmentFacilityEnumeration vRefreshmentFacility)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._refreshmentFacilityList.size()) {
            throw new IndexOutOfBoundsException("setRefreshmentFacility: Index value '" + index + "' not in range [0.." + (this._refreshmentFacilityList.size() - 1) + "]");
        }

        this._refreshmentFacilityList.set(index, vRefreshmentFacility);
    }

    /**
     * 
     * 
     * @param vRefreshmentFacilityArray
     */
    public void setRefreshmentFacility(
            final chouette.schema.types.RefreshmentFacilityEnumeration[] vRefreshmentFacilityArray) {
        //-- copy array
        _refreshmentFacilityList.clear();

        for (int i = 0; i < vRefreshmentFacilityArray.length; i++) {
                this._refreshmentFacilityList.add(vRefreshmentFacilityArray[i]);
        }
    }

    /**
     * Sets the value of '_refreshmentFacilityList' by copying the
     * given Vector. All elements will be checked for type safety.
     * 
     * @param vRefreshmentFacilityList the Vector to copy.
     */
    public void setRefreshmentFacility(
            final java.util.List<chouette.schema.types.RefreshmentFacilityEnumeration> vRefreshmentFacilityList) {
        // copy vector
        this._refreshmentFacilityList.clear();

        this._refreshmentFacilityList.addAll(vRefreshmentFacilityList);
    }

    /**
     * Sets the value of '_refreshmentFacilityList' by setting it
     * to the given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param refreshmentFacilityList the Vector to set.
     */
    public void setRefreshmentFacilityAsReference(
            final java.util.List<chouette.schema.types.RefreshmentFacilityEnumeration> refreshmentFacilityList) {
        this._refreshmentFacilityList = refreshmentFacilityList;
    }

    /**
     * 
     * 
     * @param index
     * @param vSanitaryFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setSanitaryFacility(
            final int index,
            final chouette.schema.types.SanitaryFacilityEnumeration vSanitaryFacility)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._sanitaryFacilityList.size()) {
            throw new IndexOutOfBoundsException("setSanitaryFacility: Index value '" + index + "' not in range [0.." + (this._sanitaryFacilityList.size() - 1) + "]");
        }

        this._sanitaryFacilityList.set(index, vSanitaryFacility);
    }

    /**
     * 
     * 
     * @param vSanitaryFacilityArray
     */
    public void setSanitaryFacility(
            final chouette.schema.types.SanitaryFacilityEnumeration[] vSanitaryFacilityArray) {
        //-- copy array
        _sanitaryFacilityList.clear();

        for (int i = 0; i < vSanitaryFacilityArray.length; i++) {
                this._sanitaryFacilityList.add(vSanitaryFacilityArray[i]);
        }
    }

    /**
     * Sets the value of '_sanitaryFacilityList' by copying the
     * given Vector. All elements will be checked for type safety.
     * 
     * @param vSanitaryFacilityList the Vector to copy.
     */
    public void setSanitaryFacility(
            final java.util.List<chouette.schema.types.SanitaryFacilityEnumeration> vSanitaryFacilityList) {
        // copy vector
        this._sanitaryFacilityList.clear();

        this._sanitaryFacilityList.addAll(vSanitaryFacilityList);
    }

    /**
     * Sets the value of '_sanitaryFacilityList' by setting it to
     * the given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param sanitaryFacilityList the Vector to set.
     */
    public void setSanitaryFacilityAsReference(
            final java.util.List<chouette.schema.types.SanitaryFacilityEnumeration> sanitaryFacilityList) {
        this._sanitaryFacilityList = sanitaryFacilityList;
    }

    /**
     * 
     * 
     * @param index
     * @param vTicketingFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setTicketingFacility(
            final int index,
            final chouette.schema.types.TicketingFacilityEnumeration vTicketingFacility)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._ticketingFacilityList.size()) {
            throw new IndexOutOfBoundsException("setTicketingFacility: Index value '" + index + "' not in range [0.." + (this._ticketingFacilityList.size() - 1) + "]");
        }

        this._ticketingFacilityList.set(index, vTicketingFacility);
    }

    /**
     * 
     * 
     * @param vTicketingFacilityArray
     */
    public void setTicketingFacility(
            final chouette.schema.types.TicketingFacilityEnumeration[] vTicketingFacilityArray) {
        //-- copy array
        _ticketingFacilityList.clear();

        for (int i = 0; i < vTicketingFacilityArray.length; i++) {
                this._ticketingFacilityList.add(vTicketingFacilityArray[i]);
        }
    }

    /**
     * Sets the value of '_ticketingFacilityList' by copying the
     * given Vector. All elements will be checked for type safety.
     * 
     * @param vTicketingFacilityList the Vector to copy.
     */
    public void setTicketingFacility(
            final java.util.List<chouette.schema.types.TicketingFacilityEnumeration> vTicketingFacilityList) {
        // copy vector
        this._ticketingFacilityList.clear();

        this._ticketingFacilityList.addAll(vTicketingFacilityList);
    }

    /**
     * Sets the value of '_ticketingFacilityList' by setting it to
     * the given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param ticketingFacilityList the Vector to set.
     */
    public void setTicketingFacilityAsReference(
            final java.util.List<chouette.schema.types.TicketingFacilityEnumeration> ticketingFacilityList) {
        this._ticketingFacilityList = ticketingFacilityList;
    }

    /**
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled chouette.schema.CommonFacilityGroup
     */
    public static chouette.schema.CommonFacilityGroup unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (chouette.schema.CommonFacilityGroup) org.exolab.castor.xml.Unmarshaller.unmarshal(chouette.schema.CommonFacilityGroup.class, reader);
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
