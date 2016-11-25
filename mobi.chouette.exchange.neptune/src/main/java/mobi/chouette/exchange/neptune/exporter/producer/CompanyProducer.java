package mobi.chouette.exchange.neptune.exporter.producer;

import mobi.chouette.common.Context;
import mobi.chouette.model.Company;

import org.trident.schema.trident.CompanyType;

public class CompanyProducer extends
      AbstractJaxbNeptuneProducer<CompanyType, Company>
{

   //@Override
   public CompanyType produce(Context context, Company company, boolean addExtension)
   {
      CompanyType jaxbCompany = tridentFactory.createCompanyType();

      //
      populateFromModel(context, jaxbCompany, company);

      jaxbCompany.setName(company.getName());
      jaxbCompany.setRegistration(getRegistration(company
            .getRegistrationNumber()));
      jaxbCompany.setCode(getNotEmptyString(company.getCode()));
      jaxbCompany.setEmail(getNotEmptyString(company.getEmail()));
      jaxbCompany.setFax(getNotEmptyString(company.getFax()));
      jaxbCompany.setOperatingDepartmentName(getNotEmptyString(company
            .getOperatingDepartmentName()));
      jaxbCompany.setOrganisationalUnit(getNotEmptyString(company
            .getOrganisationalUnit()));
      jaxbCompany.setPhone(getNotEmptyString(company.getPhone()));
      jaxbCompany.setShortName(getNotEmptyString(company.getShortName()));

      return jaxbCompany;
   }

}
