package mobi.chouette.exchange.netexprofile.importer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
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
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.validation.AbstractNetexProfileValidator;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;

@Log4j
@Stateless(name = NetexSchemaValidationCommand.COMMAND)
public class NetexSchemaValidationCommand implements Command, Constant {

	public static final String COMMAND = "NetexSchemaValidationCommand";

	@Override
	@SuppressWarnings("unchecked")
	public boolean execute(Context context) throws Exception {
		boolean result = SUCCESS;
		Monitor monitor = MonitorFactory.start(COMMAND);
		ActionReporter actionReporter = ActionReporter.Factory.getInstance();
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		NetexImporter importer = (NetexImporter) context.get(IMPORTER);
		List<Path> allFiles = (List<Path>) context.get(NETEX_FILE_PATHS);

		validationReporter.addItemToValidationReport(context, AbstractNetexProfileValidator._1_NETEX_SCHEMA_VALIDATION_ERROR, "E");

		final AtomicInteger counter = new AtomicInteger(0);
		ThreadFactory threadFactory = new ThreadFactory() {
			
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName("netex-schema-validation-thread-"+(counter.incrementAndGet()));
				t.setPriority(Thread.MIN_PRIORITY);
				return t;
			}
		};
		int processors = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(processors,threadFactory);

		try {
			List<Future<SchemaValidationTask>> schemaValidationResults = new ArrayList<>();

			// Compare by file size, largest first
			List<Path> allPathsSortedLargestFirst = new ArrayList<>(allFiles);
			Collections.sort(allPathsSortedLargestFirst,new Comparator<Path>() {
				@Override
				public int compare(Path o1, Path o2) {
					return (int) (o2.toFile().length() - o1.toFile().length());
				}
			});
			
			for (Path filePath : allPathsSortedLargestFirst) {
				SchemaValidationTask schemaValidationTask = new SchemaValidationTask(context, actionReporter, validationReporter, importer, filePath.toFile());
				schemaValidationResults.add(executor.submit(schemaValidationTask));
			}

			executor.shutdown();
			executor.awaitTermination(60, TimeUnit.MINUTES);

			for (Future<SchemaValidationTask> schemaValidationResult : schemaValidationResults) {
				SchemaValidationTask schemaValidationTask = schemaValidationResult.get();

				if (schemaValidationTask.getFileValidationResult() == ERROR) {
					actionReporter.addFileErrorInReport(context, schemaValidationTask.getFile().getName(), ActionReporter.FILE_ERROR_CODE.INVALID_FORMAT,
							"Netex schema compliance failed");
					result = ERROR;
				}
			}
		} catch (Exception e) {
			log.error("Netex schema validation failed ", e);
			throw e;
		} finally {
			executor.shutdown();
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}
		
		if(result == SUCCESS) {
			validationReporter.reportSuccess(context, AbstractNetexProfileValidator._1_NETEX_SCHEMA_VALIDATION_ERROR);
		}
		
		return result;
	}

	class SchemaValidationTask implements Callable<SchemaValidationTask> {

		public static final int MAX_ERROR_COUNT = 100;

		public SchemaValidationTask(Context context, ActionReporter actionReporter, ValidationReporter validationReporter, NetexImporter importer, File file) {
			super();
			this.context = context;
			this.actionReporter = actionReporter;
			this.validationReporter = validationReporter;
			this.importer = importer;
			this.file = file;
		}

		private Context context;

		private ActionReporter actionReporter;

		private ValidationReporter validationReporter;

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

					int errorCount = 0;

					@Override
					public void warning(SAXParseException exception) throws SAXException {
						addToActionReport(exception);
					}

					@Override
					public void fatalError(SAXParseException exception) throws SAXException {
						errorCount++;
						addToActionReport(exception);
					}

					@Override
					public void error(SAXParseException exception) throws SAXException {
						errorCount++;
						addToActionReport(exception);
					}

					public void addToActionReport(SAXParseException exception) throws SAXParseException {
						validationReporter.addCheckPointReportError(context, AbstractNetexProfileValidator._1_NETEX_SCHEMA_VALIDATION_ERROR,
								new DataLocation(fileName, exception.getLineNumber(), exception.getColumnNumber()), exception.getMessage());
						String message = exception.getLineNumber() + ":" + exception.getColumnNumber() + " " + exception.getMessage();
						actionReporter.addFileErrorInReport(context, fileName, ActionReporter.FILE_ERROR_CODE.INVALID_FORMAT, message);
						//log.error(fileName + " has error at line:column " + message);
						fileValidationResult = ERROR;
						if(errorCount >= MAX_ERROR_COUNT) {
							log.error(fileName + " has too many schema validation error (max is "+MAX_ERROR_COUNT+"). Aborting");
							throw exception;
						}
					}

				});
				// Default to success, code above will update to ERROR if bogus data are found
				fileValidationResult = SUCCESS;
				
				Monitor monitor = MonitorFactory.start("SchemaValidation");
				log.info("Schema validating "+fileName);
				validator.validate(xmlSource);
				log.info("Schema validation finished "+fileName+ " "+monitor.stop());

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
