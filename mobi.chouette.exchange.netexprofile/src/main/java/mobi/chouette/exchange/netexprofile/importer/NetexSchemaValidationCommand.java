package mobi.chouette.exchange.netexprofile.importer;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.model.util.Referential;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.naming.InitialContext;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.net.URL;

@Log4j
public class NetexSchemaValidationCommand implements Command, Constant {

	public static final String COMMAND = "NetexSchemaValidationCommand";

	@Getter
	@Setter
	private String fileURL;

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);
		ActionReporter actionReporter = ActionReporter.Factory.getInstance();
		File file = new File(new URL(fileURL).toURI());
		final String fileName = file.getName();
		actionReporter.addFileReport(context, fileName, IO_TYPE.INPUT);
		NetexImporter importer = (NetexImporter) context.get(IMPORTER);

		try {
			Referential referential = (Referential) context.get(REFERENTIAL);
			Source xmlSource = new StreamSource(file);

			// create a Validator instance, which can be used to validate an instance document
			Validator validator = importer.getNetexSchema().newValidator();
			validator.setErrorHandler(new ErrorHandler() {

				@Override
				public void warning(SAXParseException exception) throws SAXException {
					actionReporter.addFileErrorInReport(context, fileName, ActionReporter.FILE_ERROR_CODE.READ_ERROR, exception.getMessage());
				}
				
				@Override
				public void fatalError(SAXParseException exception) throws SAXException {
					actionReporter.addFileErrorInReport(context, fileName, ActionReporter.FILE_ERROR_CODE.READ_ERROR, exception.getMessage());
				}
				
				@Override
				public void error(SAXParseException exception) throws SAXException {
					actionReporter.addFileErrorInReport(context, fileName, ActionReporter.FILE_ERROR_CODE.READ_ERROR, exception.getMessage());
				}
			});
			
			// validate the DOM tree
			try {
				validator.validate(xmlSource);
				result = SUCCESS;
			} catch (SAXException e) {
				log.error(e);
				result = ERROR;
			}
		} catch (Exception e) {
			log.error("Netex schema validation failed ", e);
			throw e;
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}
		if (result == ERROR) {
			actionReporter.addFileErrorInReport(context, fileName,
					ActionReporter.FILE_ERROR_CODE.INVALID_FORMAT, "Netex compliance failed");
		}
		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {
		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new NetexSchemaValidationCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(NetexSchemaValidationCommand.class.getName(), new DefaultCommandFactory());
	}
}
