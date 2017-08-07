package mobi.chouette.exchange.importer.updater;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Singleton;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.StopAreaTypeEnum;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.util.Referential;

import org.rutebanken.netex.model.StopTypeEnumeration;

import static mobi.chouette.exchange.importer.updater.NeTExStopPlaceUtil.findTransportModeForStopArea;
import static mobi.chouette.exchange.importer.updater.NeTExStopPlaceUtil.mapTransportMode;

@Log4j
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@Singleton(name = StopAreaIdMapper.BEAN_NAME)
public class StopAreaIdMapper {

    public static final String BEAN_NAME = "StopAreaIdMapper";


    @EJB(beanName = StopAreaIdCache.BEAN_NAME)
    private StopAreaIdCache stopAreaIdCache;


    public void mapStopAreaIds(Referential referential) {
        referential.setStopAreas(referential.getStopAreas().entrySet().stream().map(entry -> mapIdsForStopArea(entry.getValue())).distinct().collect(Collectors.toMap(StopArea::getObjectId, Function.identity())));
    }


    private StopArea mapIdsForStopArea(StopArea stopArea) {
        String orgId = stopArea.getObjectId();
        String newId = null;
        if (orgId != null) {

            if (ChouetteAreaEnum.CommercialStopPoint.equals(stopArea.getAreaType())) {
                newId = mapId(stopAreaIdCache.getStopPlaceMapping(orgId), orgId, stopArea);
            } else if (ChouetteAreaEnum.BoardingPosition.equals(stopArea.getAreaType())) {
                newId = mapId(stopAreaIdCache.getQuayMapping(orgId), orgId, stopArea);
            }

        }


        if (newId != null) {
            stopArea.setObjectId(newId);
            log.debug("Mapped id for " + stopArea.getAreaType() + " from: " + orgId + " to: " + newId);
        }

        stopArea.getContainedStopAreas().forEach(child -> mapIdsForStopArea(child));
        return stopArea;
    }

    private String mapId(Map<StopTypeEnumeration, String> idsForOrgId, String orgId, StopArea stopArea) {

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


    public void setStopAreaIdCache(StopAreaIdCache stopAreaIdCache) {
        this.stopAreaIdCache = stopAreaIdCache;
    }
}
