package mobi.chouette.exchange.validation;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.JSONUtils;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.validation.report.ValidationReport;

import org.apache.commons.io.FileUtils;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Stateless(name = ValidationReportCommand.COMMAND)
@Log4j
public class ValidationReportCommand implements Command {

	public static final String COMMAND = "ValidationReportCommand";

	public boolean execute(Context context) throws Exception {

		// boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

        ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
        
        Path path = Paths.get(context.get(PATH).toString(), VALIDATION_FILE);
        
        String data = JSONUtils.toJSON(report);
        
        FileUtils.writeStringToFile(path.toFile(), data);
		
		log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		return true;
	}


	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {

			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange/" + COMMAND;
				result = (Command) context.lookup(name);
			} catch (NamingException e) {
				log.error(e);
			}
			return result;
		}
	}

	static {
		CommandFactory factory = new DefaultCommandFactory();
		CommandFactory.factories.put(ValidationReportCommand.class.getName(), factory);
	}
}
