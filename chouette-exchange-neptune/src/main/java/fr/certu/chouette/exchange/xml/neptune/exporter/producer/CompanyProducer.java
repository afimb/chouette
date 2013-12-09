package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import org.trident.schema.trident.CompanyType;

import fr.certu.chouette.model.neptune.Company;

public class CompanyProducer extends AbstractJaxbNeptuneProducer<CompanyType, Company> {

	@Override
	public CompanyType produce(Company company) {
		CompanyType jaxbCompany = tridentFactory.createCompanyType();
		
		//
		populateFromModel(jaxbCompany, company);
		
		jaxbCompany.setName(company.getName());
		jaxbCompany.setRegistration(getRegistration(company.getRegistrationNumber()));
		jaxbCompany.setCode(getNotEmptyString(company.getCode()));
		jaxbCompany.setEmail(getNotEmptyString(company.getEmail()));
		jaxbCompany.setFax(getNotEmptyString(company.getFax()));
		jaxbCompany.setOperatingDepartmentName(getNotEmptyString(company.getOperatingDepartmentName()));
		jaxbCompany.setOrganisationalUnit(getNotEmptyString(company.getOrganisationalUnit()));
		jaxbCompany.setPhone(getNotEmptyString(company.getPhone()));
		jaxbCompany.setShortName(getNotEmptyString(company.getShortName()));
		
		return jaxbCompany;
	}

}
