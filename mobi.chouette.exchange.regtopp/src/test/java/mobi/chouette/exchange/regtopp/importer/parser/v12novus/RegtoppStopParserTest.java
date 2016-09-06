package mobi.chouette.exchange.regtopp.importer.parser.v12novus;

import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;

public class RegtoppStopParserTest {

    private RegtoppStopParser regtoppStopParser = new RegtoppStopParser();
    private Referential referential = new Referential();


    @Test
    public void createParentStopArea() {
        StopArea boardingPosition = new StopArea();
        boardingPosition.setAreaType(ChouetteAreaEnum.BoardingPosition);
        boardingPosition.setObjectId("TST:StopArea:050106050101");
        Map<String, List<StopArea>> boardingPositionsByStopArea = new HashMap<>();
        boardingPositionsByStopArea.put("05010605", Arrays.asList(boardingPosition));

        regtoppStopParser.createParentStopArea(boardingPositionsByStopArea, createConfig("TST"), referential);

        StopArea parent = ObjectFactory.getStopArea(referential, "TST:StopArea:05010605");

        assertEquals(parent.getAreaType(), ChouetteAreaEnum.CommercialStopPoint);
        assertEquals(parent, boardingPosition.getParent());
    }

    @Test
    public void groupBoardingPositions() throws Exception {

        Map<String, StopArea> stopAreas = new HashMap<>();
        StopArea stopArea = new StopArea();
        String stopAreaId = "TST:StopArea:050106050101";
        stopArea.setObjectId(stopAreaId);
        stopAreas.put(stopAreaId, stopArea);
        referential.setStopAreas(stopAreas);
        Map<String, List<StopArea>> boardingPositionsByStopArea = regtoppStopParser.groupBoardingPositions(referential);

        StopArea actual = boardingPositionsByStopArea.get("05010605").get(0);
        assertEquals(actual, stopArea);
    }

    private RegtoppImportParameters createConfig(String prefix) {
        RegtoppImportParameters configuration = new RegtoppImportParameters();
        configuration.setObjectIdPrefix(prefix);
        return configuration;
    }
}