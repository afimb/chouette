package mobi.chouette.exchange.importer.updater;

import mobi.chouette.common.Context;
import mobi.chouette.model.*;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.LongLatTypeEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.client.PublicationDeliveryClient;
import org.rutebanken.netex.model.*;
import org.rutebanken.netex.validation.NeTExValidator;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Time;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NeTExStopPlaceRegisterUpdaterTest {

    @Test
    public void convertStopAreaAndConnectionLink() throws Exception {

        Referential referential = new Referential();

        StopArea stopArea = ObjectFactory.getStopArea(referential, "AKT:StopArea:1");
        stopArea.setName("Nesbru");
        stopArea.setAreaType(ChouetteAreaEnum.CommercialStopPoint);

        stopArea.setLatitude(new BigDecimal(59.9202707));
        stopArea.setLongitude(new BigDecimal(10.7913503));
        stopArea.setLongLatType(LongLatTypeEnum.WGS84);

        StopArea boardingPosition = ObjectFactory.getStopArea(referential, "AKT:StopArea:2");
        boardingPosition.setAreaType(ChouetteAreaEnum.BoardingPosition);
        boardingPosition.setLatitude(new BigDecimal(59.9202707));
        boardingPosition.setLongitude(new BigDecimal(10.7913503));
        boardingPosition.setLongLatType(LongLatTypeEnum.WGS84);
        boardingPosition.setParent(stopArea);

        Line line = ObjectFactory.getLine(referential, "AKT:Line:1");
        Route route = ObjectFactory.getRoute(referential, "AKT:Route:1");
        route.setLine(line);

        StopPoint sp1 = ObjectFactory.getStopPoint(referential, "AKT:StopPoint:1");
        sp1.setContainedInStopArea(stopArea);

        StopPoint sp2 = ObjectFactory.getStopPoint(referential, "AKT:StopPoint:2");
        sp2.setContainedInStopArea(boardingPosition);

        // This is a new stoppoint referring to the first stoparea
        StopPoint sp3 = ObjectFactory.getStopPoint(referential, "AKT:StopPoint:3");
        sp3.setContainedInStopArea(stopArea);

        route.getStopPoints().add(sp1);
        route.getStopPoints().add(sp2);
        route.getStopPoints().add(sp3);


        ConnectionLink link = ObjectFactory.getConnectionLink(referential, "AKT:ConnectionLink:1-2");
        link.setDefaultDuration(new Time(5 * 60 * 1000));
        link.setStartOfLink(stopArea);
        link.setEndOfLink(boardingPosition);


        Context context = new Context();

        // Build response
        NeTExStopPlaceRegisterUpdater neTExStopPlaceRegisterUpdater = new NeTExStopPlaceRegisterUpdater(createMockedPublicationDeliveryClient(stopArea));

        // Call update
        neTExStopPlaceRegisterUpdater.update(context, referential);

        // Assert stopPoints changed
        AssertJUnit.assertEquals(sp1.getContainedInStopArea().getObjectId(), "NHR:StopArea:1");
        AssertJUnit.assertEquals(sp2.getContainedInStopArea().getObjectId(), "NHR:StopArea:2");

        AssertJUnit.assertEquals(referential.getSharedConnectionLinks().values().size(), 1);
        AssertJUnit.assertEquals(referential.getSharedConnectionLinks().values().iterator().next().getObjectId(), "NHR:PathLink:1");
    }

    /**
     * Validate PublicationDeliveryStructure.
     */
    private void validate(PublicationDeliveryStructure publicationDeliveryStructure) throws JAXBException, IOException {
        JAXBContext jaxbContext = JAXBContext.newInstance(PublicationDeliveryStructure.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        JAXBSource jaxbSource = new JAXBSource(jaxbContext, new org.rutebanken.netex.model.ObjectFactory().createPublicationDelivery(publicationDeliveryStructure));
        try {
            NeTExValidator neTExValidator = new NeTExValidator();
            neTExValidator.getSchema().newValidator().validate(jaxbSource);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private PublicationDeliveryClient createMockedPublicationDeliveryClient(StopArea stopArea) throws JAXBException, IOException, SAXException {
        return new PublicationDeliveryClient("") {
            @Override
            public PublicationDeliveryStructure sendPublicationDelivery(
                    PublicationDeliveryStructure publicationDelivery) throws JAXBException, IOException {

                validate(publicationDelivery);

                Assert.assertEquals(1, ((SiteFrame) publicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame().get(0).getValue()).getStopPlaces().getStopPlace().size(), "StopPlaces not unique");

                SimplePoint_VersionStructure centroid = new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure().withLatitude(stopArea.getLatitude())
                                .withLongitude(stopArea.getLongitude()));

                StopPlace stopPlace = new StopPlace();
                stopPlace.setId("NHR:StopArea:1");
                stopPlace.setCentroid(centroid);
                stopPlace.setName(new MultilingualString().withValue("StopPlaceName"));
                stopPlace.setKeyList(createKeyListStructure("AKT:StopArea:1"));

                Quay q = new Quay();
                q.setId("NHR:StopArea:2");
                q.setKeyList(createKeyListStructure(StringUtils.join(new String[]{"AKT:StopArea:2", "OPP:StopArea:3"}, NeTExStopPlaceRegisterUpdater.IMPORTED_ID_VALUE_SEPARATOR))); // 2 values
                q.setName(new MultilingualString().withValue("QuayName"));
                q.setCentroid(centroid);

                Quays_RelStructure quays = new Quays_RelStructure();
                quays.getQuayRefOrQuay().add(q);
                stopPlace.setQuays(quays);

                List<StopPlace> stopPlaces = new ArrayList<>();
                stopPlaces.add(stopPlace);

                PathLink pathLink  = new PathLink()
                            .withId("NHR:PathLink:1")
                            .withFrom(new PathLinkEndStructure().withPlaceRef(new PlaceRefStructure().withValue(stopPlace.getId())))
                            .withTo(new PathLinkEndStructure().withPlaceRef(new PlaceRefStructure().withValue(q.getId())))
                            .withTransferDuration(new TransferDurationStructure().withDefaultDuration(Duration.of(5, ChronoUnit.MINUTES)));

                pathLink.setKeyList(createKeyListStructure("AKT:ConnectionLink:1-2"));
             


                SiteFrame siteFrame = new SiteFrame();
                siteFrame.setStopPlaces(new StopPlacesInFrame_RelStructure().withStopPlace(stopPlaces));
                siteFrame.setPathLinks(new PathLinksInFrame_RelStructure().withPathLink(pathLink));

                org.rutebanken.netex.model.ObjectFactory objectFactory = new org.rutebanken.netex.model.ObjectFactory();
                JAXBElement<SiteFrame> jaxSiteFrame = objectFactory.createSiteFrame(siteFrame);

                PublicationDeliveryStructure respoonse = new PublicationDeliveryStructure()
                        .withDescription(
                                new MultilingualString().withValue("Publication delivery from chouette")
                                        .withLang("no").withTextIdType(""))
                        .withPublicationTimestamp(OffsetDateTime.now()).withParticipantRef("participantRef")
                        .withDataObjects(new PublicationDeliveryStructure.DataObjects()
                                .withCompositeFrameOrCommonFrame(Arrays.asList(jaxSiteFrame)));
                return respoonse;

            }

            protected KeyListStructure createKeyListStructure(String value) {
                KeyListStructure kl = new KeyListStructure();
                KeyValueStructure kv = new KeyValueStructure();
                kv.setKey(NeTExStopPlaceRegisterUpdater.IMPORTED_ID);
                kv.setValue(value);
                kl.getKeyValue().add(kv);
                return kl;
            }
        };
    }
}