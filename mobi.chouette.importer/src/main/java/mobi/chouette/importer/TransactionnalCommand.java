package mobi.chouette.importer;

import java.io.IOException;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.ChainImpl;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;

@Stateless(name = TransactionnalCommand.COMMAND)
@Log4j
public class TransactionnalCommand extends ChainImpl {

	public static final String COMMAND = "TransactionnalCommand";

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean execute(Context context) throws Exception {
		return super.execute(context);
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
		CommandFactory.factories.put(TransactionnalCommand.class.getName(),
				factory);
	}
}
