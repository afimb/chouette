package mobi.chouette.exchange.importer.updater;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.updater.netex.PublicationDeliveryClient;
import mobi.chouette.exchange.importer.updater.netex.StopPlaceMapper;
import mobi.chouette.model.StopArea;
import no.rutebanken.netex.model.*;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBElement;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Log4j
@Stateless(name = NeTExStopPlaceRegisterUpdater.BEAN_NAME)
public class NeTExStopPlaceRegisterUpdater implements Updater<Map<String, StopArea>> {
    public static final String BEAN_NAME = "NeTExStopPlaceRegisterUpdater";
    
    private final PublicationDeliveryClient client = new PublicationDeliveryClient("http://localhost:1888/jersey/publication_delivery");
    private final StopPlaceMapper stopPlaceMapper = new StopPlaceMapper();

    private static final ObjectFactory objectFactory = new ObjectFactory();

    @Override
    public void update(Context context, Map<String, StopArea> oldValue, Map<String, StopArea> newValue) throws Exception {

        log.info("Received " + newValue.values().size() + " stop areas");

        List<StopPlace> stopPlaces = stopPlaceMapper.mapStopAreasToStopPlaces(newValue);

        SiteFrame siteFrame = new SiteFrame();
        siteFrame.setStopPlaces(
                new StopPlacesInFrame_RelStructure()
                        .withStopPlace(stopPlaces));

        log.info("Create site frame with "+stopPlaces.size() + " stop places");
        JAXBElement<SiteFrame> jaxSiteFrame = objectFactory.createSiteFrame(siteFrame);

        PublicationDeliveryStructure publicationDelivery = new PublicationDeliveryStructure()
                .withDescription(new MultilingualString().withValue("Publication delivery from chouette").withLang("no").withTextIdType(""))
                .withPublicationTimestamp(ZonedDateTime.now())
                .withParticipantRef("participantRef")
                .withDataObjects(
                        new PublicationDeliveryStructure.DataObjects()
                                .withCompositeFrameOrCommonFrame(Arrays.asList(jaxSiteFrame)));

        client.sendPublicationDelivery(publicationDelivery);
    }



}

