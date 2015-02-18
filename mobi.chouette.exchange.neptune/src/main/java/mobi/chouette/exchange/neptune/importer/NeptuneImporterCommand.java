package mobi.chouette.exchange.neptune.importer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.FileUtils;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.importer.CopyCommand;
import mobi.chouette.exchange.importer.RegisterCommand;
import mobi.chouette.exchange.importer.UncompressCommand;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.Progression;
import mobi.chouette.exchange.report.Report;
import mobi.chouette.exchange.report.ReportCommand;
import mobi.chouette.exchange.validation.ValidationReportCommand;
import mobi.chouette.exchange.validation.report.ValidationReport;

import com.google.common.collect.Lists;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Stateless(name = NeptuneImporterCommand.COMMAND)
@ToString
@Log4j
public class NeptuneImporterCommand implements Command, Constant {

	public static final String COMMAND = "NeptuneImporterCommand";

	@Resource(lookup = "java:comp/DefaultManagedExecutorService")
	ManagedExecutorService executor;


	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		InitialContext initialContext = (InitialContext) context
				.get(INITIAL_CONTEXT);

		// report
		Report report = new Report();
		ValidationReport validationReport = new ValidationReport();
		context.put(Constant.REPORT, report);
		context.put(Constant.VALIDATION_REPORT, validationReport);
		Progression progression = new Progression();
		report.setProgression(progression );
		progression.setStep(Progression.STEP.INITIALISATION);
		Command reportCmd = CommandFactory.create(initialContext, ReportCommand.class.getName());
		reportCmd.execute(context);
		Command validationReportCmd = CommandFactory.create(initialContext, ValidationReportCommand.class.getName());
		try {

			// uncompress data
			Command uncompress = CommandFactory.create(initialContext,
					UncompressCommand.class.getName());
			uncompress.execute(context);

			Path path = Paths.get(context.get(PATH).toString(), INPUT);
			List<Path> stream = FileUtils.listFiles(path, "*.xml", "*metadata*");
			progression.setStep(Progression.STEP.PROCESSING);
			progression.setTotal(stream.size() + 1);
			progression.setRealized(1);
			reportCmd.execute(context);

			// validation 
			// TODO [ME] ne marche pas car le contexte est partagé et la valeur FILE_URL se téléscope
			Monitor validation = MonitorFactory.start(NeptuneSAXParserCommand.COMMAND );			
			for (Path file : stream) 
			{
				context.put(FILE_URL, file.toUri().toURL().toExternalForm());
				context.put(FILE_NAME, file.toFile().getName());
				Command xmlvalidation = CommandFactory.create(initialContext,
						NeptuneSAXParserCommand.class.getName());
				result = xmlvalidation.execute(context);
			}
			log.info(Color.YELLOW + validation.stop() + Color.NORMAL);
//			Monitor validation = MonitorFactory.start("Parallel" + NeptuneSAXParserCommand.COMMAND );			
//			int n = Runtime.getRuntime().availableProcessors() / 2;
//			int size = (int )Math.ceil(stream.size() / n);
//			List<List<Path>> partition = Lists.partition(stream, size);
//			List<Task> tasks = new ArrayList<Task>();
//			for (int i = 0; i < partition.size(); i++) {
//				tasks.add(new Task(initialContext, context, partition.get(i)));
//			}
//			List<Future<Boolean>> futures = executor.invokeAll(tasks);
//			log.info(Color.YELLOW + validation.stop() + Color.NORMAL);

			for (Path file : stream) {
				progression.setRealized(progression.getRealized()+1);
				reportCmd.execute(context);
				validationReportCmd.execute(context);

				log.info("[DSU] import : " + file.toString());	

				context.put(FILE_URL, file.toUri().toURL().toExternalForm());
				context.put(FILE_NAME, file.toFile().getName());
				
				// check if file is xml ok
				FileInfo info = report.getFiles().findFileFileInfo(file.getFileName().toString());
				if (info != null && info.getStatus().equals(FileInfo.FILE_STATE.NOK))
				{
					log.warn("[ME] xml validation failed : skipped");	
					continue;
				}
				// parser
				Command parser = CommandFactory.create(initialContext,
						NeptuneParserCommand.class.getName());
				if (! parser.execute(context))
				{
					log.warn("[ME] xml parsing failed : skipped");	
					continue;
				}

				// validation
				Command validator = CommandFactory.create(initialContext,
						NeptuneValidationCommand.class.getName());
				if (! validator.execute(context))
				{
					log.warn("[ME] level 2 validation failed : skipped");	
					continue;
				}

				// register
				Command register = CommandFactory.create(initialContext,
						RegisterCommand.class.getName());
				if (! register.execute(context))
				{
					log.warn("[ME] save failed : skipped");	
					continue;
				}

				Command copy = CommandFactory.create(initialContext,
						CopyCommand.class.getName());
				copy.execute(context);

			}
			result = SUCCESS;
		} catch (Exception e) {
			log.error(e);
		}
		finally
		{
			// save report 
			report.setProgression(null);
			reportCmd.execute(context);

			// save validation report 
			validationReportCmd.execute(context);

		}

		log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		return result;
	}

	@AllArgsConstructor
	class Task implements Callable<Boolean> {

		private InitialContext initialContext;
		private Context context;
		private List<Path> files = new ArrayList<Path>();

		@Override
		public Boolean call() throws Exception {
			boolean result = SUCCESS;
			log.info("[DSU] validate : " + files.size());

			for (Path file : files) {
				context.put(FILE_URL, file.toUri().toURL().toExternalForm());
				context.put(FILE_NAME, file.toFile().getName());
				Command validation = CommandFactory.create(this.initialContext,
						NeptuneSAXParserCommand.class.getName());
				result = validation.execute(context);
			}
			return result;
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
		CommandFactory.factories.put(NeptuneImporterCommand.class.getName(),
				factory);
	}
}
