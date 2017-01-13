package mobi.chouette.exchange.generic.exporter;

import java.io.IOException;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.exchange.importer.CleanRepositoryCommand;
import mobi.chouette.model.Line;

@Log4j
@Stateless(name = GenericExportDataWriter.COMMAND)
public class GenericExportDataWriter implements Command {

	public static final String COMMAND = "GenericExporterDataWriter";

	@EJB
	private LineDAO lineDAO;

	@PersistenceContext(unitName = "referential")
	private EntityManager em;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean execute(Context context) throws Exception {
		if (!em.isJoinedToTransaction()) {
			throw new RuntimeException("No transaction");
		}

		List<Line> lineToTransfer = (List<Line>) context.get("LINES");

		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		Command cleanCommand = CommandFactory.create(initialContext, CleanRepositoryCommand.class.getName());
		log.info("Cleaning target dataspace");
		boolean cleanCommandResult = cleanCommand.execute(context);

		// Persist
		log.info("Starting to persist lines, count=" + lineToTransfer.size());

		for (Line line : lineToTransfer) {
			//log.info("Persisting line " + line.getObjectId() + " / " + line.getName());
			lineDAO.create(line);
		}
		log.info("Flushing to database");
		lineDAO.flush();
		log.info("Flush completed");

		return true;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange.generic/" + COMMAND;
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
		CommandFactory.factories.put(GenericExportDataWriter.class.getName(), new DefaultCommandFactory());
	}
}
