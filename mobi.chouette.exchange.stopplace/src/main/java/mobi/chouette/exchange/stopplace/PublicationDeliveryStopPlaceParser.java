package mobi.chouette.exchange.stopplace;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.parser.StopPlaceParser;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.util.Referential;
import org.rutebanken.netex.model.Common_VersionFrameStructure;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.Site_VersionFrameStructure;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.util.Collection;

import static javax.xml.bind.JAXBContext.newInstance;
import static mobi.chouette.exchange.netexprofile.Constant.NETEX_LINE_DATA_CONTEXT;
@Log4j
public class PublicationDeliveryStopPlaceParser {

    private static final JAXBContext jaxbContext;

    static {
        try {
            jaxbContext = newInstance(PublicationDeliveryStructure.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<StopArea> parseStopPlaces(InputStream inputStream) {
        try {
            PublicationDeliveryStructure incomingPublicationDelivery = unmarshal(inputStream);

            return convertToStopAreas(incomingPublicationDelivery);
        } catch (Exception e) {
            throw new RuntimeException("Failed to unmarshall delivery publication structure: " + e.getMessage(), e);
        }
    }

    private Collection<StopArea> convertToStopAreas(PublicationDeliveryStructure incomingPublicationDelivery) throws Exception {
        Referential referential = new Referential();
        Context context = new Context();

        context.put(Constant.REFERENTIAL, referential);
        for (JAXBElement<? extends Common_VersionFrameStructure> frameStructureElmt : incomingPublicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame()) {
            Common_VersionFrameStructure frameStructure = frameStructureElmt.getValue();
            if (frameStructure instanceof Site_VersionFrameStructure) {
                Site_VersionFrameStructure siteFrame = (Site_VersionFrameStructure) frameStructure;

                if (siteFrame.getStopPlaces() != null) {
                    context.put(NETEX_LINE_DATA_CONTEXT, siteFrame.getStopPlaces());
                    StopPlaceParser stopPlaceParser = (StopPlaceParser) ParserFactory.create(StopPlaceParser.class.getName());
                    stopPlaceParser.parse(context);
                }
            }
        }


        return referential.getStopAreas().values();
    }


    private PublicationDeliveryStructure unmarshal(InputStream inputStream) throws JAXBException {
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        JAXBElement<PublicationDeliveryStructure> jaxbElement = jaxbUnmarshaller.unmarshal(new StreamSource(inputStream), PublicationDeliveryStructure.class);
        PublicationDeliveryStructure publicationDeliveryStructure = jaxbElement.getValue();

        return publicationDeliveryStructure;

    }

}
