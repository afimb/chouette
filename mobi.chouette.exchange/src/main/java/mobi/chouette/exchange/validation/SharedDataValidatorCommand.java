/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.validation;

import java.io.IOException;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.validation.checkpoint.AccessLinkCheckPoints;
import mobi.chouette.exchange.validation.checkpoint.AccessPointCheckPoints;
import mobi.chouette.exchange.validation.checkpoint.CompanyCheckPoints;
import mobi.chouette.exchange.validation.checkpoint.ConnectionLinkCheckPoints;
import mobi.chouette.exchange.validation.checkpoint.GroupOfLineCheckPoints;
import mobi.chouette.exchange.validation.checkpoint.NetworkCheckPoints;
import mobi.chouette.exchange.validation.checkpoint.SharedLineCheckPoints;
import mobi.chouette.exchange.validation.checkpoint.StopAreaCheckPoints;
import mobi.chouette.exchange.validation.checkpoint.TimetableCheckPoints;
import mobi.chouette.exchange.validation.report.ValidationReport;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 *
 */
@Log4j
public class SharedDataValidatorCommand implements Command, Constant {
	public static final String COMMAND = "SharedDataValidatorCommand";

	private SharedLineCheckPoints sharedLineCheckPoints = new SharedLineCheckPoints();
	private NetworkCheckPoints networkCheckPoints = new NetworkCheckPoints();
	private CompanyCheckPoints companyCheckPoints = new CompanyCheckPoints();
	private GroupOfLineCheckPoints groupOfLineCheckPoints = new GroupOfLineCheckPoints();
	private TimetableCheckPoints timetableCheckPoints = new TimetableCheckPoints();
	private StopAreaCheckPoints stopAreaCheckPoints = new StopAreaCheckPoints();
	private ConnectionLinkCheckPoints connectionLinkCheckPoints = new ConnectionLinkCheckPoints();
	private AccessPointCheckPoints accessPointCheckPoints = new AccessPointCheckPoints();
	private AccessLinkCheckPoints accessLinkCheckPoints = new AccessLinkCheckPoints();

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);
		if (!context.containsKey(SOURCE))
		{
			// not called from DAO
			context.put(SOURCE, SOURCE_FILE);
		}
		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		if (report == null) {
			context.put(VALIDATION_REPORT, new ValidationReport());
		}
		try {
			sharedLineCheckPoints.validate(context, null);
			networkCheckPoints.validate(context, null);
			companyCheckPoints.validate(context, null);
			groupOfLineCheckPoints.validate(context, null);
			timetableCheckPoints.validate(context, null);
			stopAreaCheckPoints.validate(context, null);
			connectionLinkCheckPoints.validate(context, null);
			accessPointCheckPoints.validate(context, null);
			accessLinkCheckPoints.validate(context, null);

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
			Command result = new SharedDataValidatorCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(SharedDataValidatorCommand.class.getName(), new DefaultCommandFactory());
	}

}
