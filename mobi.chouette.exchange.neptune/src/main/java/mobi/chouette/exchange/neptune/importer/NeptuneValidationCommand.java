package mobi.chouette.exchange.neptune.importer;

import java.io.IOException;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.ToString;
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
import mobi.chouette.exchange.report.Report;
import mobi.chouette.exchange.validation.ValidatorFactory;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Stateless(name = NeptuneValidationCommand.COMMAND)
@ToString
@Log4j
public class NeptuneValidationCommand implements Command, Constant {

	public static final String COMMAND = "NeptuneValidationCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		Report report = (Report) context.get(REPORT);
		

		try {
			Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
			if (validationContext != null)
			{
				{
				PTNetworkValidator validator = (PTNetworkValidator) ValidatorFactory.create(PTNetworkValidator.class.getName(), context);
				validator.validate(context, null);
				}
				{
				GroupOfLineValidator validator = (GroupOfLineValidator) ValidatorFactory.create(GroupOfLineValidator.class.getName(), context);
				validator.validate(context, null);
				}
				{
				CompanyValidator validator = (CompanyValidator) ValidatorFactory.create(CompanyValidator.class.getName(), context);
				validator.validate(context, null);
				}
				{
				StopAreaValidator validator = (StopAreaValidator) ValidatorFactory.create(StopAreaValidator.class.getName(), context);
				validator.validate(context, null);
				}
				{
				AreaCentroidValidator validator = (AreaCentroidValidator) ValidatorFactory.create(AreaCentroidValidator.class.getName(), context);
				validator.validate(context, null);
				}
				{
				ConnectionLinkValidator validator = (ConnectionLinkValidator) ValidatorFactory.create(ConnectionLinkValidator.class.getName(), context);
				validator.validate(context, null);
				}
				{
				AccessPointValidator validator = (AccessPointValidator) ValidatorFactory.create(AccessPointValidator.class.getName(), context);
				validator.validate(context, null);
				}
				{
				AccessLinkValidator validator = (AccessLinkValidator) ValidatorFactory.create(AccessLinkValidator.class.getName(), context);
				validator.validate(context, null);
				}
				{
				LineValidator validator = (LineValidator) ValidatorFactory.create(LineValidator.class.getName(), context);
				validator.validate(context, null);
				}
				{
				ChouetteRouteValidator validator = (ChouetteRouteValidator) ValidatorFactory.create(ChouetteRouteValidator.class.getName(), context);
				validator.validate(context, null);
				}
				{
				PtLinkValidator validator = (PtLinkValidator) ValidatorFactory.create(PtLinkValidator.class.getName(), context);
				validator.validate(context, null);
				}
				{
				JourneyPatternValidator validator = (JourneyPatternValidator) ValidatorFactory.create(JourneyPatternValidator.class.getName(), context);
				validator.validate(context, null);
				}
				{
				StopPointValidator validator = (StopPointValidator) ValidatorFactory.create(StopPointValidator.class.getName(), context);
				validator.validate(context, null);
				}
				{
				TimetableValidator validator = (TimetableValidator) ValidatorFactory.create(TimetableValidator.class.getName(), context);
				validator.validate(context, null);
				}
				{
				VehicleJourneyValidator validator = (VehicleJourneyValidator) ValidatorFactory.create(VehicleJourneyValidator.class.getName(), context);
				validator.validate(context, null);
				}
				
			}

		} catch (Exception e) {
			throw e;
		}
		finally
		{
			AbstractValidator.resetContext(context);
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}
		return true;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange.neptune/"
						+ COMMAND;
				result = (Command) context.lookup(name);
			} catch (NamingException e) {
				log.error(e);
			}
			return result;
		}
	}

	static {
		CommandFactory factory = new DefaultCommandFactory();
		CommandFactory.factories.put(NeptuneValidationCommand.class.getName(),
				factory);
	}
}
