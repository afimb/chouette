package mobi.chouette.exchange.regtopp.importer;

import java.io.IOException;

import javax.naming.InitialContext;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.RegtoppConstant;
import mobi.chouette.exchange.regtopp.importer.index.v11.DaycodeById;
import mobi.chouette.exchange.regtopp.importer.parser.v11.RegtoppLineParser;
import mobi.chouette.exchange.regtopp.importer.parser.v11.RegtoppTimetableParser;
import mobi.chouette.exchange.regtopp.importer.version.VersionHandler;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDayCodeHeaderDKO;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.DataStats;
import mobi.chouette.exchange.report.LineInfo;
import mobi.chouette.model.Line;
import mobi.chouette.model.util.Referential;

@Log4j
public class RegtoppLineParserCommand implements Command {

	public static final String COMMAND = "RegtoppLineParserCommand";

	@Getter
	@Setter
	private String lineId;

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			Referential referential = (Referential) context.get(REFERENTIAL);
			ActionReport report = (ActionReport) context.get(REPORT);
			if (referential != null) {
				referential.clear(true);
			}

			if(referential == null) {
				log.error("Referential is null!");
			}
			
			
			String calendarStartDate = (String) context.get(RegtoppConstant.CALENDAR_START_DATE);
			if(calendarStartDate == null) {
				RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
				DaycodeById dayCodeIndex = (DaycodeById) importer.getDayCodeById();
				RegtoppDayCodeHeaderDKO header = dayCodeIndex.getHeader();
				context.put(RegtoppConstant.CALENDAR_START_DATE,header.getDate().toString());

			}
			VersionHandler versionHandler = (VersionHandler) context.get(RegtoppConstant.VERSION_HANDLER);

			if(versionHandler == null) {
				log.error("Versionhandler is null!");
			}

			// Populate shared stops
			if (referential.getSharedStopAreas().isEmpty()) {
				Parser stopParser = versionHandler.createStopParser();
				stopParser.parse(context);
			}

			// Populate shared connection links
			if (referential.getSharedConnectionLinks().isEmpty()) {
				Parser connectionLinkParser = versionHandler.createConnectionLinkParser();
				connectionLinkParser.parse(context);
			}

			// Populate shared timetables
			if (referential.getSharedTimetables().isEmpty()) {
				RegtoppTimetableParser timetableParser = (RegtoppTimetableParser) ParserFactory.create(RegtoppTimetableParser.class.getName());
				timetableParser.parse(context);
			}

			// Parse this line only
			RegtoppLineParser lineParser = (RegtoppLineParser) ParserFactory.create(RegtoppLineParser.class.getName());
			lineParser.setLineId(lineId);
			lineParser.parse(context);


			addStats(report, referential);
			result = SUCCESS;
		} catch (Exception e) {
			log.error("Failed hard to parse line:", e);
			e.printStackTrace();
			throw e;
		}

		log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		return result;
	}

	private void addStats(ActionReport report, Referential referential) {
		Line line = referential.getLines().values().iterator().next();
		LineInfo lineInfo = new LineInfo(line);
		DataStats stats = lineInfo.getStats();
		stats.setLineCount(1);

		stats.setRouteCount(referential.getRoutes().size());
		stats.setVehicleJourneyCount(referential.getVehicleJourneys().size());
		stats.setJourneyPatternCount(referential.getJourneyPatterns().size());
		report.getLines().add(lineInfo);
		DataStats globalStats = report.getStats();
		globalStats.setConnectionLinkCount(referential.getSharedConnectionLinks().size());
		globalStats.setStopAreaCount(referential.getSharedStopAreas().size());
		globalStats.setTimeTableCount(referential.getSharedTimetables().size());

		globalStats.setLineCount(globalStats.getLineCount() + stats.getLineCount());
		globalStats.setRouteCount(globalStats.getRouteCount() + stats.getRouteCount());
		globalStats.setVehicleJourneyCount(globalStats.getVehicleJourneyCount() + stats.getVehicleJourneyCount());
		globalStats.setJourneyPatternCount(globalStats.getJourneyPatternCount() + stats.getJourneyPatternCount());

	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new RegtoppLineParserCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(RegtoppLineParserCommand.class.getName(), new DefaultCommandFactory());
	}
}
