package mobi.chouette.exchange.netex.importer;

import java.io.IOException;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.netex.Constant;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_STATE;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_TYPE;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.model.Line;
import mobi.chouette.model.util.NamingUtil;
import mobi.chouette.model.util.Referential;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class NetexValidationCommand implements Command, Constant {

	public static final String COMMAND = "NetexValidationCommand";

	@Override
	public boolean execute(Context context) throws Exception {

		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		ActionReporter reporter = ActionReporter.Factory.getInstance();
		String fileName = (String) context.get(FILE_NAME);
		try {
			Referential referential = (Referential) context.get(REFERENTIAL);
			
			// TODO create level 2 validation for NETEX
			
			result = SUCCESS;
			if (result)
				addStats(context, reporter, referential);

		} catch (Exception e) {
			log.error("Neptune validation failed ", e);
			throw e;
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}
		if (result == ERROR) {
			reporter.addFileErrorInReport(context, fileName, ActionReporter.FILE_ERROR_CODE.INVALID_FORMAT, "Netex compliance failed");
		}
		return result;
	}

	private void addStats(Context context, ActionReporter reporter, Referential referential) {
		Line line = referential.getLines().values().iterator().next();
		
		reporter.addObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, NamingUtil.getName(line), OBJECT_STATE.OK, IO_TYPE.INPUT);
		reporter.addStatToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.LINE, 1);
		reporter.addStatToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.JOURNEY_PATTERN, referential.getJourneyPatterns().size());
		reporter.addStatToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.ROUTE, referential.getRoutes().size());
		reporter.addStatToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.VEHICLE_JOURNEY, referential.getVehicleJourneys().size());
		reporter.addStatToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.CONNECTION_LINK, referential.getConnectionLinks().size());
		reporter.addStatToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.TIMETABLE, referential.getTimetables().size());
		reporter.addStatToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.ACCESS_POINT, referential.getAccessPoints().size());
		reporter.addStatToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.STOP_AREA, referential.getStopAreas().size());

		// TODO report on end of processing
//		report.getLines().add(lineInfo);
//		DataStats globalStats = report.getStats();
//		globalStats.setLineCount(globalStats.getLineCount() + stats.getLineCount());
//		globalStats.setAccessPointCount(globalStats.getAccessPointCount() + stats.getAccessPointCount());
//		globalStats.setRouteCount(globalStats.getRouteCount() + stats.getRouteCount());
//		globalStats.setConnectionLinkCount(globalStats.getConnectionLinkCount() + stats.getConnectionLinkCount());
//		globalStats.setVehicleJourneyCount(globalStats.getVehicleJourneyCount() + stats.getVehicleJourneyCount());
//		globalStats.setJourneyPatternCount(globalStats.getJourneyPatternCount() + stats.getJourneyPatternCount());
//		globalStats.setStopAreaCount(globalStats.getStopAreaCount() + stats.getStopAreaCount());
//		globalStats.setTimeTableCount(globalStats.getTimeTableCount() + stats.getTimeTableCount());

	}


	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new NetexValidationCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(NetexValidationCommand.class.getName(), new DefaultCommandFactory());
	}
}
