package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chouette.schema.ChouetteFacilityTypeChoice;
import chouette.schema.types.LongLatTypeType;
import fr.certu.chouette.model.neptune.Facility;
import fr.certu.chouette.model.neptune.type.Address;
import fr.certu.chouette.model.neptune.type.FacilityLocation;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import fr.certu.chouette.model.neptune.type.ProjectedPoint;
import fr.certu.chouette.model.neptune.type.facility.AccessFacilityEnumeration;
import fr.certu.chouette.model.neptune.type.facility.AccommodationFacilityEnumeration;
import fr.certu.chouette.model.neptune.type.facility.AssistanceFacilityEnumeration;
import fr.certu.chouette.model.neptune.type.facility.FacilityFeature;
import fr.certu.chouette.model.neptune.type.facility.FareClassFacilityEnumeration;
import fr.certu.chouette.model.neptune.type.facility.HireFacilityEnumeration;
import fr.certu.chouette.model.neptune.type.facility.LuggageFacilityEnumeration;
import fr.certu.chouette.model.neptune.type.facility.MobilityFacilityEnumeration;
import fr.certu.chouette.model.neptune.type.facility.NuisanceFacilityEnumeration;
import fr.certu.chouette.model.neptune.type.facility.ParkingFacilityEnumeration;
import fr.certu.chouette.model.neptune.type.facility.PassengerCommsFacilityEnumeration;
import fr.certu.chouette.model.neptune.type.facility.PassengerInformationFacilityEnumeration;
import fr.certu.chouette.model.neptune.type.facility.RefreshmentFacilityEnumeration;
import fr.certu.chouette.model.neptune.type.facility.ReservedSpaceFacilityEnumeration;
import fr.certu.chouette.model.neptune.type.facility.RetailFacilityEnumeration;
import fr.certu.chouette.model.neptune.type.facility.SanitaryFacilityEnumeration;
import fr.certu.chouette.model.neptune.type.facility.TicketingFacilityEnumeration;

public class FacilityProducer extends AbstractCastorNeptuneProducer<chouette.schema.Facility, Facility>
{
   private Map<Class<?>,Integer> facilityEnumMap;

   public void init()
   {
      facilityEnumMap = new HashMap<Class<?>, Integer>();
      facilityEnumMap.put(AccessFacilityEnumeration.class,Integer.valueOf(0));
      facilityEnumMap.put(AccommodationFacilityEnumeration.class,Integer.valueOf(1));
      facilityEnumMap.put(AssistanceFacilityEnumeration.class,Integer.valueOf(2));
      facilityEnumMap.put(FareClassFacilityEnumeration.class,Integer.valueOf(3));
      facilityEnumMap.put(HireFacilityEnumeration.class, Integer.valueOf(4));
      facilityEnumMap.put(LuggageFacilityEnumeration.class, Integer.valueOf(5));
      facilityEnumMap.put(MobilityFacilityEnumeration.class,Integer.valueOf(6));
      facilityEnumMap.put(NuisanceFacilityEnumeration.class, Integer.valueOf(7));
      facilityEnumMap.put(ParkingFacilityEnumeration.class, Integer.valueOf(8));
      facilityEnumMap.put(PassengerCommsFacilityEnumeration.class, Integer.valueOf(9));
      facilityEnumMap.put(PassengerInformationFacilityEnumeration.class,Integer.valueOf(10));
      facilityEnumMap.put(RefreshmentFacilityEnumeration.class, Integer.valueOf(11));
      facilityEnumMap.put(ReservedSpaceFacilityEnumeration.class, Integer.valueOf(12));
      facilityEnumMap.put(RetailFacilityEnumeration.class, Integer.valueOf(13));
      facilityEnumMap.put(SanitaryFacilityEnumeration.class, Integer.valueOf(14));
      facilityEnumMap.put(TicketingFacilityEnumeration.class, Integer.valueOf(15));
   }
   
   @Override
   public chouette.schema.Facility produce(Facility facility) {
      chouette.schema.Facility castorFacility = new chouette.schema.Facility();

      //
      populateFromModel(castorFacility, facility);

      castorFacility.setComment(getNotEmptyString(facility.getComment()));
      castorFacility.setName(facility.getName());

      FacilityLocation location = facility.getFacilityLocation();
      if (location != null)
      {
         chouette.schema.FacilityLocation castorLocation = new chouette.schema.FacilityLocation();
         castorFacility.setFacilityLocation(castorLocation );
         castorLocation.setLatitude(location.getLatitude());
         castorLocation.setLongitude(location.getLongitude());

         if(location.getLongLatType() != null)
         {
            LongLatTypeEnum longLatType = location.getLongLatType();
            try 
            {
               castorLocation.setLongLatType(LongLatTypeType.fromValue(longLatType.value()));
            } 
            catch (IllegalArgumentException e) 
            {
               // TODO generate report
            }
         }

         Address address = location.getAddress();
         if(address != null)
         {
            chouette.schema.Address castorAddress = new chouette.schema.Address();
            castorAddress.setCountryCode(getNotEmptyString(address.getCountryCode()));
            castorAddress.setStreetName(getNotEmptyString(address.getStreetName()));
            castorLocation.setAddress(castorAddress);
         }

         ProjectedPoint projectedPoint = location.getProjectedPoint();
         if(projectedPoint != null)
         {
            chouette.schema.ProjectedPoint castorProjectedPoint = new chouette.schema.ProjectedPoint();
            castorProjectedPoint.setProjectionType(projectedPoint.getProjectionType());
            castorProjectedPoint.setX(projectedPoint.getX());
            castorProjectedPoint.setY(projectedPoint.getY());
            castorLocation.setProjectedPoint(castorProjectedPoint);
         }
      }

      ChouetteFacilityTypeChoice chouetteFacilityTypeChoice = new ChouetteFacilityTypeChoice();
      castorFacility.setChouetteFacilityTypeChoice(chouetteFacilityTypeChoice );
      if (facility.getConnectionLink() != null)
      {
         chouetteFacilityTypeChoice.setConnectionLinkId(getNonEmptyObjectId(facility.getConnectionLink()));
      }
      if (facility.getLine() != null)
      {
         chouetteFacilityTypeChoice.setLineId(getNonEmptyObjectId(facility.getLine()));
      }
      if (facility.getStopArea() != null)
      {
         chouetteFacilityTypeChoice.setStopAreaId(getNonEmptyObjectId(facility.getStopArea()));
      }
      if (facility.getStopPoint() != null)
      {
         chouetteFacilityTypeChoice.setStopPointId(getNonEmptyObjectId(facility.getStopPoint()));
      }
      
      castorFacility.setDescription(getNotEmptyString(facility.getDescription()));
      //FreeAccess optional
      if (facility.getFreeAccess() != null)
         castorFacility.setFreeAccess(facility.getFreeAccess().booleanValue());

      //FacilityFeature[1..n] mandatory
      List<FacilityFeature> features = facility.getFacilityFeatures();
      for (FacilityFeature feature : features) {
         chouette.schema.FacilityFeature facilityFeature = new chouette.schema.FacilityFeature();

         int type = facilityEnumMap.get(feature.getChoiceValue().getClass()).intValue();
         switch (type)
         {
         case 0 :  
            facilityFeature.setAccessFacility(chouette.schema.types.AccessFacilityEnumeration.fromValue(feature.getAccessFacility().toString()));
            break;
         case 1 :  
            facilityFeature.setAccommodationFacility(chouette.schema.types.AccommodationFacilityEnumeration.fromValue(feature.getAccommodationFacility().name()));
            break;
         case 2 :  
            facilityFeature.setAssistanceFacility(chouette.schema.types.AssistanceFacilityEnumeration.fromValue(feature.getAssistanceFacility().name()));
            break;
         case 3 :  
            facilityFeature.setFareClassFacility(chouette.schema.types.FareClassFacilityEnumeration.fromValue(feature.getFareClassFacility().name()));
            break;
         case 4 :  
            facilityFeature.setHireFacility(chouette.schema.types.HireFacilityEnumeration.fromValue(feature.getHireFacility().name()));
            break;
         case 5 :  
            facilityFeature.setLuggageFacility(chouette.schema.types.LuggageFacilityEnumeration.fromValue(feature.getLuggageFacility().name()));
            break;
         case 6 :  
            facilityFeature.setMobilityFacility(chouette.schema.types.MobilityFacilityEnumeration.fromValue(feature.getMobilityFacility().name()));
            break;
         case 7 :  
            facilityFeature.setNuisanceFacility(chouette.schema.types.NuisanceFacilityEnumeration.fromValue(feature.getNuisanceFacility().name()));
            break;
         case 8 :  
            facilityFeature.setParkingFacility(chouette.schema.types.ParkingFacilityEnumeration.fromValue(feature.getParkingFacility().name()));
            break;
         case 9 :  
            facilityFeature.setPassengerCommsFacility(chouette.schema.types.PassengerCommsFacilityEnumeration.fromValue(feature.getPassengerCommsFacility().name()));
            break;
         case 10 :  
            facilityFeature.setPassengerInformationFacility(chouette.schema.types.PassengerInformationFacilityEnumeration.fromValue(feature.getPassengerInformationFacility().name()));
            break;
         case 11 :  
            facilityFeature.setRefreshmentFacility(chouette.schema.types.RefreshmentFacilityEnumeration.fromValue(feature.getRefreshmentFacility().name()));
            break;
         case 12 :  
            facilityFeature.setReservedSpaceFacility(chouette.schema.types.ReservedSpaceFacilityEnumeration.fromValue(feature.getReservedSpaceFacility().name()));
            break;
         case 13 :  
            facilityFeature.setRetailFacility(chouette.schema.types.RetailFacilityEnumeration.fromValue(feature.getRetailFacility().name()));
            break;
         case 14 :  
            facilityFeature.setSanitaryFacility(chouette.schema.types.SanitaryFacilityEnumeration.fromValue(feature.getSanitaryFacility().name()));
            break;
         case 15 :  
            facilityFeature.setTicketingFacility(chouette.schema.types.TicketingFacilityEnumeration.fromValue(feature.getTicketingFacility().name()));
            break;
         }
         castorFacility.addFacilityFeature(facilityFeature);
      }  
     
      
      return castorFacility;
   }

}
