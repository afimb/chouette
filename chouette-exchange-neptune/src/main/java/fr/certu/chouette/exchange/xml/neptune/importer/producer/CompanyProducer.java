package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import org.trident.schema.trident.CompanyType;

import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.plugin.exchange.SharedImportedData;
import fr.certu.chouette.plugin.exchange.UnsharedImportedData;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;

public class CompanyProducer extends
      AbstractModelProducer<Company, CompanyType>
{

   @Override
   public Company produce(String sourceFile, CompanyType xmlCompany,
         ReportItem importReport, PhaseReportItem validationReport,
         SharedImportedData sharedData, UnsharedImportedData unshareableData)
   {

      Company company = new Company();

      // objectId, objectVersion, creatorId, creationTime
      populateFromCastorNeptune(company, xmlCompany, importReport);
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
      company.setRegistrationNumber(getRegistrationNumber(
            xmlCompany.getRegistration(), importReport));

      // OperatingDepartmentName optional
      company.setOperatingDepartmentName(getNonEmptyTrimedString(xmlCompany
            .getOperatingDepartmentName()));

      // OrganisationalUnit optional
      company.setOrganisationalUnit(getNonEmptyTrimedString(xmlCompany
            .getOrganisationalUnit()));

      Company sharedBean = getOrAddSharedData(sharedData, company, sourceFile,
            xmlCompany, validationReport);
      if (sharedBean != null)
         return sharedBean;
      return company;
   }

}
