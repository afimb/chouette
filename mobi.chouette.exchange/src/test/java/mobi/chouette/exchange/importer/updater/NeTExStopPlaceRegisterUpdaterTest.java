package mobi.chouette.exchange.importer.updater;

import mobi.chouette.common.Context;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class NeTExStopPlaceRegisterUpdaterTest {

    private NeTExStopPlaceRegisterUpdater neTExStopPlaceRegisterUpdater = new NeTExStopPlaceRegisterUpdater();

    public NeTExStopPlaceRegisterUpdaterTest() throws JAXBException {
    }

    @Test(enabled = false)
    public void exportStopArea() throws Exception {

        StopArea oldStoparea = new StopArea();
        oldStoparea.setName("Nesbru æøå");
        oldStoparea.setAreaType(ChouetteAreaEnum.StopPlace);

        StopArea stopArea = new StopArea();
        stopArea.setName("Nesbru");
        stopArea.setAreaType(ChouetteAreaEnum.StopPlace);

        stopArea.setLatitude(new BigDecimal(59.9202707));
        stopArea.setLongitude(new BigDecimal(10.7913503));

        Context context = new Context();

        Map<String, StopArea> stopAreas = map(stopArea);

        neTExStopPlaceRegisterUpdater.update(context, stopAreas, stopAreas);
    }

    @Test(enabled = false)
    public void ignoreUnsaved() throws Exception {
        StopArea stopArea = new StopArea();
        stopArea.setSaved(false);

        neTExStopPlaceRegisterUpdater.update(new Context(), map(stopArea), map(stopArea));

    }

    private Map<String, StopArea> map(StopArea... stopAreas ) {
        Map<String, StopArea> map = new HashMap<>();

        int i = 0;
        for(StopArea stopArea : stopAreas) {
            map.put(String.valueOf(++i), stopArea);
        }
        return map;
    }

}