package mobi.chouette.exchange.importer.updater.netex;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import no.rutebanken.netex.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StopPlaceMapper {

    public List<StopPlace> mapStopAreasToStopPlaces(Map<String, StopArea> stopAreas) {

        List<StopPlace> stopPlaces = new ArrayList<>();
        for(StopArea stopArea: stopAreas.values()) {
            StopPlace stopPlace = mapStopAreaToStopPlace(stopArea);
            stopPlaces.add(stopPlace);
        }
        return stopPlaces;

    }

    public StopPlace mapStopAreaToStopPlace(StopArea stopArea) {

        StopPlace stopPlace = new StopPlace();

        mapCentroid(stopArea, stopPlace);
        mapName(stopArea, stopPlace);

        if(stopArea.getAreaType() != null && stopArea.getAreaType().equals(ChouetteAreaEnum.StopPlace)) {
            stopPlace.setQuays(new Quays_RelStructure());
            stopArea.getContainedStopAreas().forEach(boardingPosition ->  {
                        Quay quay = new Quay();
                        mapCentroid(boardingPosition, quay);
                        mapName(boardingPosition, quay);
                        stopPlace.getQuays().getQuayRefOrQuay().add(quay);
                    });
        }

        return stopPlace;
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
