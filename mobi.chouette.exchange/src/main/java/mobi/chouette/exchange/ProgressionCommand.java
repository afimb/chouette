package mobi.chouette.exchange;

import java.io.IOException;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;

@Log4j
public class ProgressionCommand implements Command {

	public static final String COMMAND = "ProgressionCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = SUCCESS;

		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		private ProgressionCommand instance;

		@Override
		protected Command create(InitialContext context) throws IOException {
			if (instance == null) {
				instance = new ProgressionCommand();
			}
			return instance;
		}
	}

	static {
		CommandFactory.factories.put(ProgressionCommand.class.getName(),
				new DefaultCommandFactory());
	}
}
