package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import java.util.HashMap;
import java.util.Map;

import fr.certu.chouette.exchange.xml.neptune.importer.SharedImportedData;
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
import fr.certu.chouette.plugin.report.ReportItem;
/**
 * 
 * @author mamadou keira
 *
 */
public class FacilityProducer extends AbstractModelProducer<Facility, chouette.schema.Facility>{
   private Map<Class<?>,Integer> facilityEnumMap;

   public void init()
   {
      facilityEnumMap = new HashMap<Class<?>, Integer>();
      facilityEnumMap.put(chouette.schema.types.AccessFacilityEnumeration.class,Integer.valueOf(0));
      facilityEnumMap.put(chouette.schema.types.AccommodationFacilityEnumeration.class,Integer.valueOf(1));
      facilityEnumMap.put(chouette.schema.types.AssistanceFacilityEnumeration.class,Integer.valueOf(2));
      facilityEnumMap.put(chouette.schema.types.FareClassFacilityEnumeration.class,Integer.valueOf(3));
      facilityEnumMap.put(chouette.schema.types.HireFacilityEnumeration.class, Integer.valueOf(4));
      facilityEnumMap.put(chouette.schema.types.LuggageFacilityEnumeration.class, Integer.valueOf(5));
      facilityEnumMap.put(chouette.schema.types.MobilityFacilityEnumeration.class,Integer.valueOf(6));
      facilityEnumMap.put(chouette.schema.types.NuisanceFacilityEnumeration.class, Integer.valueOf(7));
      facilityEnumMap.put(chouette.schema.types.ParkingFacilityEnumeration.class, Integer.valueOf(8));
      facilityEnumMap.put(chouette.schema.types.PassengerCommsFacilityEnumeration.class, Integer.valueOf(9));
      facilityEnumMap.put(chouette.schema.types.PassengerInformationFacilityEnumeration.class,Integer.valueOf(10));
      facilityEnumMap.put(chouette.schema.types.RefreshmentFacilityEnumeration.class, Integer.valueOf(11));
      facilityEnumMap.put(chouette.schema.types.ReservedSpaceFacilityEnumeration.class, Integer.valueOf(12));
      facilityEnumMap.put(chouette.schema.types.RetailFacilityEnumeration.class, Integer.valueOf(13));
      facilityEnumMap.put(chouette.schema.types.SanitaryFacilityEnumeration.class, Integer.valueOf(14));
      facilityEnumMap.put(chouette.schema.types.TicketingFacilityEnumeration.class, Integer.valueOf(15));
   }
	

	@Override
	public Facility produce(chouette.schema.Facility xmlFacility, ReportItem report,SharedImportedData sharedData) {
		Facility facility = new Facility();
		// objectId, objectVersion, creatorId, creationTime
		populateFromCastorNeptune(facility, xmlFacility,report);
		// Name optional
		facility.setName(getNonEmptyTrimedString(xmlFacility.getName()));	
		// Comment optional
		facility.setComment(getNonEmptyTrimedString(xmlFacility.getComment()));
		chouette.schema.ChouetteFacilityTypeChoice cTypeChoice = xmlFacility.getChouetteFacilityTypeChoice();
		if(cTypeChoice != null){
			facility.setStopAreaId(getNonEmptyTrimedString(cTypeChoice.getStopAreaId()));
			facility.setLineId(getNonEmptyTrimedString(cTypeChoice.getLineId()));
			facility.setConnectionLinkId(getNonEmptyTrimedString(cTypeChoice.getConnectionLinkId()));
			facility.setStopPointId(getNonEmptyTrimedString(cTypeChoice.getStopPointId()));
		}
		facility.setDescription(getNonEmptyTrimedString(xmlFacility.getDescription()));
		//FreeAccess optional
		if (xmlFacility.hasFreeAccess())
		   facility.setFreeAccess(xmlFacility.isFreeAccess());

		chouette.schema.FacilityLocation xmlFacilityLocation = xmlFacility.getFacilityLocation();
		if(xmlFacilityLocation != null){
			FacilityLocation facilityLocation = new FacilityLocation();
			// Address optional
			chouette.schema.Address xmlAddress = xmlFacilityLocation.getAddress();		
			if(xmlAddress != null){
				Address address = new Address();
				address.setCountryCode(getNonEmptyTrimedString(xmlAddress.getCountryCode()));
				address.setStreetName(getNonEmptyTrimedString(xmlAddress.getStreetName()));
				facilityLocation.setAddress(address);
			}
			// LongLatType mandatory
			if(xmlFacilityLocation.getLongLatType() != null){
				try {
					facilityLocation.setLongLatType(LongLatTypeEnum.fromValue(xmlFacilityLocation.getLongLatType().value()));
				} catch (IllegalArgumentException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			// Latitude mandatory
			facilityLocation.setLatitude(xmlFacilityLocation.getLatitude());
			// Longitude mandatory
			facilityLocation.setLongitude(xmlFacilityLocation.getLongitude());
			// ProjectedPoint optional
			chouette.schema.ProjectedPoint xmlProjectedPoint = xmlFacilityLocation.getProjectedPoint();
			if(xmlProjectedPoint != null){
				ProjectedPoint projectedPoint = new ProjectedPoint();
				projectedPoint.setX(xmlProjectedPoint.getX());
				projectedPoint.setY(xmlProjectedPoint.getY());
				projectedPoint.setProjectionType(xmlProjectedPoint.getProjectionType());
				facilityLocation.setProjectedPoint(projectedPoint);
			}			
			//ContainedIn mandatory
			facilityLocation.setContainedIn(xmlFacilityLocation.getContainedIn());
			facility.setFacilityLocation(facilityLocation);
		}
		//FacilityFeature[1..n] mandatory
		chouette.schema.FacilityFeature[] features = xmlFacility.getFacilityFeature();
		for (chouette.schema.FacilityFeature xmlFeature : features) {
			FacilityFeature facilityFeature = new FacilityFeature();

         int type = facilityEnumMap.get(xmlFeature.getChoiceValue().getClass()).intValue();
         switch (type)
         {
         case 0 :  
            facilityFeature.setAccessFacility(AccessFacilityEnumeration.fromValue(xmlFeature.getAccessFacility().toString()));
            break;
         case 1 :  
            facilityFeature.setAccommodationFacility(AccommodationFacilityEnumeration.fromValue(xmlFeature.getAccommodationFacility().toString()));
            break;
         case 2 :  
            facilityFeature.setAssistanceFacility(AssistanceFacilityEnumeration.fromValue(xmlFeature.getAssistanceFacility().toString()));
            break;
         case 3 :  
            facilityFeature.setFareClassFacility(FareClassFacilityEnumeration.fromValue(xmlFeature.getFareClassFacility().toString()));
            break;
         case 4 :  
            facilityFeature.setHireFacility(HireFacilityEnumeration.fromValue(xmlFeature.getHireFacility().toString()));
            break;
         case 5 :  
            facilityFeature.setLuggageFacility(LuggageFacilityEnumeration.fromValue(xmlFeature.getLuggageFacility().toString()));
            break;
         case 6 :  
            facilityFeature.setMobilityFacility(MobilityFacilityEnumeration.fromValue(xmlFeature.getMobilityFacility().toString()));
            break;
         case 7 :  
            facilityFeature.setNuisanceFacility(NuisanceFacilityEnumeration.fromValue(xmlFeature.getNuisanceFacility().toString()));
            break;
         case 8 :  
            facilityFeature.setParkingFacility(ParkingFacilityEnumeration.fromValue(xmlFeature.getParkingFacility().toString()));
            break;
         case 9 :  
            facilityFeature.setPassengerCommsFacility(PassengerCommsFacilityEnumeration.fromValue(xmlFeature.getPassengerCommsFacility().toString()));
            break;
         case 10 :  
            facilityFeature.setPassengerInformationFacility(PassengerInformationFacilityEnumeration.fromValue(xmlFeature.getPassengerInformationFacility().toString()));
            break;
         case 11 :  
            facilityFeature.setRefreshmentFacility(RefreshmentFacilityEnumeration.fromValue(xmlFeature.getRefreshmentFacility().toString()));
            break;
         case 12 :  
            facilityFeature.setReservedSpaceFacility(ReservedSpaceFacilityEnumeration.fromValue(xmlFeature.getReservedSpaceFacility().toString()));
            break;
         case 13 :  
            facilityFeature.setRetailFacility(RetailFacilityEnumeration.fromValue(xmlFeature.getRetailFacility().toString()));
            break;
         case 14 :  
            facilityFeature.setSanitaryFacility(SanitaryFacilityEnumeration.fromValue(xmlFeature.getSanitaryFacility().toString()));
            break;
         case 15 :  
            facilityFeature.setTicketingFacility(TicketingFacilityEnumeration.fromValue(xmlFeature.getTicketingFacility().toString()));
            break;
         }
			facility.addFacilityFeature(facilityFeature);
		}	
		
		return facility;
	}

}
