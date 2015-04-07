package mobi.chouette.exchange.hub.exporter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.datatype.DatatypeConfigurationException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.LineInfo;
import mobi.chouette.exchange.report.LineInfo.LINE_STATE;
import mobi.chouette.exchange.report.LineStats;
import mobi.chouette.model.Line;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
@Stateless(name = HubLineProducerCommand.COMMAND)
public class HubLineProducerCommand implements Command, Constant {
	public static final String COMMAND = "HubLineProducerCommand";

	@EJB
	private LineDAO lineDAO;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);
		ActionReport report = (ActionReport) context.get(REPORT);

		try {
			Long lineId = (Long) context.get(LINE_ID);
			Line line = lineDAO.find(lineId);

			HubExportParameters configuration = (HubExportParameters) context.get(CONFIGURATION);

			ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
			if (collection == null) {
				collection = new ExportableData();
				context.put(EXPORTABLE_DATA, collection);
			}
			Date startDate = null;
			if (configuration.getStartDate() != null) {
				startDate = new Date(configuration.getStartDate().getTime());
			}

			Date endDate = null;
			if (configuration.getEndDate() != null) {
				endDate = new Date(configuration.getEndDate().getTime());
			}
			HubDataCollector collector = new HubDataCollector();

			boolean cont = (collector.collect(collection, line, startDate, endDate));
			LineInfo lineInfo = new LineInfo();
			lineInfo.setName(line.getName() + " (" + line.getNumber() + ")");
			LineStats stats = new LineStats();
			stats.setAccessPointCount(collection.getAccessPoints().size());
			stats.setConnectionLinkCount(collection.getConnectionLinks().size());
			stats.setJourneyPatternCount(collection.getJourneyPatterns().size());
			stats.setRouteCount(collection.getRoutes().size());
			stats.setStopAreaCount(collection.getStopAreas().size());
			// stats.setTimeTableCount(collection.getTimetables().size());
			// stats.setVehicleJourneyCount(collection.getVehicleJourneys().size());

			if (cont) {
				context.put(EXPORTABLE_DATA, collection);

				saveLine(context, line, collection);

				lineInfo.setStatus(LINE_STATE.OK);
				// merge lineStats to global ones
				LineStats globalStats = report.getStats();
				if (globalStats == null) {
					globalStats = new LineStats();
					report.setStats(globalStats);
				}
				globalStats.setLineCount(globalStats.getLineCount() + stats.getLineCount());
				globalStats.setRouteCount(globalStats.getRouteCount() + stats.getRouteCount());
				globalStats.setVehicleJourneyCount(globalStats.getVehicleJourneyCount()
						+ stats.getVehicleJourneyCount());
				globalStats.setJourneyPatternCount(globalStats.getJourneyPatternCount()
						+ stats.getJourneyPatternCount());
				result = SUCCESS;
			} else {
				lineInfo.setStatus(LINE_STATE.ERROR);
				result = ERROR;
			}
			report.getLines().add(lineInfo);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}

		return result;
	}

	private void saveLine(Context context, Line line, ExportableData collection) throws IOException,
			DatatypeConfigurationException {
		String rootDirectory = (String) context.get(PATH);
		Path dir = Paths.get(rootDirectory, OUTPUT);
		HubFileWriter writer = new HubFileWriter();
		// prepare data for line

	}
	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange.hub/"
						+ COMMAND;
				result = (Command) context.lookup(name);
			} catch (NamingException e) {
				log.error(e);
			}
			return result;
		}
	}

	static {
		CommandFactory.factories.put(HubLineProducerCommand.class.getName(), new DefaultCommandFactory());
	}


}
