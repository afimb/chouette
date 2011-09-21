package fr.certu.chouette.exchange.csv.neptune.importer.producer;

import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;
import fr.certu.chouette.exchange.xml.neptune.exception.ExchangeException;
import fr.certu.chouette.exchange.xml.neptune.exception.ExchangeExceptionCode;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;

public class LineProducer extends AbstractModelProducer<Line> {

	public static final String LINE_NAME_TITLE = "Nom de la ligne";
	public static final String PUBLISHED_LINE_NAME_TITLE = "Nom public";
	public static final String NUMBER_TITLE = "Numero de la ligne";
	public static final String COMMENT_TITLE = "Commentaire de la ligne";
	public static final String TRANSPORT_MODE_NAME_TITLE = "Mode de Transport (BUS,METRO,RER,TRAIN ou TRAMWAY)";

	public static final int TITLE_COLUMN = 7;
		
	@Override
	public Line produce(CSVReader csvReader, String[] firstLine){
		Line line = new Line();
		if(firstLine[TITLE_COLUMN].equals(LINE_NAME_TITLE)){
			line.setName(firstLine[TITLE_COLUMN+1]);
		}		
		else{
			return null;
		}
		try {
			line.setPublishedName(loadStringParam(csvReader, PUBLISHED_LINE_NAME_TITLE));
			line.setNumber(loadStringParam(csvReader, NUMBER_TITLE));
			line.setComment(loadStringParam(csvReader, COMMENT_TITLE));
			line.setTransportModeName(TransportModeNameEnum.valueOf(loadStringParam(csvReader, TRANSPORT_MODE_NAME_TITLE)));
			
		} catch (ExchangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return line;
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
