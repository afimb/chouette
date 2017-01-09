package mobi.chouette.exchange.netexprofile.importer;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Log4j
@Stateless(name = NetexSchemaValidationCommand.COMMAND)
public class NetexSchemaValidationCommand implements Command, Constant {

	public static final String COMMAND = "NetexSchemaValidationCommand";

	@Override
	@SuppressWarnings("unchecked")
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);
		ActionReporter actionReporter = ActionReporter.Factory.getInstance();
		NetexImporter importer = (NetexImporter) context.get(IMPORTER);
		List<Path> allFiles = (List<Path>) context.get(NETEX_FILE_PATHS);

		ExecutorService executor = Executors.newFixedThreadPool(8);
		
		try {
			List<Future<SchemaValidationTask>> schemaValidationResults = new ArrayList<>();
			
			for (Path filePath : allFiles) {
				SchemaValidationTask schemaValidationTask = new SchemaValidationTask(context, actionReporter, importer, filePath.toFile());
				String fileName = filePath.toFile().getName();
				actionReporter.addFileReport(context, fileName, IO_TYPE.INPUT);
				schemaValidationResults.add(executor.submit(schemaValidationTask));
			}
			
			executor.shutdown();
			executor.awaitTermination(60, TimeUnit.MINUTES);
			
			for(Future<SchemaValidationTask> schemaValidationResult : schemaValidationResults) {
				SchemaValidationTask schemaValidationTask = schemaValidationResult.get();

				if(schemaValidationTask.fileValidationResult == ERROR) {
					actionReporter.addFileErrorInReport(context, schemaValidationTask.getFile().getName(),
							ActionReporter.FILE_ERROR_CODE.INVALID_FORMAT, "Netex profile compliance failed");
				} else {
					result = SUCCESS;
				}
			}
		} catch (Exception e) {
			log.error("Netex schema validation failed ", e);
			throw e;
		} finally {
			executor.shutdown();
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}
		return result;
	}
	
	 class SchemaValidationTask implements Callable<SchemaValidationTask> {

		public SchemaValidationTask(Context context, ActionReporter actionReporter, NetexImporter importer, File file) {
			super();
			this.context = context;
			this.actionReporter = actionReporter;
			this.importer = importer;
			this.file = file;
		}


		private Context context;
		
		private ActionReporter actionReporter;
		
		private NetexImporter importer;
		
		@Getter
		private File file;
		
		boolean fileValidationResult = ERROR;
		
		public boolean getFileValidationResult() {
			return fileValidationResult && !actionReporter.hasFileValidationErrors(context, file.getName());
		}
		
		@Override
		public SchemaValidationTask call() throws Exception {
			String fileName = file.getName();
			Source xmlSource = new StreamSource(file);
			try {
				// validate xml file
				Validator validator = importer.getNetexSchema().newValidator();
				validator.setErrorHandler(new ErrorHandler() {

					@Override
					public void warning(SAXParseException exception) throws SAXException {
						addToActionReport(exception);
					}

					@Override
					public void fatalError(SAXParseException exception) throws SAXException {
						addToActionReport(exception);
					}

					@Override
					public void error(SAXParseException exception) throws SAXException {
						addToActionReport(exception);
					}
					
					public void addToActionReport(SAXParseException exception) {
						String message = exception.getLineNumber()+":"+exception.getColumnNumber()+" "+exception.getMessage();
						actionReporter.addFileErrorInReport(context, fileName, ActionReporter.FILE_ERROR_CODE.INVALID_FORMAT, message);
						log.error(fileName+" has error at line:column "+message);
					}
					
				});
				validator.validate(xmlSource);
				fileValidationResult = SUCCESS;
			} catch (SAXException e) {
				log.error(e);
				fileValidationResult = ERROR;
			} catch (IOException e) {
				log.error(e);
				fileValidationResult = ERROR;
			}
			
			return this;
		}


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