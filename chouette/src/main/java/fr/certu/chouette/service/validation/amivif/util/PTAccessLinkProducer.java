package fr.certu.chouette.service.validation.amivif.util;

import fr.certu.chouette.service.validation.amivif.PTAccessLink;
import fr.certu.chouette.service.validation.amivif.commun.ValidationException;

public class PTAccessLinkProducer extends ConnectionLinkProducer {
    
    public PTAccessLinkProducer(ValidationException validationException) {
    	super(validationException);
	}

	public PTAccessLink getASG(amivif.schema.PTAccessLink castorPTAccessLink) {
		if (castorPTAccessLink == null)
			return null;
		PTAccessLink ptAccessLink = (PTAccessLink)super.getASG(castorPTAccessLink);
		
		// comment optionnel
		ptAccessLink.setComment(castorPTAccessLink.getComment());
		
		return ptAccessLink;
	}
}
