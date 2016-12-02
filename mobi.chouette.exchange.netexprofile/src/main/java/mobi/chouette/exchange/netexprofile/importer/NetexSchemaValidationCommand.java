package mobi.chouette.exchange.netexprofile.importer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Validator;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.FileUtil;
import mobi.chouette.common.JobData;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;

@Log4j
@Stateless(name = NetexSchemaValidationCommand.COMMAND)
public class NetexSchemaValidationCommand implements Command, Constant {

	public static final String COMMAND = "NetexSchemaValidationCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);
		ActionReporter actionReporter = ActionReporter.Factory.getInstance();
		NetexImporter importer = (NetexImporter) context.get(IMPORTER);

		JobData jobData = (JobData) context.get(JOB_DATA);
		Path path = Paths.get(jobData.getPathName(), INPUT);
		List<Path> allFiles = FileUtil.listFiles(path, "*.xml");
		
		ExecutorService executor = Executors.newFixedThreadPool(8);
		
		try {
			
			List<Future<SchemaValidationTask>> schemaValidationResults = new ArrayList<>();
			
			for (Path filePath : allFiles) {
				
				SchemaValidationTask schemaValidationTask = new SchemaValidationTask(context, actionReporter, importer, filePath.toFile());
				schemaValidationResults.add((Future<SchemaValidationTask>) executor.submit(schemaValidationTask));
			}
			
			executor.shutdown();
			executor.awaitTermination(60, TimeUnit.MINUTES);
			
			for(Future<SchemaValidationTask> schemaValidationResult : schemaValidationResults) {
				SchemaValidationTask schemaValidationTask = (SchemaValidationTask) schemaValidationResult.get();
				if(schemaValidationTask.fileValidationResult == ERROR) {
					actionReporter.addFileErrorInReport(context, schemaValidationTask.getFile().getName(), ActionReporter.FILE_ERROR_CODE.INVALID_FORMAT, "Netex profile compliance failed");
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
		
		@Getter
		boolean fileValidationResult = ERROR;
		
		
		@Override
		public SchemaValidationTask call() throws Exception {
			String fileName = file.getName();
			actionReporter.addFileReport(context, fileName, IO_TYPE.INPUT);
			Source xmlSource = new StreamSource(file);
			try {
				// validate xml file
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
				validator.validate(xmlSource);
				fileValidationResult = !actionReporter.hasFileValidationErrors(context, fileName);
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
