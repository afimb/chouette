package mobi.chouette.exchange.neptune.importer;

import java.io.IOException;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.neptune.validation.AbstractValidator;
import mobi.chouette.exchange.neptune.validation.AccessLinkValidator;
import mobi.chouette.exchange.neptune.validation.AccessPointValidator;
import mobi.chouette.exchange.neptune.validation.AreaCentroidValidator;
import mobi.chouette.exchange.neptune.validation.ChouetteRouteValidator;
import mobi.chouette.exchange.neptune.validation.CompanyValidator;
import mobi.chouette.exchange.neptune.validation.ConnectionLinkValidator;
import mobi.chouette.exchange.neptune.validation.GroupOfLineValidator;
import mobi.chouette.exchange.neptune.validation.JourneyPatternValidator;
import mobi.chouette.exchange.neptune.validation.LineValidator;
import mobi.chouette.exchange.neptune.validation.PTNetworkValidator;
import mobi.chouette.exchange.neptune.validation.PtLinkValidator;
import mobi.chouette.exchange.neptune.validation.StopAreaValidator;
import mobi.chouette.exchange.neptune.validation.StopPointValidator;
import mobi.chouette.exchange.neptune.validation.TimetableValidator;
import mobi.chouette.exchange.neptune.validation.VehicleJourneyValidator;
import mobi.chouette.exchange.report.LineInfo;
import mobi.chouette.exchange.report.LineStats;
import mobi.chouette.exchange.report.Report;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.model.Line;
import mobi.chouette.model.util.Referential;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

//@Stateless(name = NeptuneValidationCommand.COMMAND)
@Log4j
public class NeptuneValidationCommand implements Command, Constant {

	public static final String COMMAND = "NeptuneValidationCommand";

	@Override
	public boolean execute(Context context) throws Exception {

		log.info("[DSU] validate file");
		// boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		Report report = (Report) context.get(REPORT);

		try {
			Context validationContext = (Context) context
					.get(VALIDATION_CONTEXT);
			Referential referential = (Referential) context.get(REFERENTIAL);
			if (validationContext != null) {
				{
					PTNetworkValidator validator = (PTNetworkValidator) ValidatorFactory
							.create(PTNetworkValidator.class.getName(), context);
					validator.validate(context, null);
				}
				{
					GroupOfLineValidator validator = (GroupOfLineValidator) ValidatorFactory
							.create(GroupOfLineValidator.class.getName(),
									context);
					validator.validate(context, null);
				}
				{
					CompanyValidator validator = (CompanyValidator) ValidatorFactory
							.create(CompanyValidator.class.getName(), context);
					validator.validate(context, null);
				}
				{
					StopAreaValidator validator = (StopAreaValidator) ValidatorFactory
							.create(StopAreaValidator.class.getName(), context);
					validator.validate(context, null);
				}
				{
					AreaCentroidValidator validator = (AreaCentroidValidator) ValidatorFactory
							.create(AreaCentroidValidator.class.getName(),
									context);
					validator.validate(context, null);
				}
				{
					ConnectionLinkValidator validator = (ConnectionLinkValidator) ValidatorFactory
							.create(ConnectionLinkValidator.class.getName(),
									context);
					validator.validate(context, null);
				}
				{
					AccessPointValidator validator = (AccessPointValidator) ValidatorFactory
							.create(AccessPointValidator.class.getName(),
									context);
					validator.validate(context, null);
				}
				{
					AccessLinkValidator validator = (AccessLinkValidator) ValidatorFactory
							.create(AccessLinkValidator.class.getName(),
									context);
					validator.validate(context, null);
				}
				{
					LineValidator validator = (LineValidator) ValidatorFactory
							.create(LineValidator.class.getName(), context);
					validator.validate(context, null);
				}
				{
					ChouetteRouteValidator validator = (ChouetteRouteValidator) ValidatorFactory
							.create(ChouetteRouteValidator.class.getName(),
									context);
					validator.validate(context, null);
				}
				{
					PtLinkValidator validator = (PtLinkValidator) ValidatorFactory
							.create(PtLinkValidator.class.getName(), context);
					validator.validate(context, null);
				}
				{
					JourneyPatternValidator validator = (JourneyPatternValidator) ValidatorFactory
							.create(JourneyPatternValidator.class.getName(),
									context);
					validator.validate(context, null);
				}
				CommandFactory factory;

				{
					StopPointValidator validator = (StopPointValidator) ValidatorFactory
							.create(StopPointValidator.class.getName(), context);
					validator.validate(context, null);
				}
				{
					TimetableValidator validator = (TimetableValidator) ValidatorFactory
							.create(TimetableValidator.class.getName(), context);
					validator.validate(context, null);
				}
				{
					VehicleJourneyValidator validator = (VehicleJourneyValidator) ValidatorFactory
							.create(VehicleJourneyValidator.class.getName(),
									context);
					validator.validate(context, null);
				}
				// add stats to report
				addStats(report, validationContext, referential);
			}

		} catch (Exception e) {
			throw e;
		} finally {
			AbstractValidator.resetContext(context);
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}
		return SUCCESS;
	}

	private void addStats(Report report, Context validationContext,
			Referential referential) {
		Line line = referential.getLines().values().iterator().next();
		LineInfo lineInfo = new LineInfo();
		lineInfo.setName(line.getName());
		lineInfo.setStatus(LineInfo.LINE_STATE.OK);
		LineStats stats = new LineStats();
		{
			Context localContext = (Context) validationContext
					.get(ChouetteRouteValidator.LOCAL_CONTEXT);
			stats.setRouteCount((localContext != null) ? localContext.size()
					: 0);
		}
		{
			Context localContext = (Context) validationContext
					.get(ConnectionLinkValidator.LOCAL_CONTEXT);
			stats.setConnectionLinkCount((localContext != null) ? localContext
					.size() : 0);
		}
		{
			Context localContext = (Context) validationContext
					.get(TimetableValidator.LOCAL_CONTEXT);
			stats.setTimeTableCount((localContext != null) ? localContext
					.size() : 0);
		}
		{
			Context localContext = (Context) validationContext
					.get(StopAreaValidator.LOCAL_CONTEXT);
			stats.setStopAreaCount((localContext != null) ? localContext.size()
					: 0);
		}
		{
			Context localContext = (Context) validationContext
					.get(AccessPointValidator.LOCAL_CONTEXT);
			stats.setAccesPointCount((localContext != null) ? localContext
					.size() : 0);
		}
		{
			Context localContext = (Context) validationContext
					.get(VehicleJourneyValidator.LOCAL_CONTEXT);
			stats.setVehicleJourneyCount((localContext != null) ? localContext
					.size() : 0);
		}
		{
			Context localContext = (Context) validationContext
					.get(JourneyPatternValidator.LOCAL_CONTEXT);
			stats.setJourneyPatternCount((localContext != null) ? localContext
					.size() : 0);
		}
		lineInfo.setStats(stats);
		report.getLines().getList().add(lineInfo);
		LineStats globalStats = report.getLines().getStats();
		if (globalStats == null) {
			globalStats = new LineStats();
			report.getLines().setStats(globalStats);
		}
		globalStats.setAccesPointCount(globalStats.getAccesPointCount()
				+ stats.getAccesPointCount());
		globalStats.setRouteCount(globalStats.getRouteCount()
				+ stats.getRouteCount());
		globalStats.setConnectionLinkCount(globalStats.getConnectionLinkCount()
				+ stats.getConnectionLinkCount());
		globalStats.setVehicleJourneyCount(globalStats.getVehicleJourneyCount()
				+ stats.getVehicleJourneyCount());
		globalStats.setJourneyPatternCount(globalStats.getJourneyPatternCount()
				+ stats.getJourneyPatternCount());
		globalStats.setStopAreaCount(globalStats.getStopAreaCount()
				+ stats.getStopAreaCount());
		globalStats.setTimeTableCount(globalStats.getTimeTableCount()
				+ stats.getTimeTableCount());

	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new NeptuneValidationCommand();
			// try {
			// String name = "java:app/mobi.chouette.exchange.neptune/"
			// + COMMAND;
			// result = (Command) context.lookup(name);
			// } catch (NamingException e) {
			// log.error(e);
			// }
			return result;
		}
	}

	static {
		CommandFactory.factories.put(NeptuneValidationCommand.class.getName(),
				new DefaultCommandFactory());
	}
}
