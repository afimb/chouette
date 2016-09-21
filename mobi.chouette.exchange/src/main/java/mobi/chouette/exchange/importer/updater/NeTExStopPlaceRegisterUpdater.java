package mobi.chouette.exchange.importer.updater;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.ContenerChecker;
import mobi.chouette.common.Context;
import mobi.chouette.common.PropertyNames;
import no.rutebanken.netex.client.PublicationDeliveryClient;
import mobi.chouette.exchange.importer.updater.netex.StopPlaceMapper;
import mobi.chouette.model.NeptuneIdentifiedObject;
import mobi.chouette.model.StopArea;
import no.rutebanken.netex.model.*;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Log4j
@Stateless(name = NeTExStopPlaceRegisterUpdater.BEAN_NAME)
public class NeTExStopPlaceRegisterUpdater implements Updater<Map<String, StopArea>> {
    public static final String BEAN_NAME = "NeTExStopPlaceRegisterUpdater";

    private PublicationDeliveryClient client;
    private final StopPlaceMapper stopPlaceMapper = new StopPlaceMapper();

    private static final ObjectFactory objectFactory = new ObjectFactory();
    
    public NeTExStopPlaceRegisterUpdater(PublicationDeliveryClient client) {
        this.client = client;
    }

    @EJB
    private ContenerChecker contenerChecker;

    @PostConstruct
    public void postConstruct() {
        String urlPropertyKey = contenerChecker.getContext() + PropertyNames.STOP_PLACE_REGISTER_URL;
        String url = System.getProperty(urlPropertyKey);
        if(url == null) {
            log.warn("Cannot read property " + urlPropertyKey + ". Will not update stop place registry.");
            this.client = null;
        } else {
            try {
                this.client = new PublicationDeliveryClient(url);
            } catch (JAXBException e) {
                log.warn("Cannot initialize publication delivery client", e);
            }
        }
    }

    public NeTExStopPlaceRegisterUpdater() {
    }

    @Override
    public void update(Context context, Map<String, StopArea> oldValue, Map<String, StopArea> newValue) throws JAXBException {

        if(client == null) {
            return;
        }

        log.info("Received " + newValue.values().size() + " stop areas to update");

        List<StopPlace> stopPlaces = newValue.values().stream()
                .peek(stopArea -> log.debug("id: " + stopArea.getId()
                        + " objectId: "+ stopArea.getObjectId()
                        + " name: " + stopArea.getName()
                        + " type: " + stopArea.getAreaType()
                        + " isSaved:" + stopArea.isSaved()
                        + " is detached: " + stopArea.isDetached()))
                .filter(NeptuneIdentifiedObject::isSaved)
                .filter(stopArea -> stopArea.getObjectId() != null)
                .map(stopPlaceMapper::mapStopAreaToStopPlace)
                .collect(Collectors.toList());

        if(stopPlaces.isEmpty()) {
            log.info("No stop places to update. Either they do not have objectId set or, they are not saved.");
            return;
        }

        SiteFrame siteFrame = new SiteFrame();
        siteFrame.setStopPlaces(
                new StopPlacesInFrame_RelStructure()
                        .withStopPlace(stopPlaces));

        log.info("Create site frame with "+stopPlaces.size() + " stop places");
        JAXBElement<SiteFrame> jaxSiteFrame = objectFactory.createSiteFrame(siteFrame);

        PublicationDeliveryStructure publicationDelivery = new PublicationDeliveryStructure()
                .withDescription(new MultilingualString().withValue("Publication delivery from chouette").withLang("no").withTextIdType(""))
                .withPublicationTimestamp(OffsetDateTime.now())
                .withParticipantRef("participantRef")
                .withDataObjects(
                        new PublicationDeliveryStructure.DataObjects()
                                .withCompositeFrameOrCommonFrame(Arrays.asList(jaxSiteFrame)));

        PublicationDeliveryStructure response = null;
        try {
            response = client.sendPublicationDelivery(publicationDelivery);
        } catch (JAXBException | IOException e) {
            log.warn("Got exception while sending publication delivery with "+ stopPlaces.size() + " stop places", e);
            return;
        }
        log.info("Got publication delivery structure back with "+response.getDataObjects().getCompositeFrameOrCommonFrame().size()
                + " composite frames or common frames");

        List<StopPlace> receivedStopPlaces = response.getDataObjects().getCompositeFrameOrCommonFrame().stream()
                .filter(jaxbElement -> jaxbElement.getValue() instanceof SiteFrame)
                .map(jaxbElement -> (SiteFrame) jaxbElement.getValue())
                .flatMap(receivedSiteFrame -> receivedSiteFrame.getStopPlaces().getStopPlace().stream())
                .peek(stopPlace -> log.info("got stop place with ID "+stopPlace.getId() + " and name "+  stopPlace.getName() + " back"))
                .collect(Collectors.toList());

    }



}

