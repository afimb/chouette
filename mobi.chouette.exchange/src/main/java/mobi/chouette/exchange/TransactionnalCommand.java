package mobi.chouette.exchange;

import java.io.IOException;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Chain;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;

@Log4j
@Stateless
public class TransactionnalCommand implements Constant {

	public static final String COMMAND = "TransactionnalCommand";

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean execute(Context context, Chain chain) throws Exception {
		boolean result = ERROR;
		log.info("[DSU] execute " + this.toString());
		result = chain.execute(context);
		result = SUCCESS;
		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange/"
						+ COMMAND;
				result = (Command) context.lookup(name);
			} catch (NamingException e) {
				log.error(e);
			}
			return result;
		}
	}

	static {
		CommandFactory factory = new DefaultCommandFactory();
		CommandFactory.factories
				.put(TransactionnalCommand.class.getName(), factory);
	}
}
