/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package mobi.chouette.exchange.neptune.jaxb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import lombok.extern.log4j.Log4j;

import org.trident.schema.trident.ChouettePTNetworkType;
import org.xml.sax.SAXException;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;



/**
 * Reader tool to extract XML Neptune Schema Objects (jaxb) from a file or a
 * stream
 */
@Log4j
public class JaxbNeptuneFileConverter
{

   private JAXBContext context = null;

   private Schema schema = null;
   
   private static JaxbNeptuneFileConverter instance = null;
   
   public static JaxbNeptuneFileConverter getInstance() throws Exception
   {
	   if (instance == null) instance = new JaxbNeptuneFileConverter();
	   return instance;
   }

   /**
    * constructor
    * 
    * @throws JAXBException
    * @throws URISyntaxException
    * @throws SAXException
    * @throws IOException
    */
   private JaxbNeptuneFileConverter() throws JAXBException, SAXException,
         URISyntaxException, IOException
   {
      context = JAXBContext.newInstance(ChouettePTNetworkType.class);
      SchemaFactory schemaFactory = SchemaFactory
            .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      schema = schemaFactory.newSchema(getClass().getClassLoader().getResource(
            "xsd/neptune.xsd"));
   }


   public void write(JAXBElement<ChouettePTNetworkType> rootObject, File file)
         throws JAXBException, IOException
   {
      write(rootObject, new FileOutputStream(file));
   }

   public void write(JAXBElement<ChouettePTNetworkType> network,
         OutputStream stream) throws JAXBException, IOException
   {
      Marshaller marshaller = context.createMarshaller();
      marshaller.setSchema(schema);
      marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); // NOI18N
      marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT,
            Boolean.TRUE);
      marshaller.setEventHandler(new NeptuneValidationEventHandler());
      NamespacePrefixMapper mapper = new NeptuneNamespacePrefixMapper();
      marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", mapper);
      marshaller.marshal(network, stream);
      stream.close();
   }


   /**
    * Prefix mapper to have pretty namespace in xml instead of ns1,ns2,...
    * 
    */
   private class NeptuneNamespacePrefixMapper extends NamespacePrefixMapper
   {

      private static final String TRIDENT_PREFIX = ""; // DEFAULT NAMESPACE
      private static final String TRIDENT_URI = "http://www.trident.org/schema/trident";

      private static final String SIRI_PREFIX = "siri";
      private static final String SIRI_URI = "http://www.siri.org.uk/siri";

      private static final String IFOPT_PREFIX = "acsb";
      private static final String IFOPT_URI = "http://www.ifopt.org.uk/acsb";

      @Override
      public String getPreferredPrefix(String namespaceUri, String suggestion,
            boolean requirePrefix)
      {
         if (TRIDENT_URI.equals(namespaceUri))
         {
            return TRIDENT_PREFIX;
         } else if (SIRI_URI.equals(namespaceUri))
         {
            return SIRI_PREFIX;
         } else if (IFOPT_URI.equals(namespaceUri))
         {
            return IFOPT_PREFIX;
         }
         return suggestion;
      }
   }
   
   private class NeptuneValidationEventHandler implements
   ValidationEventHandler
{

	@Override
	public boolean handleEvent(ValidationEvent event) {
        switch (event.getSeverity())
        {
        case ValidationEvent.FATAL_ERROR: return false;
        case ValidationEvent.ERROR:
        case ValidationEvent.WARNING:
        	log.warn(event.getMessage());
           break;
        }
		return false;
	}
}

}
