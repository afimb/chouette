package mobi.chouette.exchange.importer.updater;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.StopAreaTypeEnum;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.util.Referential;

import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.StopTypeEnumeration;

import static mobi.chouette.exchange.importer.updater.NeTExStopPlaceUtil.mapTransportMode;
import static mobi.chouette.exchange.importer.updater.NeTExStopPlaceUtil.findTransportModeForStopArea;
@Log4j
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@Singleton(name = StopAreaIdMapper.BEAN_NAME)
public class StopAreaIdMapper {

    public static final String BEAN_NAME = "StopAreaIdMapper";

    private Map<String, Map<StopTypeEnumeration, String>> stopPlaceIdMap = new HashMap<>();

    private Map<String, Map<StopTypeEnumeration, String>> quayIdMap = new HashMap<>();

    private String quayEndpoint;

    private String stopPlaceEndpoint;

    private long lastUpdated = 0;

    public long timeToLiveMs;

    @PostConstruct
    public void init() {
        String quayEndpointPropertyKey = "iev.stop.place.register.mapping.quay";
        quayEndpoint = System.getProperty(quayEndpointPropertyKey);
        if (quayEndpoint == null) {
            log.warn("Could not find property named " + quayEndpointPropertyKey + " in iev.properties");
            quayEndpoint = "https://api-test.rutebanken.org/tiamat/1.0/quay/id_mapping?recordsPerRoundTrip=220000&includeStopType=true";
        }

        String stopPlaceEndpointPropertyKey = "iev.stop.place.register.mapping.stopplace";
        stopPlaceEndpoint = System.getProperty(stopPlaceEndpointPropertyKey);
        if (stopPlaceEndpoint == null) {
            log.warn("Could not find property named " + stopPlaceEndpointPropertyKey + " in iev.properties");
            stopPlaceEndpoint = "https://api-test.rutebanken.org/tiamat/1.0/stop_place/id_mapping?recordsPerRoundTrip=220000&includeStopType=true";
        }

        String stopPlaceTtlPropertyKey = "iev.stop.place.register.mapping.ttl.ms";
        try {
            timeToLiveMs = Long.valueOf(System.getProperty(stopPlaceTtlPropertyKey));
        } catch (NumberFormatException nfe) {
            log.warn("Illegal value for property named " + stopPlaceTtlPropertyKey + " in iev.properties. Should be no of milliseconds ttl (long)");
        }
        if (timeToLiveMs < 1) {
            log.warn("Could not find valid property named " + stopPlaceTtlPropertyKey + " in iev.properties");
            timeToLiveMs = 1000 * 60 * 10; // 10 minutes
        }
    }


    public void mapStopAreaIds(Referential referential) {
        if (lastUpdated < System.currentTimeMillis() - timeToLiveMs) {
            refreshCache();
        }

        referential.getStopAreas().values().stream().forEach(stopArea -> mapIdsForStopArea(stopArea));
    }

    private void refreshCache() {
        int remainingUpdateRetries = 10;

        boolean result = false;

        while (!result && remainingUpdateRetries-- > 0) {
            // Fetch data and populate caches
            log.info("Cache is old, refreshing quay and stopplace cache");
            boolean stopPlaceOk = populateCache(stopPlaceIdMap, stopPlaceEndpoint);
            boolean quayOK = populateCache(quayIdMap, quayEndpoint);

            if (quayOK && stopPlaceOk) {
                lastUpdated = System.currentTimeMillis();
                result = true;
            } else {
                log.error("Error updating caches, retries left = " + remainingUpdateRetries);
                result = false;

                // TODO dodgy
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) {
                    // Swallow
                }
            }

        }

        if (result == false) {
            throw new RuntimeException("Could not update quay cache - cannot validate");
        }
    }

    private void mapIdsForStopArea(StopArea stopArea) {
        if (stopArea == null) {
            return;
        }

        String oldId = stopArea.getObjectId();
        String newId = null;
        if (oldId != null) {

            if (ChouetteAreaEnum.CommercialStopPoint.equals(stopArea.getAreaType())) {
                newId = mapId(stopPlaceIdMap, oldId, stopArea);
            } else if (ChouetteAreaEnum.BoardingPosition.equals(stopArea.getAreaType())) {
                newId = mapId(quayIdMap, oldId, stopArea);
            }

        }

        if (newId != null) {
            stopArea.setObjectId(newId);
            log.debug("Mapped id for " + stopArea.getAreaType() + " from: " + oldId + " to: " + newId);
        }

        stopArea.getContainedStopAreas().forEach(child -> mapIdsForStopArea(child));
    }

    private String mapId(Map<String, Map<StopTypeEnumeration, String>> idMap, String orgId, StopArea stopArea) {

        Map<StopTypeEnumeration, String> idsForOrgId = idMap.get(orgId);
        if (idsForOrgId != null && idsForOrgId.size() > 0) {
            // Return id if only one
            if (idsForOrgId.size() == 1) {
                return idsForOrgId.values().iterator().next();
            }


            // Attempt to find stop type with best match for stop area transport mode(s)
            Set<TransportModeNameEnum> transportModeNames = findTransportModeForStopArea(new HashSet<>(), stopArea);
            for (TransportModeNameEnum transportModeName : transportModeNames) {
                StopTypeEnumeration stopType = mapTransportMode(transportModeName);
                if (stopType != null) {

                    String transportModeMatch = idsForOrgId.get(stopType);
                    if (transportModeMatch != null) {
                        return transportModeMatch;
                    }
                } else {
                    log.warn("Unable to map transportModeName: " + transportModeName + "for StopArea with id: " + orgId);
                }

            }

            // Return id without type set (if exists)
            String defaultMatch = idsForOrgId.get(null);
            if (defaultMatch == null) {
                return defaultMatch;
            }

            //  Return random id
            return idsForOrgId.values().iterator().next();

        }
        return null;

    }




    private boolean populateCache(Map<String, Map<StopTypeEnumeration, String>> cache, String u) {
        cache.clear();
        HttpURLConnection connection = null;

        try {
            URL url = new URL(u);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.connect();

            // Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = rd.readLine()) != null) {
                String[] split = StringUtils.split(line, ",");
                String localId = null;
                StopTypeEnumeration stopPlaceType = null;
                String nsrId = null;
                if (split.length == 2) {
                    localId = split[0];
                    nsrId = split[1];
                } else if (split.length == 3) {
                    localId = split[0];
                    stopPlaceType = parseStopPlaceType(split[1]);
                    nsrId = split[2];
                } else {
                    log.error("NSR contains illegal mappings: " + u + " " + line);
                }

                if (localId != null) {
                    cache.putIfAbsent(localId, new HashMap<>());
                    String prevVal = cache.get(localId).put(stopPlaceType, nsrId);
                    if (prevVal != null && !prevVal.equals(nsrId)) {
                        log.debug("NSR contained Multiple mappings for localId:" + localId + " and stopPlaceType: " + stopPlaceType + ", discarding: " + prevVal + " in favor of: " + nsrId);
                    }
                }
            }
            rd.close();
            return true;
        } catch (Exception e) {
            log.error("Error getting NSR cache for url " + u, e);
        } finally {
            connection.disconnect();
        }

        return false;
    }

    private StopTypeEnumeration parseStopPlaceType(String value) {
        if (value == null) {
            return null;
        }
        return StopTypeEnumeration.fromValue(value);
    }

    StopAreaTypeEnum mapStopAreaType(StopTypeEnumeration netexType) {
        if (netexType == null) {
            return null;
        }

        try {
            return StopAreaTypeEnum.valueOf(org.apache.commons.lang3.StringUtils.capitalize(netexType.value()));
        } catch (IllegalArgumentException iae) {
            log.warn("Unable to map unknown StopTypeEnumeration value: " + netexType);
            return null;
        }

    }


}
