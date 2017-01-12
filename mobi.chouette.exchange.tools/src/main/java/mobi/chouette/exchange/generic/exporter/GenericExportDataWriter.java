package mobi.chouette.exchange.generic.exporter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;

import com.rits.cloning.Cloner;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.exchange.importer.CleanRepositoryCommand;
import mobi.chouette.model.Line;
import mobi.chouette.model.NeptuneObject;

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

		List<Line> lineToTransfer = (List<Line>) context.get("LINES");
		List<Line> cloneLines = cloneLines(lineToTransfer);
		
		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		Command cleanCommand = CommandFactory.create(initialContext, CleanRepositoryCommand.class.getName());
		boolean cleanCommandResult = cleanCommand.execute(context);

		
		writeLines(cloneLines);

		return true;
	}

	private List<Line> cloneLines(List<Line> lineToTransfer) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {

//		for (Line line : lineToTransfer) {
//			Set<Object> visitedObjects = new HashSet<>();
//			cleanDatabaseIdentifiers(line, visitedObjects);
//
//			// Ease garbage collection
//			visitedObjects.clear();
//		}
//
		Cloner cloner = new Cloner();
		cloner.setDumpClonedClasses(true);
		//return cloner.deepClone(lineToTransfer);
		return lineToTransfer;
	}

	private void cleanDatabaseIdentifiers(NeptuneObject neptuneObject, Set<Object> visitedObjects)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException {
		if (visitedObjects.contains(neptuneObject)) {
			return;
		}
		log.info("Cleaning database identifier for " + neptuneObject.getClass().getSimpleName() + " with id "
				+ neptuneObject.getId());
		neptuneObject.setId(null);
		visitedObjects.add(neptuneObject);
		// Follow lists
		Method[] methods = neptuneObject.getClass().getMethods();

		for (Method m : methods) {
			if (m.getReturnType().equals(List.class)) {
				List<?> listContent = (List<?>) m.invoke(neptuneObject);
				log.info("Cleaning collection for " + neptuneObject.getClass().getSimpleName() + " with method "+m.getName());

				for(Object p : listContent) {
					if(p instanceof NeptuneObject) {
						cleanDatabaseIdentifiers((NeptuneObject) p, visitedObjects);
					}
				}
				
				try {
					// Find setter
					Method setMethod = neptuneObject.getClass().getMethod(m.getName().replaceFirst("get", "set"),
							List.class);
					// Replace hibernate list implementation with new ArrayList
					List newList = new ArrayList<>(listContent);
					setMethod.invoke(neptuneObject, newList);
					log.info("Cleaned collection for " + neptuneObject.getClass().getSimpleName() + " with method "+m.getName()+", result is "+m.invoke(neptuneObject).getClass());
				} catch (NoSuchMethodException e) {
					log.info("No corresponding setter found for " + m.getName());
				}

			}

			if(m.getName().startsWith("get")) {
				Object result = m.invoke(neptuneObject);
				
				if(result instanceof NeptuneObject) {
					cleanDatabaseIdentifiers((NeptuneObject) result, visitedObjects);
				}
			}
		}

	}

	protected void writeLines(List<Line> lineToTransfer) {
		if (!em.isJoinedToTransaction()) {
			throw new RuntimeException("No transaction");
		}
		// Persist
		for (Line line : lineToTransfer) {
			lineDAO.create(line);
		}
		lineDAO.flush();
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
