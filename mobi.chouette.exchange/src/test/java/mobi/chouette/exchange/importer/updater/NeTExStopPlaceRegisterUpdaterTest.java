package mobi.chouette.exchange.importer.updater;

import mobi.chouette.common.Context;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import org.testng.annotations.Test;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


public class NeTExStopPlaceRegisterUpdaterTest {

    @Test(enabled = true)
    public void exportStopArea() throws Exception {

        NeTExStopPlaceRegisterUpdater neTExStopPlaceRegisterUpdater = new NeTExStopPlaceRegisterUpdater();

        StopArea oldStoparea = new StopArea();
        oldStoparea.setName("Nesbru æøå");
        oldStoparea.setAreaType(ChouetteAreaEnum.StopPlace);

        StopArea stopArea = new StopArea();
        stopArea.setName("Nesbru");
        stopArea.setAreaType(ChouetteAreaEnum.StopPlace);

        stopArea.setLatitude(new BigDecimal(59.9202707));
        stopArea.setLongitude(new BigDecimal(10.7913503));

        Context context = new Context();

        Map<String, StopArea> stopAreas = new HashMap<>();
        stopAreas.put("key", stopArea);

        neTExStopPlaceRegisterUpdater.update(context, stopAreas, stopAreas);
    }

}