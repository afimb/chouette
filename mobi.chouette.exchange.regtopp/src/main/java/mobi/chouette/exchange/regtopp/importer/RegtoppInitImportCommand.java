package mobi.chouette.exchange.regtopp.importer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.naming.InitialContext;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.regtopp.RegtoppConstant;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.model.util.Referential;

@Log4j
public class RegtoppInitImportCommand implements Command {

	public static final String COMMAND = "RegtoppInitImportCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			JobData jobData = (JobData) context.get(JOB_DATA);
			context.put(REFERENTIAL, new Referential());
			// prepare importer
			RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
			if (importer == null) {
				Path path = Paths.get(jobData.getPathName(), INPUT);
				RegtoppValidationReporter reporter = new RegtoppValidationReporter();
				importer = new RegtoppImporter(context, path.toString(), reporter);
				context.put(PARSER, importer);
			}
			RegtoppImportParameters parameters = (RegtoppImportParameters) context.get(CONFIGURATION);
			if (parameters.getReferencesType() == null || parameters.getReferencesType().isEmpty()) {
				parameters.setReferencesType("line");
			}
			if (context.get(VALIDATION) != null)
				context.put(VALIDATION_DATA, new ValidationData());
			result = SUCCESS;

		} catch (Exception e) {
			log.error(e, e);
			throw e;
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}

		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new RegtoppInitImportCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(RegtoppInitImportCommand.class.getName(), new DefaultCommandFactory());
	}

}
