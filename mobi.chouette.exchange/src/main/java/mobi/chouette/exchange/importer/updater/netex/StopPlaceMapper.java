package mobi.chouette.exchange.importer.updater.netex;

import mobi.chouette.model.StopArea;
import no.rutebanken.netex.model.LocationStructure;
import no.rutebanken.netex.model.MultilingualString;
import no.rutebanken.netex.model.SimplePoint_VersionStructure;
import no.rutebanken.netex.model.StopPlace;

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

        stopPlace.setCentroid(
                new SimplePoint_VersionStructure()
                        .withLocation(
                                new LocationStructure()
                                        .withLatitude(stopArea.getLatitude())
                                        .withLongitude(stopArea.getLongitude())));

        stopPlace.setName(
                new MultilingualString()
                        .withValue(stopArea.getName())
                        .withLang("")
                        .withTextIdType(""));

        return stopPlace;
    }
}
