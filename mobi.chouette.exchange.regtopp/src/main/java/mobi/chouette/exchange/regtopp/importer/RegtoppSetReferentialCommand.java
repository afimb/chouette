package mobi.chouette.exchange.regtopp.importer;

import java.io.IOException;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.exchange.regtopp.RegtoppConstant;
import mobi.chouette.model.util.Referential;

@Log4j
@Stateless(name = RegtoppSetReferentialCommand.COMMAND)
public class RegtoppSetReferentialCommand implements Command {

	public static final String COMMAND = "RegtoppSetReferentialCommand";

	@EJB
	private LineDAO lineDAO;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean execute(Context context) throws Exception {

		Map<String,Referential> lineReferentials = (Map<String, Referential>) context.get(RegtoppConstant.LINE_REFERENTIALS);
		Referential referential = lineReferentials.remove(lineReferentials.keySet().iterator().next());
		context.put(REFERENTIAL,referential);
		return SUCCESS;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange.regtopp/" + COMMAND;
				result = (Command) context.lookup(name);
			} catch (NamingException e) {
				// try another way on test context
				String name = "java:module/" + COMMAND;
				try {
					result = (Command) context.lookup(name);
				} catch (NamingException e1) {
					log.error(e);
				}
			}
			return result;
		}
	}

	static {
		CommandFactory.factories.put(RegtoppSetReferentialCommand.class.getName(), new DefaultCommandFactory());
	}
}
