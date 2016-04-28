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

		if(importer.hasDSTImporter()) {
			importer.getDestinationById();
		}
		if(importer.hasMRKImporter()) {
			importer.getFootnoteById();
		}
		if(importer.hasDKOImporter()) {
			importer.getDayCodeById();
		}
		if(importer.hasHPLImporter()) {
			importer.getStopById();
		}
		if(importer.hasLINImporter()) {
			importer.getLineById();
		}
		if(importer.hasGAVImporter()) {
			importer.getPathwayByIndexingKey();
		}
		if(importer.hasSTPImporter()) {
			importer.getStopPointsByIndexingKey();
		}
		if(importer.hasTDAImporter()) {
			importer.getRouteSegmentByLineNumber();
		}
		if(importer.hasTIXImporter()) {
			importer.getTripIndex();
		}
		if(importer.hasTMSImporter()) {
			importer.getRouteIndex();
		}
		
		
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
