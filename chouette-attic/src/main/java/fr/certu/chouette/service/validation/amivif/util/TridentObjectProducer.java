package fr.certu.chouette.service.validation.amivif.util;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.certu.chouette.service.amivif.LecteurAmivifXML;
import fr.certu.chouette.service.validation.amivif.TridentObject;
import fr.certu.chouette.service.validation.amivif.TridentObject.ValidityPeriod;
import fr.certu.chouette.service.validation.amivif.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.amivif.commun.ValidationException;

public class TridentObjectProducer {
    
	private static final Log logger = LogFactory.getLog(TridentObjectProducer.class);
	private ValidationException		validationException;

	public TridentObjectProducer(ValidationException validationException) {
		setValidationException(validationException);
	}

	public void setValidationException(ValidationException validationException) {
		this.validationException = validationException;
	}
	
	public ValidationException getValidationException() {
		return validationException;
	}

	public TridentObject getASG(amivif.schema.TridentObjectTypeType castorTridentObject) 
	{
		logger.debug("EVOCASTOR --> parameter has new type amivif.schema.TridentObjectTypeType");
		if (castorTridentObject == null)
			return null;
		TridentObject tridentObject = new TridentObject();
		
		// objectId obligatoire
		String castorObjectId = castorTridentObject.getObjectId();
		if (castorObjectId == null)
			validationException.add(TypeInvalidite.NullTridentObject, "Un \"objectId\" ne peut etre null.");
		else {
			try {
				tridentObject.setObjectId(tridentObject.new TridentId(castorObjectId));
			}
			catch(NullPointerException e) {		
				validationException.add(TypeInvalidite.NullTridentObject, "Un \"objectId\" ne peut etre null.");
			}
			catch(IndexOutOfBoundsException e) {
				validationException.add(TypeInvalidite.InvalidTridentObject, "L'\"objectId\" "+castorObjectId+" est invalid.");
			}
		}
		
		// objectVersion optionnel
		if (castorTridentObject.hasObjectVersion()) {
			if (castorTridentObject.getObjectVersion() < 1) {
				validationException.add(TypeInvalidite.InvalidObjectVersion, "L'\"objectVersion\" d'un \"TridentObject\" doit etre >= 1.");
                                tridentObject.setObjectVersion(1);
                        }
                        else
                            tridentObject.setObjectVersion((int)castorTridentObject.getObjectVersion());
		}
		else
			tridentObject.setObjectVersion(1);
		
		// creationTime optionnel
		if (castorTridentObject.getCreationTime() != null) {
			if (castorTridentObject.getCreationTime().after(new Date(System.currentTimeMillis())))
				validationException.add(TypeInvalidite.InvalidCreationTime, "La \"creationTime\" d'un \"TridentObject\" doit etre posterieure a la date actuelle.");
			tridentObject.setCreationTime(castorTridentObject.getCreationTime());
		}
		
		// expiryTime optionnel
		if (castorTridentObject.getExpiryTime() != null) {
			if (castorTridentObject.getExpiryTime().before(castorTridentObject.getCreationTime()))
				validationException.add(TypeInvalidite.InvalidExpiryTime, "L'\"expiryTime\" d'un \"TridentObject\" doit etre posterieure a la \"creationTime\".");
			tridentObject.setExpiryTime(castorTridentObject.getExpiryTime());
		}
		
		// creatorId optionnel
		if (castorTridentObject.getCreatorId() != null)
			tridentObject.setCreatorId(castorTridentObject.getCreatorId());
		
		// validityPeriod 0..w XOR validityDomain 0..1
		if (castorTridentObject.getTridentObjectTypeChoice() == null)
			;//validationException.add(TypeInvalidite.NullChoice_TridentObject, "Un \"TridentObject\" doit avoir soit une \"validityDomain\" soit une liste de \"validityPeriod\".");
		else {
			String castorValidityDomain = castorTridentObject.getTridentObjectTypeChoice().getValidityDomain();
			int castorNumberOfValidityPeriod = castorTridentObject.getTridentObjectTypeChoice().getValidityPeriodCount();
			if ((castorValidityDomain != null) && (castorNumberOfValidityPeriod > 0))
				validationException.add(TypeInvalidite.InvalidChoice_TridentObject, "Un \"TridentObject\" doit avoir soit une \"validityDomain\" soit une liste de \"validityPeriod\".");//ERROR
			tridentObject.setValidityDomain(castorValidityDomain);
			for (int i = 0; i < castorNumberOfValidityPeriod; i++)
				tridentObject.addValidityPeriod(getASG(tridentObject, castorTridentObject.getTridentObjectTypeChoice().getValidityPeriod(i)));
		}
		
		return tridentObject;
	}
	
	private ValidityPeriod getASG(TridentObject tridentObject, amivif.schema.ValidityPeriod castorValidityPeriod) {
		if (castorValidityPeriod == null)
			return null;
		ValidityPeriod validityPeriod = tridentObject.new ValidityPeriod();
		if (castorValidityPeriod.getStart() == null)
			validationException.add(TypeInvalidite.NoStart_TridentObject, "Le \"start\" d'une \"validityPeriod\" d'un \"TridentObject\" est indispensable.");
		else
			if ((castorValidityPeriod.getEnd() != null) && (castorValidityPeriod.getEnd().before(castorValidityPeriod.getStart())))
				validationException.add(TypeInvalidite.InvalidValidityPeriod_TridentObject, "Le \"start\" d'une \"validityPeriod\" d'un \"TridentObject\" doit etre posterieure a l'\"end\".");
		validityPeriod.setStart(castorValidityPeriod.getStart());
		validityPeriod.setEnd(castorValidityPeriod.getEnd());
		return validityPeriod;
	}
}
