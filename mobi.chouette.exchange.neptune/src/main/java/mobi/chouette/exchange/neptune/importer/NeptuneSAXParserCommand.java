package mobi.chouette.exchange.neptune.importer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import javax.naming.InitialContext;
import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.Report;

import org.apache.commons.io.input.BOMInputStream;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

// @Stateless(name = NeptuneSAXParserCommand.COMMAND)
@Log4j
public class NeptuneSAXParserCommand implements Command, Constant {

	public static final String COMMAND = "NeptuneSAXParserCommand";

	public static final String SCHEMA_FILE = "/xsd/neptune.xsd";

	@Getter
	@Setter
	private String fileURL;

	@Override
	public boolean execute(Context context) throws Exception {

		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		Report report = (Report) context.get(REPORT);
		FileInfo fileItem = new FileInfo();
		fileItem.setName(fileURL);

		Schema schema = (Schema) context.get(SCHEMA);
		if (schema == null) {
			SchemaFactory factory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			schema = factory.newSchema(getClass().getResource(SCHEMA_FILE));
			context.put(SCHEMA, schema);
		}

		URL url = new URL(fileURL);
		log.info("[DSU] validate file : " + url);

		Reader reader = new BufferedReader(new InputStreamReader(
				new BOMInputStream(url.openStream())), 8192 * 10);
		InputSource source = new InputSource(reader);
		// StreamSource file = new StreamSource(reader);

		try {
			NeptuneSAXErrorHandler errorHandler = new NeptuneSAXErrorHandler(
					context, fileURL);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			// factory.setNamespaceAware(true);
			factory.setSchema(schema);
			SAXParser parser = factory.newSAXParser();

			// parser.setProperty(
			// "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
			// "http://www.trident.org/schema/trident");
			XMLReader xmlReader = parser.getXMLReader();
			xmlReader.setErrorHandler(errorHandler);
			XMLFilterImpl xmlFilter = new NeptuneNamespaceFilter(xmlReader);
			xmlFilter.parse(source);
			// Validator validator = schema.newValidator();
			// validator.setProperty(
			// "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
			// "http://www.trident.org/schema/trident");
			// validator.setErrorHandler(errorHandler);
			// validator.reset();
			// validator.validate(file);
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		} catch (IOException | SAXException e) {
			log.error(e);
			fileItem.setStatus(FileInfo.STATE.NOK);
			report.getFiles().getFileInfos().add(fileItem);
			fileItem.getErrors().add(e.getMessage());
			monitor.stop();
		}

		result = SUCCESS;

		return result;

	}

	private class NeptuneNamespaceFilter extends XMLFilterImpl {
		public NeptuneNamespaceFilter(XMLReader arg0) {
			super(arg0);

		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if (uri.isEmpty())
				uri = "http://www.trident.org/schema/trident";
			super.startElement(uri, localName, qName, attributes);
		}
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new NeptuneSAXParserCommand();
			// try {
			// String name = "java:app/mobi.chouette.exchange.neptune/"
			// + COMMAND;
			// result = (Command) context.lookup(name);
			// } catch (NamingException e) {
			// log.error(e);
			// }
			return result;
		}
	}

	static {
		CommandFactory.factories.put(NeptuneSAXParserCommand.class.getName(),
				new DefaultCommandFactory());
	}
}
