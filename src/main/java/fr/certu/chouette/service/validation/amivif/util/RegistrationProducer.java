package fr.certu.chouette.service.validation.amivif.util;

import java.util.HashSet;
import java.util.Set;

import fr.certu.chouette.service.validation.amivif.Registration;
import fr.certu.chouette.service.validation.amivif.TridentObject;
import fr.certu.chouette.service.validation.amivif.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.amivif.commun.ValidationException;

public class RegistrationProducer {
    
    private ValidationException		validationException;
    
	public RegistrationProducer(ValidationException validationException) {
		setValidationException(validationException);
	}

	public void setValidationException(ValidationException validationException) {
		this.validationException = validationException;
	}
	
	public ValidationException getValidationException() {
		return validationException;
	}

	public Registration getASG(amivif.schema.Registration castorRegistration) {
		if (castorRegistration == null)
			return null;
		Registration registration = new Registration();
		
		// registrationNumber obligatoire
		if (castorRegistration.getRegistrationNumber() == null)
			validationException.add(TypeInvalidite.NullRegistrationNumber_Registration, "La \"RegistrationNumber\" de cette \"Registration\" est null.");
		else
			registration.setRegistrationNumber(castorRegistration.getRegistrationNumber());
		
		// ptNetworkId 0..w
		Set<String> aSet = new HashSet<String>();
		for (int i = 0; i < castorRegistration.getPtNetworkIDCount(); i++) {
			try {
				(new TridentObject()).new TridentId(castorRegistration.getPtNetworkID(i));
			}
			catch(NullPointerException e) {
				validationException.add(TypeInvalidite.NullTridentObjectPtNetworkId_Registration, "Un \"objectId\" ne peut etre null.");
			}
			catch(IndexOutOfBoundsException e) {
				validationException.add(TypeInvalidite.InvalidTridentObject, "L'\"objectId\" "+castorRegistration.getPtNetworkID(i)+" est invalid.");
			}
			if (aSet.add(castorRegistration.getPtNetworkID(i)))
				registration.addTransportNetworkId(castorRegistration.getPtNetworkID(i));
			else
				validationException.add(TypeInvalidite.MultipleTridentObject, "La \"Registration\" contient plusieur fois un \"ptNetworkId\" ("+castorRegistration.getPtNetworkID(i)+").");
		}
		
		// lineId 0..w
		aSet = new HashSet<String>();
		for (int i = 0; i < castorRegistration.getLineIdCount(); i++) {
			try {
				(new TridentObject()).new TridentId(castorRegistration.getLineId(i));
			}
			catch(NullPointerException e) {
				validationException.add(TypeInvalidite.NullTridentObjectLineId_Registration, "Un \"objectId\" ne peut etre null.");
			}
			catch(IndexOutOfBoundsException e) {
				validationException.add(TypeInvalidite.InvalidTridentObject, "L'\"objectId\" "+castorRegistration.getLineId(i)+" est invalid.");
			}
			if (aSet.add(castorRegistration.getLineId(i)))
				registration.addLineId(castorRegistration.getLineId(i));
			else
				validationException.add(TypeInvalidite.MultipleTridentObject, "La \"Registration\" contient plusieur fois un \"lineId\" ("+castorRegistration.getLineId(i)+").");
		}
		
		// companyId 0..1
		if (castorRegistration.getCompanyId() != null) {
			try {
				(new TridentObject()).new TridentId(castorRegistration.getCompanyId());
			}
			catch(NullPointerException e) {
				validationException.add(TypeInvalidite.NullTridentObjectCompany_Registration, "Un \"objectId\" ne peut etre null.");
			}
			catch(IndexOutOfBoundsException e) {
				validationException.add(TypeInvalidite.InvalidTridentObject, "L'\"objectId\" "+castorRegistration.getCompanyId()+" est invalid.");
			}
			registration.setCompanyId(castorRegistration.getCompanyId());
		}
		
		return registration;
	}
}
