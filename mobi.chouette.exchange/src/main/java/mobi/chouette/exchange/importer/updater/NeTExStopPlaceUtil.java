package mobi.chouette.exchange.importer.updater;

import java.util.List;
import java.util.Set;

import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.TransportModeNameEnum;

import org.rutebanken.netex.model.StopTypeEnumeration;

public class NeTExStopPlaceUtil {

    public static StopTypeEnumeration mapTransportMode(TransportModeNameEnum mode) {
        StopTypeEnumeration stopType = null;
        switch (mode) {
            case Air:
                stopType = StopTypeEnumeration.AIRPORT;
                break;
            case Train:
            case LongDistanceTrain_2:
            case LongDistanceTrain:
            case LocalTrain:
            case RapidTransit:
                stopType = StopTypeEnumeration.RAIL_STATION;
                break;
            case Metro:
                stopType = StopTypeEnumeration.METRO_STATION;
                break;
            case Tramway:
                stopType = StopTypeEnumeration.ONSTREET_TRAM;
                break;
            case Shuttle:
            case Coach:
            case Bus:
            case Trolleybus:
                stopType = StopTypeEnumeration.ONSTREET_BUS;
                break;
            case Ferry:
            case Waterborne:
                stopType = StopTypeEnumeration.FERRY_STOP;
                break;
            default:

        }
        return stopType;
    }


    public static Set<TransportModeNameEnum> findTransportModeForStopArea(Set<TransportModeNameEnum> transportModes,
                                                                      StopArea sa) {
        TransportModeNameEnum transportModeName = null;
        List<StopPoint> stopPoints = sa.getContainedStopPoints();
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
