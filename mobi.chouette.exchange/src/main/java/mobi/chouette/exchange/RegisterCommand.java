package mobi.chouette.exchange;

import java.io.IOException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.exchange.importer.updater.LineUpdater;
import mobi.chouette.exchange.importer.updater.Updater;
import mobi.chouette.exchange.importer.updater.UpdaterFactory;
import mobi.chouette.model.Line;
import mobi.chouette.model.util.Referential;

@Stateless(name = RegisterCommand.COMMAND)
@Log4j
public class RegisterCommand implements Command {

	public static final String COMMAND = "RegisterCommand";

	@EJB
	private LineDAO lineDAO;

	@Override
	public boolean execute(Context context) throws Exception {

		boolean result = ERROR;

		try {
			Referential referential = (Referential) context.get(REFERENTIAL);
			String objectId = "TODO";
			Line newValue = referential.getLines().get(objectId);
			Line oldValue = lineDAO.findByObjectId(newValue
					.getPtNetwork().getObjectId());
			if (oldValue == null) {
				oldValue = new Line();
				oldValue.setObjectId(newValue.getObjectId());
				lineDAO.create(oldValue);
			}
			Updater<Line> lineUpdater = UpdaterFactory.create(LineUpdater.class
					.getName());
			lineUpdater.update(null, oldValue, newValue);
			
			result = SUCCESS;			
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
		
		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange.neptune/"
						+ COMMAND;
				result = (Command) context.lookup(name);
			} catch (NamingException e) {
				log.error(e);
			}
			return result;
		}
	}

	static {
		CommandFactory factory = new DefaultCommandFactory();
		CommandFactory.factories.put(RegisterCommand.class.getName(),
				factory);
	}
}
