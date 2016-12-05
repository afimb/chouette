package mobi.chouette.exchange.importer.updater.netex;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Arrays;

import mobi.chouette.model.util.Referential;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.Quays_RelStructure;
import org.rutebanken.netex.model.StopPlace;
import org.testng.annotations.Test;

import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;

public class StopPlaceMapperTest {

    private StopPlaceMapper stopPlaceMapper = new StopPlaceMapper();

    @Test
    public void stopPlaceWithThreeBoardingPositions() {
        StopArea stopPlace = createStopPlace("Moensletta");

        StopArea firstBoardingPosition = createBoardingPosition(stopPlace.getName());
        StopArea secondBoardingPosition = createBoardingPosition(stopPlace.getName());
        StopArea thirdBoardingPosition = createBoardingPosition(stopPlace.getName());
        stopPlace.setContainedStopAreas(Arrays.asList(firstBoardingPosition, secondBoardingPosition, thirdBoardingPosition));

        StopPlace netexStopPlace = stopPlaceMapper.mapStopAreaToStopPlace(stopPlace);

        assertNotNull(netexStopPlace);
        assertEquals(netexStopPlace.getName().getValue(), stopPlace.getName());

        assertNotNull(netexStopPlace.getQuays(), "Quays shall not be null.");
        assertEquals(netexStopPlace.getQuays().getQuayRefOrQuay().size(), 3);
        Quay firstQuay = (Quay) netexStopPlace.getQuays().getQuayRefOrQuay().get(0);
        assertEquals(firstQuay.getName().getValue(), stopPlace.getName());
    }
    
    @Test
    public void stopPlaceWithoutBoardingPositions() {
        StopArea stopPlace = createStopPlace("Klavestadhaugen");

        StopPlace netexStopPlace = stopPlaceMapper.mapStopAreaToStopPlace(stopPlace);

        assertNotNull(netexStopPlace);
        assertEquals(netexStopPlace.getName().getValue(), stopPlace.getName());
    }

    @Test
    public void stopPlaceWithId() {
        StopArea stopPlace = createStopPlace("Hestehaugveien");
        stopPlace.setObjectId("id");
        StopPlace netexStopPlace = stopPlaceMapper.mapStopAreaToStopPlace(stopPlace);

        assertEquals(netexStopPlace.getId(), String.valueOf(stopPlace.getObjectId()));
    }

    @Test
    public void boardingPositionWithoutStopPlace() {
        StopArea boardingPosition = createBoardingPosition("Borgen");
        StopPlace netexStopPlace = stopPlaceMapper.mapStopAreaToStopPlace(boardingPosition);

        assertNotNull(netexStopPlace);
        assertEquals(netexStopPlace.getName().getValue(), boardingPosition.getName());
    }

    @Test
    public void setBoardingPositionNameIfMissing() {
        StopPlace stopPlace = new StopPlace()
                .withName(new MultilingualString().withValue("Festningen"))
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()));

        StopArea stopArea = stopPlaceMapper.mapStopPlaceToStopArea(new Referential(), stopPlace);

        assertEquals(stopArea.getContainedStopAreas().get(0).getName(), "Festningen");
    }

    @Test
    public void keepBoardingPositionNameIfDifferent() {
        StopPlace stopPlace = new StopPlace()
                .withName(new MultilingualString().withValue("Festningen"))
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay().withName(new MultilingualString().withValue("Slottet"))));

        StopArea stopArea = stopPlaceMapper.mapStopPlaceToStopArea(new Referential(), stopPlace);

        assertEquals(stopArea.getContainedStopAreas().get(0).getName(), "Slottet");
    }

    private StopArea createStopPlace(String name) {
        return createStopArea(name, ChouetteAreaEnum.StopPlace);
    }

    private StopArea createStopArea(String name, ChouetteAreaEnum chouetteAreaEnum) {
        StopArea stopPlace = new StopArea();
        stopPlace.setName(name);
        stopPlace.setAreaType(chouetteAreaEnum);
        return stopPlace;
    }


    private StopArea createBoardingPosition(String name) {
        StopArea boardingPosition = new StopArea();
        boardingPosition.setName(name);
        boardingPosition.setAreaType(ChouetteAreaEnum.BoardingPosition);
        return boardingPosition;
    }

}