package fr.certu.chouette.service.validation.amivif.util;

import java.math.BigDecimal;

import fr.certu.chouette.service.validation.amivif.Link;
import fr.certu.chouette.service.validation.amivif.LocationTridentObject;
import fr.certu.chouette.service.validation.amivif.TridentObject;
import fr.certu.chouette.service.validation.amivif.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.amivif.commun.ValidationException;

public class LinkProducer extends LocalTridentObjectProducer {
	
	public LinkProducer(ValidationException validationException) {
		super(validationException);
	}
	
	public Link getASG(amivif.schema.GeneralLinkTypeType castorLink) {
		if (castorLink == null)
			return null;
		LocationTridentObject locationTridentObject = super.getASG(castorLink);
		Link link = new Link();
		link.setLocationTridentObject(locationTridentObject);
				
		// name optionnel
		link.setName(castorLink.getName());
		
		// startOfLink obligatoire
		if (castorLink.getStartOfLink() == null)
			getValidationException().add(TypeInvalidite.NoStartOfLink_Link, "Le \"startOfLink\" d'un \"Link\" ("+castorLink.getObjectId()+") est null.");
		else {
			link.setStartOfLinkId(castorLink.getStartOfLink());
			try {
				(new TridentObject()).new TridentId(castorLink.getStartOfLink());
			}
			catch(NullPointerException e) {
				getValidationException().add(TypeInvalidite.NullTridentObject, "Le \"startOfLink\" d'un \"Link\" ("+castorLink.getObjectId()+") est null.");
			}
			catch(IndexOutOfBoundsException e) {
				getValidationException().add(TypeInvalidite.InvalidTridentObject, "Le \"startOfLink\" ("+castorLink.getStartOfLink()+") d'un \"Link\" ("+castorLink.getObjectId()+") est invalid.");
			}
		}

		// endOfLink obligatoire
		if (castorLink.getEndOfLink() == null)
			getValidationException().add(TypeInvalidite.NoEndOfLink_Link, "Le \"endOfLink\" d'un \"Link\" ("+castorLink.getObjectId()+") est null.");
		else {
			link.setEndOfLinkId(castorLink.getEndOfLink());
			try {
				(new TridentObject()).new TridentId(castorLink.getEndOfLink());
			}
			catch(NullPointerException e) {
				getValidationException().add(TypeInvalidite.NullTridentObject, "Le \"endOfLink\" d'un \"Link\" ("+castorLink.getObjectId()+") est null.");
			}
			catch(IndexOutOfBoundsException e) {
				getValidationException().add(TypeInvalidite.InvalidTridentObject, "Le \"endOfLink\" ("+castorLink.getEndOfLink()+") d'un \"Link\" ("+castorLink.getObjectId()+") est invalid.");
			}
		}
		
		// linkDistance optionnel
		link.setLinkDistance(castorLink.getLinkDistance());
		if ((link.getLinkDistance() != null) && (link.getLinkDistance().compareTo(new BigDecimal(0)) <= 0))
			getValidationException().add(TypeInvalidite.InvalidLinkDisance, "La \"linkDistance\" ("+link.getLinkDistance().toString()+") du \"Link\" ("+link.getObjectId().toString()+") doit etre  > 0.");
		
		return link;
	}
}
