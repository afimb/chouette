package fr.certu.chouette.exchange.csv.exporter.producer;

import java.util.ArrayList;
import java.util.List;

import fr.certu.chouette.model.neptune.Company;

public class CompanyProducer extends AbstractCSVNeptuneProducer<Company> {

	   public static final String  COMPANY_NAME_TITLE = "Nom de l'entreprise de transport";
	   public static final String  CODE_TITLE         = "Code Transporteur";
	   public static final String  SHORT_NAME_TITLE   = "Nom court";
	   public static final String  DESCRIPTION_TITLE  = "Description du transporteur";
	   public static final String  ZIPCODE_TITLE      = "Code postal";
	   public static final String  PHONE_TITLE        = "Téléphone";
	   public static final String  FAX_TITLE          = "Fax";
	   public static final String  EMAIL_TITLE        = "Email";
	   
	@Override
	public List<String[]> produce(Company company) {
		List<String[]> csvLinesList = new ArrayList<String[]>();
		csvLinesList.add(createCSVLine(COMPANY_NAME_TITLE, company.getName()));
		csvLinesList.add(createCSVLine(CODE_TITLE, company.getRegistrationNumber()));
		csvLinesList.add(createCSVLine(SHORT_NAME_TITLE, company.getShortName()));
		csvLinesList.add(createCSVLine(DESCRIPTION_TITLE, company.getOrganisationalUnit()));
		csvLinesList.add(createCSVLine(ZIPCODE_TITLE, company.getCode()));
		csvLinesList.add(createCSVLine(PHONE_TITLE, company.getPhone()));
		csvLinesList.add(createCSVLine(FAX_TITLE, company.getFax()));
		csvLinesList.add(createCSVLine(EMAIL_TITLE, company.getEmail()));

		return csvLinesList;
	}

}
