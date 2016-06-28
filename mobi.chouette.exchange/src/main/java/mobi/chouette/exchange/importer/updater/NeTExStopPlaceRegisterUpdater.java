package mobi.chouette.exchange.importer.updater;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.model.StopArea;
import no.rutebanken.netex.model.*;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
@Stateless(name = NeTExStopPlaceRegisterUpdater.BEAN_NAME)
public class NeTExStopPlaceRegisterUpdater implements Updater<Map<String, StopArea>> {
    public static final String BEAN_NAME = "NeTExStopPlaceRegisterUpdater";

    @Override
    public void update(Context context, Map<String, StopArea> oldValue, Map<String, StopArea> newValue) throws Exception {

        log.info("Received " + newValue.values().size() + " stop areas");

        List<StopPlace> stopPlaces = mapStopAreasToStopPlaces(newValue);

        SiteFrame siteFrame = new SiteFrame();
        siteFrame.setStopPlaces(
                new StopPlacesInFrame_RelStructure()
                        .withStopPlace(stopPlaces));

        ObjectFactory objectFactory = new ObjectFactory();
        log.info("Create site frame with "+stopPlaces.size() + " stop places");
        JAXBElement<SiteFrame> jaxSiteFrame = objectFactory.createSiteFrame(siteFrame);

        PublicationDeliveryStructure publicationDelivery = new PublicationDeliveryStructure()
                .withDescription(new MultilingualString().withValue("Publication delivery from chouette").withLang("no").withTextIdType(""))
                .withPublicationTimestamp(ZonedDateTime.now())
                .withParticipantRef("participantRef")
                .withDataObjects(
                        new PublicationDeliveryStructure.DataObjects()
                                .withCompositeFrameOrCommonFrame(Arrays.asList(jaxSiteFrame)));

        JAXBContext jaxbContext = JAXBContext.newInstance(PublicationDeliveryStructure.class);
        Marshaller marshaller = jaxbContext.createMarshaller();


        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        marshaller.marshal(objectFactory.createPublicationDelivery(publicationDelivery), byteArrayOutputStream);

        String xml = byteArrayOutputStream.toString();

        log.info("Generated xml: \n" + xml);

        sendStopPlace(xml);
    }


    private List<StopPlace> mapStopAreasToStopPlaces(Map<String, StopArea> stopAreas) {

        List<StopPlace> stopPlaces = new ArrayList<>();
        for(StopArea stopArea: stopAreas.values()) {
            StopPlace stopPlace = mapStopAreaToStopPlace(stopArea);
            stopPlaces.add(stopPlace);
        }
        return stopPlaces;

    }

    private StopPlace mapStopAreaToStopPlace(StopArea stopArea) {
        StopPlace stopPlace = new StopPlace();
        log.info("Mapping stop area "+stopArea.getId() + " " + stopArea.getName() + " to netext stop place");

        stopPlace.setCentroid(
                new SimplePoint_VersionStructure()
                        .withLocation(
                                new LocationStructure()
                                        .withLatitude(stopArea.getLatitude())
                                        .withLongitude(stopArea.getLongitude())));

        stopPlace.setName(
                new MultilingualString()
                        .withValue(stopArea.getName())
                        .withLang("")
                        .withTextIdType(""));

        return stopPlace;
    }

    private void sendStopPlace(String xml) {

        // TODO: Separate logic for mapping chouette objects to netex in separate
        // TODO: Sending data to stop place register in separate class
        // TODO: configure url to generic stop place registry
        final String tiamatPublicationDeliveryUrl = "http://localhost:1888/jersey/publication_delivery";
        try {
            // TODO: use proper http client
            URL url = new URL(tiamatPublicationDeliveryUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-type", "application/xml");

            connection.setDoOutput(true);

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), Charset.forName("UTF-8"));

            // TODO: Stream xml
            writer.write(xml);
            writer.close();

            int responseCode = connection.getResponseCode();
            log.info("POSTed stop place to URL : " + url);

            log.info("Response Code : " + responseCode);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = bufferedReader.readLine()) != null) {
                response.append(inputLine);
            }
            bufferedReader.close();

            log.info("Response: " + response.toString());


        } catch (IOException e) {
            log.warn("Error posting XML to "+tiamatPublicationDeliveryUrl, e);
        }

    }
}
