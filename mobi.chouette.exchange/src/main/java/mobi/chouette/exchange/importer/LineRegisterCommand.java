package mobi.chouette.exchange.importer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.DateTimeUtil;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.dao.VehicleJourneyDAO;
import mobi.chouette.exchange.importer.updater.LineOptimiser;
import mobi.chouette.exchange.importer.updater.LineUpdater;
import mobi.chouette.exchange.importer.updater.Updater;
import mobi.chouette.model.Line;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.util.Referential;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
@Stateless(name = LineRegisterCommand.COMMAND)
public class LineRegisterCommand implements Command {

	public static final String COMMAND = "LineRegisterCommand";

	@EJB
	private LineOptimiser optimiser;

	@EJB
	private LineDAO lineDAO;

	@EJB(beanName = LineUpdater.BEAN_NAME)
	private Updater<Line> lineUpdater;

	@Resource(lookup = "java:comp/DefaultManagedExecutorService")
	ManagedExecutorService executor;

	@EJB
	private VehicleJourneyDAO vehicleJourneyDAO;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean execute(Context context) throws Exception {

		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			if (!context.containsKey(OPTIMIZED))
			{
				context.put(OPTIMIZED, Boolean.TRUE);
			}
			Boolean optimized = (Boolean) context.get(OPTIMIZED);
			Referential cache = new Referential();
			context.put(CACHE, cache);

			Referential referential = (Referential) context.get(REFERENTIAL);
			Line newValue = referential.getLines().values().iterator().next();
			log.info("register line : " + newValue.getObjectId()+" "+newValue.getName()+ " vehicleJourney count = "+referential.getVehicleJourneys().size());

			optimiser.initialize(cache, referential);

			Line oldValue = cache.getLines().get(newValue.getObjectId());
			lineUpdater.update(context, oldValue, newValue);
			lineDAO.create(oldValue);

			if (optimized) {
				 StringWriter buffer = new StringWriter(1024);
				 final List<String> list = new ArrayList<String>(
						referential.getVehicleJourneys().keySet());
				for (VehicleJourney item : referential.getVehicleJourneys().values()) {
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
				vehicleJourneyDAO.deleteVehicleJourneyAtStops(list);
				context.put(BUFFER, buffer.toString());
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
		if (vehicleJourneyAtStop.getBoardingAlightingPossibility() != null)
			buffer.write(vehicleJourneyAtStop.getBoardingAlightingPossibility()
					.toString());
		else
			buffer.write(NULL);
		buffer.append(SEP);
		if (vehicleJourneyAtStop.getArrivalTime() != null)
			buffer.write(DateTimeUtil.getTimeText(vehicleJourneyAtStop
					.getArrivalTime()));
		else
			buffer.write(NULL);
		buffer.append(SEP);
		if (vehicleJourneyAtStop.getDepartureTime() != null)
			buffer.write(DateTimeUtil.getTimeText(vehicleJourneyAtStop
					.getDepartureTime()));
		else
			buffer.write(NULL);
		buffer.append(SEP);

		if (vehicleJourneyAtStop.getElapseDuration() != null)
			buffer.write(DateTimeUtil.getTimeText(vehicleJourneyAtStop
					.getElapseDuration()));
		else
			buffer.write(NULL);
		buffer.append(SEP);
		if (vehicleJourneyAtStop.getHeadwayFrequency() != null)
			buffer.write(DateTimeUtil.getTimeText(vehicleJourneyAtStop
					.getHeadwayFrequency()));
		else
			buffer.write(NULL);
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
		CommandFactory.factories.put(LineRegisterCommand.class.getName(),
				new DefaultCommandFactory());
	}
}
