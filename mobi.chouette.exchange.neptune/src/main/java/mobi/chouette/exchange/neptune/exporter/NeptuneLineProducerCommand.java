package mobi.chouette.exchange.neptune.exporter;

import java.io.IOException;
import java.sql.Date;

import javax.naming.InitialContext;
import javax.xml.bind.MarshalException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.exporter.SharedDataKeys;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.DataStats;
import mobi.chouette.exchange.report.LineError;
import mobi.chouette.exchange.report.LineInfo;
import mobi.chouette.model.Line;
import mobi.chouette.model.util.NeptuneUtil;

import org.xml.sax.SAXParseException;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class NeptuneLineProducerCommand implements Command, Constant {

	public static final String COMMAND = "NeptuneLineProducerCommand";

	public boolean execute(Context context) throws Exception {

		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);
		ActionReport report = (ActionReport) context.get(REPORT);

		try {

			Line line = (Line) context.get(LINE);
			NeptuneExportParameters configuration = (NeptuneExportParameters) context.get(CONFIGURATION);

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
			if (sharedData == null)
			{
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

			NeptuneDataCollector collector = new NeptuneDataCollector();
			boolean cont = (collector.collect(collection, line, startDate, endDate));
			LineInfo lineInfo = new LineInfo(line);
			DataStats stats = lineInfo.getStats();
			stats.setAccessPointCount(collection.getAccessPoints().size());
			stats.setConnectionLinkCount(collection.getConnectionLinks().size());
			stats.setJourneyPatternCount(collection.getJourneyPatterns().size());
			stats.setRouteCount(collection.getRoutes().size());
			stats.setStopAreaCount(collection.getStopAreas().size());
			stats.setTimeTableCount(collection.getTimetables().size());
			stats.setVehicleJourneyCount(collection.getVehicleJourneys().size());


			if (cont) {
				try
				{
				ChouettePTNetworkProducer producer = new ChouettePTNetworkProducer();
				producer.produce(context);

				stats.setLineCount(1);
				// merge lineStats to global ones
				DataStats globalStats = report.getStats();
				globalStats.setLineCount(globalStats.getLineCount() + stats.getLineCount());
				globalStats.setRouteCount(globalStats.getRouteCount() + stats.getRouteCount());
				globalStats.setVehicleJourneyCount(globalStats.getVehicleJourneyCount()
						+ stats.getVehicleJourneyCount());
				globalStats.setJourneyPatternCount(globalStats.getJourneyPatternCount()
						+ stats.getJourneyPatternCount());
				// compute shared objects
				sharedData.getAccessPointIds().addAll(NeptuneUtil.extractObjectIds(collection.getAccessPoints()));
				sharedData.getConnectionLinkIds().addAll(NeptuneUtil.extractObjectIds(collection.getConnectionLinks()));
				sharedData.getStopAreaIds().addAll(NeptuneUtil.extractObjectIds(collection.getStopAreas()));
				sharedData.getTimetableIds().addAll(NeptuneUtil.extractObjectIds(collection.getTimetables()));
				globalStats.setAccessPointCount(sharedData.getAccessPointIds().size());
				globalStats.setStopAreaCount(sharedData.getStopAreaIds().size());
				globalStats.setTimeTableCount(sharedData.getTimetableIds().size());
				globalStats.setConnectionLinkCount(sharedData.getConnectionLinkIds().size());
				result = SUCCESS;
				} catch (MarshalException e) {
					if (e.getCause() != null && e.getCause() instanceof SAXParseException)
					{
						log.error(e.getCause().getMessage());
						lineInfo.addError(new LineError(LineError.CODE.INVALID_FORMAT,e.getCause().getMessage()));			
					}
					else
					{
					log.error(e.getMessage());
					lineInfo.addError(new LineError(LineError.CODE.INVALID_FORMAT,e.getMessage()));
					}
				}
			
			} else {
				lineInfo.addError(new LineError(LineError.CODE.NO_DATA_ON_PERIOD,"no data on period"));				
			}
			report.getLines().add(lineInfo);

		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}

		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			return new NeptuneLineProducerCommand();
		}
	}

	static {
		CommandFactory.factories.put(NeptuneLineProducerCommand.class.getName(), new DefaultCommandFactory());
	}
}
