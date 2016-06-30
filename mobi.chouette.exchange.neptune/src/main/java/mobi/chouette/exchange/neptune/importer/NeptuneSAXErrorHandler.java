package mobi.chouette.exchange.neptune.importer;

import java.io.File;
import java.net.URL;

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
//import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.CheckPointReport;
import mobi.chouette.exchange.validation.report.DataLocation;
//import mobi.chouette.exchange.validation.report.Detail;
//import mobi.chouette.exchange.validation.report.Location;
//import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.exchange.validation.report.CheckPointReport.SEVERITY;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

@Log4j
public class NeptuneSAXErrorHandler implements ErrorHandler, Constant {

	private static final String XML_1 = "1-NEPTUNE-XML-1";
	private static final String XML_2 = "1-NEPTUNE-XML-2";

//	private ValidationReport validationReport;
//	
//	private CheckPoint report1;
//	private CheckPoint report2;
	
	private ValidationReporter validationReporter;
	private Context context;
	private String fileName;

	private static String NO_NAMESPACE_ERROR = "cvc-elt.1: Cannot find the declaration of element 'ChouettePTNetwork'.";

	@Getter
	private boolean hasErrors = false;

	public NeptuneSAXErrorHandler(Context context, String fileURL)
			throws Exception {
//		validationReport = (ValidationReport) context.get(VALIDATION_REPORT);

//		report1 = validationReport.findCheckPointByName(XML_1);
//		if (report1 == null) {
//			report1 = new CheckPoint(XML_1, CheckPoint.RESULT.OK,
//					CheckPoint.SEVERITY.ERROR);
//			validationReport.addCheckPoint(report1);
//		}
//		report2 = validationReport.findCheckPointByName(XML_2);
//		if (report2 == null) {
//			report2 = new CheckPoint(XML_2, CheckPoint.RESULT.OK,
//					CheckPoint.SEVERITY.WARNING);
//			validationReport.addCheckPoint(report2);
//		}
		this.context = context;
		validationReporter = ValidationReporter.Factory.getInstance();
		validationReporter.addItemToValidationReport(context, XML_1, "E");
		validationReporter.addItemToValidationReport(context, XML_2, "W");
		
		fileName = new File(new URL(fileURL).toURI()).getName();
	}

//	public void handleError(Exception error) {
//		if (error instanceof SAXParseException) {
//			SAXParseException cause = (SAXParseException) error;
//			Location location = new Location(fileName, cause.getLineNumber(),
//					cause.getColumnNumber());
//			location.setName("xml-grammar");
//			Detail item = new Detail(XML_1, location, cause.getMessage());
//			report1.addDetail(item);
//		} else {
//			Location location = new Location(fileName, 1, 1);
//			location.setName("xml-failure");
//			Detail item = new Detail(XML_1, location, error.toString());
//			report1.addDetail(item);
//
//		}
//	}
	
	public void handleError(Exception error) {
		if (error instanceof SAXParseException) {
			SAXParseException cause = (SAXParseException) error;
			DataLocation location = new DataLocation(fileName, cause.getLineNumber(), cause.getColumnNumber());
			validationReporter.addCheckPointReportError(context, XML_1, location, cause.getMessage());
		} else {
			DataLocation location = new DataLocation(fileName, 1, 1);
			location.setName("xml-failure");
			validationReporter.addCheckPointReportError(context, XML_1, location, error.toString());
		}
	}

	private void handleError(SAXParseException error, SEVERITY severity) {
		String key = "others";
		if (error.getMessage().contains(":")) {
			String newKey = error.getMessage()
					.substring(0, error.getMessage().indexOf(":")).trim();
			if (!newKey.contains(" ")) {
				if (newKey.contains("."))
					newKey = newKey.substring(0, newKey.indexOf("."));
				key = newKey;
			}
		}
		if (severity.equals(CheckPointReport.SEVERITY.ERROR))
			hasErrors = true;

//		Location location = new Location(fileName, error.getLineNumber(),
//				error.getColumnNumber());
//		location.setName(key);
//		Detail item = new Detail(XML_2, location, error.getMessage());
//		if (report2.getSeverity().ordinal() < severity.ordinal())
//			report2.setSeverity(severity);
//		report2.addDetail(item);
		
		DataLocation location = new DataLocation(fileName, error.getLineNumber(),
				error.getColumnNumber());
		location.setName(key);
		
		validationReporter.addCheckPointReportError(context, XML_2, location, error.getMessage());
		validationReporter.updateCheckPointReportSeverity(context, XML_2, severity);
		log.info("error handled " + error.getMessage());
		return;
	}

	@Override
	public void warning(SAXParseException exception) throws SAXException {
		handleError(exception, SEVERITY.WARNING);

	}

	@Override
	public void error(SAXParseException exception) throws SAXException {
		// forward exception if it may be a namespace declaration problem
		if (exception.getMessage().equals(NO_NAMESPACE_ERROR))
			throw exception;
		handleError(exception, SEVERITY.ERROR);

	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
		handleError(exception, SEVERITY.ERROR);
		throw exception;

	}

}
