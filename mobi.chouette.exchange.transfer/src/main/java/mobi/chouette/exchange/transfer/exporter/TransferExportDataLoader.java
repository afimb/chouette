package mobi.chouette.exchange.transfer.exporter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.ejb3.annotation.TransactionTimeout;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.exchange.transfer.Constant;
import mobi.chouette.model.Line;

@Log4j
@Stateless(name = TransferExportDataLoader.COMMAND)
public class TransferExportDataLoader implements Command, Constant {

	public static final String COMMAND = "TransferExporterDataLoader";

	@EJB
	private LineDAO lineDAO;

	@PersistenceContext(unitName = "referential")
	private EntityManager em;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@TransactionTimeout(value = 2, unit = TimeUnit.HOURS)
	public boolean execute(Context context) throws Exception {

		List<Line> lineToTransfer = prepareLines(context);
		context.put(LINES, lineToTransfer);
	     
		return true;
	}

	protected List<Line> prepareLines(Context context) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		if (!em.isJoinedToTransaction()) {
			throw new RuntimeException("No transaction");
		}
		
		TransferExportParameters configuration = (TransferExportParameters) context.get(CONFIGURATION);

		log.info("Loading all lines...");
		List<Line> allLines = lineDAO.findAll();
		
		List<Line> lineToTransfer = new ArrayList<>();
		
		LineFilter lineFilter = new LineFilter();

		log.info("Filtering lines");
		for (Line line : allLines) {
			// Clean according to date rules
			// Clean obsolete data
			boolean shouldKeep = lineFilter.filter(line, configuration.getStartDate(), configuration.getEndDate());

			if (shouldKeep) {
				lineToTransfer.add(line);
			}
		}
		
		log.info("Filtering lines completed");
		log.info("Removing Hibernate proxies");
		HibernateDeproxynator<?> deProxy = new HibernateDeproxynator<>();
		lineToTransfer = deProxy.deepDeproxy(lineToTransfer);
		log.info("Removing Hibernate proxies completed");
		

		em.clear();
		return lineToTransfer;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange.transfer/" + COMMAND;
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
		CommandFactory.factories.put(TransferExportDataLoader.class.getName(), new DefaultCommandFactory());
	}

}
