package mobi.chouette.exchange.regtopp.importer;

import java.io.IOException;
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

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.exchange.regtopp.RegtoppConstant;
import mobi.chouette.exchange.regtopp.importer.index.Index;
import mobi.chouette.exchange.regtopp.importer.index.v11.DaycodeById;
import mobi.chouette.exchange.regtopp.importer.parser.ObjectIdCreator;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDayCodeHeaderDKO;
import mobi.chouette.model.Line;

@Log4j
@Stateless(name = RegtoppRemoveObsoleteLinesCommand.COMMAND)
public class RegtoppRemoveObsoleteLinesCommand implements Command {

	public static final String COMMAND = "RegtoppRemoveObsoleteLinesCommand";

	@EJB
	private LineDAO lineDAO;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean execute(Context context) throws Exception {

		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
		
			// Find any lines that were registered with a dataset with the same date header, but that are not included in the current dataset delivery (regtopp file set)
			
			RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);
			RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
			Index<AbstractRegtoppTripIndexTIX> index = importer.getUniqueLinesByTripIndex();
			Iterator<String> keys = index.keys();
	
			String calendarStartDate = (String) context.get(RegtoppConstant.CALENDAR_START_DATE);
			if(calendarStartDate == null) {
				DaycodeById dayCodeIndex = (DaycodeById) importer.getDayCodeById();
				RegtoppDayCodeHeaderDKO header = dayCodeIndex.getHeader();
				calendarStartDate = header.getDate().toString();

			}

			// Register all lineIds that should be present with the given calendarStartDate
			Set<String> objectIdSet = new HashSet<>();
			while(keys.hasNext()) {
				objectIdSet.add(ObjectIdCreator.createLineId(configuration, keys.next(), calendarStartDate));
			}

			List<Line> findAll = lineDAO.findAll();
			
			for(Line existingLine : findAll ) {
				
				String adminCode = importer.getTripIndex().iterator().next().getAdminCode();
				String authority = existingLine.getNetwork().getRegistrationNumber();
				
				if(adminCode.equals(authority) && ObjectIdCreator.getCalendarStartDate(existingLine.getObjectId()).equals(calendarStartDate)) {
					if(!objectIdSet.contains(existingLine.getObjectId())) {
						log.info("Delete obsolete line : " + existingLine.getObjectId() + " "+existingLine.getName());
						lineDAO.delete(existingLine);
						
					}
				}
			}

			lineDAO.flush();
		
			result = SUCCESS;
		} catch (Exception ex) {
			log.error(ex.getMessage());
			throw ex;
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}
		return result;
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
		CommandFactory.factories.put(RegtoppRemoveObsoleteLinesCommand.class.getName(), new DefaultCommandFactory());
	}
}
