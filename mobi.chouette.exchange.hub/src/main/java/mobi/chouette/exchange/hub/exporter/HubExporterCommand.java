package mobi.chouette.exchange.hub.exporter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
import mobi.chouette.common.JobData;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.CompanyDAO;
import mobi.chouette.dao.GroupOfLineDAO;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.dao.NetworkDAO;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.exchange.ProgressionCommand;
import mobi.chouette.exchange.exporter.CompressCommand;
import mobi.chouette.exchange.exporter.SaveMetadataCommand;
import mobi.chouette.exchange.hub.Constant;
import mobi.chouette.exchange.hub.model.exporter.HubExporter;
import mobi.chouette.exchange.metadata.Metadata;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.ReportConstant;
import mobi.chouette.model.Company;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.util.Referential;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
@Stateless(name = HubExporterCommand.COMMAND)
public class HubExporterCommand implements Command, Constant, ReportConstant {

	public static final String COMMAND = "HubExporterCommand";

	@EJB
	private LineDAO lineDAO;

	@EJB
	private NetworkDAO ptNetworkDAO;

	@EJB
	private CompanyDAO companyDAO;

	@EJB
	private GroupOfLineDAO groupOfLineDAO;

	@EJB
	private StopAreaDAO stopAreaDAO;

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);

		// initialize reporting and progression
		ProgressionCommand progression = (ProgressionCommand) CommandFactory.create(initialContext,
				ProgressionCommand.class.getName());

		progression.initialize(context, 1);
		JobData jobData = (JobData) context.get(JOB_DATA);
		jobData.setFilename("export_" + jobData.getType() + "_" + jobData.getId() + ".zip");

		context.put(REFERENTIAL, new Referential());
		Metadata metadata = new Metadata(); // if not asked, will be used as
											// dummy
		metadata.setDate(Calendar.getInstance());
		metadata.setFormat("text");
		metadata.setTitle("Export hub ");
		try {
			metadata.setRelation(new URL("http://www.cityway.fr"));
		} catch (MalformedURLException e1) {
			log.error("problem with http://www.cityway.fr url", e1);
		}

		context.put(METADATA, metadata);

		// read parameters
		Object configuration = context.get(CONFIGURATION);
		if (!(configuration instanceof HubExportParameters)) {
			// fatal wrong parameters
			ActionReport report = (ActionReport) context.get(REPORT);
			log.error("invalid parameters for hub export " + configuration.getClass().getName());
			report.setResult(STATUS_ERROR);
			report.setFailure("invalid parameters for hub export " + configuration.getClass().getName());
			progression.dispose(context);
			return ERROR;
		}

		HubExportParameters parameters = (HubExportParameters) configuration;
		if (parameters.getStartDate() != null && parameters.getEndDate() != null)
		{
			if (parameters.getStartDate().after(parameters.getEndDate()))
			{
				ActionReport report = (ActionReport) context.get(REPORT);
				report.setResult(STATUS_ERROR);
				report.setFailure("end date before start date");
				return ERROR;
				
			}
		}

		String type = parameters.getReferencesType().toLowerCase();
		// set default type 
		if (type == null || type.isEmpty() || type.equalsIgnoreCase("all"))
		{
			// all lines
			type = "line";
			parameters.setIds(null);
		}

		try {
			Path path = Paths.get(jobData.getPathName(), OUTPUT);
			if (!Files.exists(path)) {
				Files.createDirectories(path);
			}
			HubExporter hubExporter = new HubExporter(path.toString());
			initExporter(hubExporter);
			context.put(HUB_EXPORTER, hubExporter);

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
				} else if (type.equals("group_of_line")) {
					List<GroupOfLine> list = groupOfLineDAO.findAll(ids);
					for (GroupOfLine groupOfLine : list) {
						lines.addAll(groupOfLine.getLines());
					}
				}
			}
			progression.execute(context);
			progression.start(context, lines.size() + 1);
			Command exportLine = CommandFactory.create(initialContext, HubLineProducerCommand.class.getName());

			int lineCount = 0;
			List<Line> lineList = new ArrayList<>(lines);
			
			Collections.sort(lineList,new LineSorter());
			for (Line line : lineList) {
				context.put(LINE_ID, line.getId());
				progression.execute(context);
				if (exportLine.execute(context) == ERROR) {
					continue;
				} else {
					lineCount++;
				}
			}

			if (lineCount > 0) {
				progression.execute(context);
				Command exportSharedData = CommandFactory.create(initialContext,
						HubSharedDataProducerCommand.class.getName());
				result = exportSharedData.execute(context);
			}

			hubExporter.dispose();
			
			// save metadata

			if (parameters.isAddMetadata()) {
				progression.terminate(context, 2);
				Command saveMetadata = CommandFactory.create(initialContext, SaveMetadataCommand.class.getName());
				saveMetadata.execute(context);
				progression.execute(context);
			} else {
				progression.terminate(context, 1);
			}

			// compress
			Command compress = CommandFactory.create(initialContext, CompressCommand.class.getName());
			compress.execute(context);
			progression.execute(context);

		} catch (Exception e) {
			ActionReport report = (ActionReport) context.get(REPORT);
			report.setResult(STATUS_ERROR);
			report.setFailure("Fatal :" + e);
			log.error(e.getMessage(), e);
		} finally {
			progression.dispose(context);
			log.info(Color.YELLOW + monitor.stop() + Color.NORMAL);
		}

		return result;
	}
	
	private void initExporter(HubExporter hubExporter)
	{
		// create all files event if empty
		hubExporter.getArretExporter();
		hubExporter.getCheminExporter();
		hubExporter.getCommuneExporter();
		hubExporter.getCorrespondanceExporter();
		hubExporter.getCourseExporter();
		hubExporter.getCourseOperationExporter();
		hubExporter.getDirectionExporter();
		hubExporter.getGroupeDeLigneExporter();
		hubExporter.getHoraireExporter();
		hubExporter.getItlExporter();
		hubExporter.getLigneExporter();
		hubExporter.getModeTransportExporter();
		hubExporter.getPeriodeExporter();
		hubExporter.getRenvoiExporter();
		hubExporter.getReseauExporter();
		hubExporter.getSchemaExporter();
		hubExporter.getTransporteurExporter();
	}

	public class LineSorter implements Comparator<Line> {
		@Override
		public int compare(Line arg0, Line arg1) {

			return arg0.objectIdSuffix().compareTo(arg1.objectIdSuffix());
		}
	}

	
	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange.hub/" + COMMAND;
				result = (Command) context.lookup(name);
			} catch (NamingException e) {
				log.error(e);
			}
			return result;
		}
	}

	static {
		CommandFactory.factories.put(HubExporterCommand.class.getName(), new DefaultCommandFactory());
	}
}
