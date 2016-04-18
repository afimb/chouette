package mobi.chouette.exchange.regtopp.importer;

import static mobi.chouette.exchange.regtopp.RegtoppConstant.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.naming.InitialContext;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppDayCodeDKO;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppDestinationDST;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppFootnoteMRK;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppInterchangeSAM;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppLineLIN;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppPathwayGAV;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppPeriodPER;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppRoutePointRUT;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppTableVersionTAB;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppVehicleJourneyVLP;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppZoneSON;
import mobi.chouette.exchange.regtopp.parser.RegtoppLineParser;
import mobi.chouette.exchange.regtopp.parser.RegtoppStopParser;
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

		// Run "validate" on all parsers
		if (importer.hasHPLImporter()) {
			RegtoppStopParser stopParser = (RegtoppStopParser) ParserFactory.create(RegtoppStopParser.class.getName());
			stopParser.validate(context);
		}

		if (importer.hasTIXImporter()) {
			RegtoppLineParser lineParser = (RegtoppLineParser) ParserFactory.create(RegtoppLineParser.class.getName());
			lineParser.validate(context);
		}

		// TODO Add all parsers

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
