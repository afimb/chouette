/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.neptune;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import lombok.Getter;
import lombok.extern.log4j.Log4j;

import org.trident.schema.trident.ChouettePTNetworkType;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import fr.certu.chouette.plugin.exchange.xml.exception.ExchangeExceptionCode;
import fr.certu.chouette.plugin.exchange.xml.exception.ExchangeRuntimeException;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.ReportLocation;


/**
 * Reader tool to extract XML Neptune Schema Objects (jaxb) from a file or a stream 
 */
@Log4j
public class JaxbNeptuneFileConverter 
{
	private static final int BOM_SIZE = 4;
	
	private static final String XML_1 = "1-NEPTUNE-XML-1" ;
	private static final String XML_2 = "1-NEPTUNE-XML-2" ;

	private JAXBContext context = null;

	private Schema schema = null;
	/**
	 * constructor
	 * @throws JAXBException 
	 * @throws URISyntaxException 
	 * @throws SAXException 
	 * @throws IOException 
	 */
	public JaxbNeptuneFileConverter() throws JAXBException, SAXException, URISyntaxException, IOException 
	{
		context = JAXBContext.newInstance(ChouettePTNetworkType.class);
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		schema = schemaFactory.newSchema(getClass().getClassLoader().getResource("neptune.xsd"));
	}

	/**
	 * extract Neptune object from file
	 * 
	 * @param fileName file relative or absolute path 
	 * @return Neptune model
	 */
	public ChouettePTNetworkHolder read(String fileName) 
	{
		return read(fileName, false);
	}

	/**
	 * extract Neptune object from file
	 * 
	 * @param fileName  file relative or absolute path 
	 * @param validation validate XMl
	 * @return Neptune model
	 */
	public ChouettePTNetworkHolder read(String fileName, boolean validation) 
	{
		String content = null;
		try 
		{
			log.debug("READ "+fileName);
			content = readStream(fileName, new FileInputStream(fileName));
		}
		catch(IOException e) 
		{
			throw new ExchangeRuntimeException(ExchangeExceptionCode.FILE_NOT_FOUND, e, fileName);
		}
		ChouettePTNetworkHolder chouettePTNetworkType = parseXML(fileName,content, validation, false);
		return chouettePTNetworkType;
	}



	/**
	 * extract Neptune object from inputStream (for ZipFile usage)
	 * 
	 * @param zip zipFile 
	 * @param entry entry to extract
	 * @return Neptune model
	 */
	public ChouettePTNetworkHolder read(ZipFile zip , ZipEntry entry) 
	{
		return read(zip, entry, false);
	}

	/**
	 * extract Neptune object from inputStream (for ZipFile usage)
	 * 
	 * @param zip zipFile 
	 * @param entry entry to extract
	 * @param validation
	 * @return Neptune model
	 */
	public ChouettePTNetworkHolder read(ZipFile zip , ZipEntry entry, boolean validation) 
	{
		String content = null;
		String inputName = entry.getName();
		InputStream input = null;
		try 
		{
			log.debug("READ zipped file "+inputName);
			input = zip.getInputStream(entry);
			content = readStream(inputName, input);
		}
		catch(IOException e) 
		{
			throw new ExchangeRuntimeException(ExchangeExceptionCode.FILE_NOT_FOUND, e, inputName);
		}
		finally
		{
			if (input != null)
			{
				try 
				{
					input.close();
				} 
				catch (IOException e) 
				{
					log.warn("fail to close entry",e);
				}
			}
		}

		ChouettePTNetworkHolder chouettePTNetworkType = parseXML(inputName, content, validation, true);
		return chouettePTNetworkType;
	}

	public void write(JAXBElement<ChouettePTNetworkType> rootObject, File file) throws JAXBException, IOException
	{
		write(rootObject,new FileOutputStream( file ));
	}
	public void write(JAXBElement<ChouettePTNetworkType> network, OutputStream stream) throws JAXBException, IOException
	{
		Marshaller marshaller = context.createMarshaller();
		marshaller.setSchema(schema);
        marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
        marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        NamespacePrefixMapper mapper = new NeptuneNamespacePrefixMapper();  
        marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", mapper);
        marshaller.marshal(network,stream);
        stream.close();
	}
	
	/**
	 * convert string data to Neptune model
	 * 
	 * @param contentName source name for logging purpose
	 * @param content string content to parse
	 * @return Neptune model
	 */

	private ChouettePTNetworkHolder parseXML(String contentName, String content, boolean validation, boolean isZipEntry) 
	{
		ChouettePTNetworkType chouettePTNetworkType = null;
		NeptuneValidationEventHandler handler = new NeptuneValidationEventHandler(contentName);
		PhaseReportItem report = new PhaseReportItem(PhaseReportItem.PHASE.ONE);
		CheckPointReportItem report1 = new CheckPointReportItem(XML_1,1,Report.STATE.OK,CheckPointReportItem.SEVERITY.ERROR);
		//Locale.setDefault(Locale.ENGLISH);
		try 
		{
			log.info("UNMARSHALING content of "+contentName);

			Unmarshaller unmarshaller = context.createUnmarshaller();

			unmarshaller.setSchema(schema);

			unmarshaller.setEventHandler(handler);
			// Create the XMLReader
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			SAXParser parser = factory.newSAXParser();

			parser.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", "http://www.trident.org/schema/trident");
			XMLReader reader = parser.getXMLReader();			

			// The filter class to set the correct namespace
			XMLFilterImpl xmlFilter = new NeptuneNamespaceFilter(reader);
			reader.setContentHandler(unmarshaller.getUnmarshallerHandler());
			SAXSource source = new SAXSource(xmlFilter, new InputSource(new StringReader(content)));
			JAXBElement<ChouettePTNetworkType> jaxbElt = unmarshaller.unmarshal(source,ChouettePTNetworkType.class);

			if (!validation || !handler.hasErrors)
				chouettePTNetworkType = jaxbElt.getValue();
			log.info("END OF UNMARSHALING content of "+contentName);
		} 
		catch (JAXBException | ParserConfigurationException | SAXException e) 
		{
			if (e.getCause() != null && e.getCause() instanceof SAXParseException)
			{
				SAXParseException cause = (SAXParseException) e.getCause();
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("xmlKey", "xml-grammar");
				map.put("message", cause.getMessage());
				ReportLocation location = new ReportLocation(contentName, cause.getLineNumber(), cause.getColumnNumber());
				DetailReportItem item = new DetailReportItem( XML_1, "xml-grammar", Report.STATE.ERROR, location, map );
				report1.addItem(item);
			}
			else
			{
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("xmlKey", "xml-failure");
				map.put("message", e.getMessage());
				ReportLocation location = new ReportLocation(contentName, -1 , -1 );
				DetailReportItem item = new DetailReportItem(XML_1, "xml-failure", Report.STATE.ERROR, location , map);
				report1.addItem(item);
			}
		}
		report.addItem(report1);
		if (!report1.hasItems())
			report.addItem(handler.getReport());
		return new ChouettePTNetworkHolder(chouettePTNetworkType,report );
	}

	/**
	 * check and return specific charset
	 * <br> if default Neptune charset found : return null
	 * <br> if unknown charset found : throw ExchangeRuntimeException
	 * 
	 * @param contentName name for log purpose
	 * @param contentXml xml data to check
	 * @return
	 * @throws IOException 
	 */
	private String readStream(String contentName,InputStream in) throws IOException 
	{
		byte bom[] = new byte[BOM_SIZE];
		String encoding;
		int unread;
		PushbackInputStream pushbackStream = new PushbackInputStream(in, 60);
		int n = pushbackStream.read(bom, 0, bom.length);

		// Read ahead four bytes and check for BOM marks.
		if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB) && (bom[2] == (byte) 0xBF)) {
			encoding = "UTF-8";
			unread = n - 3;
		} else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF)) {
			encoding = "UTF-16BE";
			unread = n - 2;
		} else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)) {
			encoding = "UTF-16LE";
			unread = n - 2;
		} else if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00) && (bom[2] == (byte) 0xFE) && (bom[3] == (byte) 0xFF)) {
			encoding = "UTF-32BE";
			unread = n - 4;
		} else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE) && (bom[2] == (byte) 0x00) && (bom[3] == (byte) 0x00)) {
			encoding = "UTF-32LE";
			unread = n - 4;
		} else {
			pushbackStream.unread(bom, 0, n);
			bom = new byte[60];
			n = pushbackStream.read(bom, 0, bom.length);
			byte[] array = new byte[n];
			System.arraycopy(bom, 0, array, 0, n);
			String header = new String(array);
			encoding = getCharset(contentName,header);
			unread = n;
		}

		// Unread bytes if necessary and skip BOM marks.
		if (unread > 0) {
			pushbackStream.unread(bom, (n - unread), unread);
		} else if (unread < -1) {
			pushbackStream.unread(bom, 0, 0);
		}

		// Use given encoding.
		InputStreamReader reader;
		if (encoding == null) {
			reader =  new InputStreamReader(pushbackStream);
		} else {
			reader =  new InputStreamReader(pushbackStream, encoding);
		}
		StringBuffer sb = new StringBuffer(300000);
		while (reader.ready())
		{
			char[] chars = new char[512];
			int count = reader.read(chars);
			if (count > 0)
			{
				sb.append(chars,0,count);
			}
		}
		reader.close();
		log.info(contentName+" loaded : size = "+sb.length()+", encoding = "+encoding);
		return  sb.toString();
	}
	/**
	 * check and return specific charset
	 * <br> if default Neptune charset found : return null
	 * <br> if unknown charset found : throw ExchangeRuntimeException
	 * 
	 * @param contentName name for log purpose
	 * @param contentXml xml data to check
	 * @return
	 */
	private String getCharset(String contentName,String contentXml)
	{
		int startIndex = contentXml.indexOf("encoding=");
		if (startIndex == -1) 
		{
			log.error("missing encoding for "+contentName);
			throw new ExchangeRuntimeException(ExchangeExceptionCode.INVALID_ENCODING, contentName);
		}
		startIndex += 10;
		int endIndex = contentXml.indexOf(contentXml.charAt(startIndex-1),startIndex);
		if (endIndex <= 0)
		{
			log.error("empty encoding for "+contentName);
			throw new ExchangeRuntimeException(ExchangeExceptionCode.INVALID_ENCODING, contentName);
		}
		String charsetName = contentXml.substring(startIndex, endIndex);
		try
		{
			Charset.forName(charsetName);
			return charsetName;
		}
		catch (Exception e) 
		{
			log.error("invalid encoding for "+contentName+" : "+charsetName);
			throw new ExchangeRuntimeException(ExchangeExceptionCode.INVALID_ENCODING, contentName);
		}

	}


	private class NeptuneValidationEventHandler implements ValidationEventHandler
	{

		private String fileName;

		@Getter private List<ValidationEvent> events = new ArrayList<ValidationEvent>();
		@Getter private CheckPointReportItem report = new CheckPointReportItem(XML_2,2,Report.STATE.OK,CheckPointReportItem.SEVERITY.ERROR);
		@Getter private boolean hasErrors = false;


		private NeptuneValidationEventHandler(String fileName)
		{
			this.fileName = fileName;
		}

		@Override
		public boolean handleEvent(ValidationEvent event) {
			events.add(event);
			String key = "others";
			if (event.getMessage().contains(":"))
			{
				String newKey = event.getMessage().substring(0, event.getMessage().indexOf(":")).trim();
				if (!newKey.contains(" "))
				{
					if (newKey.contains(".")) newKey = newKey.substring(0,newKey.indexOf("."));
					key = newKey;
				}
			}
			Report.STATE status = null;
			switch (event.getSeverity())
			{
			case ValidationEvent.FATAL_ERROR : 
			case ValidationEvent.ERROR : 
				hasErrors = true;
				status=Report.STATE.ERROR; break;
			case ValidationEvent.WARNING : 
				status=Report.STATE.WARNING; break;	
			}

			Map<String,Object> map = new HashMap<String,Object>();
			map.put("xmlKey", key);
			map.put("message", event.getMessage());
			
			ReportLocation location = new ReportLocation(fileName, event.getLocator().getLineNumber(), event.getLocator().getColumnNumber());
			DetailReportItem item = new DetailReportItem(XML_1,key, status, location, map);
			report.addItem(item);
			return true;
		}

	}

	/**
	 * workaround to prevent failure on old fashioned Neptune file without namespace declaration
	 * works only if no siri or ifopt items are used
	 *
	 */
	private class NeptuneNamespaceFilter extends XMLFilterImpl 
	{		
		public NeptuneNamespaceFilter(XMLReader arg0) 
		{
			super(arg0);

		}
		@Override
		public void startElement(String uri, String localName,
				String qName, Attributes attributes) throws SAXException 
				{
			if (uri.isEmpty()) uri = "http://www.trident.org/schema/trident";
			super.startElement(uri, localName, qName, 
					attributes);
				}
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
	 
	    private static final String IFOPT_PREFIX = "IFOPT_PREFIX";
	    private static final String IFOPT_URI = "http://www.ifopt.org.uk/acsb";
	    
	    @Override
	    public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) 
	    {
	        if(TRIDENT_URI.equals(namespaceUri)) {
	            return TRIDENT_PREFIX;
	        } else if(SIRI_URI.equals(namespaceUri)) {
	            return SIRI_PREFIX;
	        } else if(IFOPT_URI.equals(namespaceUri)) {
	            return IFOPT_PREFIX;
	        }
	        return suggestion;
	    }		
	}
	

}
