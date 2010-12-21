/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * Description of the features of any of the available facilities
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class AllFacilitiesFeatureStructureType extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Internal choice value storage
     */
    private java.lang.Object _choiceValue;

    /**
     * Classification of Access Facility
     */
    private chouette.schema.types.AccessFacilityEnumeration _accessFacility;

    /**
     * Classification of Accomodation Facility type - Tpeg pti23.
     */
    private chouette.schema.types.AccommodationFacilityEnumeration _accommodationFacility;

    /**
     * Classification of Assistance Facility
     */
    private chouette.schema.types.AssistanceFacilityEnumeration _assistanceFacility;

    /**
     * Classification of FareClass Facility type - Tpeg pti23.
     */
    private chouette.schema.types.FareClassFacilityEnumeration _fareClassFacility;

    /**
     * Classification of Hire Facility
     */
    private chouette.schema.types.HireFacilityEnumeration _hireFacility;

    /**
     * Classification of Luggage Facility type - Tpeg pti23.
     */
    private chouette.schema.types.LuggageFacilityEnumeration _luggageFacility;

    /**
     * Classification of Mobility Facility type - Tpeg pti23.
     */
    private chouette.schema.types.MobilityFacilityEnumeration _mobilityFacility;

    /**
     * Classification of Nuisance Facility type - Tpeg pti23.
     */
    private chouette.schema.types.NuisanceFacilityEnumeration _nuisanceFacility;

    /**
     * Classification of Access Facility
     */
    private chouette.schema.types.ParkingFacilityEnumeration _parkingFacility;

    /**
     * Classification of PassengerComms Facility type - Tpeg pti23.
     */
    private chouette.schema.types.PassengerCommsFacilityEnumeration _passengerCommsFacility;

    /**
     * Classification of PassengerInfo Facility type - Tpeg pti23.
     */
    private chouette.schema.types.PassengerInformationFacilityEnumeration _passengerInformationFacility;

    /**
     * Classification of Refreshment Facility type - Tpeg pti23.
     */
    private chouette.schema.types.RefreshmentFacilityEnumeration _refreshmentFacility;

    /**
     * Classification of Reserved Space Facility
     */
    private chouette.schema.types.ReservedSpaceFacilityEnumeration _reservedSpaceFacility = chouette.schema.types.ReservedSpaceFacilityEnumeration.fromValue("unknown");

    /**
     * Classification of Retail Facility
     */
    private chouette.schema.types.RetailFacilityEnumeration _retailFacility = chouette.schema.types.RetailFacilityEnumeration.fromValue("unknown");

    /**
     * Classification of Sanitary Facility type - Tpeg pti23.
     */
    private chouette.schema.types.SanitaryFacilityEnumeration _sanitaryFacility;

    /**
     * Classification of Ticketing Facility type - Tpeg pti23.
     */
    private chouette.schema.types.TicketingFacilityEnumeration _ticketingFacility;


      //----------------/
     //- Constructors -/
    //----------------/

    public AllFacilitiesFeatureStructureType() {
        super();
        setReservedSpaceFacility(chouette.schema.types.ReservedSpaceFacilityEnumeration.fromValue("unknown"));
        setRetailFacility(chouette.schema.types.RetailFacilityEnumeration.fromValue("unknown"));
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'accessFacility'. The field
     * 'accessFacility' has the following description:
     * Classification of Access Facility
     * 
     * @return the value of field 'AccessFacility'.
     */
    public chouette.schema.types.AccessFacilityEnumeration getAccessFacility(
    ) {
        return this._accessFacility;
    }

    /**
     * Returns the value of field 'accommodationFacility'. The
     * field 'accommodationFacility' has the following description:
     * Classification of Accomodation Facility type - Tpeg pti23.
     * 
     * @return the value of field 'AccommodationFacility'.
     */
    public chouette.schema.types.AccommodationFacilityEnumeration getAccommodationFacility(
    ) {
        return this._accommodationFacility;
    }

    /**
     * Returns the value of field 'assistanceFacility'. The field
     * 'assistanceFacility' has the following description:
     * Classification of Assistance Facility
     * 
     * @return the value of field 'AssistanceFacility'.
     */
    public chouette.schema.types.AssistanceFacilityEnumeration getAssistanceFacility(
    ) {
        return this._assistanceFacility;
    }

    /**
     * Returns the value of field 'choiceValue'. The field
     * 'choiceValue' has the following description: Internal choice
     * value storage
     * 
     * @return the value of field 'ChoiceValue'.
     */
    public java.lang.Object getChoiceValue(
    ) {
        return this._choiceValue;
    }

    /**
     * Returns the value of field 'fareClassFacility'. The field
     * 'fareClassFacility' has the following description:
     * Classification of FareClass Facility type - Tpeg pti23.
     * 
     * @return the value of field 'FareClassFacility'.
     */
    public chouette.schema.types.FareClassFacilityEnumeration getFareClassFacility(
    ) {
        return this._fareClassFacility;
    }

    /**
     * Returns the value of field 'hireFacility'. The field
     * 'hireFacility' has the following description: Classification
     * of Hire Facility
     * 
     * @return the value of field 'HireFacility'.
     */
    public chouette.schema.types.HireFacilityEnumeration getHireFacility(
    ) {
        return this._hireFacility;
    }

    /**
     * Returns the value of field 'luggageFacility'. The field
     * 'luggageFacility' has the following description:
     * Classification of Luggage Facility type - Tpeg pti23.
     * 
     * @return the value of field 'LuggageFacility'.
     */
    public chouette.schema.types.LuggageFacilityEnumeration getLuggageFacility(
    ) {
        return this._luggageFacility;
    }

    /**
     * Returns the value of field 'mobilityFacility'. The field
     * 'mobilityFacility' has the following description:
     * Classification of Mobility Facility type - Tpeg pti23.
     * 
     * @return the value of field 'MobilityFacility'.
     */
    public chouette.schema.types.MobilityFacilityEnumeration getMobilityFacility(
    ) {
        return this._mobilityFacility;
    }

    /**
     * Returns the value of field 'nuisanceFacility'. The field
     * 'nuisanceFacility' has the following description:
     * Classification of Nuisance Facility type - Tpeg pti23.
     * 
     * @return the value of field 'NuisanceFacility'.
     */
    public chouette.schema.types.NuisanceFacilityEnumeration getNuisanceFacility(
    ) {
        return this._nuisanceFacility;
    }

    /**
     * Returns the value of field 'parkingFacility'. The field
     * 'parkingFacility' has the following description:
     * Classification of Access Facility
     * 
     * @return the value of field 'ParkingFacility'.
     */
    public chouette.schema.types.ParkingFacilityEnumeration getParkingFacility(
    ) {
        return this._parkingFacility;
    }

    /**
     * Returns the value of field 'passengerCommsFacility'. The
     * field 'passengerCommsFacility' has the following
     * description: Classification of PassengerComms Facility type
     * - Tpeg pti23.
     * 
     * @return the value of field 'PassengerCommsFacility'.
     */
    public chouette.schema.types.PassengerCommsFacilityEnumeration getPassengerCommsFacility(
    ) {
        return this._passengerCommsFacility;
    }

    /**
     * Returns the value of field 'passengerInformationFacility'.
     * The field 'passengerInformationFacility' has the following
     * description: Classification of PassengerInfo Facility type -
     * Tpeg pti23.
     * 
     * @return the value of field 'PassengerInformationFacility'.
     */
    public chouette.schema.types.PassengerInformationFacilityEnumeration getPassengerInformationFacility(
    ) {
        return this._passengerInformationFacility;
    }

    /**
     * Returns the value of field 'refreshmentFacility'. The field
     * 'refreshmentFacility' has the following description:
     * Classification of Refreshment Facility type - Tpeg pti23.
     * 
     * @return the value of field 'RefreshmentFacility'.
     */
    public chouette.schema.types.RefreshmentFacilityEnumeration getRefreshmentFacility(
    ) {
        return this._refreshmentFacility;
    }

    /**
     * Returns the value of field 'reservedSpaceFacility'. The
     * field 'reservedSpaceFacility' has the following description:
     * Classification of Reserved Space Facility
     * 
     * @return the value of field 'ReservedSpaceFacility'.
     */
    public chouette.schema.types.ReservedSpaceFacilityEnumeration getReservedSpaceFacility(
    ) {
        return this._reservedSpaceFacility;
    }

    /**
     * Returns the value of field 'retailFacility'. The field
     * 'retailFacility' has the following description:
     * Classification of Retail Facility
     * 
     * @return the value of field 'RetailFacility'.
     */
    public chouette.schema.types.RetailFacilityEnumeration getRetailFacility(
    ) {
        return this._retailFacility;
    }

    /**
     * Returns the value of field 'sanitaryFacility'. The field
     * 'sanitaryFacility' has the following description:
     * Classification of Sanitary Facility type - Tpeg pti23.
     * 
     * @return the value of field 'SanitaryFacility'.
     */
    public chouette.schema.types.SanitaryFacilityEnumeration getSanitaryFacility(
    ) {
        return this._sanitaryFacility;
    }

    /**
     * Returns the value of field 'ticketingFacility'. The field
     * 'ticketingFacility' has the following description:
     * Classification of Ticketing Facility type - Tpeg pti23.
     * 
     * @return the value of field 'TicketingFacility'.
     */
    public chouette.schema.types.TicketingFacilityEnumeration getTicketingFacility(
    ) {
        return this._ticketingFacility;
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
     * Sets the value of field 'accessFacility'. The field
     * 'accessFacility' has the following description:
     * Classification of Access Facility
     * 
     * @param accessFacility the value of field 'accessFacility'.
     */
    public void setAccessFacility(
            final chouette.schema.types.AccessFacilityEnumeration accessFacility) {
        this._accessFacility = accessFacility;
        this._choiceValue = accessFacility;
    }

    /**
     * Sets the value of field 'accommodationFacility'. The field
     * 'accommodationFacility' has the following description:
     * Classification of Accomodation Facility type - Tpeg pti23.
     * 
     * @param accommodationFacility the value of field
     * 'accommodationFacility'.
     */
    public void setAccommodationFacility(
            final chouette.schema.types.AccommodationFacilityEnumeration accommodationFacility) {
        this._accommodationFacility = accommodationFacility;
        this._choiceValue = accommodationFacility;
    }

    /**
     * Sets the value of field 'assistanceFacility'. The field
     * 'assistanceFacility' has the following description:
     * Classification of Assistance Facility
     * 
     * @param assistanceFacility the value of field
     * 'assistanceFacility'.
     */
    public void setAssistanceFacility(
            final chouette.schema.types.AssistanceFacilityEnumeration assistanceFacility) {
        this._assistanceFacility = assistanceFacility;
        this._choiceValue = assistanceFacility;
    }

    /**
     * Sets the value of field 'fareClassFacility'. The field
     * 'fareClassFacility' has the following description:
     * Classification of FareClass Facility type - Tpeg pti23.
     * 
     * @param fareClassFacility the value of field
     * 'fareClassFacility'.
     */
    public void setFareClassFacility(
            final chouette.schema.types.FareClassFacilityEnumeration fareClassFacility) {
        this._fareClassFacility = fareClassFacility;
        this._choiceValue = fareClassFacility;
    }

    /**
     * Sets the value of field 'hireFacility'. The field
     * 'hireFacility' has the following description: Classification
     * of Hire Facility
     * 
     * @param hireFacility the value of field 'hireFacility'.
     */
    public void setHireFacility(
            final chouette.schema.types.HireFacilityEnumeration hireFacility) {
        this._hireFacility = hireFacility;
        this._choiceValue = hireFacility;
    }

    /**
     * Sets the value of field 'luggageFacility'. The field
     * 'luggageFacility' has the following description:
     * Classification of Luggage Facility type - Tpeg pti23.
     * 
     * @param luggageFacility the value of field 'luggageFacility'.
     */
    public void setLuggageFacility(
            final chouette.schema.types.LuggageFacilityEnumeration luggageFacility) {
        this._luggageFacility = luggageFacility;
        this._choiceValue = luggageFacility;
    }

    /**
     * Sets the value of field 'mobilityFacility'. The field
     * 'mobilityFacility' has the following description:
     * Classification of Mobility Facility type - Tpeg pti23.
     * 
     * @param mobilityFacility the value of field 'mobilityFacility'
     */
    public void setMobilityFacility(
            final chouette.schema.types.MobilityFacilityEnumeration mobilityFacility) {
        this._mobilityFacility = mobilityFacility;
        this._choiceValue = mobilityFacility;
    }

    /**
     * Sets the value of field 'nuisanceFacility'. The field
     * 'nuisanceFacility' has the following description:
     * Classification of Nuisance Facility type - Tpeg pti23.
     * 
     * @param nuisanceFacility the value of field 'nuisanceFacility'
     */
    public void setNuisanceFacility(
            final chouette.schema.types.NuisanceFacilityEnumeration nuisanceFacility) {
        this._nuisanceFacility = nuisanceFacility;
        this._choiceValue = nuisanceFacility;
    }

    /**
     * Sets the value of field 'parkingFacility'. The field
     * 'parkingFacility' has the following description:
     * Classification of Access Facility
     * 
     * @param parkingFacility the value of field 'parkingFacility'.
     */
    public void setParkingFacility(
            final chouette.schema.types.ParkingFacilityEnumeration parkingFacility) {
        this._parkingFacility = parkingFacility;
        this._choiceValue = parkingFacility;
    }

    /**
     * Sets the value of field 'passengerCommsFacility'. The field
     * 'passengerCommsFacility' has the following description:
     * Classification of PassengerComms Facility type - Tpeg pti23.
     * 
     * @param passengerCommsFacility the value of field
     * 'passengerCommsFacility'.
     */
    public void setPassengerCommsFacility(
            final chouette.schema.types.PassengerCommsFacilityEnumeration passengerCommsFacility) {
        this._passengerCommsFacility = passengerCommsFacility;
        this._choiceValue = passengerCommsFacility;
    }

    /**
     * Sets the value of field 'passengerInformationFacility'. The
     * field 'passengerInformationFacility' has the following
     * description: Classification of PassengerInfo Facility type -
     * Tpeg pti23.
     * 
     * @param passengerInformationFacility the value of field
     * 'passengerInformationFacility'.
     */
    public void setPassengerInformationFacility(
            final chouette.schema.types.PassengerInformationFacilityEnumeration passengerInformationFacility) {
        this._passengerInformationFacility = passengerInformationFacility;
        this._choiceValue = passengerInformationFacility;
    }

    /**
     * Sets the value of field 'refreshmentFacility'. The field
     * 'refreshmentFacility' has the following description:
     * Classification of Refreshment Facility type - Tpeg pti23.
     * 
     * @param refreshmentFacility the value of field
     * 'refreshmentFacility'.
     */
    public void setRefreshmentFacility(
            final chouette.schema.types.RefreshmentFacilityEnumeration refreshmentFacility) {
        this._refreshmentFacility = refreshmentFacility;
        this._choiceValue = refreshmentFacility;
    }

    /**
     * Sets the value of field 'reservedSpaceFacility'. The field
     * 'reservedSpaceFacility' has the following description:
     * Classification of Reserved Space Facility
     * 
     * @param reservedSpaceFacility the value of field
     * 'reservedSpaceFacility'.
     */
    public void setReservedSpaceFacility(
            final chouette.schema.types.ReservedSpaceFacilityEnumeration reservedSpaceFacility) {
        this._reservedSpaceFacility = reservedSpaceFacility;
        this._choiceValue = reservedSpaceFacility;
    }

    /**
     * Sets the value of field 'retailFacility'. The field
     * 'retailFacility' has the following description:
     * Classification of Retail Facility
     * 
     * @param retailFacility the value of field 'retailFacility'.
     */
    public void setRetailFacility(
            final chouette.schema.types.RetailFacilityEnumeration retailFacility) {
        this._retailFacility = retailFacility;
        this._choiceValue = retailFacility;
    }

    /**
     * Sets the value of field 'sanitaryFacility'. The field
     * 'sanitaryFacility' has the following description:
     * Classification of Sanitary Facility type - Tpeg pti23.
     * 
     * @param sanitaryFacility the value of field 'sanitaryFacility'
     */
    public void setSanitaryFacility(
            final chouette.schema.types.SanitaryFacilityEnumeration sanitaryFacility) {
        this._sanitaryFacility = sanitaryFacility;
        this._choiceValue = sanitaryFacility;
    }

    /**
     * Sets the value of field 'ticketingFacility'. The field
     * 'ticketingFacility' has the following description:
     * Classification of Ticketing Facility type - Tpeg pti23.
     * 
     * @param ticketingFacility the value of field
     * 'ticketingFacility'.
     */
    public void setTicketingFacility(
            final chouette.schema.types.TicketingFacilityEnumeration ticketingFacility) {
        this._ticketingFacility = ticketingFacility;
        this._choiceValue = ticketingFacility;
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
