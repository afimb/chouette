package fr.certu.chouette.exchange.csv.neptune.importer.producer;

import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;
import fr.certu.chouette.exchange.xml.neptune.exception.ExchangeException;
import fr.certu.chouette.exchange.xml.neptune.exception.ExchangeExceptionCode;
import fr.certu.chouette.model.neptune.PTNetwork;

public class PTNetworkProducer extends AbstractModelProducer<PTNetwork> {

	public static final String PTNETWORK_NAME_TITLE = "Nom du réseau";
	public static final String CODE_TITLE = "Code Réseau";
	public static final String DESCRIPTION_TITLE = "Description du réseau";
	
	public static final int TITLE_COLUMN = 7;
		
	@Override
	public PTNetwork produce(CSVReader csvReader, String[] firstLine){
		PTNetwork ptNetwork = new PTNetwork();
		if(firstLine[TITLE_COLUMN].equals(PTNETWORK_NAME_TITLE)){
			ptNetwork.setName(firstLine[TITLE_COLUMN+1]);
		}		
		else{
			return null;
		}
		try {
			ptNetwork.setRegistrationNumber(loadStringParam(csvReader, CODE_TITLE));
			ptNetwork.setDescription(loadStringParam(csvReader, DESCRIPTION_TITLE));
		} catch (ExchangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ptNetwork;
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
