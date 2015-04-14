package mobi.chouette.exchange.neptune.exporter.producer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobi.chouette.exchange.neptune.model.Facility;
import mobi.chouette.exchange.neptune.model.facility.AccessFacilityEnumeration;
import mobi.chouette.exchange.neptune.model.facility.AccommodationFacilityEnumeration;
import mobi.chouette.exchange.neptune.model.facility.AssistanceFacilityEnumeration;
import mobi.chouette.exchange.neptune.model.facility.FacilityFeature;
import mobi.chouette.exchange.neptune.model.facility.FareClassFacilityEnumeration;
import mobi.chouette.exchange.neptune.model.facility.HireFacilityEnumeration;
import mobi.chouette.exchange.neptune.model.facility.LuggageFacilityEnumeration;
import mobi.chouette.exchange.neptune.model.facility.MobilityFacilityEnumeration;
import mobi.chouette.exchange.neptune.model.facility.NuisanceFacilityEnumeration;
import mobi.chouette.exchange.neptune.model.facility.ParkingFacilityEnumeration;
import mobi.chouette.exchange.neptune.model.facility.PassengerCommsFacilityEnumeration;
import mobi.chouette.exchange.neptune.model.facility.PassengerInformationFacilityEnumeration;
import mobi.chouette.exchange.neptune.model.facility.RefreshmentFacilityEnumeration;
import mobi.chouette.exchange.neptune.model.facility.ReservedSpaceFacilityEnumeration;
import mobi.chouette.exchange.neptune.model.facility.RetailFacilityEnumeration;
import mobi.chouette.exchange.neptune.model.facility.SanitaryFacilityEnumeration;
import mobi.chouette.exchange.neptune.model.facility.TicketingFacilityEnumeration;
import mobi.chouette.model.type.LongLatTypeEnum;

import org.trident.schema.trident.AddressType;
import org.trident.schema.trident.ChouetteFacilityType;
import org.trident.schema.trident.ChouetteFacilityType.FacilityLocation;
import org.trident.schema.trident.LongLatTypeType;
import org.trident.schema.trident.ProjectedPointType;

import uk.org.siri.siri.AllFacilitiesFeatureStructure;

public class FacilityProducer extends
      AbstractJaxbNeptuneProducer<ChouetteFacilityType, Facility>
{
   private Map<Class<?>, Integer> facilityEnumMap;

   public void init()
   {
      facilityEnumMap = new HashMap<Class<?>, Integer>();
      facilityEnumMap.put(AccessFacilityEnumeration.class, Integer.valueOf(0));
      facilityEnumMap.put(AccommodationFacilityEnumeration.class,
            Integer.valueOf(1));
      facilityEnumMap.put(AssistanceFacilityEnumeration.class,
            Integer.valueOf(2));
      facilityEnumMap.put(FareClassFacilityEnumeration.class,
            Integer.valueOf(3));
      facilityEnumMap.put(HireFacilityEnumeration.class, Integer.valueOf(4));
      facilityEnumMap.put(LuggageFacilityEnumeration.class, Integer.valueOf(5));
      facilityEnumMap
            .put(MobilityFacilityEnumeration.class, Integer.valueOf(6));
      facilityEnumMap
            .put(NuisanceFacilityEnumeration.class, Integer.valueOf(7));
      facilityEnumMap.put(ParkingFacilityEnumeration.class, Integer.valueOf(8));
      facilityEnumMap.put(PassengerCommsFacilityEnumeration.class,
            Integer.valueOf(9));
      facilityEnumMap.put(PassengerInformationFacilityEnumeration.class,
            Integer.valueOf(10));
      facilityEnumMap.put(RefreshmentFacilityEnumeration.class,
            Integer.valueOf(11));
      facilityEnumMap.put(ReservedSpaceFacilityEnumeration.class,
            Integer.valueOf(12));
      facilityEnumMap.put(RetailFacilityEnumeration.class, Integer.valueOf(13));
      facilityEnumMap.put(SanitaryFacilityEnumeration.class,
            Integer.valueOf(14));
      facilityEnumMap.put(TicketingFacilityEnumeration.class,
            Integer.valueOf(15));
   }

   //@Override
   public ChouetteFacilityType produce(Facility facility, boolean addExtension)
   {
      ChouetteFacilityType jaxbFacility = tridentFactory
            .createChouetteFacilityType();

      //
      populateFromModel(jaxbFacility, facility);

      jaxbFacility.setComment(getNotEmptyString(facility.getComment()));
      jaxbFacility.setName(facility.getName());

      if (facility.getLongLatType() != null && facility.getLatitude() != null
            && facility.getLongitude() != null)
      {
         FacilityLocation jaxbLocation = tridentFactory
               .createChouetteFacilityTypeFacilityLocation();
         jaxbFacility.setFacilityLocation(jaxbLocation);
         jaxbLocation.setContainedIn(facility.getContainedIn());
         jaxbLocation.setLatitude(facility.getLatitude());
         jaxbLocation.setLongitude(facility.getLongitude());

         LongLatTypeEnum longLatType = facility.getLongLatType();
         try
         {
            jaxbLocation.setLongLatType(LongLatTypeType.fromValue(longLatType
                  .name()));
         } catch (IllegalArgumentException e)
         {
            // TODO generate report
         }

         if (facility.getCountryCode() != null
               || facility.getStreetName() != null)
         {
            AddressType jaxbAddress = tridentFactory.createAddressType();
            jaxbAddress.setCountryCode(getNotEmptyString(facility
                  .getCountryCode()));
            jaxbAddress.setStreetName(getNotEmptyString(facility
                  .getStreetName()));
            jaxbLocation.setAddress(jaxbAddress);
         }

         if (facility.getProjectionType() != null && facility.getX() != null
               && facility.getY() != null)
         {
            ProjectedPointType jaxbProjectedPoint = tridentFactory
                  .createProjectedPointType();
            jaxbProjectedPoint.setProjectionType(facility.getProjectionType());
            jaxbProjectedPoint.setX(facility.getX());
            jaxbProjectedPoint.setY(facility.getY());
            jaxbLocation.setProjectedPoint(jaxbProjectedPoint);
         }
      }

      if (facility.getConnectionLink() != null)
      {
         jaxbFacility.setConnectionLinkId(getNonEmptyObjectId(facility
               .getConnectionLink()));
      }
      if (facility.getLine() != null)
      {
         jaxbFacility.setLineId(getNonEmptyObjectId(facility.getLine()));
      }
      if (facility.getStopArea() != null)
      {
         jaxbFacility
               .setStopAreaId(getNonEmptyObjectId(facility.getStopArea()));
      }
      if (facility.getStopPoint() != null)
      {
         jaxbFacility.setStopPointId(getNonEmptyObjectId(facility
               .getStopPoint()));
      }

      jaxbFacility.setDescription(getNotEmptyString(facility.getDescription()));
      // FreeAccess optional
      if (facility.getFreeAccess() != null)
         jaxbFacility.setFreeAccess(facility.getFreeAccess().booleanValue());

      // FacilityFeature[1..n] mandatory
      List<FacilityFeature> features = facility.getFacilityFeatures();
      for (FacilityFeature feature : features)
      {
         AllFacilitiesFeatureStructure facilityFeature = siriFactory
               .createAllFacilitiesFeatureStructure();

         int type = facilityEnumMap.get(feature.getChoiceValue().getClass())
               .intValue();
         switch (type)
         {
         case 0:
            facilityFeature
                  .setAccessFacility(uk.org.siri.siri.AccessFacilityEnumeration
                        .fromValue(feature.getAccessFacility().toString()));
            break;
         case 1:
            facilityFeature
                  .setAccommodationFacility(uk.org.siri.siri.AccommodationFacilityEnumeration
                        .fromValue(feature.getAccommodationFacility().name()));
            break;
         case 2:
            facilityFeature
                  .setAssistanceFacility(uk.org.siri.siri.AssistanceFacilityEnumeration
                        .fromValue(feature.getAssistanceFacility().name()));
            break;
         case 3:
            facilityFeature
                  .setFareClassFacility(uk.org.siri.siri.FareClassFacilityEnumeration
                        .fromValue(feature.getFareClassFacility().name()));
            break;
         case 4:
            facilityFeature
                  .setHireFacility(uk.org.siri.siri.HireFacilityEnumeration
                        .fromValue(feature.getHireFacility().name()));
            break;
         case 5:
            facilityFeature
                  .setLuggageFacility(uk.org.siri.siri.LuggageFacilityEnumeration
                        .fromValue(feature.getLuggageFacility().name()));
            break;
         case 6:
            facilityFeature
                  .setMobilityFacility(uk.org.siri.siri.MobilityFacilityEnumeration
                        .fromValue(feature.getMobilityFacility().name()));
            break;
         case 7:
            facilityFeature
                  .setNuisanceFacility(uk.org.siri.siri.NuisanceFacilityEnumeration
                        .fromValue(feature.getNuisanceFacility().name()));
            break;
         case 8:
            facilityFeature
                  .setParkingFacility(uk.org.siri.siri.ParkingFacilityEnumeration
                        .fromValue(feature.getParkingFacility().name()));
            break;
         case 9:
            facilityFeature
                  .setPassengerCommsFacility(uk.org.siri.siri.PassengerCommsFacilityEnumeration
                        .fromValue(feature.getPassengerCommsFacility().name()));
            break;
         case 10:
            facilityFeature
                  .setPassengerInformationFacility(uk.org.siri.siri.PassengerInformationFacilityEnumeration
                        .fromValue(feature.getPassengerInformationFacility()
                              .name()));
            break;
         case 11:
            facilityFeature
                  .setRefreshmentFacility(uk.org.siri.siri.RefreshmentFacilityEnumeration
                        .fromValue(feature.getRefreshmentFacility().name()));
            break;
         case 12:
            facilityFeature
                  .setReservedSpaceFacility(uk.org.siri.siri.ReservedSpaceFacilityEnumeration
                        .fromValue(feature.getReservedSpaceFacility().name()));
            break;
         case 13:
            facilityFeature
                  .setRetailFacility(uk.org.siri.siri.RetailFacilityEnumeration
                        .fromValue(feature.getRetailFacility().name()));
            break;
         case 14:
            facilityFeature
                  .setSanitaryFacility(uk.org.siri.siri.SanitaryFacilityEnumeration
                        .fromValue(feature.getSanitaryFacility().name()));
            break;
         case 15:
            facilityFeature
                  .setTicketingFacility(uk.org.siri.siri.TicketingFacilityEnumeration
                        .fromValue(feature.getTicketingFacility().name()));
            break;
         }
         jaxbFacility.getFacilityFeature().add(facilityFeature);
      }

      return jaxbFacility;
   }

}
