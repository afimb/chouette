package mobi.chouette.exchange.netex.exporter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.CompanyDAO;
import mobi.chouette.dao.GroupOfLineDAO;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.dao.NetworkDAO;
import mobi.chouette.exchange.ProgressionCommand;
import mobi.chouette.exchange.exporter.CompressCommand;
import mobi.chouette.exchange.exporter.SaveMetadataCommand;
import mobi.chouette.exchange.metadata.Metadata;
import mobi.chouette.exchange.netex.Constant;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.model.Company;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.util.Referential;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
@Stateless(name = NetexExporterCommand.COMMAND)
public class NetexExporterCommand implements Command, Constant {

	public static final String COMMAND = "NetexExporterCommand";

	@EJB
	private LineDAO lineDAO;

	@EJB
	private NetworkDAO ptNetworkDAO;

	@EJB
	private CompanyDAO companyDAO;

	@EJB
	private GroupOfLineDAO groupOfLineDAO;

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		InitialContext initialContext = (InitialContext) context
				.get(INITIAL_CONTEXT);

		// initialize reporting and progression
		ProgressionCommand progression = (ProgressionCommand) CommandFactory
				.create(initialContext, ProgressionCommand.class.getName());
		progression.initialize(context,1);

		context.put(REFERENTIAL, new Referential());
		Metadata metadata = new Metadata(); // if not asked, will be used as dummy
        metadata.setDate(Calendar.getInstance());
        metadata.setFormat("application/xml");
        metadata.setTitle("Export NeTEx ");
        try
        {
           metadata.setRelation(new URL("http://www.chouette.mobi/pourquoi-chouette/convertir-des-donnees/"));
        }
        catch (MalformedURLException e1)
        {
           log.error("problem with http://www.chouette.mobi/pourquoi-chouette/convertir-des-donnees/ url", e1);
        }

		context.put(METADATA, metadata);

		// read parameters
		Object configuration = context.get(CONFIGURATION);
		if (!(configuration instanceof NetexExportParameters)) {
			// fatal wrong parameters
			ActionReport report = (ActionReport) context.get(REPORT);
			log.error("invalid parameters for netex export "
					+ configuration.getClass().getName());
			report.setFailure("invalid parameters for netex export "
					+ configuration.getClass().getName());
			progression.dispose(context);
			return ERROR;
		}

		NetexExportParameters parameters = (NetexExportParameters) configuration;

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
				List<Network> list = ptNetworkDAO.findAll(ids);
				for (Network ptNetwork : list) {
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
		progression.execute(context);

		try {

			Path path = Paths.get(context.get(PATH).toString(), OUTPUT);
			if (!Files.exists(path)) {
				Files.createDirectories(path);
			}

			progression.start(context, lines.size());
			Command export = CommandFactory.create(initialContext,
					NetexProducerCommand.class.getName());

			// export each line
			for (Line line : lines) {
				context.put(LINE_ID, line.getId());
				progression.execute(context);
				if (export.execute(context) == ERROR) {
					continue;
				}
			}
			
			// save metadata
			if (parameters.isAddMetadata())
			{
				progression.terminate(context,2);
				Command saveMetadata = CommandFactory.create(initialContext,
						SaveMetadataCommand.class.getName());
				saveMetadata.execute(context);
				progression.execute(context);
			}
			else
			{
				progression.terminate(context,1);
			}
			
			// compress
			Command compress = CommandFactory.create(initialContext,
					CompressCommand.class.getName());
			compress.execute(context);
			progression.execute(context);

			result = SUCCESS;
		} catch (Exception e) {
			ActionReport report = (ActionReport) context.get(REPORT);
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
				String name = "java:app/mobi.chouette.exchange.netex/"
						+ COMMAND;
				result = (Command) context.lookup(name);
			} catch (NamingException e) {
				log.error(e);
			}
			return result;
		}
	}

	static {
		CommandFactory.factories.put(NetexExporterCommand.class.getName(),
				new DefaultCommandFactory());
	}
}
