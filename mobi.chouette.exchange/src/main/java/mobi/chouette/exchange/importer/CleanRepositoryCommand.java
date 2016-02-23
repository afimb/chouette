package mobi.chouette.exchange.importer;

import java.io.IOException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.GenericDAO;
import mobi.chouette.model.Company;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.RouteSection;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.Timetable;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
@Stateless(name = CleanRepositoryCommand.COMMAND)
public class CleanRepositoryCommand implements Command {

	public static final String COMMAND = "CleanRepositoryCommand";

	@EJB (mappedName="java:app/mobi.chouette.dao/LineDAO")
	private GenericDAO<Line> lineDAO;

	@EJB (mappedName="java:app/mobi.chouette.dao/NetworkDAO")
	private GenericDAO<Network> networkDAO;
	
	@EJB (mappedName="java:app/mobi.chouette.dao/StopAreaDAO")
	private GenericDAO<StopArea> stopAreaDAO;

	@EJB (mappedName="java:app/mobi.chouette.dao/RouteSectionDAO")
	private GenericDAO<RouteSection> routeSectionDAO;

	@EJB (mappedName="java:app/mobi.chouette.dao/CompanyDAO")
	private GenericDAO<Company> companyDAO;
	
	@EJB (mappedName="java:app/mobi.chouette.dao/TimetableDAO")
	private GenericDAO<Timetable> timetableDAO;
	
	@EJB (mappedName="java:app/mobi.chouette.dao/GroupOfLineDAO")
	private GenericDAO<GroupOfLine> groupOfLineDAO;
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean execute(Context context) throws Exception {

		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			lineDAO.truncate();
			companyDAO.truncate();
			networkDAO.truncate();
			routeSectionDAO.truncate();
			stopAreaDAO.truncate();
			timetableDAO.truncate();
			groupOfLineDAO.truncate();

			result = SUCCESS;
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
		log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		return result;
	}


	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange/" + COMMAND;
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
		CommandFactory.factories.put(CleanRepositoryCommand.class.getName(),
				new DefaultCommandFactory());
	}
}
