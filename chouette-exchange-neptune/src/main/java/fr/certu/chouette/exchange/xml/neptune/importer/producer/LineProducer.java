package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import java.util.List;

import lombok.extern.log4j.Log4j;

import org.trident.schema.trident.LineExtensionType;

import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;
import fr.certu.chouette.plugin.exchange.SharedImportedData;
import fr.certu.chouette.plugin.exchange.UnsharedImportedData;
import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;

@Log4j
public class LineProducer extends AbstractModelProducer<Line,org.trident.schema.trident.ChouettePTNetworkType.ChouetteLineDescription.Line>
{
	@Override
	public Line produce(String sourceFile,org.trident.schema.trident.ChouettePTNetworkType.ChouetteLineDescription.Line xmlLine,ReportItem importReport, PhaseReportItem validationReport,SharedImportedData sharedData, UnsharedImportedData unshareableData)
	{
		Line line = new Line();
		// objectId, objectVersion, creatorId, creationTime
		populateFromCastorNeptune(line, xmlLine,importReport);

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
				line.setTransportModeName(TransportModeNameEnum.valueOf(xmlLine.getTransportModeName().value()));
			}
			catch (IllegalArgumentException e) 
			{
				ReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.UNKNOWN_ENUM, Report.STATE.ERROR,"TransportModeName",xmlLine.getTransportModeName().value());
				importReport.addItem(item);
			}
		}
		// LineEnd [0..w] : TODO 
		List<String> jaxbLineEnds = xmlLine.getLineEnd();
		for (String lineEnd : jaxbLineEnds) 
		{
			String realLineEnd = getNonEmptyTrimedString(lineEnd);
			if (realLineEnd == null)
			{
				ReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.EMPTY_TAG, Report.STATE.ERROR, "LineEnd");
				importReport.addItem(item);
			}
			else
			{
				line.addLineEnd(realLineEnd);
			}
		}

		// RouteId [1..w]  
		List<String> jaxbRouteIds = xmlLine.getRouteId();
		for (String routeId : jaxbRouteIds) 
		{
			String realRouteId = getNonEmptyTrimedString(routeId);
			if (realRouteId == null)
			{
				ReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.EMPTY_TAG, Report.STATE.ERROR, "RouteId");
				importReport.addItem(item);
			}
			else
			{
				line.addRouteId(realRouteId);
			}
		}
		if (line.getRouteIds() == null )
		{
			ReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.EMPTY_LINE, Report.STATE.ERROR, line.getObjectId());
			importReport.addItem(item);
		}

		// Registration optional
		line.setRegistrationNumber(getRegistrationNumber(xmlLine.getRegistration(),importReport));

		// PtNetworkShortcut optional : correct old fashioned form
		String ptNetworkId = getNonEmptyTrimedString(xmlLine.getPtNetworkIdShortcut());
		if (ptNetworkId != null && ptNetworkId.contains(":PTNetwork:"))
		{
			ptNetworkId = ptNetworkId.replace(":PTNetwork:", ":"+PTNetwork.PTNETWORK_KEY+":");
		}
		line.setPtNetworkIdShortcut(ptNetworkId);

		// Comment optional
		line.setComment(getNonEmptyTrimedString(xmlLine.getComment()));

		// LineExtension optional
		LineExtensionType xmlLineExtension = xmlLine.getLineExtension();
		if(xmlLineExtension != null){

			// MobilityRestrictedSuitability
			if (xmlLineExtension.isMobilityRestrictedSuitability() != null)
				line.setMobilityRestrictedSuitable(xmlLineExtension.isMobilityRestrictedSuitability());

			if(xmlLineExtension.getAccessibilitySuitabilityDetails() != null)
			{
				for(Object xmlAccessibilitySuitabilityDetailsItem : xmlLineExtension.getAccessibilitySuitabilityDetails().getMobilityNeedOrPsychosensoryNeedOrMedicalNeed())
				{

					try
					{
						line.addUserNeed(UserNeedEnum.fromValue(xmlAccessibilitySuitabilityDetailsItem.toString()));
					}
					catch (IllegalArgumentException e) 
					{
						log.error("unknown userneeds enum "+xmlAccessibilitySuitabilityDetailsItem.toString());
						ReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.UNKNOWN_ENUM, Report.STATE.ERROR,"UserNeed",xmlAccessibilitySuitabilityDetailsItem.toString());
						importReport.addItem(item);
					}

				}
			}

		}

		return line;
	}

}
