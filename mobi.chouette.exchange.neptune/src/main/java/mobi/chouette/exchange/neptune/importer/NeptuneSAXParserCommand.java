package mobi.chouette.exchange.neptune.importer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;

import org.apache.commons.io.input.BOMInputStream;
import org.xml.sax.SAXException;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Stateless(name = NeptuneSAXParserCommand.COMMAND)
@Log4j
public class NeptuneSAXParserCommand implements Command, Constant {

	public static final String COMMAND = "NeptuneSAXParserCommand";

	public static final String SCHEMA_FILE = "/xsd/neptune.xsd";

	@Override
	public boolean execute(Context context) throws Exception {

		Monitor monitor = MonitorFactory.start(COMMAND);

		Validator validator = (Validator) context.get(VALIDATOR);
		if (validator == null) {
			SchemaFactory factory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			NeptuneSAXErrorHandler handler = new NeptuneSAXErrorHandler(context);
			factory.setErrorHandler(handler);
			Schema schema = factory.newSchema(getClass().getResource(
					SCHEMA_FILE));
			validator = schema.newValidator();
			context.put(VALIDATOR, validator);
		}

		URL url = new URL((String) context.get(FILE_URL));		
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new BOMInputStream(url.openStream())), 8192 * 10);
		Source file = new StreamSource(reader);

		try {
			validator.validate(file);
			log.info(Color.MAGENTA + "[DSU] " + monitor.stop() + Color.NORMAL);
		} catch (IOException | SAXException e) {
			log.error(e);
			monitor.stop();
		}

		return false;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange.neptune/" + COMMAND;
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
