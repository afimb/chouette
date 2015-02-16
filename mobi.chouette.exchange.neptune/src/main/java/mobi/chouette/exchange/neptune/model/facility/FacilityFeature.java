package mobi.chouette.exchange.neptune.model.facility;

import java.io.Serializable;

import lombok.Setter;

public class FacilityFeature implements Serializable
{

   private static final long serialVersionUID = 2770570120139346512L;

   private static final Class<?>[] enumTypes = {
         AccessFacilityEnumeration.class,
         AccommodationFacilityEnumeration.class,
         AssistanceFacilityEnumeration.class,
         FareClassFacilityEnumeration.class, HireFacilityEnumeration.class,
         LuggageFacilityEnumeration.class, MobilityFacilityEnumeration.class,
         NuisanceFacilityEnumeration.class, ParkingFacilityEnumeration.class,
         PassengerCommsFacilityEnumeration.class,
         PassengerInformationFacilityEnumeration.class,
         RefreshmentFacilityEnumeration.class,
         ReservedSpaceFacilityEnumeration.class,
         RetailFacilityEnumeration.class, SanitaryFacilityEnumeration.class,
         TicketingFacilityEnumeration.class };

   private int getEnumType()
   {
      for (int i = 0; i < enumTypes.length; i++)
      {
         if (choiceValue.getClass().equals(enumTypes[i]))
            return i;
      }
      return -1;
   }

   private int getOrdinal()
   {
      return ((Enum<?>) choiceValue).ordinal();
   }

   public int getChoiceCode()
   {
      return getEnumType() * 512 + getOrdinal();
   }

   public void setChoiceCode(int code)
   {
      int enumType = code / 512;
      int ordinal = code % 512;

      Class<?> c = enumTypes[enumType];
      try
      {
         Object[] values = (Object[]) c.getMethod("values").invoke(null);
         choiceValue = values[ordinal];

      } catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Internal choice value storage
    */
   @Setter
   private Object choiceValue;

   /**
    * Classification of Access Facility
    */
   private AccessFacilityEnumeration accessFacility;

   /**
    * Classification of Accomodation Facility type - Tpeg pti23.
    */
   private AccommodationFacilityEnumeration accommodationFacility;

   /**
    * Classification of Assistance Facility
    */
   private AssistanceFacilityEnumeration assistanceFacility;

   /**
    * Classification of FareClass Facility type - Tpeg pti23.
    */
   private FareClassFacilityEnumeration fareClassFacility;

   /**
    * Classification of Hire Facility
    */
   private HireFacilityEnumeration hireFacility;

   /**
    * Classification of Luggage Facility type - Tpeg pti23.
    */
   private LuggageFacilityEnumeration luggageFacility;

   /**
    * Classification of Mobility Facility type - Tpeg pti23.
    */
   private MobilityFacilityEnumeration mobilityFacility;

   /**
    * Classification of Nuisance Facility type - Tpeg pti23.
    */
   private NuisanceFacilityEnumeration nuisanceFacility;

   /**
    * Classification of Access Facility
    */
   private ParkingFacilityEnumeration parkingFacility;

   /**
    * Classification of PassengerComms Facility type - Tpeg pti23.
    */
   private PassengerCommsFacilityEnumeration passengerCommsFacility;

   /**
    * Classification of PassengerInfo Facility type - Tpeg pti23.
    */
   private PassengerInformationFacilityEnumeration passengerInformationFacility;

   /**
    * Classification of Refreshment Facility type - Tpeg pti23.
    */
   private RefreshmentFacilityEnumeration refreshmentFacility;

   /**
    * Classification of Reserved Space Facility
    */
   private ReservedSpaceFacilityEnumeration reservedSpaceFacility = ReservedSpaceFacilityEnumeration
         .fromValue("unknown");

   /**
    * Classification of Retail Facility
    */
   private RetailFacilityEnumeration retailFacility = RetailFacilityEnumeration
         .fromValue("unknown");

   /**
    * Classification of Sanitary Facility type - Tpeg pti23.
    */
   private SanitaryFacilityEnumeration sanitaryFacility;

   /**
    * Classification of Ticketing Facility type - Tpeg pti23.
    */
   private TicketingFacilityEnumeration ticketingFacility;

   // ----------------/
   // - Constructors -/
   // ----------------/

   public FacilityFeature()
   {
      super();
   }

   // -----------/
   // - Methods -/
   // -----------/

   /**
    * Returns the value of field 'accessFacility'. The field 'accessFacility'
    * has the following description: Classification of Access Facility
    * 
    * @return the value of field 'AccessFacility'.
    */
   public AccessFacilityEnumeration getAccessFacility()
   {
      return this.accessFacility;
   }

   /**
    * Returns the value of field 'accommodationFacility'. The field
    * 'accommodationFacility' has the following description: Classification of
    * Accomodation Facility type - Tpeg pti23.
    * 
    * @return the value of field 'AccommodationFacility'.
    */
   public AccommodationFacilityEnumeration getAccommodationFacility()
   {
      return this.accommodationFacility;
   }

   /**
    * Returns the value of field 'assistanceFacility'. The field
    * 'assistanceFacility' has the following description: Classification of
    * Assistance Facility
    * 
    * @return the value of field 'AssistanceFacility'.
    */
   public AssistanceFacilityEnumeration getAssistanceFacility()
   {
      return this.assistanceFacility;
   }

   /**
    * Returns the value of field 'choiceValue'. The field 'choiceValue' has the
    * following description: Internal choice value storage
    * 
    * @return the value of field 'ChoiceValue'.
    */
   public java.lang.Object getChoiceValue()
   {
      return this.choiceValue;
   }

   /**
    * Returns the value of field 'fareClassFacility'. The field
    * 'fareClassFacility' has the following description: Classification of
    * FareClass Facility type - Tpeg pti23.
    * 
    * @return the value of field 'FareClassFacility'.
    */
   public FareClassFacilityEnumeration getFareClassFacility()
   {
      return this.fareClassFacility;
   }

   /**
    * Returns the value of field 'hireFacility'. The field 'hireFacility' has
    * the following description: Classification of Hire Facility
    * 
    * @return the value of field 'HireFacility'.
    */
   public HireFacilityEnumeration getHireFacility()
   {
      return this.hireFacility;
   }

   /**
    * Returns the value of field 'luggageFacility'. The field 'luggageFacility'
    * has the following description: Classification of Luggage Facility type -
    * Tpeg pti23.
    * 
    * @return the value of field 'LuggageFacility'.
    */
   public LuggageFacilityEnumeration getLuggageFacility()
   {
      return this.luggageFacility;
   }

   /**
    * Returns the value of field 'mobilityFacility'. The field
    * 'mobilityFacility' has the following description: Classification of
    * Mobility Facility type - Tpeg pti23.
    * 
    * @return the value of field 'MobilityFacility'.
    */
   public MobilityFacilityEnumeration getMobilityFacility()
   {
      return this.mobilityFacility;
   }

   /**
    * Returns the value of field 'nuisanceFacility'. The field
    * 'nuisanceFacility' has the following description: Classification of
    * Nuisance Facility type - Tpeg pti23.
    * 
    * @return the value of field 'NuisanceFacility'.
    */
   public NuisanceFacilityEnumeration getNuisanceFacility()
   {
      return this.nuisanceFacility;
   }

   /**
    * Returns the value of field 'parkingFacility'. The field 'parkingFacility'
    * has the following description: Classification of Access Facility
    * 
    * @return the value of field 'ParkingFacility'.
    */
   public ParkingFacilityEnumeration getParkingFacility()
   {
      return this.parkingFacility;
   }

   /**
    * Returns the value of field 'passengerCommsFacility'. The field
    * 'passengerCommsFacility' has the following description: Classification of
    * PassengerComms Facility type - Tpeg pti23.
    * 
    * @return the value of field 'PassengerCommsFacility'.
    */
   public PassengerCommsFacilityEnumeration getPassengerCommsFacility()
   {
      return this.passengerCommsFacility;
   }

   /**
    * Returns the value of field 'passengerInformationFacility'. The field
    * 'passengerInformationFacility' has the following description:
    * Classification of PassengerInfo Facility type - Tpeg pti23.
    * 
    * @return the value of field 'PassengerInformationFacility'.
    */
   public PassengerInformationFacilityEnumeration getPassengerInformationFacility()
   {
      return this.passengerInformationFacility;
   }

   /**
    * Returns the value of field 'refreshmentFacility'. The field
    * 'refreshmentFacility' has the following description: Classification of
    * Refreshment Facility type - Tpeg pti23.
    * 
    * @return the value of field 'RefreshmentFacility'.
    */
   public RefreshmentFacilityEnumeration getRefreshmentFacility()
   {
      return this.refreshmentFacility;
   }

   /**
    * Returns the value of field 'reservedSpaceFacility'. The field
    * 'reservedSpaceFacility' has the following description: Classification of
    * Reserved Space Facility
    * 
    * @return the value of field 'ReservedSpaceFacility'.
    */
   public ReservedSpaceFacilityEnumeration getReservedSpaceFacility()
   {
      return this.reservedSpaceFacility;
   }

   /**
    * Returns the value of field 'retailFacility'. The field 'retailFacility'
    * has the following description: Classification of Retail Facility
    * 
    * @return the value of field 'RetailFacility'.
    */
   public RetailFacilityEnumeration getRetailFacility()
   {
      return this.retailFacility;
   }

   /**
    * Returns the value of field 'sanitaryFacility'. The field
    * 'sanitaryFacility' has the following description: Classification of
    * Sanitary Facility type - Tpeg pti23.
    * 
    * @return the value of field 'SanitaryFacility'.
    */
   public SanitaryFacilityEnumeration getSanitaryFacility()
   {
      return this.sanitaryFacility;
   }

   /**
    * Returns the value of field 'ticketingFacility'. The field
    * 'ticketingFacility' has the following description: Classification of
    * Ticketing Facility type - Tpeg pti23.
    * 
    * @return the value of field 'TicketingFacility'.
    */
   public TicketingFacilityEnumeration getTicketingFacility()
   {
      return this.ticketingFacility;
   }

   /**
    * Sets the value of field 'accessFacility'. The field 'accessFacility' has
    * the following description: Classification of Access Facility
    * 
    * @param accessFacility
    *           the value of field 'accessFacility'.
    */
   public void setAccessFacility(final AccessFacilityEnumeration accessFacility)
   {
      this.accessFacility = accessFacility;
      this.choiceValue = accessFacility;
   }

   /**
    * Sets the value of field 'accommodationFacility'. The field
    * 'accommodationFacility' has the following description: Classification of
    * Accomodation Facility type - Tpeg pti23.
    * 
    * @param accommodationFacility
    *           the value of field 'accommodationFacility'.
    */
   public void setAccommodationFacility(
         final AccommodationFacilityEnumeration accommodationFacility)
   {
      this.accommodationFacility = accommodationFacility;
      this.choiceValue = accommodationFacility;
   }

   /**
    * Sets the value of field 'assistanceFacility'. The field
    * 'assistanceFacility' has the following description: Classification of
    * Assistance Facility
    * 
    * @param assistanceFacility
    *           the value of field 'assistanceFacility'.
    */
   public void setAssistanceFacility(
         final AssistanceFacilityEnumeration assistanceFacility)
   {
      this.assistanceFacility = assistanceFacility;
      this.choiceValue = assistanceFacility;
   }

   /**
    * Sets the value of field 'fareClassFacility'. The field 'fareClassFacility'
    * has the following description: Classification of FareClass Facility type -
    * Tpeg pti23.
    * 
    * @param fareClassFacility
    *           the value of field 'fareClassFacility'.
    */
   public void setFareClassFacility(
         final FareClassFacilityEnumeration fareClassFacility)
   {
      this.fareClassFacility = fareClassFacility;
      this.choiceValue = fareClassFacility;
   }

   /**
    * Sets the value of field 'hireFacility'. The field 'hireFacility' has the
    * following description: Classification of Hire Facility
    * 
    * @param hireFacility
    *           the value of field 'hireFacility'.
    */
   public void setHireFacility(final HireFacilityEnumeration hireFacility)
   {
      this.hireFacility = hireFacility;
      this.choiceValue = hireFacility;
   }

   /**
    * Sets the value of field 'luggageFacility'. The field 'luggageFacility' has
    * the following description: Classification of Luggage Facility type - Tpeg
    * pti23.
    * 
    * @param luggageFacility
    *           the value of field 'luggageFacility'.
    */
   public void setLuggageFacility(
         final LuggageFacilityEnumeration luggageFacility)
   {
      this.luggageFacility = luggageFacility;
      this.choiceValue = luggageFacility;
   }

   /**
    * Sets the value of field 'mobilityFacility'. The field 'mobilityFacility'
    * has the following description: Classification of Mobility Facility type -
    * Tpeg pti23.
    * 
    * @param mobilityFacility
    *           the value of field 'mobilityFacility'
    */
   public void setMobilityFacility(
         final MobilityFacilityEnumeration mobilityFacility)
   {
      this.mobilityFacility = mobilityFacility;
      this.choiceValue = mobilityFacility;
   }

   /**
    * Sets the value of field 'nuisanceFacility'. The field 'nuisanceFacility'
    * has the following description: Classification of Nuisance Facility type -
    * Tpeg pti23.
    * 
    * @param nuisanceFacility
    *           the value of field 'nuisanceFacility'
    */
   public void setNuisanceFacility(
         final NuisanceFacilityEnumeration nuisanceFacility)
   {
      this.nuisanceFacility = nuisanceFacility;
      this.choiceValue = nuisanceFacility;
   }

   /**
    * Sets the value of field 'parkingFacility'. The field 'parkingFacility' has
    * the following description: Classification of Access Facility
    * 
    * @param parkingFacility
    *           the value of field 'parkingFacility'.
    */
   public void setParkingFacility(
         final ParkingFacilityEnumeration parkingFacility)
   {
      this.parkingFacility = parkingFacility;
      this.choiceValue = parkingFacility;
   }

   /**
    * Sets the value of field 'passengerCommsFacility'. The field
    * 'passengerCommsFacility' has the following description: Classification of
    * PassengerComms Facility type - Tpeg pti23.
    * 
    * @param passengerCommsFacility
    *           the value of field 'passengerCommsFacility'.
    */
   public void setPassengerCommsFacility(
         final PassengerCommsFacilityEnumeration passengerCommsFacility)
   {
      this.passengerCommsFacility = passengerCommsFacility;
      this.choiceValue = passengerCommsFacility;
   }

   /**
    * Sets the value of field 'passengerInformationFacility'. The field
    * 'passengerInformationFacility' has the following description:
    * Classification of PassengerInfo Facility type - Tpeg pti23.
    * 
    * @param passengerInformationFacility
    *           the value of field 'passengerInformationFacility'.
    */
   public void setPassengerInformationFacility(
         final PassengerInformationFacilityEnumeration passengerInformationFacility)
   {
      this.passengerInformationFacility = passengerInformationFacility;
      this.choiceValue = passengerInformationFacility;
   }

   /**
    * Sets the value of field 'refreshmentFacility'. The field
    * 'refreshmentFacility' has the following description: Classification of
    * Refreshment Facility type - Tpeg pti23.
    * 
    * @param refreshmentFacility
    *           the value of field 'refreshmentFacility'.
    */
   public void setRefreshmentFacility(
         final RefreshmentFacilityEnumeration refreshmentFacility)
   {
      this.refreshmentFacility = refreshmentFacility;
      this.choiceValue = refreshmentFacility;
   }

   /**
    * Sets the value of field 'reservedSpaceFacility'. The field
    * 'reservedSpaceFacility' has the following description: Classification of
    * Reserved Space Facility
    * 
    * @param reservedSpaceFacility
    *           the value of field 'reservedSpaceFacility'.
    */
   public void setReservedSpaceFacility(
         final ReservedSpaceFacilityEnumeration reservedSpaceFacility)
   {
      this.reservedSpaceFacility = reservedSpaceFacility;
      this.choiceValue = reservedSpaceFacility;
   }

   /**
    * Sets the value of field 'retailFacility'. The field 'retailFacility' has
    * the following description: Classification of Retail Facility
    * 
    * @param retailFacility
    *           the value of field 'retailFacility'.
    */
   public void setRetailFacility(final RetailFacilityEnumeration retailFacility)
   {
      this.retailFacility = retailFacility;
      this.choiceValue = retailFacility;
   }

   /**
    * Sets the value of field 'sanitaryFacility'. The field 'sanitaryFacility'
    * has the following description: Classification of Sanitary Facility type -
    * Tpeg pti23.
    * 
    * @param sanitaryFacility
    *           the value of field 'sanitaryFacility'
    */
   public void setSanitaryFacility(
         final SanitaryFacilityEnumeration sanitaryFacility)
   {
      this.sanitaryFacility = sanitaryFacility;
      this.choiceValue = sanitaryFacility;
   }

   /**
    * Sets the value of field 'ticketingFacility'. The field 'ticketingFacility'
    * has the following description: Classification of Ticketing Facility type -
    * Tpeg pti23.
    * 
    * @param ticketingFacility
    *           the value of field 'ticketingFacility'.
    */
   public void setTicketingFacility(
         final TicketingFacilityEnumeration ticketingFacility)
   {
      this.ticketingFacility = ticketingFacility;
      this.choiceValue = ticketingFacility;
   }

}
