package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import org.springframework.beans.BeanUtils;

import fr.certu.chouette.model.neptune.Facility;
import fr.certu.chouette.model.neptune.type.Address;
import fr.certu.chouette.model.neptune.type.FacilityLocation;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import fr.certu.chouette.model.neptune.type.ProjectedPoint;
import fr.certu.chouette.model.neptune.type.facility.FacilityFeature;
import fr.certu.chouette.plugin.report.ReportItem;
/**
 * 
 * @author mamadou keira
 *
 */
public class FacilityProducer extends AbstractModelProducer<Facility, chouette.schema.Facility>{

	@Override
	public Facility produce(chouette.schema.Facility xmlFacility, ReportItem report) {
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
		facility.setFreeAccess(xmlFacility.isFreeAccess());
		//Has_freeAccess optional
		facility.setHas_freeAccess(xmlFacility.hasFreeAccess());

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
			BeanUtils.copyProperties(xmlFeature, facilityFeature);
			facility.addFacilityFeature(facilityFeature);
		}	
		return facility;
	}

}
