package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import java.util.ArrayList;
import java.util.List;

import org.trident.schema.trident.PTNetworkType;

import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.type.PTNetworkSourceTypeEnum;
import fr.certu.chouette.plugin.exchange.SharedImportedData;
import fr.certu.chouette.plugin.exchange.UnsharedImportedData;
import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;


public class PTNetworkProducer extends AbstractModelProducer<PTNetwork, PTNetworkType> {


	@Override
	public PTNetwork produce(String sourceFile,PTNetworkType xmlPTNetwork,ReportItem importReport, PhaseReportItem validationReport,SharedImportedData sharedData, UnsharedImportedData unshareableData) 
	{
		if (xmlPTNetwork == null) return null;
		
		PTNetwork ptNetwork = new PTNetwork();
		
		// objectId, objectVersion, creatorId, creationTime
		populateFromCastorNeptune(ptNetwork, xmlPTNetwork,importReport);
		
		if (ptNetwork.getObjectId().contains(":PTNetwork:"))
		{
			// correct ptnetwork id when old fashioned form
			ptNetwork.setObjectId(ptNetwork.getObjectId().replace(":PTNetwork:", ":"+PTNetwork.PTNETWORK_KEY+":"));
		}
		
		// VersionDate mandatory
		ptNetwork.setVersionDate(getDate(xmlPTNetwork.getVersionDate()));
		
		// Description optional
		ptNetwork.setDescription(getNonEmptyTrimedString(xmlPTNetwork.getDescription()));
		
		// Name mandatory
		ptNetwork.setName(getNonEmptyTrimedString(xmlPTNetwork.getName()));
		
		// Registration optional
		ptNetwork.setRegistrationNumber(getRegistrationNumber(xmlPTNetwork.getRegistration(),importReport));
		
		// SourceName optional
		ptNetwork.setSourceName(getNonEmptyTrimedString(xmlPTNetwork.getSourceName()));
		
		// SourceIdentifier optional
		ptNetwork.setSourceIdentifier(getNonEmptyTrimedString(xmlPTNetwork.getSourceIdentifier()));
		
		// SourceType optional
		if(xmlPTNetwork.getSourceType() != null)
		{
			try{
				ptNetwork.setSourceType(PTNetworkSourceTypeEnum.valueOf(xmlPTNetwork.getSourceType().value()));
			}
			catch (IllegalArgumentException e) 
			{
				ReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.UNKNOWN_ENUM, Report.STATE.ERROR,"SourceType",xmlPTNetwork.getSourceType().value());
				importReport.addItem(item);
			}
		}
		
		
		//Comment optional
		ptNetwork.setComment(getNonEmptyTrimedString(xmlPTNetwork.getComment()));
		
		// remove line refs for cross file checking (must put back after) 
		List<String> xmlLineIds = new ArrayList<String>(xmlPTNetwork.getLineId());
		xmlPTNetwork.getLineId().clear();
		
		PTNetwork sharedBean = getOrAddSharedData(sharedData, ptNetwork, sourceFile, xmlPTNetwork,validationReport);
		if (sharedBean != null) ptNetwork = sharedBean;
		
		// replace lineRefs for level 2 validation
		xmlPTNetwork.getLineId().addAll(xmlLineIds);
		
		// LineIds [O..w]
		for(String xmlLineId : xmlLineIds)
		{
			String lineId = getNonEmptyTrimedString(xmlLineId);
			if(lineId == null)
			{
				// should not happen
			}
			else
			{
				ptNetwork.addLineId(lineId);

			}
		}

		return ptNetwork;
	}

}
