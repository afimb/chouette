package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import fr.certu.chouette.model.neptune.Company;

public class CompanyProducer extends AbstractCastorNeptuneProducer<chouette.schema.Company, Company> {

	@Override
	public chouette.schema.Company produce(Company company) {
		chouette.schema.Company castorCompany = new chouette.schema.Company();
		
		//
		populateFromModel(castorCompany, company);
		
		castorCompany.setName(company.getName());
		castorCompany.setRegistration(getRegistration(company.getRegistrationNumber()));
		castorCompany.setCode(getNotEmptyString(company.getCode()));
		castorCompany.setEmail(getNotEmptyString(company.getEmail()));
		castorCompany.setFax(getNotEmptyString(company.getFax()));
		castorCompany.setOperatingDepartmentName(getNotEmptyString(company.getOperatingDepartmentName()));
		castorCompany.setOrganisationalUnit(getNotEmptyString(company.getOrganisationalUnit()));
		castorCompany.setPhone(getNotEmptyString(company.getPhone()));
		castorCompany.setShortName(getNotEmptyString(company.getShortName()));
		
		return castorCompany;
	}

}
