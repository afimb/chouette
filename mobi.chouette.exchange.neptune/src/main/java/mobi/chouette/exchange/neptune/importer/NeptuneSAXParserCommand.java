package mobi.chouette.exchange.neptune.importer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.XMLConstants;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.importer.report.FileItem;
import mobi.chouette.exchange.importer.report.Report;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Detail;
import mobi.chouette.exchange.validation.report.Location;
import mobi.chouette.exchange.validation.report.ValidationReport;

import org.apache.commons.io.input.BOMInputStream;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Stateless(name = NeptuneSAXParserCommand.COMMAND)
@Log4j
public class NeptuneSAXParserCommand implements Command, Constant {

	private static final String XML_1 = "1-NEPTUNE-XML-1";
	private static final String XML_2 = "1-NEPTUNE-XML-2";

	public static final String COMMAND = "NeptuneSAXParserCommand";

	public static final String SCHEMA_FILE = "/xsd/neptune.xsd";

	@Override
	public boolean execute(Context context) throws Exception {

		Monitor monitor = MonitorFactory.start(COMMAND);

		Report report = (Report) context.get(REPORT);
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		FileItem fileItem = new FileItem();
		fileItem.setName((String) context.get(FILE_URL));

		Schema schema = (Schema) context.get(SCHEMA);
		if (schema == null) {
			SchemaFactory factory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			NeptuneSAXErrorHandler handler = new NeptuneSAXErrorHandler(context);
			factory.setErrorHandler(handler);
			schema = factory.newSchema(getClass().getResource(
					SCHEMA_FILE));
			context.put(SCHEMA, schema);
		}

		URL url = new URL((String) context.get(FILE_URL));
		log.info("[DSU] validate file : " + url);
		

		Reader reader = new BufferedReader(new InputStreamReader(
				new BOMInputStream(url.openStream())), 8192 * 10);
		Source file = new StreamSource(reader);

		try {
//			NeptuneValidationEventHandler errorHandler = new NeptuneValidationEventHandler(url.getFile());
			Validator validator = schema.newValidator();
			validator.setProperty(
					"http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
					"http://www.trident.org/schema/trident");
//			validator.setErrorHandler(errorHandler);
			validator.reset();
			validator.validate(file);
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		} catch (IOException | SAXException e) {
			log.error(e);
			monitor.stop();
		}

		return false;
	}

	private class NeptuneValidationEventHandler implements
	ErrorHandler
	{

		private String fileName;

		@Getter
		private CheckPoint report = new CheckPoint(XML_2, 2,
				CheckPoint.STATE.OK, CheckPoint.SEVERITY.ERROR);
		@Getter
		private boolean hasErrors = false;

		private NeptuneValidationEventHandler(String fileName)
		{
			this.fileName = fileName;
		}

		public boolean handleError(SAXParseException error,CheckPoint.SEVERITY severity)
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
			return true;
		}

		@Override
		public void error(SAXParseException arg0) throws SAXException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void fatalError(SAXParseException arg0) throws SAXException {
			throw arg0;
			
		}

		@Override
		public void warning(SAXParseException arg0) throws SAXException {
			// TODO Auto-generated method stub
			
		}

	}


	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange.neptune/"
						+ COMMAND;
				result = (Command) context.lookup(name);
			} catch (NamingException e) {
				log.error(e);
			}
			return result;
		}
	}

	static {
		CommandFactory factory = new DefaultCommandFactory();
		CommandFactory.factories.put(NeptuneSAXParserCommand.class.getName(),
				factory);
	}
}
