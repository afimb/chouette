package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import org.exolab.castor.types.Date;

import chouette.schema.types.SourceTypeType;

import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.type.PTNetworkSourceTypeEnum;

public class PTNetworkProducer extends AbstractCastorNeptuneProducer<chouette.schema.PTNetwork, PTNetwork> {

	@Override
	public chouette.schema.PTNetwork produce(PTNetwork ptNetwork) {
		chouette.schema.PTNetwork castorPTNetwork = new chouette.schema.PTNetwork();

		//
		populateFromModel(castorPTNetwork, ptNetwork);

		castorPTNetwork.setName(ptNetwork.getName());
		castorPTNetwork.setRegistration(getRegistration(ptNetwork.getRegistrationNumber()));
		
		castorPTNetwork.setDescription(getNotEmptyString(ptNetwork.getDescription()));
		castorPTNetwork.setSourceIdentifier(getNotEmptyString(ptNetwork.getSourceIdentifier()));
		castorPTNetwork.setSourceName(getNotEmptyString(ptNetwork.getSourceName()));
		castorPTNetwork.setComment(getNotEmptyString(ptNetwork.getComment()));
		castorPTNetwork.setLineId(NeptuneIdentifiedObject.extractObjectIds(ptNetwork.getLines()));
		if(ptNetwork.getVersionDate() != null){
			castorPTNetwork.setVersionDate(new Date(ptNetwork.getVersionDate()));
		}

		try {
			PTNetworkSourceTypeEnum ptNetworkSourceType = ptNetwork.getPTNetworkSourceType();
			if(ptNetworkSourceType != null){
				castorPTNetwork.setSourceType(SourceTypeType.fromValue(ptNetworkSourceType.value()));
			}
		} catch (IllegalArgumentException e) {
			// TODO generate report
		}

		return castorPTNetwork;
	}

}
