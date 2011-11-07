package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import fr.certu.chouette.exchange.xml.neptune.importer.SharedImportedData;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.plugin.report.ReportItem;

public class JourneyPatternProducer extends AbstractModelProducer<JourneyPattern, chouette.schema.JourneyPattern> {

	@Override
	public JourneyPattern produce(chouette.schema.JourneyPattern xmlJourneyPattern,ReportItem report,SharedImportedData sharedData) 
	{
		JourneyPattern journeyPattern = new JourneyPattern();
		
		// objectId, objectVersion, creatorId, creationTime
		populateFromCastorNeptune(journeyPattern, xmlJourneyPattern,report);
		
		// Name optional
		journeyPattern.setName(getNonEmptyTrimedString(xmlJourneyPattern.getName()));
		
		// PublishedName optional
		journeyPattern.setPublishedName(getNonEmptyTrimedString(xmlJourneyPattern.getPublishedName()));
		
		//RouteId mandatory
		journeyPattern.setRouteId(getNonEmptyTrimedString(xmlJourneyPattern.getRouteId()));
		
		//Origin optional
		journeyPattern.setOrigin(getNonEmptyTrimedString(xmlJourneyPattern.getOrigin()));
		
		//Destination optional
		journeyPattern.setDestination(getNonEmptyTrimedString(xmlJourneyPattern.getDestination()));
		
		//StopPointIds [2..w]
		String[] castorStopPointIds = xmlJourneyPattern.getStopPointList();
		for (String castorStopPointId : castorStopPointIds) 
		{
			String stopPointId = getNonEmptyTrimedString(castorStopPointId);
			if (stopPointId == null)
			{
				// TODO tracer 
			}
			else
			{
				journeyPattern.addStopPointId(stopPointId);
			}
		}
		
		//RegistrationNumber optional
		journeyPattern.setRegistrationNumber(getRegistrationNumber(xmlJourneyPattern.getRegistration(),report));
		
		//Comment optional
		journeyPattern.setComment(getNonEmptyTrimedString(xmlJourneyPattern.getComment()));
		
		// lineIdShortCut 
		journeyPattern.setLineIdShortcut(getNonEmptyTrimedString(xmlJourneyPattern.getLineIdShortcut()));
		
		return journeyPattern;
	}

}
