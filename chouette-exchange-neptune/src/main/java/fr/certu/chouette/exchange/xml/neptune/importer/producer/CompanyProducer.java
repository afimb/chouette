package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import fr.certu.chouette.exchange.xml.neptune.importer.SharedImportedData;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.plugin.report.ReportItem;

public class CompanyProducer extends AbstractModelProducer<Company, chouette.schema.Company> {

	@Override
	public Company produce(chouette.schema.Company xmlCompany,ReportItem report,SharedImportedData sharedData) 
	{

		Company company = new Company();
		
		// objectId, objectVersion, creatorId, creationTime
		populateFromCastorNeptune(company, xmlCompany, report);
		Company sharedBean = sharedData.get(company);
      if (sharedBean != null) return sharedBean;
		// Name mandatory
		company.setName(getNonEmptyTrimedString(xmlCompany.getName()));
		
		// ShortName optional
		company.setShortName(getNonEmptyTrimedString(xmlCompany.getShortName()));
		
		// Code optional
		company.setCode(getNonEmptyTrimedString(xmlCompany.getCode()));

		// Phone optional
		company.setPhone(getNonEmptyTrimedString(xmlCompany.getPhone()));
		
		//Fax Optional
		company.setFax(getNonEmptyTrimedString(xmlCompany.getFax()));
		
		//Email Optional
		company.setEmail(getNonEmptyTrimedString(xmlCompany.getEmail()));
		
		//RegistrationNumber optional
		company.setRegistrationNumber(getRegistrationNumber(xmlCompany.getRegistration(),report));
		
		//OperatingDepartmentName optional
		company.setOperatingDepartmentName(getNonEmptyTrimedString(xmlCompany.getOperatingDepartmentName()));
		
		//OrganisationalUnit optional 
		company.setOrganisationalUnit(getNonEmptyTrimedString(xmlCompany.getOrganisationalUnit()));
		
		return company;
	}

}
