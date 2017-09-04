package mobi.chouette.exchange.importer.updater;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import mobi.chouette.model.ScheduledStopPoint;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.TransportModeNameEnum;

import org.rutebanken.netex.model.StopTypeEnumeration;

public class NeTExStopPlaceUtil {

	public static StopTypeEnumeration mapTransportMode(TransportModeNameEnum mode) {
		switch (mode) {

		case Air:
			return StopTypeEnumeration.AIRPORT;
		case Bus:
		case TrolleyBus:
		case Coach:
			return StopTypeEnumeration.ONSTREET_BUS;
		case Ferry:
			return StopTypeEnumeration.FERRY_STOP;
		case Metro:
			return StopTypeEnumeration.METRO_STATION;
		case Rail:
			return StopTypeEnumeration.RAIL_STATION;
		case Tram:
			return StopTypeEnumeration.ONSTREET_TRAM;
		case Water:
			return StopTypeEnumeration.HARBOUR_PORT; // This and Ferry -
														// possible incorrect.
														// Is ferry a valid
														// transport mode?
		case Lift:
		case Cableway:
			return StopTypeEnumeration.LIFT_STATION;
		default:
			return null;
		}
	}

	public static Set<TransportModeNameEnum> findTransportModeForStopArea(Set<TransportModeNameEnum> transportModes,
			StopArea sa) {
		TransportModeNameEnum transportModeName = null;
		List<StopPoint> stopPoints = sa.getContainedScheduledStopPoints().stream().map(ScheduledStopPoint::getStopPoints).flatMap(List::stream).collect(Collectors.toList());
		for (StopPoint stop : stopPoints) {
			if (stop.getRoute() != null && stop.getRoute().getLine() != null) {
				transportModeName = stop.getRoute().getLine().getTransportModeName();
				if (transportModeName != null) {
					transportModes.add(transportModeName);
					break;
				}
			}
		}

		for (StopArea child : sa.getContainedStopAreas()) {
			transportModes = findTransportModeForStopArea(transportModes, child);
		}

		return transportModes;
	}

}
