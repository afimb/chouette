package mobi.chouette.exchange.gtfs.importer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.FileUtil;
import mobi.chouette.common.JobData;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.gtfs.Constant;
import mobi.chouette.exchange.gtfs.model.importer.GtfsException;
import mobi.chouette.exchange.gtfs.parser.GtfsAgencyParser;
import mobi.chouette.exchange.gtfs.parser.GtfsCalendarParser;
import mobi.chouette.exchange.gtfs.parser.GtfsRouteParser;
import mobi.chouette.exchange.gtfs.parser.GtfsStopParser;
import mobi.chouette.exchange.gtfs.parser.GtfsTransferParser;
import mobi.chouette.exchange.gtfs.parser.GtfsTripParser;
import mobi.chouette.exchange.gtfs.validation.ValidationReporter;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.report.ActionError;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class GtfsValidationCommand implements Command, Constant {

	public static final String COMMAND = "GtfsValidationCommand";
	
	private static final List<String> processableAllFiles = Arrays.asList(GTFS_AGENCY_FILE,
			GTFS_STOPS_FILE,
			GTFS_ROUTES_FILE,
			GTFS_SHAPES_FILE,
			GTFS_TRIPS_FILE,
			GTFS_STOP_TIMES_FILE,
			GTFS_CALENDAR_FILE,
			GTFS_CALENDAR_DATES_FILE,
			GTFS_FREQUENCIES_FILE,
			GTFS_TRANSFERS_FILE);

	private static final List<String> processableStopAreaFiles = Arrays.asList(GTFS_STOPS_FILE, GTFS_TRANSFERS_FILE);

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);
		
		ActionReport report = (ActionReport) context.get(REPORT);
		
		JobData jobData = (JobData) context.get(JOB_DATA);
		// check ignored files
		Path path = Paths.get(jobData.getPathName(), INPUT);
		List<Path> list = FileUtil.listFiles(path, "*");
		
		GtfsImportParameters parameters = (GtfsImportParameters) context.get(CONFIGURATION);
		boolean all = !(parameters.getReferencesType().equalsIgnoreCase("stop_area"));
		List<String> processableFiles = processableAllFiles;
		if (!all) {
			processableFiles = processableStopAreaFiles;
		}
		
		ValidationReporter validationReporter = (ValidationReporter) context.get(GTFS_REPORTER);
		for (Path fileName : list) {
			if (!processableFiles.contains(fileName.getFileName().toString())) {
				FileInfo file = new FileInfo(fileName.getFileName().toString(), FILE_STATE.IGNORED);
				report.getFiles().add(file);
				validationReporter.reportError(context, new GtfsException(fileName.getFileName().toString(), 1, null, GtfsException.ERROR.UNUSED_FILE, null, null), fileName.getFileName().toString());
			}
			else
			{
				// TODO : implement a new status : UNCHECKED
				FileInfo file = new FileInfo(fileName.getFileName().toString(), FILE_STATE.IGNORED);
				report.getFiles().add(file);				
			}
		}
		
		try {
			
			if (all) {
				// agency.txt
				GtfsAgencyParser agencyParser = (GtfsAgencyParser) ParserFactory.create(GtfsAgencyParser.class.getName());
				agencyParser.validate(context);
				
				// routes.txt
				GtfsRouteParser routeParser = (GtfsRouteParser) ParserFactory.create(GtfsRouteParser.class.getName());
				routeParser.validate(context);
			}
			
			// stops.txt
			GtfsStopParser stopParser = (GtfsStopParser) ParserFactory.create(GtfsStopParser.class.getName());
			stopParser.validate(context);
			
			if (all) {
				// calendar.txt & calendar_dates.txt
				GtfsCalendarParser calendarParser = (GtfsCalendarParser) ParserFactory.create(GtfsCalendarParser.class.getName());
				calendarParser.validate(context);
				
				// shapes.txt, trips.txt, stop_times.txt & frequencies.txt
				GtfsTripParser tripParser = (GtfsTripParser) ParserFactory.create(GtfsTripParser.class.getName());
				tripParser.validate(context);
			}
			
			// transfers.txt
			GtfsTransferParser transferParser = (GtfsTransferParser) ParserFactory.create(GtfsTransferParser.class.getName());
			transferParser.validate(context);
			
			result = SUCCESS;
		} catch (GtfsException e) {
			// log.error(e,e);
			if (e.getError().equals(GtfsException.ERROR.SYSTEM))
				throw e;
			else
				report.setFailure(new ActionError(ActionError.CODE.INVALID_DATA, e.getError().name()+" "+e.getPath()));
			
		} catch (Exception e) {
			if (e instanceof RuntimeException)
				log.error(e, e);
			throw e;
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}
		
		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {
		
		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new GtfsValidationCommand();
			return result;
		}
	}
	
	static {
		CommandFactory.factories.put(GtfsValidationCommand.class.getName(), new DefaultCommandFactory());
	}
}
