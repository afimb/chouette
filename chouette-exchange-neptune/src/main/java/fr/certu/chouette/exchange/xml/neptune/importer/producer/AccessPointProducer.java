package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import fr.certu.chouette.exchange.xml.neptune.importer.SharedImportedData;
import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.type.AccessPointTypeEnum;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
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
		accessPoint.setContainedInStopArea(getNonEmptyTrimedString(xmlAccessPoint.getContainedIn()));
		// Address optional
		chouette.schema.Address xmlAddress = xmlAccessPoint.getAddress();		
		if(xmlAddress != null){
			accessPoint.setCountryCode(getNonEmptyTrimedString(xmlAddress.getCountryCode()));
			accessPoint.setStreetName(getNonEmptyTrimedString(xmlAddress.getStreetName()));
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
			accessPoint.setX(xmlProjectedPoint.getX());
			accessPoint.setY(xmlProjectedPoint.getY());
			accessPoint.setProjectionType(xmlProjectedPoint.getProjectionType());
		}
		// OpenningTime optional
		if(xmlAccessPoint.getOpeningTime() != null)
			accessPoint.setOpeningTime(getTime(xmlAccessPoint.getOpeningTime()));	
		// ClosingTime optional
		if(xmlAccessPoint.getClosingTime() != null)
			accessPoint.setClosingTime(getTime(xmlAccessPoint.getClosingTime()));
		//Type optional
		if(xmlAccessPoint.getType() != null)
		{
			try {
				accessPoint.setType(AccessPointTypeEnum.fromValue(xmlAccessPoint.getType().value()));
			} catch (IllegalArgumentException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		accessPoint.setLiftAvailable(xmlAccessPoint.getLiftAvailability());
		
		// MobilityRestrictedSuitability optional
		accessPoint.setMobilityRestrictedSuitable(xmlAccessPoint.getMobilityRestrictedSuitability());
		
		// StairsAvailability optional
		accessPoint.setStairsAvailable(xmlAccessPoint.getStairsAvailability());
		
		return accessPoint;
	}

}
