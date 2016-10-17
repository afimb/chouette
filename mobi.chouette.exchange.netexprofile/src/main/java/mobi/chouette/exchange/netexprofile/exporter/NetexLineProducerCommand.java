package mobi.chouette.exchange.netexprofile.exporter;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.exporter.SharedDataKeys;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.model.Line;
import mobi.chouette.model.util.NamingUtil;

import javax.naming.InitialContext;
import java.io.IOException;
import java.sql.Date;

@Log4j
public class NetexLineProducerCommand implements Command, Constant {
	public static final String COMMAND = "NetexLineProducerCommand";

	@Override
	public boolean execute(Context context) throws Exception {

		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);
		ActionReporter reporter = ActionReporter.Factory.getInstance();

		try {

			Line line = (Line) context.get(LINE);
			log.info("procesing line " + NamingUtil.getName(line));
			NetexExportParameters configuration = (NetexExportParameters) context.get(CONFIGURATION);

			ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
			if (collection == null)
			{
				collection = new  ExportableData();
				context.put(EXPORTABLE_DATA, collection);
			}
			else
			{
				collection.clear();
			}

			SharedDataKeys sharedData = (SharedDataKeys) context.get(SHARED_DATA_KEYS);
			if (sharedData == null) {
				sharedData = new SharedDataKeys();
				context.put(SHARED_DATA_KEYS, sharedData);
			}

			Date startDate = null;
			if (configuration.getStartDate() != null) {
				startDate = new Date(configuration.getStartDate().getTime());
			}

			Date endDate = null;
			if (configuration.getEndDate() != null) {
				endDate = new Date(configuration.getEndDate().getTime());
			}

			NetexDataCollector collector = new NetexDataCollector();
			boolean cont = (collector.collect(collection, line, startDate, endDate));
			reporter.setStatToObjectReport(context, line.getObjectId(), ActionReporter.OBJECT_TYPE.LINE, ActionReporter.OBJECT_TYPE.LINE, 0);
			reporter.setStatToObjectReport(context, line.getObjectId(), ActionReporter.OBJECT_TYPE.LINE, ActionReporter.OBJECT_TYPE.JOURNEY_PATTERN,
					collection.getJourneyPatterns().size());
			reporter.setStatToObjectReport(context, line.getObjectId(), ActionReporter.OBJECT_TYPE.LINE, ActionReporter.OBJECT_TYPE.ROUTE, collection
					.getRoutes().size());
			reporter.setStatToObjectReport(context, line.getObjectId(), ActionReporter.OBJECT_TYPE.LINE, ActionReporter.OBJECT_TYPE.VEHICLE_JOURNEY,
					collection.getVehicleJourneys().size());

			if (cont) {
				context.put(EXPORTABLE_DATA, collection);

				saveLine(context, line);
				reporter.setStatToObjectReport(context, line.getObjectId(), ActionReporter.OBJECT_TYPE.LINE, ActionReporter.OBJECT_TYPE.LINE, 1);
				result = SUCCESS;
			} else {
				reporter.addErrorToObjectReport(context, line.getObjectId(), ActionReporter.OBJECT_TYPE.LINE,
						ActionReporter.ERROR_CODE.NO_DATA_ON_PERIOD, "no data on period");
				result = SUCCESS; // else export will stop here
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}
		return result;
	}

	private boolean saveLine(Context context, Line line) {
		throw new UnsupportedOperationException();
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new NetexLineProducerCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(NetexLineProducerCommand.class.getName(), new DefaultCommandFactory());
	}

}
