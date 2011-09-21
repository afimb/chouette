/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.exchange.csv.neptune.importer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;
import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.csv.neptune.importer.producer.CompanyProducer;
import fr.certu.chouette.exchange.csv.neptune.importer.producer.LineProducer;
import fr.certu.chouette.exchange.csv.neptune.importer.producer.PTNetworkProducer;
import fr.certu.chouette.exchange.csv.neptune.importer.producer.TimetableProducer;
import fr.certu.chouette.exchange.xml.neptune.exception.ExchangeException;
import fr.certu.chouette.exchange.xml.neptune.exception.ExchangeExceptionCode;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.plugin.report.ReportItem;

public class CSVImportLinePlugin implements IImportPlugin<Line> 
{

	private static final Logger logger = Logger.getLogger(CSVImportLinePlugin.class);

	private FormatDescription description;

	private List<String> allowedExtensions = Arrays.asList(new String[]{"csv"});

	@Getter @Setter private TimetableProducer timetableProducer;
	@Getter @Setter private PTNetworkProducer ptNetworkProducer;
	@Getter @Setter private CompanyProducer companyProducer;
	@Getter @Setter private LineProducer lineProducer;

	
	/**
	 * 
	 */
	public CSVImportLinePlugin()
	{
		description = new FormatDescription(this.getClass().getName()) ;
		description.setName("CSVLine");
		List<ParameterDescription> params = new ArrayList<ParameterDescription>();
		ParameterDescription param1 = new ParameterDescription("csvFile",ParameterDescription.TYPE.FILEPATH,false,true);
		param1.setAllowedExtensions(Arrays.asList(new String[]{"csv"}));
		params.add(param1);
		description.setParameterDescriptions(params);		
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.plugin.exchange.IExchangePlugin#getDescription()
	 */
	@Override
	public FormatDescription getDescription() 
	{
		return description;
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.plugin.exchange.IImportPlugin#doImport(java.util.List, fr.certu.chouette.plugin.report.ReportHolder)
	 */
	@Override
	public List<Line> doImport(List<ParameterValue> parameters,ReportHolder reportContainer)
	throws ChouetteException 
	{
		String filePath = null;
		for (ParameterValue value : parameters) 
		{
			if (value instanceof SimpleParameterValue)
			{
				SimpleParameterValue svalue = (SimpleParameterValue) value;
				if (svalue.getName().equals("csvFile"))
				{
					filePath = svalue.getFilepathValue();
				}
				else
				{
					throw new IllegalArgumentException("unexpected argument "+svalue.getName());
				}
			}
			else
			{
				throw new IllegalArgumentException("unexpected argument "+value.getName());
			}
		}
		if (filePath == null) 
		{
			logger.error("missing argument csvFile");
			throw new IllegalArgumentException("csvFile required");
		}

		String extension = FilenameUtils.getExtension(filePath).toLowerCase();
		if (!allowedExtensions.contains(extension))
		{
			logger.error("invalid argument csvFile "+filePath+", allowed format : "+Arrays.toString(allowedExtensions.toArray()));
			throw new IllegalArgumentException("invalid file type : "+extension);
		}

		List<Line> lines = null ; 

		// simple file processing
		logger.info("start import simple file "+filePath);
		lines = processImport(filePath);
		logger.info("import terminated");
		return lines;
	}

	/**
	 * @param rootObject
	 * @param validate
	 * @param report
	 * @param entryName
	 * @return
	 * @throws ExchangeException
	 */
	private List<Line> processImport(String filePath) throws ExchangeException 
	{
		CSVReader csvReader = null;
		List<Timetable> timetables = new ArrayList<Timetable>();
		PTNetwork ptNetwork= null;
		Company company = null;
		List<Line> lines = new ArrayList<Line>();
		
		try {
			csvReader = new CSVReader(new FileReader(filePath));
		} catch (FileNotFoundException e) {
			throw new ExchangeException(ExchangeExceptionCode.FILE_NOT_FOUND, filePath);
		}
		
		String[] currentLine;
		try {
			currentLine = csvReader.readNext();
		} catch (IOException e) {
			throw new ExchangeException(ExchangeExceptionCode.INVALID_CSV_FILE, filePath);
		}
		
		while(currentLine.equals(TimetableProducer.TIMETABLE_LABEL_TITLE)){
			timetables.add(timetableProducer.produce(csvReader, currentLine));
			try {
				currentLine = csvReader.readNext(); //empty line
				currentLine = csvReader.readNext();
			} catch (IOException e) {
				throw new ExchangeException(ExchangeExceptionCode.INVALID_CSV_FILE, filePath);
			}
		}
		
		ptNetwork = ptNetworkProducer.produce(csvReader, currentLine);
		try {
			currentLine = csvReader.readNext(); //empty line
			currentLine = csvReader.readNext();
		} catch (IOException e) {
			throw new ExchangeException(ExchangeExceptionCode.INVALID_CSV_FILE, filePath);
		}
		
		company = companyProducer.produce(csvReader, currentLine);
		try {
			currentLine = csvReader.readNext(); //empty line
			currentLine = csvReader.readNext();
		} catch (IOException e) {
			throw new ExchangeException(ExchangeExceptionCode.INVALID_CSV_FILE, filePath);
		}
		
		while(currentLine.equals(LineProducer.LINE_NAME_TITLE)){
			lines.add(lineProducer.produce(csvReader, currentLine));
			try {
				currentLine = csvReader.readNext(); //empty line
				currentLine = csvReader.readNext();
			} catch (IOException e) {
				throw new ExchangeException(ExchangeExceptionCode.INVALID_CSV_FILE, filePath);
			}
		}
		

		return lines;
	}


}
