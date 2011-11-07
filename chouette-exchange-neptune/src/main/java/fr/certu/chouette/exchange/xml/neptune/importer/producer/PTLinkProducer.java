package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import fr.certu.chouette.exchange.xml.neptune.importer.SharedImportedData;
import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.plugin.report.ReportItem;

public class PTLinkProducer extends AbstractModelProducer<PTLink, chouette.schema.PtLink> {

	@Override
	public PTLink produce(chouette.schema.PtLink xmlPTLink,ReportItem report,SharedImportedData sharedData) 
	{
		PTLink ptLink= new PTLink();
		
		// objectId, objectVersion, creatorId, creationTime
		populateFromCastorNeptune(ptLink, xmlPTLink,report);
				
		// Name optional
		ptLink.setName(getNonEmptyTrimedString(xmlPTLink.getName()));
		
		// Comment optional
		ptLink.setComment(getNonEmptyTrimedString(xmlPTLink.getComment()));
		
		//StartOfLink mandatory
		ptLink.setStartOfLinkId(getNonEmptyTrimedString(xmlPTLink.getStartOfLink()));
		
		//EndOfLink mandatory
		ptLink.setEndOfLinkId(getNonEmptyTrimedString(xmlPTLink.getEndOfLink()));
		
		//LinkDistance optional
		ptLink.setLinkDistance(xmlPTLink.getLinkDistance());
		
		return ptLink;
	}

}
