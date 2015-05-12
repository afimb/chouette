package mobi.chouette.exchange.netex.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import javax.naming.InitialContext;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileError;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;

import org.apache.commons.io.input.BOMInputStream;
import org.xml.sax.SAXException;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class NetexSAXParserCommand implements Command, Constant {

	public static final String COMMAND = "NetexSAXParserCommand";

	public static final String SCHEMA_FILE = "/xsd/chouette-netex.xsd";

	@Getter
	@Setter
	private String fileURL;

	@Override
	public boolean execute(Context context) throws Exception {

		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		ActionReport report = (ActionReport) context.get(REPORT);

		String fileName = new File(new URL(fileURL).toURI()).getName();

		FileInfo fileItem = new FileInfo(fileName,FILE_STATE.OK);

		Schema schema = (Schema) context.get(SCHEMA);
		if (schema == null) {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			schema = factory.newSchema(getClass().getResource(SCHEMA_FILE));
			context.put(SCHEMA, schema);
		}

		URL url = new URL(fileURL);

		NetexSAXErrorHandler handler = new NetexSAXErrorHandler(context, fileURL);
		Reader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new BOMInputStream(url.openStream())), 8192 * 10);
			StreamSource file = new StreamSource(reader);

			Validator validator = schema.newValidator();
			validator.setErrorHandler(handler);
			validator.validate(file);
			result = SUCCESS;
		} catch (IOException | SAXException e) {
			log.error(e.getMessage(), e);
			report.getFiles().add(fileItem);
			fileItem.addError(new FileError(FileError.CODE.INVALID_FORMAT,e.getMessage()));
		} finally {
			if (reader != null ) reader.close();
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}

		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new NetexSAXParserCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(NetexSAXParserCommand.class.getName(), new DefaultCommandFactory());
	}
}
