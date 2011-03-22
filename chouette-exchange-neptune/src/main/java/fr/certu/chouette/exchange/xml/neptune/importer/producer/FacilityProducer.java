package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import org.springframework.beans.BeanUtils;

import fr.certu.chouette.model.neptune.Facility;
import fr.certu.chouette.model.neptune.type.Address;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import fr.certu.chouette.model.neptune.type.ProjectedPoint;
import fr.certu.chouette.model.neptune.type.facility.FacilityFeature;
import fr.certu.chouette.plugin.report.ReportItem;

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
		
		chouette.schema.FacilityLocation facilityLocation = xmlFacility.getFacilityLocation();
		if(facilityLocation != null){
			// Address optional
			chouette.schema.Address xmlAddress = facilityLocation.getAddress();		
			if(xmlAddress != null){
				Address address = new Address();
				address.setCountryCode(getNonEmptyTrimedString(xmlAddress.getCountryCode()));
				address.setStreetName(getNonEmptyTrimedString(xmlAddress.getStreetName()));
				facility.setAddress(address);
			}
			// LongLatType mandatory
			if(facilityLocation.getLongLatType() != null){
				try {
					facility.setLongLatType(LongLatTypeEnum.fromValue(facilityLocation.getLongLatType().value()));
				} catch (IllegalArgumentException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			// Latitude mandatory
			facility.setLatitude(facilityLocation.getLatitude());
			// Longitude mandatory
			facility.setLongitude(facilityLocation.getLongitude());
			// ProjectedPoint optional
			chouette.schema.ProjectedPoint xmlProjectedPoint = facilityLocation.getProjectedPoint();
			if(xmlProjectedPoint != null){
				ProjectedPoint projectedPoint = new ProjectedPoint();
				projectedPoint.setX(xmlProjectedPoint.getX());
				projectedPoint.setY(xmlProjectedPoint.getY());
				projectedPoint.setProjectionType(xmlProjectedPoint.getProjectionType());
				facility.setProjectedPoint(projectedPoint);
			}			
			//ContainedIn mandatory
			facility.setContainedIn(facilityLocation.getContainedIn());
			//FacilityFeature[1..n] mandatory
			chouette.schema.FacilityFeature[] features = xmlFacility.getFacilityFeature();
			for (chouette.schema.FacilityFeature xmlFeature : features) {
				FacilityFeature facilityFeature = new FacilityFeature();
				BeanUtils.copyProperties(xmlFeature, facilityFeature);
			}
		}
		
		return facility;
	}

}
