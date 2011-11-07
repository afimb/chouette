package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import java.util.Date;

import fr.certu.chouette.exchange.xml.neptune.importer.SharedImportedData;
import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.type.Address;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import fr.certu.chouette.model.neptune.type.ProjectedPoint;
import fr.certu.chouette.plugin.report.ReportItem;
/**
 * 
 * @author mamadou keira
 *
 */
public class AccessPointProducer extends AbstractModelProducer<AccessPoint, chouette.schema.AccessPoint>{

	@Override
	public AccessPoint produce(chouette.schema.AccessPoint xmlAccessPoint, ReportItem report,SharedImportedData sharedData) {
		AccessPoint accessPoint = new AccessPoint();
		
		// objectId, objectVersion, creatorId, creationTime
		populateFromCastorNeptune(accessPoint, xmlAccessPoint,report);
		// Name optional
		accessPoint.setName(getNonEmptyTrimedString(xmlAccessPoint.getName()));
		// Comment optional
		accessPoint.setComment(getNonEmptyTrimedString(xmlAccessPoint.getComment()));
		// ContainedIn
		accessPoint.setContainedIn(getNonEmptyTrimedString(xmlAccessPoint.getContainedIn()));
		// Address optional
		chouette.schema.Address xmlAddress = xmlAccessPoint.getAddress();		
		if(xmlAddress != null){
			Address address = new Address();
			address.setCountryCode(getNonEmptyTrimedString(xmlAddress.getCountryCode()));
			address.setStreetName(getNonEmptyTrimedString(xmlAddress.getStreetName()));
			accessPoint.setAddress(address);
		}
		// LongLatType mandatory
		if(xmlAccessPoint.getLongLatType() != null){
			try {
				accessPoint.setLongLatType(LongLatTypeEnum.fromValue(xmlAccessPoint.getLongLatType().value()));
			} catch (IllegalArgumentException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		// Latitude mandatory
		accessPoint.setLatitude(xmlAccessPoint.getLatitude());
		// Longitude mandatory
		accessPoint.setLongitude(xmlAccessPoint.getLongitude());
		// ProjectedPoint optional
		chouette.schema.ProjectedPoint xmlProjectedPoint = xmlAccessPoint.getProjectedPoint();
		if(xmlProjectedPoint != null){
			ProjectedPoint projectedPoint = new ProjectedPoint();
			projectedPoint.setX(xmlProjectedPoint.getX());
			projectedPoint.setY(xmlProjectedPoint.getY());
			projectedPoint.setProjectionType(xmlProjectedPoint.getProjectionType());
			accessPoint.setProjectedPoint(projectedPoint);
		}
		// OpenningTime optional
		if(xmlAccessPoint.getOpeningTime() != null)
			accessPoint.setOpenningTime(new Date(xmlAccessPoint.getOpeningTime().toLong()));	
		// ClosingTime optional
		if(xmlAccessPoint.getClosingTime() != null)
			accessPoint.setClosingTime(new Date(xmlAccessPoint.getClosingTime().toLong()));
		//Type optinal
		if(xmlAccessPoint.getType() != null)
			accessPoint.setType(xmlAccessPoint.getType().value());
		accessPoint.setLiftAvailable(xmlAccessPoint.getLiftAvailability());
		
		// MobilityRestrictedSuitability optional
		accessPoint.setMobilityRestrictedSuitable(xmlAccessPoint.getMobilityRestrictedSuitability());
		
		// StairsAvailability optional
		accessPoint.setStairsAvailable(xmlAccessPoint.getStairsAvailability());
		
		return accessPoint;
	}

}
