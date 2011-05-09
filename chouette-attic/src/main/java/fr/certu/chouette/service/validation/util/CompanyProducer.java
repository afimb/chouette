package fr.certu.chouette.service.validation.util;

import fr.certu.chouette.service.validation.commun.LoggingManager;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.commun.ValidationException;
import fr.certu.chouette.service.validation.util.CompanyProducer;
import fr.certu.chouette.service.validation.util.RegistrationProducer;
import fr.certu.chouette.service.validation.Company;
import fr.certu.chouette.service.validation.Registration;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

class CompanyProducer {
	
    private static final Logger              logger              = Logger.getLogger(fr.certu.chouette.service.validation.util.CompanyProducer.class);
    private              ValidationException validationException;
    private              Company             company             = null;
    
    CompanyProducer(ValidationException validationException) {
    	setValidationException(validationException);
    }
    
    void setValidationException(ValidationException validationException) {
		this.validationException = validationException;
	}
	
	ValidationException getValidationException() {
		return validationException;
	}
	
	void setCompany(Company company) {
		this.company = company;
	}
	
	Company getCompany() {
		return company;
	}
	
	Company getASG(chouette.schema.Company castorCompany) {
		company = new Company();
		String[] params = null;
		
		// ObjectId obligatoire
		String castorObjectId = castorCompany.getObjectId();
		if (castorObjectId == null) {
			params = LoggingManager.getParams(castorCompany.getName());
			LoggingManager.log(logger, "Pas de \"objectId\" pour ce \"Company\" ().", params, Level.ERROR);
			validationException.add(TypeInvalidite.NOOBJECTID_COMPANY, "Pas de \"objectId\" pour ce \"Company\" ().", params);
		}
		else {
			castorObjectId = castorObjectId.trim();
			if (castorObjectId.length() == 0) {
				params = LoggingManager.getParams(castorCompany.getName());
				LoggingManager.log(logger, "Pas de \"objectId\" pour ce \"Company\" ().", params, Level.ERROR);
				validationException.add(TypeInvalidite.NOOBJECTID_COMPANY, "Pas de \"objectId\" pour ce \"Company\" ().", params);
			}
			else {
				if (!MainSchemaProducer.isTridentLike(castorObjectId)) {
					params = LoggingManager.getParams(castorObjectId);
					LoggingManager.log(logger, "L'\"objectId\" () pour ce \"Company\" est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDOBJECTID_COMPANY, "L'\"objectId\" () pour ce \"Company\" est invalide.", params);
				}
				company.setObjectId(castorObjectId);		
			}
		}
		
		// ObjectVersion optionnel
		if (castorCompany.hasObjectVersion()) {
			int castorObjectVersion = (int)castorCompany.getObjectVersion();
			if (castorObjectVersion < 1) {
				if (castorCompany.getName() != null)
					params = LoggingManager.getParams(""+castorObjectVersion, castorCompany.getName());
				else
					params = LoggingManager.getParams(""+castorObjectVersion, castorCompany.getObjectId());
				LoggingManager.log(logger, "La version () \"objectVersion\" du \"Company\" () est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDOBJECTVERSION_COMPANY, "La version () \"objectVersion\" du \"Company\" () est invalide.", params);
			}
			else
				company.setObjectVersion(castorObjectVersion);
		}
		else {
			if (castorCompany.getName() != null)
				params = LoggingManager.getParams(castorCompany.getName());
			else
				params = LoggingManager.getParams(castorCompany.getObjectId());
			LoggingManager.log(logger, "Pas d'\"objectVersion\" pour ce \"Company\" ().", params, Level.INFO);
		}
		
		// CreationTime optionnel
		java.util.Date castorCreationTime = castorCompany.getCreationTime();
		if (castorCreationTime == null) {
			if (castorCompany.getName() != null)
				params = LoggingManager.getParams(castorCompany.getName());
			else
				params = LoggingManager.getParams(castorCompany.getObjectId());
			LoggingManager.log(logger, "Pas de \"creationTime\" pour ce \"Company\" ().", params, Level.INFO);
		}
		else
			if (castorCreationTime.after(new java.util.Date(System.currentTimeMillis()))) {
				if (castorCompany.getName() != null)
					params = LoggingManager.getParams(castorCreationTime.toString(), castorCompany.getName());
				else
					params = LoggingManager.getParams(castorCreationTime.toString(), castorCompany.getObjectId());
				LoggingManager.log(logger, "La \"creationTime\" () de ce \"Company\" () est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDCREATIONTIME_COMPANY, "La \"creationTime\" () de ce \"Company\" () est invalide.", params);
			}
			else
				company.setCreationTime(castorCreationTime);
		
		// CreatorId optionnel
		String castorCreatorId = castorCompany.getCreatorId();
		params = null;
		if (castorCompany.getName() != null)
			params = new String[]{castorCompany.getName()};
		else if (castorCompany.getObjectId() != null)
			params = new String[]{castorCompany.getObjectId()};			
		if (castorCreatorId == null)
			LoggingManager.log(logger, "Pas de \"creatorId\" pour ce \"Company\" ().", params, Level.INFO);
		else {
			castorCreatorId = castorCreatorId.trim();
			if (castorCreatorId.length() == 0)
				LoggingManager.log(logger, "L'objet de type \"creatorId\" dans ce \"Company\" () est vide.", params, Level.WARN);
			else
				company.setCreatorId(castorCreatorId);
		}
		
		// Name obligatoire
		String castorName = castorCompany.getName();
		params = null;
		if (castorCompany.getObjectId() != null)
			params = new String[]{castorCompany.getObjectId()};			
		if (castorName == null) {
			LoggingManager.log(logger, "Pas de \"name\" pour ce \"Company\" ().", params, Level.ERROR);
			validationException.add(TypeInvalidite.NONAME_COMPANY, "Pas de \"name\" pour ce \"Company\" ().", params);
		}
		else {
			castorName = castorName.trim();
			if (castorName.length() == 0) {
				LoggingManager.log(logger, "Pas de \"name\" pour ce \"Company\" ().", params, Level.ERROR);
				validationException.add(TypeInvalidite.NONAME_COMPANY, "Pas de \"name\" pour ce \"Company\" ().", params);
			}
			else
				company.setName(castorName);
		}
		
		// ShortName optionnel
		String castorShortName = castorCompany.getShortName();
		params = null;
		if (castorCompany.getName() != null)
			params = new String[]{castorCompany.getName()};
		else if (castorCompany.getObjectId() != null)
			params = new String[]{castorCompany.getObjectId()};			
		if (castorShortName == null)
			LoggingManager.log(logger, "Pas de \"shortName\" pour ce \"Company\" ().", params, Level.INFO);
		else {
			castorShortName = castorShortName.trim();
			if (castorShortName.length() == 0)
				LoggingManager.log(logger, "Pas de \"shortName\" vide pour ce \"Company\" ().", params, Level.WARN);
			else
				company.setShortName(castorShortName);
		}
		
		// OrganisationalUnit optionnel
		String castorOrganisationalUnit = castorCompany.getOrganisationalUnit();
		if (castorOrganisationalUnit == null)
			LoggingManager.log(logger, "Pas de \"organisationalUnit\" pour ce \"Company\" ().", params, Level.INFO);
		else {
			castorOrganisationalUnit = castorOrganisationalUnit.trim();
			if (castorOrganisationalUnit.length() == 0)
				LoggingManager.log(logger, "Pas de \"organisationalUnit\" vide pour ce \"Company\" ().", params, Level.WARN);
			else
				company.setOrganisationalUnit(castorOrganisationalUnit);
		}
		
		// OperatingDepartmentName optionnel
		String castorOperatingDepartmentName = castorCompany.getOperatingDepartmentName();
		if (castorOperatingDepartmentName == null)
			LoggingManager.log(logger, "Pas de \"operatingDepartmentName\" pour ce \"Company\" ().", params, Level.INFO);
		else {
			castorOperatingDepartmentName = castorOperatingDepartmentName.trim();
			if (castorOperatingDepartmentName.length() == 0)
				LoggingManager.log(logger, "Pas de \"operatingDepartmentName\" vide pour ce \"Company\" ().", params, Level.WARN);
			else
				company.setOperatingDepartmentName(castorOperatingDepartmentName);
		}
		
		// Code optionnel
		String castorCode = castorCompany.getCode();
		if (castorCode == null)
			LoggingManager.log(logger, "Pas de \"code\" pour ce \"Company\" ().", params, Level.INFO);
		else {
			castorCode = castorCode.trim();
			if (castorCode.length() == 0)
				LoggingManager.log(logger, "Pas de \"code\" vide pour ce \"Company\" ().", params, Level.WARN);
			else
				company.setCode(castorCode);
		}
		
		// Phone optionnel
		String castorPhone = castorCompany.getPhone();
		if (castorPhone == null)
			LoggingManager.log(logger, "Pas de \"phone\" pour ce \"Company\" ().", params, Level.INFO);
		else {
			castorPhone = castorPhone.trim();
			if (castorPhone.length() == 0)
				LoggingManager.log(logger, "Pas de \"phone\" vide pour ce \"Company\" ().", params, Level.WARN);
			else
				company.setPhone(castorPhone);
		}
		
		// Fax optionnel
		String castorFax = castorCompany.getFax();
		if (castorFax == null)
			LoggingManager.log(logger, "Pas de \"fax\" pour ce \"Company\" ().", params, Level.INFO);
		else {
			castorFax = castorFax.trim();
			if (castorFax.length() == 0)
				LoggingManager.log(logger, "Pas de \"fax\" vide pour ce \"Company\" ().", params, Level.WARN);
			else
				company.setFax(castorFax);
		}
		
		// Email optionnel
		String castorEmail = castorCompany.getEmail();
		if (castorEmail == null)
			LoggingManager.log(logger, "Pas de \"email\" pour ce \"Company\" ().", params, Level.INFO);
		else {
			castorEmail = castorEmail.trim();
			if (castorEmail.length() == 0)
				LoggingManager.log(logger, "Pas de \"email\" vide pour ce \"Company\" ().", params, Level.WARN);
			else
				company.setEmail(castorEmail);
		}
		
		// Registartion optionnel
		chouette.schema.Registration castorRegistration = castorCompany.getRegistration();
		if (castorRegistration == null)
			LoggingManager.log(logger, "Pas de \"registration\" pour ce \"Company\" ().", params, Level.INFO);
		else {
			Registration registration = (new RegistrationProducer(validationException)).getASG(castorRegistration);
			if (registration == null)
				LoggingManager.log(logger, "Error lors de la construction de la \"registration\" pour ce \"Company\" ().", params, Level.ERROR);
			else {
				company.setRegistration(registration);
				registration.setCompany(company);
			}
		}
		
		return company;
	}
}
