package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import org.trident.schema.trident.CompanyType;

import fr.certu.chouette.exchange.xml.neptune.importer.Context;
import fr.certu.chouette.model.neptune.Company;

public class CompanyProducer extends
      AbstractModelProducer<Company, CompanyType>
{

   @Override
   public Company produce(Context context, CompanyType xmlCompany)
   {

      Company company = new Company();

      // objectId, objectVersion, creatorId, creationTime
      populateFromCastorNeptune(context, company, xmlCompany);
      // Name mandatory
      company.setName(getNonEmptyTrimedString(xmlCompany.getName()));

      // ShortName optional
      company.setShortName(getNonEmptyTrimedString(xmlCompany.getShortName()));

      // Code optional
      company.setCode(getNonEmptyTrimedString(xmlCompany.getCode()));

      // Phone optional
      company.setPhone(getNonEmptyTrimedString(xmlCompany.getPhone()));

      // Fax Optional
      company.setFax(getNonEmptyTrimedString(xmlCompany.getFax()));

      // Email Optional
      company.setEmail(getNonEmptyTrimedString(xmlCompany.getEmail()));

      // RegistrationNumber optional
      company.setRegistrationNumber(getRegistrationNumber(context, 
            xmlCompany.getRegistration()));

      // OperatingDepartmentName optional
      company.setOperatingDepartmentName(getNonEmptyTrimedString(xmlCompany
            .getOperatingDepartmentName()));

      // OrganisationalUnit optional
      company.setOrganisationalUnit(getNonEmptyTrimedString(xmlCompany
            .getOrganisationalUnit()));

      Company sharedBean = getOrAddSharedData(context, company, xmlCompany);
      if (sharedBean != null)
         return sharedBean;
      return company;
   }

}
