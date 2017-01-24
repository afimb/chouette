package mobi.chouette.exchange.importer.updater.netex;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.TransportModeNameEnum;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.*;

/**
 * Map from chouette model to NeTEx
 */
@Log4j
public class StopPlaceMapper {

    private static final String VERSION = "1";

    /**
     * Map stop area with contained stop areas.
     *
     * @param stopArea Typically stop areas of {@link ChouetteAreaEnum#StopPlace} or
     *                 {@link ChouetteAreaEnum#CommercialStopPoint}
     * @return NeTEx stop place
     */
    public StopPlace mapStopAreaToStopPlace(StopArea stopArea) {
        StopPlace stopPlace = mapStopPlace(stopArea);
        if (stopArea.getContainedStopAreas().size() > 0) {
            stopPlace.setQuays(new Quays_RelStructure());
            for (StopArea children : stopArea.getContainedStopAreas()) {
                Quay quay = mapQuay(children);
                stopPlace.getQuays().getQuayRefOrQuay().add(quay);
            }
        }

        return stopPlace;
    }

    protected Quay mapQuay(StopArea stopArea) {
        Quay quay = new Quay();
        mapId(stopArea, quay);
        setVersion(quay);
        mapCentroid(stopArea, quay);
        mapQuayName(stopArea, quay);
        mapCompassBearing(stopArea, quay);
        if (StringUtils.isNotBlank(stopArea.getComment())) {
            quay.setDescription(new MultilingualString().withValue(stopArea.getComment()));
        }
        return quay;
    }

    private StopPlace mapStopPlace(StopArea stopArea) {
        StopPlace stopPlace = new StopPlace();
        mapId(stopArea, stopPlace);
        setVersion(stopPlace);
        mapCentroid(stopArea, stopPlace);
        mapName(stopArea, stopPlace);
        return stopPlace;
    }

    private void mapCompassBearing(StopArea stopArea, Quay quay) {
        if (stopArea.getCompassBearing() != null) {
            quay.setCompassBearing(new Float(stopArea.getCompassBearing()));
        }
    }

    public void setVersion(EntityInVersionStructure entity) {
        entity.setVersion(VERSION);
    }

    private void mapId(StopArea stopArea, Zone_VersionStructure zone) {
        zone.setId(stopArea.getObjectId());
    }

    private void mapCentroid(StopArea stopArea, Zone_VersionStructure zone) {
        setVersion(zone);
        if (stopArea.getLatitude() != null && stopArea.getLongitude() != null) {
            zone.setCentroid(new SimplePoint_VersionStructure().withLocation(
                    new LocationStructure().withLatitude(stopArea.getLatitude()).withLongitude(stopArea.getLongitude())));
        }
    }

    private void mapName(StopArea stopArea, Zone_VersionStructure zone) {
        zone.setName(new MultilingualString().withValue(stopArea.getName()).withLang("no").withTextIdType(""));

    }

    private void mapQuayName(StopArea stopArea, Zone_VersionStructure zone) {

        String quayName = stopArea.getRegistrationNumber();
        if (quayName == null) {
            quayName = stopArea.getName();
        }

        zone.setName(new MultilingualString().withValue(quayName).withLang("no").withTextIdType(""));
    }

    public void mapTransportMode(StopPlace sp, TransportModeNameEnum mode) {
        switch (mode) {
            case Air:
                sp.setStopPlaceType(StopTypeEnumeration.AIRPORT);
                break;
            case Train:
            case LongDistanceTrain_2:
            case LongDistanceTrain:
            case LocalTrain:
            case RapidTransit:
                sp.setStopPlaceType(StopTypeEnumeration.RAIL_STATION);
                break;
            case Metro:
                sp.setStopPlaceType(StopTypeEnumeration.METRO_STATION);
                break;
            case Tramway:
                sp.setStopPlaceType(StopTypeEnumeration.TRAM_STATION);
                break;
            case Shuttle:
            case Coach:
            case Bus:
            case Trolleybus:
                sp.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
                break;
            case Ferry:
                sp.setStopPlaceType(StopTypeEnumeration.HARBOUR_PORT);
                break;
            case Waterborne:
                sp.setStopPlaceType(StopTypeEnumeration.FERRY_STOP);
                break;
            default:
                log.warn("Could not map stop place type for stop place " + sp.getId() + " Chouette type was: " + mode);
        }
        log.debug("Mapped stop place type from " + mode + " to " + sp.getStopPlaceType());
    }
}
