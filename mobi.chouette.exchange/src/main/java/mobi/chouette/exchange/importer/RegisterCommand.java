package mobi.chouette.exchange.importer;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.DateTimeUtils;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.dao.VehicleJourneyDAO;
import mobi.chouette.exchange.importer.updater.LineUpdater;
import mobi.chouette.exchange.importer.updater.Updater;
import mobi.chouette.model.Line;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.util.Referential;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Stateless(name = RegisterCommand.COMMAND)
@Log4j
public class RegisterCommand implements Command {

	public static final String COMMAND = "RegisterCommand";

	@EJB
	private LineDAO lineDAO;

	@EJB(beanName = LineUpdater.BEAN_NAME)
	private Updater<Line> lineUpdater;

	@EJB
	private VehicleJourneyDAO vehicleJourneyDAO;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean execute(Context context) throws Exception {

		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			Boolean optimized = Boolean.FALSE;
			Referential cache = new Referential();
			context.put(CACHE, cache);

			Referential referential = (Referential) context.get(REFERENTIAL);
			Line newValue = referential.getLines().values().iterator().next();
			log.info("[DSU] register line : " + newValue.getObjectId());

			// Updater<Line> lineUpdater = UpdaterFactory.create(initialContext,
			// LineUpdater.class.getName());
			Line oldValue = lineDAO.findByObjectId(newValue.getObjectId());
			if (oldValue == null) {
				oldValue = new Line();
				oldValue.setObjectId(newValue.getObjectId());

			}
			context.put(OPTIMIZED, optimized);
			lineUpdater.update(context, oldValue, newValue);
			lineDAO.create(oldValue);

			if (optimized) {
				StringWriter buffer = new StringWriter(1024);
				List<VehicleJourney> list = new ArrayList<VehicleJourney>(
						referential.getVehicleJourneys().values());
				for (VehicleJourney item : list) {
					VehicleJourney vehicleJourney = cache.getVehicleJourneys()
							.get(item.getObjectId());

					List<VehicleJourneyAtStop> vehicleJourneyAtStops = item
							.getVehicleJourneyAtStops();
					for (VehicleJourneyAtStop vehicleJourneyAtStop : vehicleJourneyAtStops) {

						StopPoint stopPoint = cache.getStopPoints().get(
								vehicleJourneyAtStop.getStopPoint()
										.getObjectId());

						write(buffer, vehicleJourney, stopPoint,
								vehicleJourneyAtStop);
					}
				}
				vehicleJourneyDAO.update(list, buffer.toString());
			}

			result = SUCCESS;
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
		log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		return result;
	}

	private void write(StringWriter buffer, VehicleJourney vehicleJourney,
			StopPoint stopPoint, VehicleJourneyAtStop vehicleJourneyAtStop)
			throws IOException {
		buffer.write(vehicleJourney.getId().toString());
		buffer.append(SEP);
		buffer.write(stopPoint.getId().toString());
		buffer.append(SEP);
		if (vehicleJourneyAtStop.getConnectingServiceId() != null)
			buffer.write(vehicleJourneyAtStop.getConnectingServiceId());
		buffer.append(SEP);
		if (vehicleJourneyAtStop.getBoardingAlightingPossibility() != null)
			buffer.write(vehicleJourneyAtStop.getBoardingAlightingPossibility()
					.toString());
		buffer.append(SEP);
		if (vehicleJourneyAtStop.getArrivalTime() != null)
			buffer.write(DateTimeUtils.getTimeText(vehicleJourneyAtStop.getArrivalTime()));
		buffer.append(SEP);
		if (vehicleJourneyAtStop.getDepartureTime() != null)
			buffer.write(DateTimeUtils.getTimeText(vehicleJourneyAtStop.getDepartureTime()));
		buffer.append(SEP);
		if (vehicleJourneyAtStop.getWaitingTime() != null)
			buffer.write(DateTimeUtils.getTimeText(vehicleJourneyAtStop.getWaitingTime()));
		else
			buffer.write(DateTimeUtils.getTimeText(new Time(0)));

		buffer.append(SEP);
		if (vehicleJourneyAtStop.getElapseDuration() != null)
			buffer.write(DateTimeUtils.getTimeText(vehicleJourneyAtStop.getElapseDuration()));
		else
			buffer.write(DateTimeUtils.getTimeText(new Time(0)));
		buffer.append(SEP);
		if (vehicleJourneyAtStop.getHeadwayFrequency() != null)
			buffer.write(DateTimeUtils.getTimeText(vehicleJourneyAtStop.getHeadwayFrequency()));
		else
			buffer.write(DateTimeUtils.getTimeText(new Time(0)));
		buffer.append('\n');
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {

			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange/" + COMMAND;
				result = (Command) context.lookup(name);
			} catch (NamingException e) {
				log.error(e);
			}
			return result;
		}
	}

	static {
		CommandFactory factory = new DefaultCommandFactory();
		CommandFactory.factories.put(RegisterCommand.class.getName(), factory);
	}
}
