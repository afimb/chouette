package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import org.trident.schema.trident.AddressType;
import org.trident.schema.trident.ProjectedPointType;

import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.type.Address;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import fr.certu.chouette.model.neptune.type.ProjectedPoint;
import fr.certu.chouette.plugin.exchange.SharedImportedData;
import fr.certu.chouette.plugin.exchange.UnsharedImportedData;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;

public class StopPointProducer extends AbstractModelProducer<StopPoint,org.trident.schema.trident.ChouettePTNetworkType.ChouetteLineDescription.StopPoint>
{
	@Override
	public StopPoint produce(String sourceFile,org.trident.schema.trident.ChouettePTNetworkType.ChouetteLineDescription.StopPoint xmlStopPoint,ReportItem importReport, PhaseReportItem validationReport,SharedImportedData sharedData, UnsharedImportedData unshareableData) 
	{
		StopPoint stopPoint = new StopPoint();
		// objectId, objectVersion, creatorId, creationTime
		populateFromCastorNeptune(stopPoint, xmlStopPoint, importReport);

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
		
		// PtNetworkShortcut optional : correct old fashioned form
		String ptNetworkId = getNonEmptyTrimedString(xmlStopPoint.getPtNetworkIdShortcut());
		if (ptNetworkId != null && ptNetworkId.contains(":PTNetwork:"))
		{
			ptNetworkId = ptNetworkId.replace(":PTNetwork:", ":"+PTNetwork.PTNETWORK_KEY+":");
		}
		stopPoint.setPtNetworkIdShortcut(ptNetworkId);
		
		// Address optional
		AddressType xmlAddress = xmlStopPoint.getAddress();		
		if(xmlAddress != null){
			Address address = new Address();
			address.setCountryCode(getNonEmptyTrimedString(xmlAddress.getCountryCode()));
			address.setStreetName(getNonEmptyTrimedString(xmlAddress.getStreetName()));
			stopPoint.setAddress(address);
		}
		
		// ProjectedPoint optional
		ProjectedPointType xmlProjectedPoint = xmlStopPoint.getProjectedPoint();
		if(xmlProjectedPoint != null){
			ProjectedPoint projectedPoint = new ProjectedPoint();
			projectedPoint.setX(xmlProjectedPoint.getX());
			projectedPoint.setY(xmlProjectedPoint.getY());
			projectedPoint.setProjectionType(xmlProjectedPoint.getProjectionType());
			stopPoint.setProjectedPoint(projectedPoint);
		}
		
		return stopPoint;
	}

}
