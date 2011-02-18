package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import fr.certu.chouette.model.neptune.PTLink;

public class PTLinkProducer extends AbstractCastorNeptuneProducer<chouette.schema.PtLink, PTLink> {

	@Override
	public chouette.schema.PtLink produce(PTLink ptLink) {
		chouette.schema.PtLink castorPTLink = new chouette.schema.PtLink();
		
		//
		populateFromModel(castorPTLink, ptLink);
		
		castorPTLink.setComment(ptLink.getComment());
		castorPTLink.setName(ptLink.getName());
		castorPTLink.setStartOfLink(getNonEmptyObjectId(ptLink.getStartOfLink()));
		castorPTLink.setEndOfLink(getNonEmptyObjectId(ptLink.getEndOfLink()));
		castorPTLink.setLinkDistance(ptLink.getLinkDistance());
						
		return castorPTLink;
	}

}
