package fr.certu.chouette.service.validation.util;

import fr.certu.chouette.service.validation.commun.LoggingManager;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.commun.ValidationException;
import fr.certu.chouette.service.validation.util.RegistrationProducer;
import fr.certu.chouette.service.validation.ChouetteAreaType;
import fr.certu.chouette.service.validation.Registration;
import fr.certu.chouette.service.validation.StopAreaExtension;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

class StopAreaExtensionProducer {
	
	private static final Logger              logger              = Logger.getLogger(fr.certu.chouette.service.validation.util.AreaCentroidProducer.class);
	private              ValidationException validationException;
	private              StopAreaExtension   stopAreaExtension   = null;
	
	StopAreaExtensionProducer(ValidationException validationException) {
		setValidationException(validationException);
	}
	
	void setValidationException(ValidationException validationException) {
		this.validationException = validationException;
	}
	
	ValidationException getValidationException() {
		return validationException;
	}
	
	void setStopAreaExtension(StopAreaExtension stopAreaExtension) {
		this.stopAreaExtension = stopAreaExtension;
	}
	
	StopAreaExtension getStopAreaExtension() {
		return stopAreaExtension;
	}
	
	StopAreaExtension getASG(chouette.schema.StopAreaExtension castorStopAreaExtension) {
		stopAreaExtension = new StopAreaExtension();
		String[] params = null;
		
		// AreaType obligatoire
		if (castorStopAreaExtension.getAreaType() != null)
			switch (castorStopAreaExtension.getAreaType()) 
			{
				case BOARDINGPOSITION:
					stopAreaExtension.setType(ChouetteAreaType.BOARDINGPOSITION);
					break;
				case COMMERCIALSTOPPOINT:
					stopAreaExtension.setType(ChouetteAreaType.COMMERCIALSTOPPOINT);
					break;
				case ITL:
					stopAreaExtension.setType(ChouetteAreaType.ITL);
					break;
				case QUAY:
					stopAreaExtension.setType(ChouetteAreaType.QUAY);
					break;
				case STOPPLACE:
					stopAreaExtension.setType(ChouetteAreaType.STOPPLACE);
					break;
				default:
					LoggingManager.log(logger, "Le \"areaType\" pour ce \"StopAreaExtension\" est invalide.", Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDTYPE_STOPAREAEXTENSION, "Le \"areaType\" pour ce \"StopAreaExtension\" est invalide.");
			}
		
		// NearestTopicNName optionnel
		String castorNearestTopicName = castorStopAreaExtension.getNearestTopicName();
		if (castorNearestTopicName == null)
			LoggingManager.log(logger, "Pas de \"nearestTopicName\" pour ce \"StopAreaExtension\".", Level.INFO);
		else {
			castorNearestTopicName = castorNearestTopicName.trim();
			if (castorNearestTopicName.length() == 0)
				LoggingManager.log(logger, "Le \"nearestTopicName\" pour ce \"StopAreaExtension\" est vide.", Level.WARN);
			else
				stopAreaExtension.setNearestTopicName(castorNearestTopicName);
		}
		
		// FareCode optionnel
		if (castorStopAreaExtension.hasFareCode()) {
			int castorFareCode = castorStopAreaExtension.getFareCode();
			if (castorFareCode < 0) {
				params = new String[]{""+castorFareCode};
				LoggingManager.log(logger, "Le \"fareCode\" () de ce \"StopAreaExtension\" est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDFARECODE_STOPAREAEXTENSION, "Le \"fareCode\" () de ce \"StopAreaExtension\" est invalide.", params);
			}
			else
				stopAreaExtension.setFareCode(castorFareCode);
		}
		else
			LoggingManager.log(logger, "Pas de \"fareCode\" pour ce \"StopAreaExtension\".", Level.INFO);
		
		// Registration optionnel
		chouette.schema.Registration castorRegistration = castorStopAreaExtension.getRegistration();
		if (castorRegistration == null)
			LoggingManager.log(logger, "Pas de \"registration\" pour ce \"StopAreaExtension\".", Level.INFO);
		else {
			Registration registration = (new RegistrationProducer(validationException)).getASG(castorRegistration);
			if (registration == null)
				LoggingManager.log(logger, "Error lors de la construction de la \"registration\" pour ce \"StopAreaExtension\".", Level.ERROR);
			else {
				stopAreaExtension.setRegistration(registration);
				registration.setStopAreaExtension(stopAreaExtension);
			}
		}
		
		return stopAreaExtension;
	}
}
