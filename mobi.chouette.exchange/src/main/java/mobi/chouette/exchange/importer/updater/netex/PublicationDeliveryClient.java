package mobi.chouette.exchange.importer.updater.netex;

import lombok.extern.log4j.Log4j;
import no.rutebanken.netex.model.ObjectFactory;
import no.rutebanken.netex.model.PublicationDeliveryStructure;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Log4j
public class PublicationDeliveryClient {

    private final ObjectFactory objectFactory = new ObjectFactory();

    private final String publicationDeliveryUrl;

    public PublicationDeliveryClient(String publicationDeliveryUrl) {
        this.publicationDeliveryUrl = publicationDeliveryUrl;
    }

    public void sendPublicationDelivery(PublicationDeliveryStructure publicationDelivery) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(PublicationDeliveryStructure.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        try {
            // TODO: use proper http client
            URL url = new URL(publicationDeliveryUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-type", "application/xml");

            connection.setDoOutput(true);

            marshaller.marshal(objectFactory.createPublicationDelivery(publicationDelivery), connection.getOutputStream());

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
            log.warn("Error posting XML to " + publicationDeliveryUrl, e);
        }

    }
}
