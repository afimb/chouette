package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.type.Address;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import fr.certu.chouette.model.neptune.type.ProjectedPoint;
import fr.certu.chouette.plugin.report.ReportItem;

public class StopPointProducer extends AbstractModelProducer<StopPoint,chouette.schema.StopPoint>
{
	@Override
	public StopPoint produce(chouette.schema.StopPoint xmlStopPoint,ReportItem report) 
	{
		StopPoint stopPoint = new StopPoint();
		// objectId, objectVersion, creatorId, creationTime
		populateFromCastorNeptune(stopPoint, xmlStopPoint, report);

		// Name mandatory
		stopPoint.setName(getNonEmptyTrimedString(xmlStopPoint.getName()));

		// Comment optional
		stopPoint.setComment(getNonEmptyTrimedString(xmlStopPoint.getComment()));
		
		// LongLatType mandatory
		if(xmlStopPoint.getLongLatType() != null){
			try {
				stopPoint.setLongLatType(LongLatTypeEnum.fromValue(xmlStopPoint.getLongLatType().value()));
			} catch (IllegalArgumentException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
		// Latitude mandatory
		stopPoint.setLatitude(xmlStopPoint.getLatitude());
		
		// Longitude mandatory
		stopPoint.setLongitude(xmlStopPoint.getLongitude());
		
		// ContainedInStopArea 
		stopPoint.setContainedInStopAreaId(getNonEmptyTrimedString(xmlStopPoint.getContainedIn()));
		
		// LineIdShortcut optional
		stopPoint.setLineIdShortcut(getNonEmptyTrimedString(xmlStopPoint.getLineIdShortcut()));
		
		//PTNetworkIdShortcut optional
		stopPoint.setPtNetworkIdShortcut(getNonEmptyTrimedString(xmlStopPoint.getPtNetworkIdShortcut()));
		
		// Address optional
		chouette.schema.Address xmlAddress = xmlStopPoint.getAddress();		
		if(xmlAddress != null){
			Address address = new Address();
			address.setCountryCode(getNonEmptyTrimedString(xmlAddress.getCountryCode()));
			address.setStreetName(getNonEmptyTrimedString(xmlAddress.getStreetName()));
		}
		
		// ProjectedPoint optional
		chouette.schema.ProjectedPoint xmlProjectedPoint = xmlStopPoint.getProjectedPoint();
		if(xmlProjectedPoint != null){
			ProjectedPoint projectedPoint = new ProjectedPoint();
			projectedPoint.setX(xmlProjectedPoint.getX());
			projectedPoint.setY(xmlProjectedPoint.getY());
			projectedPoint.setProjectionType(xmlProjectedPoint.getProjectionType());
		}
		
		return stopPoint;
	}

}
