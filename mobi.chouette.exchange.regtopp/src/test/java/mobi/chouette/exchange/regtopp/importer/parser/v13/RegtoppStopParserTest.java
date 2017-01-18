package mobi.chouette.exchange.regtopp.importer.parser.v13;

import mobi.chouette.exchange.regtopp.model.v13.RegtoppStopPointSTP;
import mobi.chouette.model.StopArea;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class RegtoppStopParserTest {

    @Test
    public void setBoardingPositionNameFromParentStop() throws Exception {
        RegtoppStopPointSTP regtoppStopPointSTP =  new RegtoppStopPointSTP();
        regtoppStopPointSTP.setStopPointName("stopPointName");

        String parentStopAreaName = "Parent stop name";
        StopArea parentStopArea = new StopArea();
        parentStopArea.setName(parentStopAreaName);

        StopArea boardingPosition = new StopArea();

        new RegtoppStopParser().setNameAndDescription(regtoppStopPointSTP, boardingPosition, parentStopArea);

        assertEquals(boardingPosition.getName(), parentStopAreaName);
    }

}