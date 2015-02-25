package mobi.chouette.exchange.gtfs.exporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.CompanyDAO;
import mobi.chouette.dao.GroupOfLineDAO;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.dao.PTNetworkDAO;
import mobi.chouette.exchange.ProgressionCommand;
import mobi.chouette.exchange.report.Report;
import mobi.chouette.model.Company;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.Line;
import mobi.chouette.model.PTNetwork;
import mobi.chouette.model.util.Referential;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
@Stateless(name = GtfsExporterCommand.COMMAND)
public class GtfsExporterCommand implements Command, Constant {

	public static final String COMMAND = "GtfsExporterCommand";

	@EJB
	private LineDAO lineDAO;

	@EJB
	private PTNetworkDAO ptNetworkDAO;

	@EJB
	private CompanyDAO companyDAO;

	@EJB
	private GroupOfLineDAO groupOfLineDAO;
	
	@EJB
	private GroupOfLineDAO stopAreaDAO;

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
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
		if (!(configuration instanceof GtfsExportParameters)) {
			// fatal wrong parameters
			Report report = (Report) context.get(REPORT);
			log.error("invalid parameters for gtfs export "
					+ configuration.getClass().getName());
			report.setFailure("invalid parameters for gtfs export "
					+ configuration.getClass().getName());
			progression.dispose(context);
			return ERROR;
		}

		GtfsExportParameters parameters = (GtfsExportParameters) configuration;

		String type = parameters.getReferencesType().toLowerCase();
		List<Object> ids = null;
		if (parameters.getIds() != null) {
			ids = new ArrayList<Object>(parameters.getIds());
		}

		Set<Line> lines = new HashSet<Line>();
		if (ids == null || ids.isEmpty()) {
			lines.addAll(lineDAO.findAll());
		} else {
			if (type.equals("line")) {
				lines.addAll(lineDAO.findAll(ids));
			} else if (type.equals("network")) {
				List<PTNetwork> list = ptNetworkDAO.findAll(ids);
				for (PTNetwork ptNetwork : list) {
					lines.addAll(ptNetwork.getLines());
				}
			} else if (type.equals("company")) {
				List<Company> list = companyDAO.findAll(ids);
				for (Company company : list) {
					lines.addAll(company.getLines());
				}
			} else if (type.equals("groupofline")) {
				List<GroupOfLine> list = groupOfLineDAO.findAll(ids);
				for (GroupOfLine groupOfLine : list) {
					lines.addAll(groupOfLine.getLines());
				}
			}
		}

		try {

			Path path = Paths.get(context.get(PATH).toString(), OUTPUT);
			if (!Files.exists(path)) {
				Files.createDirectories(path);
			}

			progression.start(context, lines.size());
			Command export = CommandFactory.create(initialContext,
					NeptuneProducerCommand.class.getName());

			for (Line line : lines) {
				context.put(LINE_ID, line.getId());
				progression.execute(context);
				if (export.execute(context) == ERROR) {
					continue;
				}
			}

			result = SUCCESS;
		} catch (Exception e) {
			Report report = (Report) context.get(REPORT);
			report.setFailure("Fatal :" + e);
			log.error(e.getMessage(), e);
		} finally {
			progression.dispose(context);
			log.info(Color.YELLOW + monitor.stop() + Color.NORMAL);
		}

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
		CommandFactory.factories.put(GtfsExporterCommand.class.getName(),
				new DefaultCommandFactory());
	}
}
