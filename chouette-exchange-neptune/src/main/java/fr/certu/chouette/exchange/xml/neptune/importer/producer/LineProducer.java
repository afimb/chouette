package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import chouette.schema.AccessibilitySuitabilityDetailsItem;
import chouette.schema.LineExtension;
import fr.certu.chouette.exchange.xml.neptune.importer.SharedImportedData;
import fr.certu.chouette.exchange.xml.neptune.report.NeptuneReportItem;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;

public class LineProducer extends AbstractModelProducer<Line,chouette.schema.Line>
{
	@Override
	public Line produce(chouette.schema.Line xmlLine,ReportItem report,SharedImportedData sharedData)
	{
		Line line = new Line();
		// objectId, objectVersion, creatorId, creationTime
		populateFromCastorNeptune(line, xmlLine,report);

		// Name optional
		line.setName(getNonEmptyTrimedString(xmlLine.getName()));

		// Number optional
		line.setNumber(getNonEmptyTrimedString(xmlLine.getNumber()));

		// PublishedName optional
		line.setPublishedName(getNonEmptyTrimedString(xmlLine.getPublishedName()));

		// TransportModeName optional
		if (xmlLine.getTransportModeName() != null)
		{
			try
			{
				line.setTransportModeName(TransportModeNameEnum.fromValue(xmlLine.getTransportModeName().value()));
			}
			catch (IllegalArgumentException e) 
			{
				ReportItem item = new NeptuneReportItem(NeptuneReportItem.KEY.UNKNOWN_ENUM, Report.STATE.ERROR,"TransportModeName",xmlLine.getTransportModeName().value());
				report.addItem(item);
			}
		}
		// LineEnd [0..w] : TODO 
		String[] castorLineEnds = xmlLine.getLineEnd();
		for (String lineEnd : castorLineEnds) 
		{
			String realLineEnd = getNonEmptyTrimedString(lineEnd);
			if (realLineEnd == null)
			{
				ReportItem item = new NeptuneReportItem(NeptuneReportItem.KEY.EMPTY_TAG, Report.STATE.ERROR, "LineEnd");
				report.addItem(item);
			}
			else
			{
				line.addLineEnd(realLineEnd);
			}
		}

		// RouteId [1..w]  
		String[] castorRouteIds = xmlLine.getRouteId();
		for (String routeId : castorRouteIds) 
		{
			String realRouteId = getNonEmptyTrimedString(routeId);
			if (realRouteId == null)
			{
				ReportItem item = new NeptuneReportItem(NeptuneReportItem.KEY.EMPTY_TAG, Report.STATE.ERROR, "RouteId");
				report.addItem(item);
			}
			else
			{
				line.addRouteId(realRouteId);
			}
		}

		// Registration optional
		line.setRegistrationNumber(getRegistrationNumber(xmlLine.getRegistration(),report));

		// PtNetworkShortcut optional
		line.setPtNetworkIdShortcut(getNonEmptyTrimedString(xmlLine.getPtNetworkIdShortcut()));

		// Comment optional
		line.setComment(getNonEmptyTrimedString(xmlLine.getComment()));

		// LineExtension optional
		LineExtension xmlLineExtension = xmlLine.getLineExtension();
		if(xmlLineExtension != null){

			// MobilityRestrictedSuitability
			if (xmlLineExtension.hasMobilityRestrictedSuitability())
				line.setMobilityRestrictedSuitable(xmlLineExtension.getMobilityRestrictedSuitability());

			if(xmlLineExtension.getAccessibilitySuitabilityDetails() != null){
				for(AccessibilitySuitabilityDetailsItem xmlAccessibilitySuitabilityDetailsItem : xmlLineExtension.getAccessibilitySuitabilityDetails().getAccessibilitySuitabilityDetailsItem()){
					if(xmlAccessibilitySuitabilityDetailsItem.getUserNeedGroup() != null){
						try
						{
							line.addUserNeed(UserNeedEnum.fromValue(xmlAccessibilitySuitabilityDetailsItem.getUserNeedGroup().getChoiceValue().toString()));
						}
						catch (IllegalArgumentException e) 
						{
							ReportItem item = new NeptuneReportItem(NeptuneReportItem.KEY.UNKNOWN_ENUM, Report.STATE.ERROR,"UserNeed",xmlAccessibilitySuitabilityDetailsItem.getUserNeedGroup().getChoiceValue().toString());
							report.addItem(item);
						}
					}
				}
			}

		}

		return line;
	}

}
