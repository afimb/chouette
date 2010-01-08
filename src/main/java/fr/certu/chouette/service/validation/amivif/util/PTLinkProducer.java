package fr.certu.chouette.service.validation.amivif.util;

import fr.certu.chouette.service.validation.amivif.Link;
import fr.certu.chouette.service.validation.amivif.PTLink;
import fr.certu.chouette.service.validation.amivif.commun.ValidationException;

public class PTLinkProducer extends LinkProducer {
    
    public PTLinkProducer(ValidationException validationException) {
		super(validationException);
	}

	public PTLink getASG(amivif.schema.PTLink castorPTLink) {
		if (castorPTLink == null)
			return null;
		Link link = super.getASG(castorPTLink);
		PTLink ptLink = new PTLink();
		ptLink.setLink(link);
		
		// comment optionnel
		ptLink.setComment(castorPTLink.getComment());
		
		return ptLink;
	}
}
