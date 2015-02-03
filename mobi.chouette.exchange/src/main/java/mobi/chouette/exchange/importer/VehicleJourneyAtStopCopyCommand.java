package mobi.chouette.exchange.importer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.VehicleJourneyDAO;
import mobi.chouette.model.util.Referential;

@Stateless(name = VehicleJourneyAtStopCopyCommand.COMMAND)
@Log4j
public class VehicleJourneyAtStopCopyCommand implements Command {
	public static final String COMMAND = "VehicleJourneyAtStopCopyCommand";

	@EJB
	VehicleJourneyDAO vehicleJourneyDAO;

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		try {

			Referential referential = (Referential) context.get(REFERENTIAL);
			ByteArrayOutputStream out = (ByteArrayOutputStream) context
					.get(Constant.BUFFER);
			if (out != null) {
				byte[] data = out.toByteArray();
				vehicleJourneyDAO.update(referential.getVehicleJourneys()
						.values(), data);

			}
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
				String name = "java:app/mobi.chouette.exchange/"
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
		CommandFactory.factories.put(VehicleJourneyAtStopCopyCommand.class.getName(),
				factory);
	}
}
