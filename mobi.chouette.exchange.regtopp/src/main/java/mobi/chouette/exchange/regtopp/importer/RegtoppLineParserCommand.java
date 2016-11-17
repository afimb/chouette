package mobi.chouette.exchange.regtopp.importer;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.CompassBearingGenerator;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.RegtoppConstant;
import mobi.chouette.exchange.regtopp.importer.index.Index;
import mobi.chouette.exchange.regtopp.importer.index.v11.DaycodeById;
import mobi.chouette.exchange.regtopp.importer.parser.v11.RegtoppLineParser;
import mobi.chouette.exchange.regtopp.importer.parser.v11.RegtoppTimetableParser;
import mobi.chouette.exchange.regtopp.importer.version.VersionHandler;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDayCodeHeaderDKO;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.model.Line;
import mobi.chouette.model.util.NamingUtil;
import mobi.chouette.model.util.Referential;

import javax.naming.InitialContext;
import java.io.IOException;
import java.util.Iterator;

import static mobi.chouette.common.Constant.CONFIGURATION;
import static mobi.chouette.exchange.report.ActionReporter.*;

@Log4j
public class RegtoppLineParserCommand implements Command {

	public static final String COMMAND = "RegtoppLineParserCommand";

	@Getter
	@Setter
	private String lineId;

	@Getter
	@Setter
	private boolean batchParse;

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			Referential referential = (Referential) context.get(REFERENTIAL);
			RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
			if (referential != null) {
				referential.clear(true);
			}

			if(referential == null) {
				log.error("Referential is null!");
			}
			
			
			String calendarStartDate = (String) context.get(RegtoppConstant.CALENDAR_START_DATE);
			if(calendarStartDate == null) {
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

			if(batchParse) {
				Index<AbstractRegtoppTripIndexTIX> index = importer.getUniqueLinesByTripIndex();
				Iterator<String> keys = index.keys();
				RegtoppLineParser lineParser = (RegtoppLineParser) ParserFactory.create(RegtoppLineParser.class.getName());
				while (keys.hasNext()) {
					String lineId = keys.next();
					lineParser.setLineId(lineId);
					lineParser.parse(context);
				}
				
				CompassBearingGenerator compassBearingGenerator = new CompassBearingGenerator();
				compassBearingGenerator.cacluateCompassBearings(referential);
				
			} else {
				// Parse this line only
				RegtoppLineParser lineParser = (RegtoppLineParser) ParserFactory.create(RegtoppLineParser.class.getName());
				lineParser.setLineId(lineId);
				lineParser.parse(context);
			}

			addStats(context, referential);
			result = SUCCESS;
		} catch (Exception e) {
			log.error("Failed hard to parse line:", e);
			e.printStackTrace();
			throw e;
		}

		log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		return result;
	}

	private void addStats(Context context, Referential referential) {
		ActionReporter actionReporter = Factory.getInstance();

		Line line = referential.getLines().values().iterator().next();

		actionReporter.addObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, NamingUtil.getName(line),
				OBJECT_STATE.OK, IO_TYPE.INPUT);

		actionReporter.setStatToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.LINE, 1);
		actionReporter.setStatToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.JOURNEY_PATTERN,
				referential.getJourneyPatterns().size());
		actionReporter.setStatToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.ROUTE, referential
				.getRoutes().size());
		actionReporter.setStatToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.VEHICLE_JOURNEY,
				referential.getVehicleJourneys().size());

		// global stats
		actionReporter.addObjectReport(context, "global", OBJECT_TYPE.ROUTE, "routes", OBJECT_STATE.OK, IO_TYPE.OUTPUT);
		actionReporter.addObjectReport(context, "global", OBJECT_TYPE.LINE, "lines", OBJECT_STATE.OK, IO_TYPE.OUTPUT);

		actionReporter.setStatToObjectReport(context, "global", OBJECT_TYPE.ROUTE, OBJECT_TYPE.ROUTE, referential.getRoutes().size());
		actionReporter.setStatToObjectReport(context, "global", OBJECT_TYPE.LINE, OBJECT_TYPE.LINE, referential.getLines().size());
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
