package mobi.chouette.exchange.importer;

import java.io.ByteArrayOutputStream;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
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
}
