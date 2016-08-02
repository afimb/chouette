package mobi.chouette.exchange.importer.updater.netex;

import lombok.extern.log4j.Log4j;
import no.rutebanken.netex.model.ObjectFactory;
import no.rutebanken.netex.model.PublicationDeliveryStructure;

import javax.xml.bind.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Log4j
public class PublicationDeliveryClient {

    private final ObjectFactory objectFactory = new ObjectFactory();

    private final String publicationDeliveryUrl;

    private final JAXBContext jaxbContext;

    public PublicationDeliveryClient(String publicationDeliveryUrl) throws JAXBException {
        this.publicationDeliveryUrl = publicationDeliveryUrl;
        this.jaxbContext = JAXBContext.newInstance(PublicationDeliveryStructure.class);
    }

    public PublicationDeliveryStructure sendPublicationDelivery(PublicationDeliveryStructure publicationDelivery) throws JAXBException, IOException {

        Marshaller marshaller = jaxbContext.createMarshaller();
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        try {
            URL url = new URL(publicationDeliveryUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-type", "application/xml");
            connection.setDoOutput(true);

            marshaller.marshal(objectFactory.createPublicationDelivery(publicationDelivery), connection.getOutputStream());

            int responseCode = connection.getResponseCode();
            log.info("POSTed stop place to URL : " + url);

            log.info("Response Code : " + responseCode);

            JAXBElement<PublicationDeliveryStructure> element = (JAXBElement<PublicationDeliveryStructure>) unmarshaller.unmarshal(connection.getInputStream());

            return element.getValue();
        } catch (IOException e) {
            throw new IOException("Error posting XML to " + publicationDeliveryUrl, e);
        }

    }
}
