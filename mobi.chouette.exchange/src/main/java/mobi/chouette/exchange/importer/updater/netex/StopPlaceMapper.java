package mobi.chouette.exchange.importer.updater.netex;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import no.rutebanken.netex.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StopPlaceMapper {

    public StopPlace mapStopAreaToStopPlace(StopArea stopArea) {

        if(stopArea.getAreaType().equals(ChouetteAreaEnum.BoardingPosition)) {
            if(stopArea.getParent() != null) {
                // Boarding position with parent

                StopPlace parentStopPlace = mapStopAreaToStopPlace(stopArea.getParent());
                return parentStopPlace;
            } else {
                // Boarding position without parent stop place.

                StopPlace stopPlace = createStopPlace(stopArea);
                Quay quay = createQuay(stopArea);
                stopPlace.setQuays(new Quays_RelStructure());
                stopPlace.getQuays().getQuayRefOrQuay().add(quay);
                return stopPlace;
            }
        }

        // StopPlace with contained stop areas

        StopPlace stopPlace = createStopPlace(stopArea);

        if(stopArea.getAreaType() != null && stopArea.getAreaType().equals(ChouetteAreaEnum.StopPlace)) {
            stopPlace.setQuays(new Quays_RelStructure());

            stopArea.getContainedStopAreas().forEach(boardingPosition ->  {
                        Quay quay = createQuay(boardingPosition);
                        stopPlace.getQuays().getQuayRefOrQuay().add(quay);
                    });
        }

        return stopPlace;
    }

    private StopPlace createStopPlace(StopArea stopArea) {
        StopPlace stopPlace = new StopPlace();
        mapId(stopArea, stopPlace);
        mapCentroid(stopArea, stopPlace);
        mapName(stopArea, stopPlace);
        return stopPlace;
    }

    private Quay createQuay(StopArea stopArea) {
        Quay quay = new Quay();
        mapId(stopArea, quay);
        mapCentroid(stopArea, quay);
        mapName(stopArea, quay);
        return quay;
    }

    private void mapId(StopArea stopArea, Zone_VersionStructure zone) {
        zone.setId(stopArea.getObjectId());
    }

    public void mapCentroid(StopArea stopArea, Zone_VersionStructure zone) {
        zone.setCentroid(
                new SimplePoint_VersionStructure()
                        .withLocation(
                                new LocationStructure()
                                        .withLatitude(stopArea.getLatitude())
                                        .withLongitude(stopArea.getLongitude())));
    }

    public void mapName(StopArea stopArea, Zone_VersionStructure zone) {

        zone.setName(
                new MultilingualString()
                        .withValue(stopArea.getName())
                        .withLang("")
                        .withTextIdType(""));

    }

}
