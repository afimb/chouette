package fr.certu.chouette.exchange.gtfs.importer.producer;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsAgency;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.plugin.report.Report;

public class CompanyProducer extends AbstractModelProducer<Company, GtfsAgency>
{
   private static Logger logger = Logger.getLogger(CompanyProducer.class);

   @Override
   public Company produce(GtfsAgency gtfsAgency, Report report)
   {

      Company company = new Company();

      company.setObjectId(composeObjectId(Company.COMPANY_KEY,
            gtfsAgency.getAgencyId(), logger));

      // Name mandatory
      company.setName(getNonEmptyTrimedString(gtfsAgency.getAgencyName()));

      // OrganisationalUnit : URL Mandatory
      if (gtfsAgency.getAgencyUrl() != null)
         company.setOrganisationalUnit(getNonEmptyTrimedString(gtfsAgency
               .getAgencyUrl().toString()));

      // Phone optional
      company.setPhone(getNonEmptyTrimedString(gtfsAgency.getAgencyPhone()));

      // RegistrationNumber optional
      String[] token = company.getObjectId().split(":");
      company.setRegistrationNumber(token[2]);

      return company;
   }

}
