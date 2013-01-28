package fr.certu.chouette.exchange.gtfs.importer.producer;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.model.GtfsAgency;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.plugin.report.ReportItem;

public class CompanyProducer extends AbstractModelProducer<Company, GtfsAgency> 
{
    private static Logger logger = Logger.getLogger(CompanyProducer.class);
	@Override
	public Company produce(GtfsAgency gtfsAgency,ReportItem report) 
	{

		Company company = new Company();
		
		company.setObjectId(composeObjectId( Company.COMPANY_KEY, gtfsAgency.getAgencyId(),logger));
		
		// Name mandatory
		company.setName(getNonEmptyTrimedString(gtfsAgency.getAgencyName()));
		
		// Code optional
		company.setCode(getNonEmptyTrimedString(gtfsAgency.getAgencyId()));

		// Phone optional
		company.setPhone(getNonEmptyTrimedString(gtfsAgency.getAgencyPhone()));
				
		//RegistrationNumber optional
		String[] token = company.getObjectId().split(":");
		company.setRegistrationNumber(token[2]);
		
		return company;
	}

}
