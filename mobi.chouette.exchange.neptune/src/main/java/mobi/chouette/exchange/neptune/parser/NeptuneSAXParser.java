package mobi.chouette.exchange.neptune.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.ejb.Stateless;
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
import mobi.chouette.exchange.neptune.Constant;

import org.xml.sax.SAXException;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Stateless
@Log4j
public class NeptuneSAXParser implements Command, Constant {

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
			log.info(Color.SUCCESS + "[DSU] " + monitor.stop()
					+ Color.NORMAL);
		} catch (IOException | SAXException e) {
			log.error(e);
			monitor.stop();
		}

		return false;
	}

}
