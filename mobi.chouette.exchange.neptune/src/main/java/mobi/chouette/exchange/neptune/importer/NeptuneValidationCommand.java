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
import mobi.chouette.exchange.neptune.validation.ITLValidator;
import mobi.chouette.exchange.neptune.validation.JourneyPatternValidator;
import mobi.chouette.exchange.neptune.validation.LineValidator;
import mobi.chouette.exchange.neptune.validation.PTNetworkValidator;
import mobi.chouette.exchange.neptune.validation.PtLinkValidator;
import mobi.chouette.exchange.neptune.validation.StopAreaValidator;
import mobi.chouette.exchange.neptune.validation.StopPointValidator;
import mobi.chouette.exchange.neptune.validation.TimeSlotValidator;
import mobi.chouette.exchange.neptune.validation.TimetableValidator;
import mobi.chouette.exchange.neptune.validation.VehicleJourneyValidator;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_STATE;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_TYPE;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.Line;
import mobi.chouette.model.util.NamingUtil;
import mobi.chouette.model.util.Referential;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class NeptuneValidationCommand implements Command, Constant {

	public static final String COMMAND = "NeptuneValidationCommand";

	@Override
	public boolean execute(Context context) throws Exception {

		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		ActionReporter reporter = ActionReporter.Factory.getInstance();

		String fileName = (String) context.get(FILE_NAME);

		try {
			Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
			Referential referential = (Referential) context.get(REFERENTIAL);

			if (validationContext != null) {
				{
					PTNetworkValidator validator = (PTNetworkValidator) ValidatorFactory.create(
							PTNetworkValidator.class.getName(), context);
					validator.validate(context, null);
				}
				{
					GroupOfLineValidator validator = (GroupOfLineValidator) ValidatorFactory.create(
							GroupOfLineValidator.class.getName(), context);
					validator.validate(context, null);
				}
				{
					CompanyValidator validator = (CompanyValidator) ValidatorFactory.create(
							CompanyValidator.class.getName(), context);
					validator.validate(context, null);
				}
				{
					StopAreaValidator validator = (StopAreaValidator) ValidatorFactory.create(
							StopAreaValidator.class.getName(), context);
					validator.validate(context, null);
				}
				{
					AreaCentroidValidator validator = (AreaCentroidValidator) ValidatorFactory.create(
							AreaCentroidValidator.class.getName(), context);
					validator.validate(context, null);
				}
				{
					ConnectionLinkValidator validator = (ConnectionLinkValidator) ValidatorFactory.create(
							ConnectionLinkValidator.class.getName(), context);
					validator.validate(context, null);
				}
				{
					AccessPointValidator validator = (AccessPointValidator) ValidatorFactory.create(
							AccessPointValidator.class.getName(), context);
					validator.validate(context, null);
				}
				{
					AccessLinkValidator validator = (AccessLinkValidator) ValidatorFactory.create(
							AccessLinkValidator.class.getName(), context);
					validator.validate(context, null);
				}
				{
					LineValidator validator = (LineValidator) ValidatorFactory.create(LineValidator.class.getName(),
							context);
					validator.validate(context, null);
				}
				{
					ChouetteRouteValidator validator = (ChouetteRouteValidator) ValidatorFactory.create(
							ChouetteRouteValidator.class.getName(), context);
					validator.validate(context, null);
				}
				{
					PtLinkValidator validator = (PtLinkValidator) ValidatorFactory.create(
							PtLinkValidator.class.getName(), context);
					validator.validate(context, null);
				}
				{
					JourneyPatternValidator validator = (JourneyPatternValidator) ValidatorFactory.create(
							JourneyPatternValidator.class.getName(), context);
					validator.validate(context, null);
				}

				{
					StopPointValidator validator = (StopPointValidator) ValidatorFactory.create(
							StopPointValidator.class.getName(), context);
					validator.validate(context, null);
				}
				{
					TimetableValidator validator = (TimetableValidator) ValidatorFactory.create(
							TimetableValidator.class.getName(), context);
					validator.validate(context, null);
				}
				{
					VehicleJourneyValidator validator = (VehicleJourneyValidator) ValidatorFactory.create(
							VehicleJourneyValidator.class.getName(), context);
					validator.validate(context, null);
				}
				{
					ITLValidator validator = (ITLValidator) ValidatorFactory.create(ITLValidator.class.getName(),
							context);
					validator.validate(context, null);
				}
				{
					TimeSlotValidator validator = (TimeSlotValidator) ValidatorFactory.create(
							TimeSlotValidator.class.getName(), context);
					validator.validate(context, null);
				}
				// check if ok before add stats to report
				result = checkValid(context);
				if (result)
					addStats(context, reporter, validationContext, referential);

			}

		} catch (Exception e) {
			log.error("Neptune validation failed ", e);
			throw e;
		} finally {
			AbstractValidator.resetContext(context);
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}
		if (result == ERROR) {
			reporter.addFileErrorInReport(context, fileName, ActionReporter.FILE_ERROR_CODE.INVALID_FORMAT,
					"Neptune compliance failed");
		}
		return result;
	}

	private void addStats(Context context, ActionReporter reporter, Context validationContext, Referential referential) {
		Line line = referential.getLines().values().iterator().next();
		reporter.addObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, NamingUtil.getName(line),
				OBJECT_STATE.OK, IO_TYPE.INPUT);
		reporter.addStatToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.LINE, 1);
		{
			Context localContext = (Context) validationContext.get(ChouetteRouteValidator.LOCAL_CONTEXT);
			int count = (localContext != null) ? localContext.size() : 0;
			reporter.addStatToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.ROUTE, count);
		}
		{
			Context localContext = (Context) validationContext.get(ConnectionLinkValidator.LOCAL_CONTEXT);
			int count = (localContext != null) ? localContext.size() : 0;
			reporter.addStatToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.CONNECTION_LINK,
					count);
		}
		{
			Context localContext = (Context) validationContext.get(TimetableValidator.LOCAL_CONTEXT);
			int count = (localContext != null) ? localContext.size() : 0;
			reporter.addStatToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.TIMETABLE, count);
		}
		{
			Context localContext = (Context) validationContext.get(StopAreaValidator.LOCAL_CONTEXT);
			int count = (localContext != null) ? localContext.size() : 0;
			reporter.addStatToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.STOP_AREA, count);
		}
		{
			Context localContext = (Context) validationContext.get(AccessPointValidator.LOCAL_CONTEXT);
			int count = (localContext != null) ? localContext.size() : 0;
			reporter.addStatToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.ACCESS_POINT,
					count);
		}
		{
			Context localContext = (Context) validationContext.get(VehicleJourneyValidator.LOCAL_CONTEXT);
			int count = (localContext != null) ? localContext.size() : 0;
			reporter.addStatToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.VEHICLE_JOURNEY,
					count);
		}
		{
			Context localContext = (Context) validationContext.get(JourneyPatternValidator.LOCAL_CONTEXT);
			int count = (localContext != null) ? localContext.size() : 0;
			reporter.addStatToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.JOURNEY_PATTERN,
					count);
		}

		// DataStats globalStats = report.getStats();
		// globalStats.setLineCount(globalStats.getLineCount() +
		// stats.getLineCount());
		// globalStats.setAccessPointCount(globalStats.getAccessPointCount() +
		// stats.getAccessPointCount());
		// globalStats.setRouteCount(globalStats.getRouteCount() +
		// stats.getRouteCount());
		// globalStats.setConnectionLinkCount(globalStats.getConnectionLinkCount()
		// + stats.getConnectionLinkCount());
		// globalStats.setVehicleJourneyCount(globalStats.getVehicleJourneyCount()
		// + stats.getVehicleJourneyCount());
		// globalStats.setJourneyPatternCount(globalStats.getJourneyPatternCount()
		// + stats.getJourneyPatternCount());
		// globalStats.setStopAreaCount(globalStats.getStopAreaCount() +
		// stats.getStopAreaCount());
		// globalStats.setTimeTableCount(globalStats.getTimeTableCount() +
		// stats.getTimeTableCount());

	}

	private boolean checkValid(Context context) {
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		return validationReporter.checkValidationReportValidity(context);
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new NeptuneValidationCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(NeptuneValidationCommand.class.getName(), new DefaultCommandFactory());
	}
}
