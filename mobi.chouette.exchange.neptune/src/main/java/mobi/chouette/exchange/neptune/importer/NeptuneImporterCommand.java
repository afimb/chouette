package mobi.chouette.exchange.neptune.importer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.FileUtils;
import mobi.chouette.common.chain.Chain;
import mobi.chouette.common.chain.ChainCommand;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.importer.CopyCommand;
import mobi.chouette.exchange.importer.LineRegisterCommand;
import mobi.chouette.exchange.importer.UncompressCommand;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.Report;
import mobi.chouette.exchange.validation.report.ValidationReport;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
@Stateless(name = NeptuneImporterCommand.COMMAND)
public class NeptuneImporterCommand implements Command, Constant {

	public static final String COMMAND = "NeptuneImporterCommand";

	// validation
//	Monitor validation = MonitorFactory.start("Parallel" + NeptuneSAXParserCommand.COMMAND );			
//	int n = Runtime.getRuntime().availableProcessors() / 2;
//	int size = (int )Math.ceil(stream.size() / n);
//	List<List<Path>> partition = Lists.partition(stream, size);
//	List<Task> tasks = new ArrayList<Task>();
//	for (int i = 0; i < n; i++) {
//		tasks.add(new Task(initialContext, context, partition.get(i)));
//	}
//	List<Future<Boolean>> futures = executor.invokeAll(tasks);
//	log.info(Color.YELLOW + validation.stop() + Color.NORMAL);
	
	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			InitialContext initialContext = (InitialContext) context
					.get(INITIAL_CONTEXT);

			// report
			Report report = new Report();
			ValidationReport validationReport = new ValidationReport();
			context.put(Constant.REPORT, report);
			context.put(Constant.VALIDATION_REPORT, validationReport);

			// uncompress data
			Command uncompress = CommandFactory.create(initialContext,
					UncompressCommand.class.getName());
			uncompress.execute(context);

			Path path = Paths.get(context.get(PATH).toString(), INPUT);
			List<Path> stream = FileUtils.listFiles(path, "*.xml");

			// validation schema (niv 1)
			Chain validations = (Chain) CommandFactory.create(initialContext,
					ChainCommand.class.getName());
			for (Path file : stream) {
			
				// TODO parallel schema validation
				// TODO filter skip metadata file
				if (file.toFile().getName().toLowerCase().contains("metadata")) {
					FileInfo fileItem = new FileInfo();
					fileItem.setName(file.toFile().getName());
					fileItem.setStatus(FileInfo.STATE.UNCHECKED);
					report.getFiles().getFileInfos().add(fileItem);
					continue;
				}
				
				String url = file.toUri().toURL().toExternalForm();
				log.info("[DSU] validation schema : " + url);
				NeptuneSAXParserCommand schema = (NeptuneSAXParserCommand) CommandFactory.create(initialContext,
						NeptuneSAXParserCommand.class.getName());
				schema.setFileURL(url);
				validations.add(schema);				
			}
			validations.execute(context);
			
			
			Chain chain = (Chain) CommandFactory.create(initialContext,
					ChainCommand.class.getName());
			
			for (Path file : stream) {

				// TODO filter skip metadata file
				if (file.toFile().getName().toLowerCase().contains("metadata")) {
					FileInfo fileItem = new FileInfo();
					fileItem.setName(file.toFile().getName());
					fileItem.setStatus(FileInfo.STATE.UNCHECKED);
					report.getFiles().getFileInfos().add(fileItem);
					continue;
				}
				log.info("[DSU] import : " + file.toString());
				
				// parser
				NeptuneParserCommand parser = (NeptuneParserCommand) CommandFactory.create(initialContext,
						NeptuneParserCommand.class.getName());
				parser.setFileURL(file.toUri().toURL().toExternalForm());
				chain.add(parser);

				// validation
//				Command validation = CommandFactory.create(initialContext,
//						NeptuneValidationCommand.class.getName());
//				chain.add(validation);

				// register
				Command register = CommandFactory.create(initialContext,
						LineRegisterCommand.class.getName());
				chain.add(register);

				Command copy = CommandFactory.create(initialContext,
						CopyCommand.class.getName());
				chain.add(copy);

			}
			chain.execute(context);

			result = SUCCESS;
		} catch (Exception e) {
			log.error(e);
		}

		log.info(Color.YELLOW + monitor.stop() + Color.NORMAL);
		return result;
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
		CommandFactory.factories.put(NeptuneImporterCommand.class.getName(),
				new DefaultCommandFactory());
	}
}
