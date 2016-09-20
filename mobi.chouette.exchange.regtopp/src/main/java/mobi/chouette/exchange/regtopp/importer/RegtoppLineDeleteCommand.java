package mobi.chouette.exchange.regtopp.importer;

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
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.LineError;
import mobi.chouette.exchange.report.LineInfo;
import mobi.chouette.model.Line;
import mobi.chouette.model.util.Referential;

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

			Line existingLine = lineDAO.findByObjectId(newLine.getObjectId());
			if (existingLine != null) {
				log.info("Delete existing line before import: " + existingLine.getObjectId() + " "+existingLine.getName());
				lineDAO.delete(existingLine);
				lineDAO.flush();
			}

			result = SUCCESS;
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ActionReport report = (ActionReport) context.get(REPORT);
			LineInfo info = report.findLineInfo(newLine.getObjectId());
			if (info == null) {
				info = new LineInfo(newLine);
				report.getLines().add(info);
			}
			if (ex.getCause() != null) {
				Throwable e = ex.getCause();
				while (e.getCause() != null) {
					log.error(e.getMessage());
					e = e.getCause();
				}
				if (e instanceof SQLException) {
					Throwable ee = ((SQLException) e).getNextException();
					LineError error = new LineError(LineError.CODE.WRITE_ERROR,
							ee == null ? e.getMessage() : ee.getMessage());
					info.addError(error);
				} else {
					LineError error = new LineError(LineError.CODE.INTERNAL_ERROR, e.getMessage());
					info.addError(error);
				}
			} else {
				LineError error = new LineError(LineError.CODE.INTERNAL_ERROR, ex.getMessage());
				info.addError(error);
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
