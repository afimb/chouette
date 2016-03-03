package mobi.chouette.exchange.regtopp.importer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.InitialContext;

import org.apache.commons.lang.StringUtils;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.FileUtil;
import mobi.chouette.common.JobData;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.regtopp.Constant;
import mobi.chouette.exchange.regtopp.model.RegtoppDayCode;
import mobi.chouette.exchange.regtopp.model.RegtoppDayCodeHeader;
import mobi.chouette.exchange.regtopp.model.RegtoppStop;
import mobi.chouette.exchange.regtopp.model.importer.ParseableFile;
import mobi.chouette.exchange.regtopp.model.importer.RegtoppException;
import mobi.chouette.exchange.regtopp.model.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.validation.ValidationReporter;
import mobi.chouette.exchange.report.ActionError;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;

@Log4j
public class RegtoppFilePresenceValidationCommand implements Command, Constant {

	public static final String COMMAND = "RegtoppFilePresenceValidationCommand";
	
	private static final List<String> mandatoryFiles = Arrays.asList(
			REGTOPP_TRIPINDEX_FILE,
			REGTOPP_TRIPDATA_FILE,
			REGTOPP_STOPPLACE_FILE,
			REGTOPP_DAYCODE_FILE);

	private static final List<String> optionalFiles = Arrays.asList(
			REGTOPP_DESTINATION_FILE,
			REGTOPP_REMARKS_FILE,
			REGTOPP_PATHWAY_FILE,
			REGTOPP_INTERCHANGE_FILE,
			REGTOPP_ZONE_FILE,
			REGTOPP_LINE_FILE,
			REGTOPP_VEHICHLE_JOURNEY_FILE);


	
	
	
	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);
		
		ActionReport report = (ActionReport) context.get(REPORT);
		
		JobData jobData = (JobData) context.get(JOB_DATA);
		// check ignored files
		Path path = Paths.get(jobData.getPathName(), INPUT);
		List<Path> list = FileUtil.listFiles(path, "*");
		
		RegtoppImportParameters parameters = (RegtoppImportParameters) context.get(CONFIGURATION);
		List<String> processableFiles = mandatoryFiles;
		
		Set<String> fileNamePrefixesFound = new HashSet<String>();
		
		ValidationReporter validationReporter = (ValidationReporter) context.get(REGTOPP_REPORTER);
		
		RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);

		
		
		for (Path fileName : list) {
			String name = fileName.getFileName().toString();
			
			boolean validFilePattern = false;
			
			// Match all regtopp file patterns
			for(String pattern : processableFiles) {
				if(name.toUpperCase().matches(pattern)) {
					validFilePattern = true;
					fileNamePrefixesFound.add(name.substring(0, name.lastIndexOf(".")));
				}
			}
			
			if (!validFilePattern) {
				FileInfo file = new FileInfo(name, FILE_STATE.IGNORED);
				report.getFiles().add(file);
			} else {
				FileInfo file = new FileInfo(name, FILE_STATE.ERROR);
				report.getFiles().add(file);
				if(name.toUpperCase().endsWith(".HPL")) {
					ParseableFile parseableFile = new ParseableFile(fileName.toFile(),Arrays.asList(new Class[] {RegtoppStop.class}),file);
					importer.registerFileForIndex(RegtoppImporter.INDEX.STOP_BY_ID.name(),parseableFile);
				} else if(name.toUpperCase().endsWith(".DKO")) {
					ParseableFile parseableFile = new ParseableFile(fileName.toFile(),Arrays.asList(new Class[]{RegtoppDayCodeHeader.class,RegtoppDayCode.class}),file);
					importer.registerFileForIndex(RegtoppImporter.INDEX.DAYCODE_BY_ID.name(),parseableFile);
				}
				
			}
		}
		
		if(fileNamePrefixesFound.size() > 1) {
			validationReporter.reportError(context, new RegtoppException(StringUtils.join(fileNamePrefixesFound,","), 1, null, RegtoppException.ERROR.MULTIPLE_ADMIN_CODES, null, null),StringUtils.join(fileNamePrefixesFound,",")+".*" );
		}
		
		
		try {
			
			
			
//			RegtoppStopParser stopParser = (RegtoppStopParser) ParserFactory.create(RegtoppStopParser.class.getName());
//			stopParser.validate(context);
			
			
				// agency.txt
//				GtfsAgencyParser agencyParser = (GtfsAgencyParser) ParserFactory.create(GtfsAgencyParser.class.getName());
//				agencyParser.validate(context);
				
				// routes.txt
//				GtfsRouteParser routeParser = (GtfsRouteParser) ParserFactory.create(GtfsRouteParser.class.getName());
//				routeParser.validate(context);
			
			// stops.txt
			
				// calendar.txt & calendar_dates.txt
//				GtfsCalendarParser calendarParser = (GtfsCalendarParser) ParserFactory.create(GtfsCalendarParser.class.getName());
//				calendarParser.validate(context);
				
				// shapes.txt, trips.txt, stop_times.txt & frequencies.txt
//				GtfsTripParser tripParser = (GtfsTripParser) ParserFactory.create(GtfsTripParser.class.getName());
//				tripParser.validate(context);
			
			// transfers.txt
//			GtfsTransferParser transferParser = (GtfsTransferParser) ParserFactory.create(GtfsTransferParser.class.getName());
//			transferParser.validate(context);
			
			result = SUCCESS;
		} catch (RegtoppException e) {
			// log.error(e,e);
			if (e.getError().equals(RegtoppException.ERROR.SYSTEM))
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
			Command result = new RegtoppFilePresenceValidationCommand();
			return result;
		}
	}
	
	static {
		CommandFactory.factories.put(RegtoppFilePresenceValidationCommand.class.getName(), new DefaultCommandFactory());
	}
}
