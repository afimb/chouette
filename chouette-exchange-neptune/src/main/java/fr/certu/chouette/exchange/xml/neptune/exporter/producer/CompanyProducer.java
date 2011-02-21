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
		castorCompany.setCode(company.getCode());
		castorCompany.setEmail(company.getEmail());
		castorCompany.setFax(company.getFax());
		castorCompany.setOperatingDepartmentName(company.getOperatingDepartmentName());
		castorCompany.setOrganisationalUnit(company.getOrganisationalUnit());
		castorCompany.setPhone(company.getPhone());
		castorCompany.setShortName(company.getShortName());
		
		return castorCompany;
	}

}
