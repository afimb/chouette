package mobi.chouette.exchange.neptune.exporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.FileUtils;
import mobi.chouette.common.chain.Chain;
import mobi.chouette.common.chain.ChainCommand;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.ProgressionCommand;
import mobi.chouette.exchange.importer.CopyCommand;
import mobi.chouette.exchange.importer.LineRegisterCommand;
import mobi.chouette.exchange.importer.UncompressCommand;
import mobi.chouette.exchange.report.Report;
import mobi.chouette.model.Line;
import mobi.chouette.model.util.Referential;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class NeptuneExporterCommand implements Command, Constant {

	public static final String COMMAND = "NeptuneExporterCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = SUCCESS;
		Monitor monitor = MonitorFactory.start(COMMAND);

		InitialContext initialContext = (InitialContext) context
				.get(INITIAL_CONTEXT);

		// initialize reporting and progression
		ProgressionCommand progression = (ProgressionCommand) CommandFactory
				.create(initialContext, ProgressionCommand.class.getName());
		progression.initialize(context);

		context.put(REFERENTIAL, new Referential());

				// read parameters
		Object configuration = context.get(CONFIGURATION);
		if (!(configuration instanceof NeptuneExportParameters)) {
			// fatal wrong parameters
			Report report = (Report) context.get(REPORT);
			log.error("invalid parameters for neptune export "
					+ configuration.getClass().getName());
			report.setFailure("invalid parameters for neptune export "
					+ configuration.getClass().getName());
			progression.dispose(context);
			return false;
		}

		NeptuneExportParameters parameters = (NeptuneExportParameters) configuration;

		String type = parameters.getReferencesType().toLowerCase();
		List<Integer> ids = parameters.getIds();
        if (ids != null || ids.isEmpty())
        {
        	// load all lines
        }
        else
        {
        	// filter lines on query
        	// types vaut "line", "network", "company" ou "groupofline"
        	// ids est la liste d'id base (pk)
        }
		int lineCount = 1; // positionner ici le nombre de lignes à traiter
		List<Line> lines = null; // la liste des lignes à traiter
		
		try {

			Path path = Paths.get(context.get(PATH).toString(), OUTPUT);
			if (!Files.exists(path)) {
				Files.createDirectories(path);
			}
			
			progression.start(context, lineCount);

			ChainCommand master = (ChainCommand) CommandFactory.create(
					initialContext, ChainCommand.class.getName());
			master.setIgnored(true);

			for (Line line : lines) 
			{

				Chain chain = (Chain) CommandFactory.create(initialContext,
						ChainCommand.class.getName());
				master.add(chain);

				chain.add(progression);

				// ajouter ici la commande NeptuneProducerCommand
				

			}
			master.execute(context);

		} catch (Exception e) {
			Report report = (Report) context.get(REPORT);
			log.error(e);
			report.setFailure("Fatal :"+e);

		} finally {
			progression.dispose(context);
		}

		log.info(Color.YELLOW + monitor.stop() + Color.NORMAL);
		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new NeptuneExporterCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(NeptuneExporterCommand.class.getName(),
				new DefaultCommandFactory());
	}
}
