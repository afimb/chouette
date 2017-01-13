package mobi.chouette.exchange.generic.exporter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
import mobi.chouette.model.Line;

@Log4j
@Stateless(name = GenericExportDataLoader.COMMAND)
public class GenericExportDataLoader implements Command {

	public static final String COMMAND = "GenericExporterDataLoader";

	@EJB
	private LineDAO lineDAO;

	@PersistenceContext(unitName = "referential")
	private EntityManager em;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean execute(Context context) throws Exception {

		List<Line> lineToTransfer = prepareLines(context);
		context.put("LINES", lineToTransfer);

		return true;
	}

	protected List<Line> prepareLines(Context context) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		if (!em.isJoinedToTransaction()) {
			throw new RuntimeException("No transaction");
		}
		
		GenericExportParameters configuration = (GenericExportParameters) context.get(CONFIGURATION);

		List<Line> allLines = lineDAO.findAll();
		HibernateDeproxynator<?> deProxy = new HibernateDeproxynator<>();
		allLines = deProxy.deepDeproxy(allLines);
		List<Line> lineToTransfer = new ArrayList<>();

		LineFilter lineFilter = new LineFilter();

		for (Line line : allLines) {
			// Clean according to date rules
			// Clean obsolete data
			boolean shouldKeep = lineFilter.filter(line, configuration.getStartDate(), configuration.getEndDate());

			if (shouldKeep) {
				lineToTransfer.add(line);
			}
		}

		em.clear();
		return lineToTransfer;
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
		CommandFactory.factories.put(GenericExportDataLoader.class.getName(), new DefaultCommandFactory());
	}

}
