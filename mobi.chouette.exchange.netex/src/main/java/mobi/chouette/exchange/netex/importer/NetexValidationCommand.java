package mobi.chouette.exchange.netex.importer;

import java.io.IOException;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.netex.Constant;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileError;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.LineInfo;
import mobi.chouette.exchange.report.DataStats;
import mobi.chouette.model.Line;
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

		ActionReport report = (ActionReport) context.get(REPORT);
		String fileName = (String) context.get(FILE_NAME);
		FileInfo fileInfo = report.findFileInfo(fileName);
		try {
			Referential referential = (Referential) context.get(REFERENTIAL);
			
			// TODO create level 2 validation for NETEX
			
			result = SUCCESS;
			if (result)
				addStats(report, referential);

		} catch (Exception e) {
			log.error("Neptune validation failed ", e);
			throw e;
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}
		if (result == ERROR) {
			fileInfo.addError(new FileError(FileError.CODE.INVALID_FORMAT, "Neptune compliance failed"));
		}
		return result;
	}

	private void addStats(ActionReport report, Referential referential) {
		Line line = referential.getLines().values().iterator().next();
		LineInfo lineInfo = new LineInfo(line);
		DataStats stats = lineInfo.getStats();
		stats.setLineCount(1);
		stats.setRouteCount(referential.getRoutes().size());
		stats.setConnectionLinkCount(referential.getConnectionLinks().size());
		stats.setTimeTableCount(referential.getTimetables().size());
		stats.setStopAreaCount(referential.getStopAreas().size());
		stats.setAccessPointCount(referential.getAccessPoints().size());
		stats.setVehicleJourneyCount(referential.getVehicleJourneys().size());
		stats.setJourneyPatternCount(referential.getJourneyPatterns().size());

		report.getLines().add(lineInfo);
		DataStats globalStats = report.getStats();
		globalStats.setLineCount(globalStats.getLineCount() + stats.getLineCount());
		globalStats.setAccessPointCount(globalStats.getAccessPointCount() + stats.getAccessPointCount());
		globalStats.setRouteCount(globalStats.getRouteCount() + stats.getRouteCount());
		globalStats.setConnectionLinkCount(globalStats.getConnectionLinkCount() + stats.getConnectionLinkCount());
		globalStats.setVehicleJourneyCount(globalStats.getVehicleJourneyCount() + stats.getVehicleJourneyCount());
		globalStats.setJourneyPatternCount(globalStats.getJourneyPatternCount() + stats.getJourneyPatternCount());
		globalStats.setStopAreaCount(globalStats.getStopAreaCount() + stats.getStopAreaCount());
		globalStats.setTimeTableCount(globalStats.getTimeTableCount() + stats.getTimeTableCount());

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
