package mobi.chouette.exchange.neptune.importer;

import java.io.IOException;
import java.util.Collection;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.type.AlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingAlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingPossibilityEnum;
import mobi.chouette.model.type.PTDirectionEnum;
import mobi.chouette.model.util.Referential;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class NeptuneSetDefaultValuesCommand implements Command, Constant {

	public static final String COMMAND = "NeptuneSetDefaultValuesCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		// report service

		try {

			Referential referential = (Referential) context.get(REFERENTIAL);

			processDefaulValues(referential);

			result = SUCCESS;
		} catch (Exception e) {

			// report service
			log.error("parsing failed ", e);
			throw e;
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}

		return result;
	}

	private void processDefaulValues(Referential referential) {

		// order should respect Neptune Xml file
		processLines(referential.getLines().values());
		processRoutes(referential.getRoutes().values());

	}

	private void processLines(Collection<Line> values) {

		for (Line line : values) {
			// default name = number ou published name
			if (line.getName() == null) {
				line.setName(line.getNumber() != null ? line.getNumber() : line.getPublishedName());
			}
		}
	}

	private void processRoutes(Collection<Route> values) {

		for (Route route : values) {
			// default direction and wayback = R if opposite Route = A, else A

			if (route.getDirection() == null) {
				PTDirectionEnum oppositeDirection = route.getOppositeRoute() != null ? route.getOppositeRoute()
						.getDirection() : PTDirectionEnum.R;
				route.setDirection(getOppositeDirection(oppositeDirection));
			}
			if (route.getWayBack() == null) {
				route.setWayBack(route.getOppositeRoute() != null && route.getWayBack().equals("A") ? "R" : "A");
			}

			processBoardingAlightingForRoute(route);

		}
	}

	private void processBoardingAlightingForRoute(Route route) {
		boolean invalidData = false; 
		boolean usefullData = false;

		b1: for (JourneyPattern jp : route.getJourneyPatterns()) {
			for (VehicleJourney vj : jp.getVehicleJourneys()) {
				for (VehicleJourneyAtStop vjas : vj.getVehicleJourneyAtStops()) {
					if (!updateStopPoint(vjas)) {
						invalidData = true;
						break b1;
					}
				}
			}
		}
		if (!invalidData) {
			// check if every stoppoints were updated, complete missing ones to
			// normal; if all normal clean all
			for (StopPoint sp : route.getStopPoints()) {
				if (sp.getForAlighting() == null)
					sp.setForAlighting(AlightingPossibilityEnum.normal);
				if (sp.getForBoarding() == null)
					sp.setForBoarding(BoardingPossibilityEnum.normal);
			}
			for (StopPoint sp : route.getStopPoints()) {
				if (!sp.getForAlighting().equals(AlightingPossibilityEnum.normal)) {
					usefullData = true;
					break;
				}
				if (!sp.getForBoarding().equals(BoardingPossibilityEnum.normal)) {
					usefullData = true;
					break;
				}
			}

		}
		if (invalidData || !usefullData) {
			// remove useless informations
			for (StopPoint sp : route.getStopPoints()) {
				sp.setForAlighting(null);
				sp.setForBoarding(null);
			}
		}

	}

	private boolean updateStopPoint(VehicleJourneyAtStop vjas) {
		StopPoint sp = vjas.getStopPoint();
		BoardingPossibilityEnum forBoarding = getForBoarding(vjas.getBoardingAlightingPossibility());
		AlightingPossibilityEnum forAlighting = getForAlighting(vjas.getBoardingAlightingPossibility());
		if (sp.getForBoarding() != null && !sp.getForBoarding().equals(forBoarding))
			return false;
		if (sp.getForAlighting() != null && !sp.getForAlighting().equals(forAlighting))
			return false;
		sp.setForBoarding(forBoarding);
		sp.setForAlighting(forAlighting);
		return true;
	}

	private AlightingPossibilityEnum getForAlighting(BoardingAlightingPossibilityEnum boardingAlightingPossibility) {
		if (boardingAlightingPossibility == null)
			return AlightingPossibilityEnum.normal;
		switch (boardingAlightingPossibility) {
		case BoardAndAlight:
			return AlightingPossibilityEnum.normal;
		case AlightOnly:
			return AlightingPossibilityEnum.normal;
		case BoardOnly:
			return AlightingPossibilityEnum.forbidden;
		case NeitherBoardOrAlight:
			return AlightingPossibilityEnum.forbidden;
		case BoardAndAlightOnRequest:
			return AlightingPossibilityEnum.request_stop;
		case AlightOnRequest:
			return AlightingPossibilityEnum.request_stop;
		case BoardOnRequest:
			return AlightingPossibilityEnum.normal;
		}
		return null;
	}

	private BoardingPossibilityEnum getForBoarding(BoardingAlightingPossibilityEnum boardingAlightingPossibility) {
		if (boardingAlightingPossibility == null)
			return BoardingPossibilityEnum.normal;
		switch (boardingAlightingPossibility) {
		case BoardAndAlight:
			return BoardingPossibilityEnum.normal;
		case AlightOnly:
			return BoardingPossibilityEnum.forbidden;
		case BoardOnly:
			return BoardingPossibilityEnum.normal;
		case NeitherBoardOrAlight:
			return BoardingPossibilityEnum.forbidden;
		case BoardAndAlightOnRequest:
			return BoardingPossibilityEnum.request_stop;
		case AlightOnRequest:
			return BoardingPossibilityEnum.normal;
		case BoardOnRequest:
			return BoardingPossibilityEnum.request_stop;
		}
		return null;
	}

	private PTDirectionEnum getOppositeDirection(PTDirectionEnum direction) {
		if (direction == null)
			return PTDirectionEnum.A;
		switch (direction) {
		case A:
			return PTDirectionEnum.R;
		case R:
			return PTDirectionEnum.A;
		case ClockWise:
			return PTDirectionEnum.CounterClockWise;
		case CounterClockWise:
			return PTDirectionEnum.ClockWise;
		case North:
			return PTDirectionEnum.South;
		case South:
			return PTDirectionEnum.North;
		case NorthWest:
			return PTDirectionEnum.SouthEast;
		case SouthWest:
			return PTDirectionEnum.NorthEast;
		case NorthEast:
			return PTDirectionEnum.SouthWest;
		case SouthEast:
			return PTDirectionEnum.NorthWest;
		case East:
			return PTDirectionEnum.West;
		case West:
			return PTDirectionEnum.East;
		}
		return PTDirectionEnum.A;

	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new NeptuneSetDefaultValuesCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(NeptuneSetDefaultValuesCommand.class.getName(), new DefaultCommandFactory());
	}
}
