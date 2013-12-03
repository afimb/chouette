package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import java.util.List;

import org.trident.schema.trident.AddressType;
import org.trident.schema.trident.ChouetteFacilityType;
import org.trident.schema.trident.ChouetteFacilityType.FacilityLocation;
import org.trident.schema.trident.ProjectedPointType;

import uk.org.siri.siri.AllFacilitiesFeatureStructure;
import fr.certu.chouette.model.neptune.Facility;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
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
import fr.certu.chouette.plugin.exchange.SharedImportedData;
import fr.certu.chouette.plugin.exchange.UnsharedImportedData;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
/**
 * 
 * @author mamadou keira
 *
 */
public class FacilityProducer extends AbstractModelProducer<Facility, ChouetteFacilityType>{


	@Override
	public Facility produce(String sourceFile,ChouetteFacilityType xmlFacility, ReportItem importReport, PhaseReportItem validationReport,SharedImportedData sharedData, UnsharedImportedData unshareableData) {
		Facility facility = new Facility();
		// objectId, objectVersion, creatorId, creationTime
		populateFromCastorNeptune(facility, xmlFacility,importReport);
		// Name optional
		facility.setName(getNonEmptyTrimedString(xmlFacility.getName()));	
		// Comment optional
		facility.setComment(getNonEmptyTrimedString(xmlFacility.getComment()));

		facility.setStopAreaId(getNonEmptyTrimedString(xmlFacility.getStopAreaId()));
		facility.setLineId(getNonEmptyTrimedString(xmlFacility.getLineId()));
		facility.setConnectionLinkId(getNonEmptyTrimedString(xmlFacility.getConnectionLinkId()));
		facility.setStopPointId(getNonEmptyTrimedString(xmlFacility.getStopPointId()));

		facility.setDescription(getNonEmptyTrimedString(xmlFacility.getDescription()));
		//FreeAccess optional
		if (xmlFacility.isSetFreeAccess())
			facility.setFreeAccess(xmlFacility.isFreeAccess());

		FacilityLocation xmlFacilityLocation = xmlFacility.getFacilityLocation();
		if(xmlFacilityLocation != null){
			// FacilityLocation facilityLocation = new FacilityLocation();
			// Address optional
			AddressType xmlAddress = xmlFacilityLocation.getAddress();		
			if(xmlAddress != null){
				// Address address = new Address();
				facility.setCountryCode(getNonEmptyTrimedString(xmlAddress.getCountryCode()));
				facility.setStreetName(getNonEmptyTrimedString(xmlAddress.getStreetName()));
				// facilityLocation.setAddress(address);
			}
			// LongLatType mandatory
			if(xmlFacilityLocation.getLongLatType() != null){
				try {
					facility.setLongLatType(LongLatTypeEnum.fromValue(xmlFacilityLocation.getLongLatType().value()));
				} catch (IllegalArgumentException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			// Latitude mandatory
			facility.setLatitude(xmlFacilityLocation.getLatitude());
			// Longitude mandatory
			facility.setLongitude(xmlFacilityLocation.getLongitude());
			// ProjectedPoint optional
			ProjectedPointType xmlProjectedPoint = xmlFacilityLocation.getProjectedPoint();
			if(xmlProjectedPoint != null){
				// ProjectedPoint projectedPoint = new ProjectedPoint();
				facility.setX(xmlProjectedPoint.getX());
				facility.setY(xmlProjectedPoint.getY());
				facility.setProjectionType(xmlProjectedPoint.getProjectionType());
				// facilityLocation.setProjectedPoint(projectedPoint);
			}			
			//ContainedIn mandatory
			facility.setContainedIn(xmlFacilityLocation.getContainedIn());
			// facility.setFacilityLocation(facilityLocation);
		}
		//FacilityFeature[1..n] mandatory
		List<AllFacilitiesFeatureStructure> features = xmlFacility.getFacilityFeature();
		for (AllFacilitiesFeatureStructure xmlFeature : features) {
			FacilityFeature facilityFeature = new FacilityFeature();

			if (xmlFeature.isSetAccessFacility())
				facilityFeature.setAccessFacility(AccessFacilityEnumeration.fromValue(xmlFeature.getAccessFacility().value()));
			else if (xmlFeature.isSetAccommodationFacility())  
				facilityFeature.setAccommodationFacility(AccommodationFacilityEnumeration.fromValue(xmlFeature.getAccommodationFacility().value()));
			else if (xmlFeature.isSetAssistanceFacility())  
				facilityFeature.setAssistanceFacility(AssistanceFacilityEnumeration.fromValue(xmlFeature.getAssistanceFacility().value()));
			else if (xmlFeature.isSetFareClassFacility())  
				facilityFeature.setFareClassFacility(FareClassFacilityEnumeration.fromValue(xmlFeature.getFareClassFacility().value()));
			else if (xmlFeature.isSetHireFacility())  
				facilityFeature.setHireFacility(HireFacilityEnumeration.fromValue(xmlFeature.getHireFacility().value()));
			else if (xmlFeature.isSetLuggageFacility())  
				facilityFeature.setLuggageFacility(LuggageFacilityEnumeration.fromValue(xmlFeature.getLuggageFacility().value()));
			else if (xmlFeature.isSetMobilityFacility())  
				facilityFeature.setMobilityFacility(MobilityFacilityEnumeration.fromValue(xmlFeature.getMobilityFacility().value()));
			else if (xmlFeature.isSetNuisanceFacility())  
				facilityFeature.setNuisanceFacility(NuisanceFacilityEnumeration.fromValue(xmlFeature.getNuisanceFacility().value()));
			else if (xmlFeature.isSetParkingFacility())  
				facilityFeature.setParkingFacility(ParkingFacilityEnumeration.fromValue(xmlFeature.getParkingFacility().value()));
			else if (xmlFeature.isSetPassengerCommsFacility())  
				facilityFeature.setPassengerCommsFacility(PassengerCommsFacilityEnumeration.fromValue(xmlFeature.getPassengerCommsFacility().value()));
			else if (xmlFeature.isSetPassengerInformationFacility())  
				facilityFeature.setPassengerInformationFacility(PassengerInformationFacilityEnumeration.fromValue(xmlFeature.getPassengerInformationFacility().value()));
			else if (xmlFeature.isSetRefreshmentFacility())  
				facilityFeature.setRefreshmentFacility(RefreshmentFacilityEnumeration.fromValue(xmlFeature.getRefreshmentFacility().value()));
			else if (xmlFeature.isSetReservedSpaceFacility())  
				facilityFeature.setReservedSpaceFacility(ReservedSpaceFacilityEnumeration.fromValue(xmlFeature.getReservedSpaceFacility().value()));
			else if (xmlFeature.isSetRetailFacility())  
				facilityFeature.setRetailFacility(RetailFacilityEnumeration.fromValue(xmlFeature.getRetailFacility().value()));
			else if (xmlFeature.isSetSanitaryFacility())  
				facilityFeature.setSanitaryFacility(SanitaryFacilityEnumeration.fromValue(xmlFeature.getSanitaryFacility().value()));
			else if (xmlFeature.isSetTicketingFacility())  
				facilityFeature.setTicketingFacility(TicketingFacilityEnumeration.fromValue(xmlFeature.getTicketingFacility().value()));

			facility.addFacilityFeature(facilityFeature);
		}	

		return facility;
	}

}
