package mobi.chouette.exchange.report;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.JSONUtils;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;

import org.apache.commons.io.FileUtils;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

// @Stateless(name = ReportCommand.COMMAND)
@Log4j
public class ReportCommand implements Command {

	public static final String COMMAND = "ReportCommand";

	public boolean execute(Context context) throws Exception {

		Monitor monitor = MonitorFactory.start(COMMAND);
		Report report = (Report) context.get(REPORT);
		Path path = Paths.get(context.get(PATH).toString(), REPORT_FILE);
		String data = JSONUtils.toJSON(report).replaceAll("\\},\\{", "\n},\n{");
		FileUtils.writeStringToFile(path.toFile(), data);
		log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		return SUCCESS;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {

			Command result = new ReportCommand();
			// try {
			// String name = "java:app/mobi.chouette.exchange/" + COMMAND;
			// result = (Command) context.lookup(name);
			// } catch (NamingException e) {
			// log.error(e);
			// }
			return result;
		}
	}

	static {
		CommandFactory.factories.put(ReportCommand.class.getName(),
				new DefaultCommandFactory());
	}
}
