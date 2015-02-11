package mobi.chouette.exchange.neptune.importer;

import java.net.URL;

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Detail;
import mobi.chouette.exchange.validation.report.Location;
import mobi.chouette.exchange.validation.report.Phase;
import mobi.chouette.exchange.validation.report.ValidationReport;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

@Log4j
public class NeptuneSAXErrorHandler implements ErrorHandler, Constant {

	private static final String XML_1 = "1-NEPTUNE-XML-1";
	private static final String XML_2 = "1-NEPTUNE-XML-2";

	private ValidationReport validationReport;
	
	private CheckPoint report ;

	private String fileName ;
	
	@Getter
	private boolean hasErrors = false;

	
	public NeptuneSAXErrorHandler(Context context)throws Exception
	{
	    validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
	    Phase phaseOne = validationReport.findPhaseByName(Phase.GROUP.ONE.name());
	    if (phaseOne == null)
	    {
	    	phaseOne = new Phase(Phase.GROUP.ONE.name());
	    }
	    
	    report = phaseOne.findCheckPointByName(XML_2);
	    if (report == null)
	    {
	    report = new CheckPoint(XML_2, 2,
				CheckPoint.STATE.OK, CheckPoint.SEVERITY.WARNING);
	    }
	    URL url = new URL((String) context.get(FILE_URL));
	    fileName = url.getFile();
	}

	private void handleError(SAXParseException error,CheckPoint.SEVERITY severity)
	{
		String key = "others";
		if (error.getMessage().contains(":"))
		{
			String newKey = error.getMessage()
					.substring(0, error.getMessage().indexOf(":")).trim();
			if (!newKey.contains(" "))
			{
				if (newKey.contains("."))
					newKey = newKey.substring(0, newKey.indexOf("."));
				key = newKey;
			}
		}
		if (severity.equals(CheckPoint.SEVERITY.ERROR))
			hasErrors = true;

		Location location = new Location(fileName, error.getLineNumber(), error.getColumnNumber());
		location.setName(key);
		Detail item = new Detail(XML_1,
				location, error.getMessage());
		if (report.getSeverity().ordinal() < severity.ordinal())
			report.setSeverity(severity);
		report.addDetail(item);
		log.info("error handled "+ error.getMessage());
		return ;
	}

	@Override
	public void warning(SAXParseException exception) throws SAXException {
		handleError(exception, CheckPoint.SEVERITY.WARNING);

	}

	@Override
	public void error(SAXParseException exception) throws SAXException {
		handleError(exception, CheckPoint.SEVERITY.ERROR);
		throw exception;

	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
		handleError(exception, CheckPoint.SEVERITY.ERROR);
		throw exception;

	}

}
