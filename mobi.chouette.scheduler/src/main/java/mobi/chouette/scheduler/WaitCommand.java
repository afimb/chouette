package mobi.chouette.scheduler;

import java.io.IOException;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;

@Stateless(name = WaitCommand.COMMAND)
@Log4j
public class WaitCommand implements Command, Constant {

	public static final String COMMAND = "WaitCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		sleep(60 * 1000);
		return Constant.SUCCESS;
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ignored) {
		}
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				result = (Command) context.lookup(JAVA_MODULE + COMMAND);
			} catch (NamingException e) {
				log.error(e);
			}
			return result;
		}

	}

	static {
		CommandFactory factory = new DefaultCommandFactory();
		CommandFactory.factories.put(WaitCommand.class.getName(), factory);
	}
}
