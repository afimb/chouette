package mobi.chouette.exchange.transfer.exporter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.dao.BlockDAO;
import mobi.chouette.dao.ReferentialLastUpdateDAO;
import mobi.chouette.model.Block;
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
	private BlockDAO blockDAO;

	@EJB
	private LineDAO lineDAO;

	@EJB
	private ReferentialLastUpdateDAO referentialLastUpdateDAO;

	@PersistenceContext(unitName = "referential")
	private EntityManager em;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@TransactionTimeout(value = 2, unit = TimeUnit.HOURS)
	public boolean execute(Context context) throws Exception {

		List<Block> blocksToTransfer = prepareBlocks(context);
		context.put(BLOCKS, blocksToTransfer);

		List<Line> lineToTransfer = prepareLines(context);
		context.put(LINES, lineToTransfer);



		LocalDateTime lastUpdateTimestamp = referentialLastUpdateDAO.getLastUpdateTimestamp();
		context.put(REFERENTIAL_LAST_UPDATE_TIMESTAMP, lastUpdateTimestamp);
	     
		return true;
	}

	protected List<Block> prepareBlocks(Context context) {
		if (!em.isJoinedToTransaction()) {
			throw new RuntimeException("No transaction");
		}

		TransferExportParameters configuration = (TransferExportParameters) context.get(CONFIGURATION);

		log.info("Loading all shared blocks...");
		List<Block> allBlocks = blockDAO.findAll();
		log.info("Filtering blocks");
		List<Block> blocksToTransfer = allBlocks
				.stream()
				.filter(block -> block.filter(configuration.getStartDate(), configuration.getEndDate()))
				.collect(Collectors.toList());
		log.info("Filtering blocks completed");
		log.info("Removing Hibernate proxies");
		HibernateDeproxynator<?> deProxy = new HibernateDeproxynator<>();
		blocksToTransfer = deProxy.deepDeproxy(blocksToTransfer);
		log.info("Removing Hibernate proxies completed");


		em.clear();
		return blocksToTransfer;
	}

	protected List<Line> prepareLines(Context context) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		if (!em.isJoinedToTransaction()) {
			throw new RuntimeException("No transaction");
		}
		
		TransferExportParameters configuration = (TransferExportParameters) context.get(CONFIGURATION);

		log.info("Loading all lines...");
		List<Line> allLines = lineDAO.findAll();
		log.info("Filtering lines");
		List<Line> linesToTransfer = allLines
				.stream()
				.filter(line -> line.filter(configuration.getStartDate(), configuration.getEndDate()))
				.collect(Collectors.toList());
		log.info("Filtering lines completed");
		log.info("Removing Hibernate proxies");
		HibernateDeproxynator<?> deProxy = new HibernateDeproxynator<>();
		linesToTransfer = deProxy.deepDeproxy(linesToTransfer);
		log.info("Removing Hibernate proxies completed");
		

		em.clear();
		return linesToTransfer;
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
