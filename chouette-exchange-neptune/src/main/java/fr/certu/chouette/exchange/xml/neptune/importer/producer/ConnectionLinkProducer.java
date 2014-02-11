package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import lombok.extern.log4j.Log4j;

import org.trident.schema.trident.ConnectionLinkExtensionType;

import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.type.ConnectionLinkTypeEnum;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;
import fr.certu.chouette.plugin.exchange.SharedImportedData;
import fr.certu.chouette.plugin.exchange.UnsharedImportedData;
import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
@Log4j
public class ConnectionLinkProducer extends AbstractModelProducer<ConnectionLink, org.trident.schema.trident.ChouettePTNetworkType.ConnectionLink> {

	@Override
	public ConnectionLink produce(String sourceFile,org.trident.schema.trident.ChouettePTNetworkType.ConnectionLink xmlConnectionLink,ReportItem importReport, PhaseReportItem validationReport,SharedImportedData sharedData, UnsharedImportedData unshareableData) 
	{

		ConnectionLink connectionLink= new ConnectionLink();

		// objectId, objectVersion, creatorId, creationTime
		populateFromCastorNeptune(connectionLink, xmlConnectionLink,importReport);

		// Name optional
		connectionLink.setName(getNonEmptyTrimedString(xmlConnectionLink.getName()));

		// Comment optional
		connectionLink.setComment(getNonEmptyTrimedString(xmlConnectionLink.getComment()));

		//StartOfLink mandatory
		connectionLink.setStartOfLinkId(getNonEmptyTrimedString(xmlConnectionLink.getStartOfLink()));

		//EndOfLink mandatory
		connectionLink.setEndOfLinkId(getNonEmptyTrimedString(xmlConnectionLink.getEndOfLink()));

		//LinkDistance optional
		connectionLink.setLinkDistance(xmlConnectionLink.getLinkDistance());

		// LiftAvailability optional
		if (xmlConnectionLink.isSetLiftAvailability())
			connectionLink.setLiftAvailable(xmlConnectionLink.isLiftAvailability());

		// MobilityRestrictedSuitability optional
		if (xmlConnectionLink.isSetMobilityRestrictedSuitability())
			connectionLink.setMobilityRestrictedSuitable(xmlConnectionLink.isMobilityRestrictedSuitability());

		// StairsAvailability optional
		if (xmlConnectionLink.isSetStairsAvailability())
			connectionLink.setStairsAvailable(xmlConnectionLink.isStairsAvailability());

		// ConnectionLinkExtension optional
		ConnectionLinkExtensionType xmlConnectionLinkExtension = xmlConnectionLink.getConnectionLinkExtension();
		if(xmlConnectionLinkExtension != null){
			if(xmlConnectionLinkExtension.getAccessibilitySuitabilityDetails() != null){
				for(Object xmlAccessibilitySuitabilityDetailsItem : xmlConnectionLinkExtension.getAccessibilitySuitabilityDetails().getMobilityNeedOrPsychosensoryNeedOrMedicalNeed()){
					try
					{
						connectionLink.addUserNeed(UserNeedEnum.fromValue(xmlAccessibilitySuitabilityDetailsItem.toString()));
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

		// DefaultDuration optional
		connectionLink.setDefaultDuration(getTime(xmlConnectionLink.getDefaultDuration()));

		// FrequentTravellerDuration optional
		connectionLink.setFrequentTravellerDuration(getTime(xmlConnectionLink.getFrequentTravellerDuration()));

		// OccasionalTravellerDuration optional
		connectionLink.setOccasionalTravellerDuration(getTime(xmlConnectionLink.getOccasionalTravellerDuration()));

		// MobilityRestrictedTravellerDuration optional
		connectionLink.setMobilityRestrictedTravellerDuration(getTime(xmlConnectionLink.getMobilityRestrictedTravellerDuration()));

		// LinkType optional
		if(xmlConnectionLink.getLinkType() != null)
		{
			try
			{
				connectionLink.setLinkType(ConnectionLinkTypeEnum.fromValue(xmlConnectionLink.getLinkType().value()));
			}
			catch (IllegalArgumentException e) 
			{
				// TODO: traiter le cas de non correspondance
			}
		}

		ConnectionLink sharedBean = getOrAddSharedData(sharedData, connectionLink, sourceFile, xmlConnectionLink,validationReport);
		if (sharedBean != null) return sharedBean;
		return connectionLink;
	}

}
