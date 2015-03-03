/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.validator;

import java.io.IOException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.exchange.report.LineInfo;
import mobi.chouette.exchange.report.LineInfo.LINE_STATE;
import mobi.chouette.exchange.report.LineStats;
import mobi.chouette.exchange.report.Report;
import mobi.chouette.model.Line;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 *
 */
@Log4j
@Stateless(name = DaoLineValidatorCommand.COMMAND)
public class DaoLineValidatorCommand implements Command, Constant {
	public static final String COMMAND = "DaoLineValidatorCommand";

	@EJB
	private LineDAO lineDAO;


	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);

		try {
			Command lineValidatorCommand = CommandFactory.create(initialContext,
					LineValidatorCommand.class.getName());

			Long lineId = (Long) context.get(LINE_ID);
			Line line = lineDAO.find(lineId);
			
			ValidationDataCollector collector = new ValidationDataCollector();
			collector.collect(data, line);

			lineValidatorCommand.execute(context);
			if (context.get(ACTION).equals(VALIDATOR))
			{
				Report report = (Report) context.get(REPORT);
				LineInfo lineInfo = new LineInfo();
				lineInfo.setName(line.getName() + " (" + line.getNumber() + ")");
				LineStats stats = new LineStats();
				stats.setJourneyPatternCount(data.getJourneyPatterns().size());
				stats.setRouteCount(data.getRoutes().size());
				stats.setVehicleJourneyCount(data.getVehicleJourneys().size());

				lineInfo.setStatus(LINE_STATE.OK);
				// merge lineStats to global ones
				LineStats globalStats = report.getLines().getStats();
				if (globalStats == null) {
					globalStats = new LineStats();
					report.getLines().setStats(globalStats);
				}
				globalStats.setRouteCount(globalStats.getRouteCount() + stats.getRouteCount());
				globalStats.setVehicleJourneyCount(globalStats.getVehicleJourneyCount() + stats.getVehicleJourneyCount());
				globalStats.setJourneyPatternCount(globalStats.getJourneyPatternCount() + stats.getJourneyPatternCount());
				report.getLines().getList().add(lineInfo);
			}
			result = SUCCESS;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}

		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange/" + COMMAND;
				result = (Command) context.lookup(name);
			} catch (NamingException e) {
				log.error(e);
			}
			return result;
		}
	}

	static {
		CommandFactory.factories.put(DaoLineValidatorCommand.class.getName(), new DefaultCommandFactory());
	}

}
