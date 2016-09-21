package mobi.chouette.exchange.importer.updater;

import mobi.chouette.common.ContenerChecker;
import mobi.chouette.common.Context;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import no.rutebanken.netex.client.PublicationDeliveryClient;
import no.rutebanken.netex.model.PublicationDeliveryStructure;
import org.junit.Before;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class NeTExStopPlaceRegisterUpdaterTest {

    private static NeTExStopPlaceRegisterUpdater neTExStopPlaceRegisterUpdater;

    @BeforeClass
    public static void setup() throws JAXBException {
        neTExStopPlaceRegisterUpdater = new NeTExStopPlaceRegisterUpdater(new PublicationDeliveryClient("") {
            @Override
            public PublicationDeliveryStructure sendPublicationDelivery(
                    PublicationDeliveryStructure publicationDelivery) throws JAXBException, IOException {
                return new PublicationDeliveryStructure();
            }
        });
    }

    /**
     * Test for manually testing the NeTExStopPlaceRegisterUpdater.
     */
    @Test(enabled = true)
    public void exportStopArea() throws Exception {
        StopArea stopArea = new StopArea();
        stopArea.setName("Nesbru");
        stopArea.setAreaType(ChouetteAreaEnum.StopPlace);

        stopArea.setLatitude(new BigDecimal(59.9202707));
        stopArea.setLongitude(new BigDecimal(10.7913503));
        stopArea.setObjectId("123");
        stopArea.setSaved(true);

        StopArea boardingPosition = new StopArea();
        boardingPosition.setAreaType(ChouetteAreaEnum.BoardingPosition);
        boardingPosition.setObjectId("345");
        boardingPosition.setParent(stopArea);
        boardingPosition.setLatitude(new BigDecimal(59.9202707));
        boardingPosition.setLongitude(new BigDecimal(10.7913503));

        Context context = new Context();

        Map<String, StopArea> stopAreas = map(boardingPosition);

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