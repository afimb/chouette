package fr.certu.chouette.exchange.xml.neptune.importer;


import java.io.File;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import chouette.schema.ChouettePTNetwork;
import chouette.schema.ChouettePTNetworkTypeType;
import fr.certu.chouette.exchange.xml.neptune.LoggingManager;
import fr.certu.chouette.exchange.xml.neptune.exception.ExchangeExceptionCode;
import fr.certu.chouette.exchange.xml.neptune.exception.ExchangeRuntimeException;

public class NeptuneFileReader 
{
	
	private static final Logger              logger              = Logger.getLogger(NeptuneFileReader.class);
	private static final String              JEU_CARACTERES      = "ISO-8859-1"; 
	
	public NeptuneFileReader() 
	{
	}
	
	public ChouettePTNetworkTypeType read(String fileName) 
	{
		String contenu = null;
		try 
		{
			logger.debug("RECUPERATION DU contenu");
			contenu = FileUtils.readFileToString(new File(fileName), JEU_CARACTERES);
		}
		catch(Exception e) 
		{
			String msg = e.getMessage();
			LoggingManager.log(logger, msg, Level.ERROR);
			throw new ExchangeRuntimeException(ExchangeExceptionCode.FILE_NOT_FOUND, e, fileName);
		}
		
		ChouettePTNetworkTypeType chouettePTNetworkType = null;
		try 
		{
			logger.debug("UNMARSHALING OF contenu");
			Unmarshaller anUnmarshaller = new Unmarshaller(ChouettePTNetwork.class);
			anUnmarshaller.setIgnoreExtraElements(false);
			anUnmarshaller.setValidation(false);
			chouettePTNetworkType = (ChouettePTNetworkTypeType)anUnmarshaller.unmarshal(new StringReader(contenu));
			logger.debug("END OF UNMARSHALING OF contenu");
		}
		catch (org.exolab.castor.xml.ValidationException ex) 
		{
			org.exolab.castor.xml.ValidationException e = ex;
			logger.debug("ValidationException "+e.getMessage());
			do 
			{
				String msg = e.getMessage();
				LoggingManager.log(logger, msg, Level.ERROR);
				e = e.getNext();
			} 
			while (e != null);
			throw new ExchangeRuntimeException(ExchangeExceptionCode.INVALID_XML_FILE,ex, fileName);
		}
		catch (MarshalException e) 
		{
			if ((e instanceof MarshalException) && (e.getCause() != null) && (e.getCause() instanceof SAXException)) 
			{
				try 
				{
					test_xml(fileName);
				}
				catch (SAXParseException e1) 
				{
					String msg1 = e1.getMessage() + " AT LINE " +e1.getLineNumber()+ " COLUMN "+ e1.getColumnNumber();
					logger.error("SAXParseException "+msg1);
					LoggingManager.log(logger, msg1, Level.ERROR);
					throw new ExchangeRuntimeException(ExchangeExceptionCode.INVALID_XML_FILE,e1, fileName);
				}
				catch (Exception e1)
				{
					String msg1 = e1.getMessage();
					logger.error("Exception "+msg1);
					LoggingManager.log(logger, msg1, Level.ERROR);
					throw new ExchangeRuntimeException(ExchangeExceptionCode.INVALID_NEPTUNE_FILE,e1, fileName);
				}
			}
			String mesg = "";
			if (e.getMessage() != null)
			{
				mesg += e.getMessage()+" : ";
			}
			mesg += e.toString();
			Throwable ex = e;
			while (ex.getCause() != null) 
			{
				ex = ex.getCause();
				mesg += "\n";
				if (ex.getMessage() != null)
					mesg += ex.getMessage()+" : ";
				mesg += ex.toString();
			}
			logger.error("MarshalException "+mesg);
			LoggingManager.log(logger, mesg, Level.ERROR);
			throw new ExchangeRuntimeException(ExchangeExceptionCode.INVALID_NEPTUNE_FILE, mesg);

		}
		return chouettePTNetworkType;
	}
	
	private void test_xml(String fichier) throws ParserConfigurationException, Exception 
	{
		logger.debug("Test du fichier " + fichier);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		builder.parse(fichier);
		logger.debug("Test OK du fichier " + fichier);
	}
}
