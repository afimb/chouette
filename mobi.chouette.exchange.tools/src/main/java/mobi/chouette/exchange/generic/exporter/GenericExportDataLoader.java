package mobi.chouette.exchange.generic.exporter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.model.Line;
import mobi.chouette.model.NeptuneObject;

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
		List<Line> lineToTransfer = new ArrayList<>();

		LineFilter lineFilter = new LineFilter();

		HibernateDeproxynator<?> deProxy = new HibernateDeproxynator<>();
		allLines = deProxy.deepDeproxy(allLines);

		for (Line line : allLines) {
			// Clean identfiers
//			Set<Object> visitedObjects = new HashSet<>();
//			eagerLoadLazyReferences(line, visitedObjects);
//
//			// Ease garbage collection
//			visitedObjects.clear();
			
			
			// Clean according to date rules
			// Clean obsolete data
			boolean shouldKeep = lineFilter.filter(line, configuration.getStartDate(), configuration.getEndDate());

			if (shouldKeep) {
				lineToTransfer.add(line);
//			} else {
//				log.warn("Skipping line "+line.getObjectId());
			}
		}
		// transaction.commit();

		em.clear();
		return lineToTransfer;
	}

	private void eagerLoadLazyReferences(NeptuneObject neptuneObject, Set<Object> visitedObjects)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException {
		if (visitedObjects.contains(neptuneObject)) {
			log.info(
					"Skipping existing object " + neptuneObject.getClass().getName() + "/"+neptuneObject.hashCode());
			return;
		}

		if (neptuneObject instanceof HibernateProxy) {
			log.info("Unwrapping object " + neptuneObject.getClass().getName()+"/"+neptuneObject.hashCode());
			neptuneObject = (NeptuneObject) ((HibernateProxy) neptuneObject).getHibernateLazyInitializer()
					.getImplementation();
		}

		visitedObjects.add(neptuneObject);
		// Follow lists
		Method[] methods = neptuneObject.getClass().getMethods();

		for (Method m : methods) {
			if (m.getReturnType().equals(List.class)) {
				List<?> listContent = (List<?>) m.invoke(neptuneObject);
				// Eager load possibly lazy collection
				log.info(
						"Initializing collection " + neptuneObject.getClass().getName() + "/"+neptuneObject.hashCode()+" and method " + m.getName());
				Hibernate.initialize(listContent);

				// Replace any Hibernate List implementations with ArrayList
				List newContent = new ArrayList<>();

				for (int i = 0; i < listContent.size(); i++) {

					Object unwrapped = listContent.get(i);
					if (unwrapped instanceof HibernateProxy) {
						unwrapped = ((HibernateProxy) listContent.get(i)).getHibernateLazyInitializer()
								.getImplementation();
					}
					newContent.add(unwrapped);
				}

				listContent.clear();
				listContent.addAll(newContent);
				
				boolean hasVistitedCollection = visitedObjects.contains(listContent);
				if(!hasVistitedCollection) {
					log.info(
							"Not visited collection " + listContent.getClass().getName() + "/"+listContent.hashCode()+" and method " + m.getName());
					
					visitedObjects.add(listContent);
					for(Object c : listContent) {
						if (c instanceof NeptuneObject) {
							eagerLoadLazyReferences((NeptuneObject) c, visitedObjects);
						}
					}
				}
			}

			if (m.getName().startsWith("get")) {
				Object result = m.invoke(neptuneObject);
				if (result instanceof HibernateProxy) {
					log.info("Initializing reference " + result.getClass().getName() + "/"+result.hashCode()+" and method " + m.getName());
					Object entity = ((HibernateProxy) result).getHibernateLazyInitializer().getImplementation();

					Method setMethod = neptuneObject.getClass().getMethod(m.getName().replaceFirst("get", "set"),
							entity.getClass());
					setMethod.invoke(neptuneObject, entity);

				}

				result = m.invoke(neptuneObject);
				if (result instanceof NeptuneObject) {
					eagerLoadLazyReferences((NeptuneObject) result, visitedObjects);
				}
			}

		}

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
