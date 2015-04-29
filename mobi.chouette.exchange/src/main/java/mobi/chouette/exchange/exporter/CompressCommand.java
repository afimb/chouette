package mobi.chouette.exchange.exporter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.FileUtil;
import mobi.chouette.common.JobData;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class CompressCommand implements Command, Constant {

	public static final String COMMAND = "CompressCommand";

	@Override
	public boolean execute(Context context) throws Exception {

		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			JobData jobData = (JobData) context.get(JOB_DATA);
			String path = jobData.getPathName();
			String file = jobData.getFilename();
			Path target = Paths.get(path, OUTPUT);
			Path filename = Paths.get(path, file);
			FileUtil.compress(target.toString(), filename.toString());
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
			Command result = new CompressCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(CompressCommand.class.getName(),
				new DefaultCommandFactory());
	}
}
