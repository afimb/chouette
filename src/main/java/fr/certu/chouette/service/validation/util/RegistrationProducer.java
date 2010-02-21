package fr.certu.chouette.service.validation.util;

import fr.certu.chouette.service.validation.commun.LoggingManager;
import fr.certu.chouette.service.validation.commun.ValidationException;
import fr.certu.chouette.service.validation.Registration;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

class RegistrationProducer {
	
	private static final Logger              logger              = Logger.getLogger(fr.certu.chouette.service.validation.util.RegistrationProducer.class);
	private              ValidationException validationException;
	private              Registration        registration        = null;
	
	RegistrationProducer(ValidationException validationException) {
		setValidationException(validationException);
	}
	
	void setValidationException(ValidationException validationException) {
		this.validationException = validationException;
	}
	
	ValidationException getValidationException() {
		return validationException;
	}
	
	void setRegistration(Registration registration) {
		this.registration = registration;
	}
	
	Registration getRegistration() {
		return registration;
	}
	
	Registration getASG(chouette.schema.Registration castorRegistration) {
		registration = new Registration();
		String castorRegistrationNumber = castorRegistration.getRegistrationNumber(); 
		if (castorRegistrationNumber == null) {
			LoggingManager.log(logger, "La \"registrationNmber\" de cette \"Registration\" est null.", Level.WARN);
			return null;
		}
		else {
			castorRegistrationNumber = castorRegistrationNumber.trim();
			if (castorRegistrationNumber.length() == 0) {
				LoggingManager.log(logger, "La \"registrationNmber\" de cette \"Registration\" est null.", Level.WARN);
				return null;
			}
			else
				registration.setRegistrationNumber(castorRegistrationNumber);
		}
		return registration;
	}
}
