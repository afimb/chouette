/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.validator;

import java.io.IOException;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.exchange.validation.LineValidatorCommand;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.ValidationDataCollector;
import mobi.chouette.model.Line;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 *
 */
@Log4j 
@Stateless(name = DaoLineValidatorCommand.COMMAND)
public class DaoLineValidatorCommand implements Command, Constant {
	public static final String COMMAND = "DaoLineValidatorCommand";

	@Resource
	private SessionContext daoContext;
	
	@EJB 
	private LineDAO lineDAO;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);

		try {
			
			Command lineValidatorCommand = CommandFactory.create(initialContext,
					LineValidatorCommand.class.getName());

			Long lineId = (Long) context.get(LINE_ID);
			Line line = lineDAO.find(lineId);
			
			ValidationDataCollector collector = new ValidationDataCollector();
			collector.collect(data, line);

			result = lineValidatorCommand.execute(context);
			// daoContext.setRollbackOnly();
			
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
				String name = "java:app/mobi.chouette.exchange.validator/" + COMMAND;
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
		CommandFactory.factories.put(DaoLineValidatorCommand.class.getName(), new DefaultCommandFactory());
	}

}
