package fr.certu.chouette.service.validation.amivif.util;

import fr.certu.chouette.service.validation.amivif.ConnectionLink;
import fr.certu.chouette.service.validation.amivif.Link;
import fr.certu.chouette.service.validation.amivif.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.amivif.commun.ValidationException;

public class ConnectionLinkProducer extends LinkProducer {
    
    public ConnectionLinkProducer(ValidationException validationException) {
		super(validationException);
	}
    
	public ConnectionLink getASG(amivif.schema.ConnectionLink castorConnectionLink) {
		if (castorConnectionLink == null)
			return null;
		Link link = super.getASG(castorConnectionLink);
		ConnectionLink connectionLink = new ConnectionLink();
		connectionLink.setLink(link);
		
		// linkType optionnel
		if (castorConnectionLink.getLinkType() != null)
		{
			switch (castorConnectionLink.getLinkType()) 
			{
				case UNDERGROUND:
					connectionLink.setConnectionLinkType(ConnectionLink.ConnectionLinkType.Underground);
					break;
				case OVERGROUND:
					connectionLink.setConnectionLinkType(ConnectionLink.ConnectionLinkType.Overground);
					break;
				case MIXED:
					connectionLink.setConnectionLinkType(ConnectionLink.ConnectionLinkType.Mixed);
					break;
				default:
					getValidationException().add(TypeInvalidite.InvalidLinkType_ConnectionLink, "Le \"linkType\" de la \"ConnectionLink\" ("+castorConnectionLink.getObjectId()+") est invalid.");
			}
	}
		
		// defaultDuration optionnel
		connectionLink.setDefaultDuration(castorConnectionLink.getDefaultDuration());
		
		// frequentTravellerDuration optionnel
		connectionLink.setFrequentTravellerDuration(castorConnectionLink.getFrequentTravellerDuration());
		
		// occasionalTravellerDuration optionnel
		connectionLink.setOccasionalTravellerDuration(castorConnectionLink.getOccasionalTravellerDuration());
		
		// mobilityRestrictedTravellerDuration optionnel
		connectionLink.setMobilityRestrictedTravellerDuration(castorConnectionLink.getMobilityRestrictedTravellerDuration());
		
		// mobilityRestrictedSuitability optionnel
		connectionLink.setMobilityRestrictedSuitability(castorConnectionLink.getMobilityRestrictedSuitability());
		
		// stairsAvailability optionnel
		connectionLink.setStairsAvailability(castorConnectionLink.getStairsAvailability());
		
		// liftAvailability optionnel
		connectionLink.setLiftAvailability(castorConnectionLink.getLiftAvailability());
		
		// comment optionnel
		connectionLink.setComment(castorConnectionLink.getComment());
		
		// AMIVIF_ConnectionLink_Extension optionnel
		if (castorConnectionLink.getAMIVIF_ConnectionLink_Extension() != null)
			connectionLink.setDisplay(castorConnectionLink.getAMIVIF_ConnectionLink_Extension().getDisplay());
		else
			connectionLink.setDisplay(false);
		
		return connectionLink;
	}
}
