package mobi.chouette.exchange.regtopp.importer;

import static mobi.chouette.exchange.regtopp.RegtoppConstant.REGTOPP_REPORTER;

import java.io.IOException;

import javax.naming.InitialContext;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.importer.Validator;
import mobi.chouette.exchange.regtopp.RegtoppConstant;
import mobi.chouette.exchange.regtopp.importer.version.VersionHandler;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;
import mobi.chouette.exchange.report.ActionReport;

@Log4j
public class RegtoppFileConsistencyValidationCommand implements Command {

	public static final String COMMAND = RegtoppFileConsistencyValidationCommand.class.getSimpleName();

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		ActionReport report = (ActionReport) context.get(REPORT);

		JobData jobData = (JobData) context.get(JOB_DATA);
		// check ignored files

		RegtoppImportParameters parameters = (RegtoppImportParameters) context.get(CONFIGURATION);
		// TODO read ie version from here

		RegtoppValidationReporter validationReporter = (RegtoppValidationReporter) context.get(REGTOPP_REPORTER);

		RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
		VersionHandler versionHandler = (VersionHandler) context.get(RegtoppConstant.VERSION_HANDLER);

		// Run "validate" on all parsers
		Validator stopParser = (Validator) versionHandler.createStopParser();
		stopParser.validate(context);

		Validator connectionLinkParser = (Validator) versionHandler.createConnectionLinkParser();
		connectionLinkParser.validate(context);

		Validator lineParser = versionHandler.createTripParser();
		lineParser.validate(context);

		Validator routeParser = versionHandler.createRouteParser();
		routeParser.validate(context);

		return true;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new RegtoppFileConsistencyValidationCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(RegtoppFileConsistencyValidationCommand.class.getName(), new DefaultCommandFactory());
	}
}
