package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import java.lang.reflect.Method;
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
	private Map<Class<?>,Class<?>> facilityEnumMap;
	public void init(){
		facilityEnumMap = new HashMap<Class<?>, Class<?>>();
		facilityEnumMap.put(chouette.schema.types.AccessFacilityEnumeration.class,AccessFacilityEnumeration.class);
		facilityEnumMap.put(chouette.schema.types.AccommodationFacilityEnumeration.class, AccommodationFacilityEnumeration.class);
		facilityEnumMap.put(chouette.schema.types.AssistanceFacilityEnumeration.class,AssistanceFacilityEnumeration.class);
		facilityEnumMap.put(chouette.schema.types.FareClassFacilityEnumeration.class,FareClassFacilityEnumeration.class);
		facilityEnumMap.put(chouette.schema.types.HireFacilityEnumeration.class, HireFacilityEnumeration.class);
		facilityEnumMap.put(chouette.schema.types.LuggageFacilityEnumeration.class, LuggageFacilityEnumeration.class);
		facilityEnumMap.put(chouette.schema.types.MobilityFacilityEnumeration.class, MobilityFacilityEnumeration.class);
		facilityEnumMap.put(chouette.schema.types.NuisanceFacilityEnumeration.class, NuisanceFacilityEnumeration.class);
		facilityEnumMap.put(chouette.schema.types.ParkingFacilityEnumeration.class, ParkingFacilityEnumeration.class);
		facilityEnumMap.put(chouette.schema.types.PassengerCommsFacilityEnumeration.class, PassengerCommsFacilityEnumeration.class);
		facilityEnumMap.put(chouette.schema.types.PassengerInformationFacilityEnumeration.class,PassengerInformationFacilityEnumeration.class);
		facilityEnumMap.put(chouette.schema.types.RefreshmentFacilityEnumeration.class, RefreshmentFacilityEnumeration.class);
		facilityEnumMap.put(chouette.schema.types.ReservedSpaceFacilityEnumeration.class, ReservedSpaceFacilityEnumeration.class);
		facilityEnumMap.put(chouette.schema.types.RetailFacilityEnumeration.class, RetailFacilityEnumeration.class);
		facilityEnumMap.put(chouette.schema.types.SanitaryFacilityEnumeration.class, SanitaryFacilityEnumeration.class);
		facilityEnumMap.put(chouette.schema.types.TicketingFacilityEnumeration.class, TicketingFacilityEnumeration.class);
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

			Class<?> neptuneEnum = facilityEnumMap.get(xmlFeature.getChoiceValue().getClass());
			try {
				Method fromValueMethod = neptuneEnum.getMethod("fromValue", String.class);
				Object value =  fromValueMethod.invoke(null, xmlFeature.getChoiceValue().toString());
				facilityFeature.setChoiceValue(value);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			facility.addFacilityFeature(facilityFeature);
		}	
		
		return facility;
	}

}
