package mobi.chouette.exchange.netexprofile.importer;

import java.io.IOException;
import java.sql.SQLException;

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
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.ActionReporter.ERROR_CODE;
import mobi.chouette.exchange.report.ActionReporter.Factory;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_STATE;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_TYPE;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.util.NamingUtil;
import mobi.chouette.model.util.Referential;

@Log4j
@Stateless(name = NetexprofileLineDeleteCommand.COMMAND)
public class NetexprofileLineDeleteCommand implements Command {

	public static final String COMMAND = "NetexprofileLineDeleteCommand";

	@EJB
	private LineDAO lineDAO;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean execute(Context context) throws Exception {

		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		Referential referential = (Referential) context.get(REFERENTIAL);

		Line newLine = referential.getLines().values().iterator().next();
		try {

			Line existingLine = lineDAO.findByObjectId(newLine.getObjectId());
			if (existingLine != null) {
				log.info("Delete existing line before import: " + existingLine.getObjectId() + " "+existingLine.getName());
				clearRouteSectionReferences(existingLine);
				lineDAO.delete(existingLine);
				lineDAO.flush();
			}
			result = SUCCESS;
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ActionReporter actionReporter = Factory.getInstance();

			actionReporter.addObjectReport(context, newLine.getObjectId(),
					OBJECT_TYPE.LINE, NamingUtil.getName(newLine), OBJECT_STATE.ERROR, IO_TYPE.INPUT);

			if (ex.getCause() != null) {
				Throwable e = ex.getCause();
				while (e.getCause() != null) {
					log.error(e.getMessage());
					e = e.getCause();
				}
				if (e instanceof SQLException) {
					e = ((SQLException) e).getNextException();
					actionReporter.addErrorToObjectReport(context, newLine.getObjectId(), OBJECT_TYPE.LINE, ERROR_CODE.WRITE_ERROR,  e.getMessage());

				} else {
					actionReporter.addErrorToObjectReport(context, newLine.getObjectId(), OBJECT_TYPE.LINE, ERROR_CODE.INTERNAL_ERROR,  e.getMessage());
				}
			} else {
				actionReporter.addErrorToObjectReport(context, newLine.getObjectId(), OBJECT_TYPE.LINE, ERROR_CODE.INTERNAL_ERROR,  ex.getMessage());
			}
			throw ex;
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}
		return result;
	}

	/**
	 * Journey patterns may reference multiple route sections, each of which may be referenced by multiple journey patterns.
	 * Without a back reference from route section to journey pattern there is no way of knowing whether route section is
	 * referred to by other journey patters or whether it should be deleted.
	 *
	 * Only deleting join table rows for now, route sections will only be deleted by cleaning space.
	 */
	private void clearRouteSectionReferences(Line existingLine) {
		for (Route route:existingLine.getRoutes()){
			for (JourneyPattern journeyPattern:route.getJourneyPatterns()){
				journeyPattern.getRouteSections().clear();
			}
		}
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange.netexprofile/" + COMMAND;
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
		CommandFactory.factories.put(NetexprofileLineDeleteCommand.class.getName(), new DefaultCommandFactory());
	}
}
