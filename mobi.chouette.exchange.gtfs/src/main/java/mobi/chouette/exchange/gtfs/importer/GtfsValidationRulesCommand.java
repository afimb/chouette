package mobi.chouette.exchange.gtfs.importer;

import java.io.IOException;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.gtfs.validation.Constant;
import mobi.chouette.exchange.gtfs.validation.GtfsValidationRules;
import mobi.chouette.exchange.gtfs.validation.GtfsValidationReporter;
import mobi.chouette.exchange.validation.report.ValidationReport;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class GtfsValidationRulesCommand implements Command, Constant {

	public static final String COMMAND = "GtfsValidationRulesCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		Monitor monitor = MonitorFactory.start(COMMAND);
		
		ValidationReport validationReport = (ValidationReport)context.get(VALIDATION_REPORT);
		if (validationReport == null) {
			validationReport = new ValidationReport();
			context.put(VALIDATION_REPORT, validationReport);
		}
		validationReport.setMaxByFile(true);
		GtfsImportParameters parameters = (GtfsImportParameters)context.get(CONFIGURATION);
		validationReport.addAllCheckPoints((new GtfsValidationRules()).checkPoints(parameters));

		GtfsValidationReporter gtfsValidationReporter = (GtfsValidationReporter)context.get(GTFS_REPORTER);
		if (gtfsValidationReporter == null) {
			gtfsValidationReporter = new GtfsValidationReporter();
			context.put(GTFS_REPORTER, gtfsValidationReporter);
		}
		
		log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		return SUCCESS;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new GtfsValidationRulesCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(GtfsValidationRulesCommand.class.getName(), new DefaultCommandFactory());
	}

}
