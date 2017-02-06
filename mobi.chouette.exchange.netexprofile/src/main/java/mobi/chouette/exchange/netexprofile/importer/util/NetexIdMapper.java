package mobi.chouette.exchange.netexprofile.importer.util;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class NetexIdMapper {

    @Getter
    @Setter
    private static Map<String, String> routeStopMapper = new HashMap<>();

    @Getter
    @Setter
    private static Map<String, String> stopPointMapper = new HashMap<>();

    public static void addRouteStopIdMapping(String routePointId, String stopPointId) {
        if (!routeStopMapper.containsKey(routePointId)) {
            routeStopMapper.put(routePointId, stopPointId);
        }
    }

    public static void addStopPointIdMapping(String scheduledStopPointId, String stopPointId) {
        if (!stopPointMapper.containsKey(scheduledStopPointId)) {
            stopPointMapper.put(scheduledStopPointId, stopPointId);
        }
    }

}
