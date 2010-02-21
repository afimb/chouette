package fr.certu.chouette.service.validation.amivif.util;

import fr.certu.chouette.service.validation.amivif.Company;
import fr.certu.chouette.service.validation.amivif.TridentObject;
import fr.certu.chouette.service.validation.amivif.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.amivif.commun.ValidationException;

public class CompanyProducer extends TridentObjectProducer {
    
    private RegistrationProducer	registrationProducer	= new RegistrationProducer(getValidationException());

    public CompanyProducer(ValidationException validationException) {
    	super(validationException);
	}

	public Company getASG(amivif.schema.Company castorCompany) {
		if (castorCompany == null)
			return null;
		
		// TridentObject obligatoire
		TridentObject tridentObject = super.getASG(castorCompany);
		Company company = new Company();
		company.setTridentObject(tridentObject);
		
		// name obligatoire
		String castorName = castorCompany.getName();
		if ((castorName == null) || (castorName.length() == 0))
			getValidationException().add(TypeInvalidite.NoName_Company, "Une \"Company\" doit toujours avoir un \"name\".");
		else
			company.setName(castorName);
		
		// shortName optionnel
		company.setShortName(castorCompany.getShortName());
		
		// organisationalUnit optionnel
		company.setOrganisationalUnit(castorCompany.getOrganisationalUnit());
		
		// operatingDepartmentName
		company.setOperatingDepartmentName(castorCompany.getOperatingDepartmentName());
		
		// code optionnel
		company.setCode(castorCompany.getCode());
		
		// phone optionnel
		company.setPhone(castorCompany.getCode());
		
		// fax optionnel
		company.setFax(castorCompany.getFax());
		
		// email
		company.setEmail(castorCompany.getEmail());
		
		// registration optionnel
		company.setRegistration(registrationProducer.getASG(castorCompany.getRegistration()));
		if (company.getRegistration() != null)
			if (company.getRegistration().getCompanyId() != null)
				if (company.getRegistration().getCompanyId().equals(company.getObjectId().toString()))
					company.getRegistration().setCompany(company);
				else
					getValidationException().add(TypeInvalidite.InvalidRegistartion_Company, "La \"companyId\" ("+company.getRegistration().getCompanyId()+") de la \"registration\" ne correspond pas a l'identifiant de la \"Company\" ("+company.getObjectId().toString()+").");
		
		return company;
	}
}
