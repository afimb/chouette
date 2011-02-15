package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.type.PTNetworkSourceTypeEnum;

public class PTNetworkProducer extends AbstractModelProducer<PTNetwork, chouette.schema.PTNetwork> {

	@Override
	public PTNetwork produce(chouette.schema.PTNetwork xmlPTNetwork) 
	{
		if (xmlPTNetwork == null) return null;
		PTNetwork ptNetwork = new PTNetwork();
		
		// objectId, objectVersion, creatorId, creationTime
		populateFromCastorNeptune(ptNetwork, xmlPTNetwork);
		
		// VersionDate mandatory
		ptNetwork.setVersionDate(getDate(xmlPTNetwork.getVersionDate()));
		
		// Description optional
		ptNetwork.setDescription(getNonEmptyTrimedString(xmlPTNetwork.getDescription()));
		
		// Name mandatory
		ptNetwork.setName(getNonEmptyTrimedString(xmlPTNetwork.getName()));
		
		// Registration optional
		ptNetwork.setRegistrationNumber(getRegistrationNumber(xmlPTNetwork.getRegistration()));
		
		// SourceName optional
		ptNetwork.setSourceName(getNonEmptyTrimedString(xmlPTNetwork.getSourceName()));
		
		// SourceIdentifier optional
		ptNetwork.setSourceIdentifier(getNonEmptyTrimedString(xmlPTNetwork.getSourceIdentifier()));
		
		// SourceType optional
		if(xmlPTNetwork.getSourceType() != null){
			try{
				ptNetwork.setPTNetworkSourceType(PTNetworkSourceTypeEnum.fromValue(xmlPTNetwork.getSourceType().value()));
			}
			catch (IllegalArgumentException e) 
			{
			// TODO: traiter le cas de non correspondance
			}
		}
		
		// LineIds [O..w]
		String[] castorLineIds = xmlPTNetwork.getLineId();
		for(String castorLineId : castorLineIds){
			String lineId = getNonEmptyTrimedString(castorLineId);
			if(lineId == null){
				//TODO : tracer
			}
			else{
				ptNetwork.addLineId(lineId);
			}
		}
		
		//Comment optional
		ptNetwork.setComment(getNonEmptyTrimedString(xmlPTNetwork.getComment()));
		
		return ptNetwork;
	}

}
