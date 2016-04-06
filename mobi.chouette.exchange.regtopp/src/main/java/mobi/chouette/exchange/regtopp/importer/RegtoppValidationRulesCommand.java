package mobi.chouette.exchange.regtopp.importer;

import java.io.IOException;

import javax.naming.InitialContext;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;
import mobi.chouette.exchange.validation.report.ValidationReport;

import static mobi.chouette.exchange.regtopp.Constant.*;

@Log4j
public class RegtoppValidationRulesCommand implements Command {

	public static final String COMMAND = "RegtoppValidationRulesCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		Monitor monitor = MonitorFactory.start(COMMAND);

		ValidationReport validationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);
		if (validationReport == null) {
			validationReport = new ValidationReport();
			context.put(MAIN_VALIDATION_REPORT, validationReport);
		}
		RegtoppImportParameters parameters = (RegtoppImportParameters) context.get(CONFIGURATION);
//		validationReport.setCheckPoints((new RegtoppValidationRules()).checkPoints(parameters));

		RegtoppValidationReporter validationReporter = (RegtoppValidationReporter) context.get(REGTOPP_REPORTER);
		if (validationReporter == null) {
			validationReporter = new RegtoppValidationReporter();
			context.put(REGTOPP_REPORTER, validationReporter);
		}

		log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		return SUCCESS;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new RegtoppValidationRulesCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(RegtoppValidationRulesCommand.class.getName(), new DefaultCommandFactory());
	}

}
