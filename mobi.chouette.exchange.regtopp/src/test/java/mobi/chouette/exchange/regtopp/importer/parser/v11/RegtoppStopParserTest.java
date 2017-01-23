package mobi.chouette.exchange.regtopp.importer.parser.v11;

import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.importer.parser.ObjectIdCreator;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.enums.InterchangeWeight;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppStopHPL;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.util.Coordinate;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.testng.annotations.Test;

import javax.ejb.EJB;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;

public class RegtoppStopParserTest {

    private RegtoppStopParser regtoppStopParser = new RegtoppStopParser();
    private Referential referential = new Referential();

    @Test
    public void stopAreaShouldBeStopPlaceType() throws Exception {
        AbstractRegtoppStopHPL regtoppStop = createStop("12345618");
        RegtoppImportParameters configuration = createConfig("prefix");

        regtoppStopParser.mapRegtoppStop(regtoppStop, configuration, referential, Coordinate.UTM_32N);

        StopArea stopArea = getStopArea(configuration, regtoppStop.getStopId());
        assertEquals(stopArea.getAreaType(), ChouetteAreaEnum.CommercialStopPoint);
    }

    @Test
    public void stopAreaShouldContainBoardingPosition() throws Exception {
        AbstractRegtoppStopHPL regtoppStop = createStop("12345680");
        RegtoppImportParameters configuration = createConfig("prefix");

        regtoppStopParser.mapRegtoppStop(regtoppStop, configuration, referential, Coordinate.UTM_32N);

        StopArea stopArea = getStopArea(configuration, regtoppStop.getStopId());
        assertEquals(stopArea.getContainedStopAreas().size(), 1);
        assertEquals(stopArea.getContainedStopAreas().get(0).getAreaType(), ChouetteAreaEnum.BoardingPosition);
    }

    @Test
    public void stopAreaShouldHaveEightDigitsId() throws Exception {
        AbstractRegtoppStopHPL regtoppStop = createStop("12345679");
        RegtoppImportParameters configuration = createConfig("prefix");

        regtoppStopParser.mapRegtoppStop(regtoppStop, configuration, referential, Coordinate.UTM_32N);

        StopArea stopArea = getStopArea(configuration, regtoppStop.getStopId());
        assertEquals(stopArea.objectIdSuffix().length(), 8);
    }

    @Test
    public void boardingPositionShouldHaveTenDigitsId() throws Exception {
        AbstractRegtoppStopHPL regtoppStop = createStop("11020517");
        RegtoppImportParameters configuration = createConfig("prefix");

        regtoppStopParser.mapRegtoppStop(regtoppStop, configuration, referential, Coordinate.UTM_32N);

        StopArea stopArea = getStopArea(configuration, regtoppStop.getStopId());
        assertEquals(stopArea.getContainedStopAreas().get(0).objectIdSuffix().length(), 10, stopArea.getObjectId());
    }

    @Test
    public void parentStopAreaShouldBeSet() throws Exception {
        AbstractRegtoppStopHPL regtoppStop = createStop("11020517");
        RegtoppImportParameters configuration = createConfig("prefix");

        regtoppStopParser.mapRegtoppStop(regtoppStop, configuration, referential, Coordinate.UTM_32N);

        StopArea boardingPosition = getStopArea(configuration, regtoppStop.getStopId() + RegtoppStopParser.BOARDING_POSITION_ID_SUFFIX);

        assertNotNull(boardingPosition.getParent());
    }

    private RegtoppImportParameters createConfig(String prefix) {
        RegtoppImportParameters configuration = new RegtoppImportParameters();
        configuration.setObjectIdPrefix(prefix);
        return configuration;
    }


    private StopArea getStopArea(RegtoppImportParameters configuration, String stopId) {
        String objectId = ObjectIdCreator.createStopAreaId(configuration, stopId);
        return ObjectFactory.getStopArea(referential, objectId);
    }

    private AbstractRegtoppStopHPL createStop(String stopId) {
        AbstractRegtoppStopHPL regtoppStop = new RegtoppStopHPL(InterchangeWeight.Normal, 0, 0);
        regtoppStop.setStopId(stopId);
        regtoppStop.setY(new BigDecimal("6533687"));
        regtoppStop.setX(new BigDecimal("318578"));
        return regtoppStop;
    }

}