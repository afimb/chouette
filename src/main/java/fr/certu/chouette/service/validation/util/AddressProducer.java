package fr.certu.chouette.service.validation.util;

import fr.certu.chouette.service.validation.Address;
import fr.certu.chouette.service.validation.commun.LoggingManager;
import fr.certu.chouette.service.validation.commun.ValidationException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

class AddressProducer {
	
	private static final Logger              logger              = Logger.getLogger(fr.certu.chouette.service.validation.util.AreaCentroidProducer.class);
	private static       ValidationException validationException;
	private              Address             address             = null;
	
	AddressProducer(ValidationException validationException) {
		setValidationException(validationException);
	}
	
	void setValidationException(ValidationException validationException) {
		AddressProducer.validationException = validationException;
	}
	
	ValidationException getValidationException() {
		return validationException;
	}
	
	void setAddress(Address address) {
		this.address = address;
	}
	
	Address getAddress() {
		return address;
	}
	
	Address getASG(chouette.schema.Address castorAddress) {
		Address address = new Address();
		
		// CountryCode optionnel
		String castorCountryCode = castorAddress.getCountryCode();
		if (castorCountryCode == null)
			LoggingManager.log(logger, "Pas d'objet de type \"countryCode\" dans cette \"Address\".", Level.INFO);
		else {
			castorCountryCode = castorCountryCode.trim();
			if (castorCountryCode.length() == 0)
				LoggingManager.log(logger, "L'objet de type \"countryCode\" dans cette \"Address\" est vide.", Level.WARN);
			else
				address.setCountryCode(castorCountryCode);
		}
		
		// StreetName optionnel
		String castorStreetName = castorAddress.getStreetName();
		if (castorStreetName == null)
			LoggingManager.log(logger, "Pas d'objet de type \"streetName\" dans cette \"Address\".", Level.INFO);
		else {
			castorStreetName = castorStreetName.trim();
			if (castorStreetName.length() == 0)
				LoggingManager.log(logger, "L'objet de type \"streetName\" dans cette \"Address\" est vide.", Level.WARN);
			else
				address.setStreetName(castorStreetName);
		}
		
		if ((address.getCountryCode() == null) && (address.getStreetName() == null))
			return null;
		
		return address;
	}
}
