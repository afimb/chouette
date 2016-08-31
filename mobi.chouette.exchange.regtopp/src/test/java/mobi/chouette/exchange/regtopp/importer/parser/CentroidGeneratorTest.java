package mobi.chouette.exchange.regtopp.importer.parser;

import mobi.chouette.model.NeptuneLocalizedObject;
import mobi.chouette.model.StopArea;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.testng.Assert.*;

public class CentroidGeneratorTest {

    @Test
    public void generateCentroidFromTwoStopAreas() {

        NeptuneLocalizedObject stopArea1 = new StopArea();
        stopArea1.setLatitude(BigDecimal.valueOf(48.879876));
        stopArea1.setLongitude(BigDecimal.valueOf(-56.200827));

        NeptuneLocalizedObject stopArea2 = new StopArea();
        stopArea2.setLatitude(BigDecimal.valueOf(48.560325));
        stopArea2.setLongitude(BigDecimal.valueOf(0.344533));

        NeptuneLocalizedObject destination = new StopArea();

        new CentroidGenerator().generate(Arrays.asList(stopArea1, stopArea2), destination);
        assertTrue(destination.hasCoordinates());
        System.out.println(destination.getLongitude()+","+destination.getLatitude());
        assertTrue(destination.getLongitude().doubleValue() > stopArea1.getLongitude().doubleValue());
        assertTrue(destination.getLongitude().doubleValue() < stopArea2.getLongitude().doubleValue());
    }
}