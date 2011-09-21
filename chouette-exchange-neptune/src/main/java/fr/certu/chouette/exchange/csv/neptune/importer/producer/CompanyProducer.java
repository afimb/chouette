package fr.certu.chouette.exchange.csv.neptune.importer.producer;

import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;
import fr.certu.chouette.exchange.xml.neptune.exception.ExchangeException;
import fr.certu.chouette.exchange.xml.neptune.exception.ExchangeExceptionCode;
import fr.certu.chouette.model.neptune.Company;

public class CompanyProducer extends AbstractModelProducer<Company> {

	public static final String COMPANY_NAME_TITLE = "Nom de l'entreprise de transport";
	public static final String CODE_TITLE = "Code Transporteur";
	public static final String SHORT_NAME_TITLE = "Nom court";
	public static final String DESCRIPTION_TITLE = "Description du transporteur";
	public static final String ZIPCODE_TITLE = "Code postal";
	public static final String PHONE_TITLE = "Téléphone";
	public static final String FAX_TITLE = "Fax";
	public static final String EMAIL_TITLE = "Email";
	
	public static final int TITLE_COLUMN = 7;
		
	@Override
	public Company produce(CSVReader csvReader, String[] firstLine){
		Company company = new Company();
		if(firstLine[TITLE_COLUMN].equals(COMPANY_NAME_TITLE)){
			company.setName(firstLine[TITLE_COLUMN+1]);
		}		
		else{
			return null;
		}
		try {
			company.setRegistrationNumber(loadStringParam(csvReader, CODE_TITLE));
			company.setShortName(loadStringParam(csvReader, DESCRIPTION_TITLE));
			company.setOrganisationalUnit(loadStringParam(csvReader, DESCRIPTION_TITLE));
			company.setCode(loadStringParam(csvReader, ZIPCODE_TITLE));
			company.setPhone(loadStringParam(csvReader, PHONE_TITLE));
			company.setFax(loadStringParam(csvReader, FAX_TITLE));
			company.setEmail(loadStringParam(csvReader, EMAIL_TITLE));

		} catch (ExchangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return company;
	}

	private String loadStringParam(CSVReader csvReader, String title) throws ExchangeException{
		String[] currentLine = null;
		try {
			currentLine = csvReader.readNext();
		} catch (IOException e) {
			throw new ExchangeException(ExchangeExceptionCode.INVALID_CSV_FILE, e);
		}
		if(currentLine[TITLE_COLUMN].equals(title)){
			return currentLine[TITLE_COLUMN+1];
		}
		else{
			throw new ExchangeException(ExchangeExceptionCode.INVALID_CSV_FILE,"Unable to read '"+title+"' in csv file");
		}
	}
}
