package mobi.chouette.exchange.regtopp.importer;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.model.Line;
import mobi.chouette.model.util.NamingUtil;
import mobi.chouette.model.util.Referential;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;

import static mobi.chouette.exchange.report.ActionReporter.*;

@Log4j
@Stateless(name = RegtoppLineDeleteCommand.COMMAND)
public class RegtoppLineDeleteCommand implements Command {

	public static final String COMMAND = "RegtoppLineDeleteCommand";

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

			
			lineDAO.deleteByObjectId(Arrays.asList(newLine.getObjectId()));
			log.info("Delete existing line before import: " + newLine.getObjectId() + " "+newLine.getName());
			
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
		CommandFactory.factories.put(RegtoppLineDeleteCommand.class.getName(), new DefaultCommandFactory());
	}
}
