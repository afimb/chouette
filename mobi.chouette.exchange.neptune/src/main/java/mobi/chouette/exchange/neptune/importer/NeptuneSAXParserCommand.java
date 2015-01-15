package mobi.chouette.exchange.neptune.importer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.neptune.Constant;

import org.xml.sax.SAXException;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Stateless(name = NeptuneSAXParserCommand.COMMAND)
@Log4j
public class NeptuneSAXParserCommand implements Command, Constant {

	public static final String COMMAND = "NeptuneSAXParserCommand";

	private static final String FILE = "/xsd/neptune.xsd";

	@Override
	public boolean execute(Context context) throws Exception {

		Monitor monitor = MonitorFactory.start();

		Schema schema = (Schema) context.get(NEPTUNE_SCHEMA);
		if (schema != null) {
			InputStream in = this.getClass().getResourceAsStream(FILE);
			Source source = new StreamSource(in);
			SchemaFactory factory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			NeptuneErrorHandler handler = new NeptuneErrorHandler(context);
			factory.setErrorHandler(handler);
			schema = factory.newSchema(source);
			context.put(NEPTUNE_SCHEMA, schema);
		}

		String path = (String) context.get(NEPTUNE_FILE);
		Source file = new StreamSource(new File(path));

		Validator validator = schema.newValidator();
		try {
			validator.validate(file);
			log.info(Color.SUCCESS + "[DSU] " + monitor.stop() + Color.NORMAL);
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
				result = (Command) context.lookup(JAVA_MODULE + COMMAND);
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
