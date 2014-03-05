package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.StopPoint;
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
//		stopPoint.setComment(getNonEmptyTrimedString(xmlStopPoint.getComment()));
		
		// LongLatType mandatory but ignored in chouette
		
		// Latitude mandatory  but ignored in chouette
		
		// Longitude mandatory  but ignored in chouette
		
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
				
		return stopPoint;
	}

}
