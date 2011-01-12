package fr.certu.chouette.exchange.xml.neptune;

import fr.certu.chouette.model.neptune.Company;

public class CompanyProducer extends AbstractModelProducer<Company, chouette.schema.Company> {

	@Override
	public Company produce(chouette.schema.Company xmlCompagny) {
		Company company = new Company();
		
		// objectId, objectVersion, creatorId, creationTime
		populateTridentObject(company, xmlCompagny);
		
		// Name mandatory
		company.setName(getNonEmptyTrimedString(xmlCompagny.getName()));
		
		// ShortName optional
		company.setShortName(getNonEmptyTrimedString(xmlCompagny.getShortName()));
		
		// Code optional
		company.setCode(getNonEmptyTrimedString(xmlCompagny.getCode()));

		// Phone optional
		company.setPhone(getNonEmptyTrimedString(xmlCompagny.getPhone()));
		
		//Fax Optional
		company.setFax(getNonEmptyTrimedString(xmlCompagny.getFax()));
		
		//Email Optional
		company.setEmail(getNonEmptyTrimedString(xmlCompagny.getEmail()));
		
		//RegistrationNumber optional
		company.setRegistrationNumber(getNonEmptyTrimedString(xmlCompagny.getRegistration().getRegistrationNumber()));
		
		//OperatingDepartmentName optional
		company.setOperatingDepartmentName(getNonEmptyTrimedString(xmlCompagny.getOperatingDepartmentName()));
		
		//OrganisationalUnit optional 
		company.setOrganisationalUnit(getNonEmptyTrimedString(xmlCompagny.getOrganisationalUnit()));
		
		return company;
	}

}
