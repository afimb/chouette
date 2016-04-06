package mobi.chouette.exchange.regtopp.importer;

import static mobi.chouette.exchange.regtopp.Constant.*;

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
import mobi.chouette.exchange.regtopp.model.RegtoppDayCodeDKO;
import mobi.chouette.exchange.regtopp.model.RegtoppDestinationDST;
import mobi.chouette.exchange.regtopp.model.RegtoppFootnoteMRK;
import mobi.chouette.exchange.regtopp.model.RegtoppInterchangeSAM;
import mobi.chouette.exchange.regtopp.model.RegtoppLineLIN;
import mobi.chouette.exchange.regtopp.model.RegtoppPathwayGAV;
import mobi.chouette.exchange.regtopp.model.RegtoppPeriodPER;
import mobi.chouette.exchange.regtopp.model.RegtoppRoutePointRUT;
import mobi.chouette.exchange.regtopp.model.RegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.model.RegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.RegtoppTableVersionTAB;
import mobi.chouette.exchange.regtopp.model.RegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.RegtoppVehicleJourneyVLP;
import mobi.chouette.exchange.regtopp.model.RegtoppZoneSON;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;
import mobi.chouette.exchange.regtopp.parser.RegtoppStopParser;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;
import mobi.chouette.exchange.report.ActionReport;


@Log4j
public class RegtoppFileConsistencyValidationCommand implements Command {

	public static final String COMMAND = "RegtoppFilePresenceValidationCommand";

	private static final List<String> mandatoryFileExtensions = Arrays.asList(RegtoppTripIndexTIX.FILE_EXTENSION, RegtoppRouteTMS.FILE_EXTENSION,
			RegtoppStopHPL.FILE_EXTENSION, RegtoppDayCodeDKO.FILE_EXTENSION);

	private static final List<String> optionalFileExtensions = Arrays.asList(RegtoppDestinationDST.FILE_EXTENSION, RegtoppFootnoteMRK.FILE_EXTENSION,
			RegtoppPathwayGAV.FILE_EXTENSION, RegtoppInterchangeSAM.FILE_EXTENSION, RegtoppZoneSON.FILE_EXTENSION, RegtoppLineLIN.FILE_EXTENSION,
			RegtoppVehicleJourneyVLP.FILE_EXTENSION, RegtoppTableVersionTAB.FILE_EXTENSION, RegtoppPeriodPER.FILE_EXTENSION,
			RegtoppRoutePointRUT.FILE_EXTENSION);

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

		// TODO Add all parsers

		// TODO
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
