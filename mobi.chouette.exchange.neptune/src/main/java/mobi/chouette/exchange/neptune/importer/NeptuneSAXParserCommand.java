package mobi.chouette.exchange.neptune.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.Report;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Stateless(name = NeptuneSAXParserCommand.COMMAND)
@Log4j
public class NeptuneSAXParserCommand implements Command, Constant {

	public static final String COMMAND = "NeptuneSAXParserCommand";

	public static final String SCHEMA_FILE = "/xsd/neptune.xsd";

	static final String JAXP_SCHEMA_LANGUAGE =
	        "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	    
	    static final String W3C_XML_SCHEMA =
	        "http://www.w3.org/2001/XMLSchema";

	    static final String JAXP_SCHEMA_SOURCE =
	        "http://java.sun.com/xml/jaxp/properties/schemaSource";
	@Override
	public boolean execute(Context context) throws Exception {

		Monitor monitor = MonitorFactory.start(COMMAND);

		Report report = (Report) context.get(REPORT);
		FileInfo fileItem = new FileInfo();
		fileItem.setName((String) context.get(FILE_NAME));

		Schema schema = (Schema) context.get(SCHEMA);
		if (schema == null) {
			SchemaFactory factory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			schema = factory.newSchema(getClass().getResource(
					SCHEMA_FILE));
			context.put(SCHEMA, schema);
		}

		URL url = new URL((String) context.get(FILE_URL));
		log.info("[DSU] validate file : " + url);

		Reader reader = new BufferedReader(new InputStreamReader(
				new BOMInputStream(url.openStream())), 8192 * 10);
		StreamSource file = new StreamSource(reader);

		try {
			NeptuneSAXErrorHandler errorHandler = new NeptuneSAXErrorHandler(context);
			Validator validator = schema.newValidator();
			validator.setErrorHandler(errorHandler);
			validator.reset();
			validator.validate(file);
	         if (errorHandler.isHasErrors())
	         {
	 			fileItem.setStatus(FileInfo.FILE_STATE.NOK);
				report.getFiles().getFileInfos().add(fileItem);
				fileItem.getErrors().add("Xml errors");	     
				return false;
	         }
	         return true;
		} catch (IOException | SAXException e) {
		    
			if (!context.containsKey("REPLAY_VALIDATOR") && e.getMessage().contains("ChouettePTNetwork"))
			{
				log.warn(e);
				reader.close();
				addNameSpace(url);
				context.put("REPLAY_VALIDATOR", Boolean.TRUE);
				boolean res = execute(context);
				context.remove("REPLAY_VALIDATOR");
				return res;
			}
			log.error(e);
			fileItem.setStatus(FileInfo.FILE_STATE.NOK);
			report.getFiles().getFileInfos().add(fileItem);
			fileItem.getErrors().add(e.getMessage());
			
		}
		finally
		{
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}

		return false;
	}
	
	 private void addNameSpace(URL url) 
	 {
		try {
			File tmp = File.createTempFile("netpuneImport", ".xml");
			FileUtils.copyInputStreamToFile(url.openStream(), tmp);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new BOMInputStream(new FileInputStream(tmp))), 8192 * 10);
			
			File f = new File(url.toURI());
			
			PrintWriter writer = new PrintWriter(f);
			
			String l ;
			while ((l = reader.readLine()) != null)
			{
				if (l.contains("<ChouettePTNetwork>"))
				{
					l = l.replace("<ChouettePTNetwork>", "<ChouettePTNetwork xmlns=\"http://www.trident.org/schema/trident\" " +
							"xmlns:acsb=\"http://www.ifopt.org.uk/acsb\" " +
							"xmlns:siri=\"http://www.siri.org.uk/siri\">");
					log.info(" <ChouettePTNetwork> replaced :" + l);
				}
				writer.println(l);
			}
			reader.close();
			writer.close();
			tmp.delete();

		} catch (IOException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	private class NeptuneResolverResource implements LSResourceResolver
	 {
		 private LSResourceResolver delegate;
		 public NeptuneResolverResource(LSResourceResolver arg0)
	      {
			 delegate = arg0;
	      }
		 
		@Override
		public LSInput resolveResource(String type, String namespaceURI,
				String publicId, String systemId, String baseURI) 
		{
			log.warn("URI = "+namespaceURI);
			
			if (namespaceURI.isEmpty())
				namespaceURI = "http://www.trident.org/schema/trident";
			return delegate.resolveResource(type, namespaceURI, publicId, systemId, baseURI);
		}
		 
	 }



	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange.neptune/"
						+ COMMAND;
				result = (Command) context.lookup(name);
			} catch (NamingException e) {
				log.error(e);
			}
			return result;
		}
	}

	static {
		CommandFactory factory = new DefaultCommandFactory();
		CommandFactory.factories.put(NeptuneSAXParserCommand.class.getName(),
				factory);
	}
}
