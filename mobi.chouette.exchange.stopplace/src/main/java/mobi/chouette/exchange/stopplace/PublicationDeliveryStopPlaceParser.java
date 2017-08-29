package mobi.chouette.exchange.stopplace;

import java.io.InputStream;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.parser.StopPlaceParser;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.util.Referential;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rutebanken.netex.model.Common_VersionFrameStructure;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.Site_VersionFrameStructure;
import org.rutebanken.netex.model.StopPlace;

import static javax.xml.bind.JAXBContext.newInstance;
import static mobi.chouette.exchange.netexprofile.Constant.NETEX_LINE_DATA_CONTEXT;

@Log4j
public class PublicationDeliveryStopPlaceParser {
    private static final String IMPORT_ID_KEY = "imported-id";
    private static final String MERGED_ID_KEY = "merged-id";
    private static final String ID_VALUE_SEPARATOR = ",";
    private InputStream inputStream;
    private Instant now;

    @Getter
    private StopAreaUpdateContext updateContext;

    public PublicationDeliveryStopPlaceParser(InputStream inputStream) {
        this.inputStream = inputStream;
        now = Instant.now();
        updateContext = new StopAreaUpdateContext();
        parseStopPlaces();
    }


    public void parseStopPlaces() {
        try {
            PublicationDeliveryStructure incomingPublicationDelivery = unmarshal(inputStream);

            convertToStopAreas(incomingPublicationDelivery);
        } catch (Exception e) {
            throw new RuntimeException("Failed to unmarshall delivery publication structure: " + e.getMessage(), e);
        }
    }

    private void convertToStopAreas(PublicationDeliveryStructure incomingPublicationDelivery) throws Exception {

        Context context = new Context();
        Referential referential = new Referential();
        context.put(Constant.REFERENTIAL, referential);
        StopPlaceParser stopPlaceParser = (StopPlaceParser) ParserFactory.create(StopPlaceParser.class.getName());

        for (JAXBElement<? extends Common_VersionFrameStructure> frameStructureElmt : incomingPublicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame()) {
            Common_VersionFrameStructure frameStructure = frameStructureElmt.getValue();

            if (frameStructure instanceof Site_VersionFrameStructure) {
                Site_VersionFrameStructure siteFrame = (Site_VersionFrameStructure) frameStructure;

                if (siteFrame.getStopPlaces() != null) {

                    if (siteFrame.getTariffZones() != null) {
                        context.put(NETEX_LINE_DATA_CONTEXT, siteFrame.getTariffZones());
                        stopPlaceParser.parse(context);
                    }

                    context.put(NETEX_LINE_DATA_CONTEXT, siteFrame.getStopPlaces());
                    stopPlaceParser.parse(context);


                    for (StopPlace stopPlace : siteFrame.getStopPlaces().getStopPlace()) {

                        if (!isActive(stopPlace, now)) {
                            updateContext.getInactiveStopAreaIds().add(stopPlace.getId());
                            referential.getStopAreas().remove(stopPlace.getId());
                        } else if (stopPlace.getQuays() != null && !CollectionUtils.isEmpty(stopPlace.getQuays().getQuayRefOrQuay())) {
                            stopPlace.getQuays().getQuayRefOrQuay().forEach(quay -> collectMergedIdForQuay(quay));
                        }
                    }

                }
            }
        }

        updateContext.getActiveStopAreas().addAll(referential.getStopAreas().values().stream().filter(sa -> sa.getParent() == null).collect(Collectors.toSet()));
    }

    private void collectMergedIdForQuay(Object quayObj) {
        if (quayObj instanceof Quay) {
            Quay quay = (Quay) quayObj;
            if (quay.getKeyList() != null && quay.getKeyList().getKeyValue() != null) {
                quay.getKeyList().getKeyValue().stream().filter(kv -> MERGED_ID_KEY.equals(kv.getKey())).forEach(kv -> addMergedIds(quay.getId(), kv.getValue()));
                quay.getKeyList().getKeyValue().stream().filter(kv -> IMPORT_ID_KEY.equals(kv.getKey())).forEach(kv -> addMergedIds(quay.getId(), kv.getValue()));
            }
        }
    }

    private void addMergedIds(String mergedToId, String mergedFromIdsAsString) {
        Set<String> mergedFromIds = Arrays.asList(mergedFromIdsAsString.split(ID_VALUE_SEPARATOR)).stream().filter(id -> !StringUtils.isEmpty(id)).collect(Collectors.toSet());

        if (updateContext.getMergedQuays().get(mergedToId) != null) {
            updateContext.getMergedQuays().get(mergedToId).addAll(mergedFromIds);
        } else {
            updateContext.getMergedQuays().put(mergedToId, mergedFromIds);
        }
    }

    private boolean isActive(StopPlace stopPlace, Instant atTime) {
        if (CollectionUtils.isEmpty(stopPlace.getValidBetween()) || stopPlace.getValidBetween().get(0) == null) {
            return true;
        }
        OffsetDateTime validTo = stopPlace.getValidBetween().get(0).getToDate();

        return validTo == null || validTo.toInstant().isAfter(atTime);
    }

    private PublicationDeliveryStructure unmarshal(InputStream inputStream) throws JAXBException {
        JAXBContext jaxbContext = newInstance(PublicationDeliveryStructure.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        JAXBElement<PublicationDeliveryStructure> jaxbElement = jaxbUnmarshaller.unmarshal(new StreamSource(inputStream), PublicationDeliveryStructure.class);
        PublicationDeliveryStructure publicationDeliveryStructure = jaxbElement.getValue();

        return publicationDeliveryStructure;

    }

}
