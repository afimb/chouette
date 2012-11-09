/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.exchange.xml.neptune.importer;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
import fr.certu.chouette.exchange.LoggingManager;
import fr.certu.chouette.exchange.xml.neptune.exception.ExchangeExceptionCode;
import fr.certu.chouette.exchange.xml.neptune.exception.ExchangeRuntimeException;

/**
 * Reader tool to extract XML Neptune Schema Objects (Castor) from a file or a stream 
 */
public class NeptuneFileReader 
{
	private static final Logger              logger              = Logger.getLogger(NeptuneFileReader.class);
	private static final String              NEPTUNE_CHARACTER_SET      = "ISO-8859-1"; 

	/**
	 * constructor
	 */
	public NeptuneFileReader() 
	{
	}

	/**
	 * extract Neptune object from file
	 * 
	 * @param fileName file relative or absolute path 
	 * @return Neptune model
	 */
	public ChouettePTNetworkTypeType read(String fileName) 
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
	public ChouettePTNetworkTypeType read(String fileName, boolean validation) 
	{
		String content = null;
		try 
		{
			logger.debug("READ "+fileName);
			content = FileUtils.readFileToString(new File(fileName), NEPTUNE_CHARACTER_SET);
		}
		catch(Exception e) 
		{
			String msg = e.getMessage();
			LoggingManager.log(logger, msg, Level.ERROR);
			throw new ExchangeRuntimeException(ExchangeExceptionCode.FILE_NOT_FOUND, e, fileName);
		}
		// check if charset was good 
		Charset charset = checkCharset(fileName,content);
		if (charset != null)
		{
			// must reload data with new charset
			return read(fileName,charset,validation);
		}
		ChouettePTNetworkTypeType chouettePTNetworkType = parseXML(fileName,content, validation, false);
		return chouettePTNetworkType;
	}
	
	/**
	 * extract Neptune object from file
	 * 
	 * @param fileName  file relative or absolute path 
	 * @param charset non default charset
	 * @param validation validate XML
	 * @return
	 */
	private ChouettePTNetworkTypeType read(String fileName, Charset charset, boolean validation) 
	{
		String content = null;
		try 
		{
			logger.debug("READ "+fileName);
			content = FileUtils.readFileToString(new File(fileName), charset);
		}
		catch(Exception e) 
		{
			String msg = e.getMessage();
			LoggingManager.log(logger, msg, Level.ERROR);
			throw new ExchangeRuntimeException(ExchangeExceptionCode.FILE_NOT_FOUND, e, fileName);
		}
		ChouettePTNetworkTypeType chouettePTNetworkType = parseXML(fileName,content, validation, false);
		return chouettePTNetworkType;
	}
	

	/**
	 * extract Neptune object from inputStream (for ZipFile usage)
	 * 
	 * @param zip zipFile 
	 * @param entry entry to extract
	 * @return Neptune model
	 */
	public ChouettePTNetworkTypeType read(ZipFile zip , ZipEntry entry) 
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
	public ChouettePTNetworkTypeType read(ZipFile zip , ZipEntry entry, boolean validation) 
	{
		String content = null;
		String inputName = entry.getName();
		InputStream input = null;
		try 
		{
			StringBuilder buffer = new StringBuilder();
			logger.debug("READ zipped file "+inputName);
		    input = zip.getInputStream(entry);
			BufferedReader reader = new BufferedReader(new InputStreamReader(input,NEPTUNE_CHARACTER_SET));
			String line = reader.readLine();
			while (line != null)
			{
				buffer.append(line);
				line = reader.readLine();
			}
			reader.close();
			content = buffer.toString();
		}
		catch(Exception e) 
		{
			String msg = e.getMessage();
			LoggingManager.log(logger, msg, Level.ERROR);
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
					LoggingManager.log(logger, "fail to close entry", Level.WARN,e);
				}
			}
		}
        // check encoding
		Charset charset = checkCharset(inputName, content);
		if (charset != null)
		{
			return read(zip,entry,charset,validation);
		}
		
		ChouettePTNetworkTypeType chouettePTNetworkType = parseXML(inputName, content, validation, true);
		return chouettePTNetworkType;
	}

	/**
	 * extract Neptune object from inputStream (for ZipFile usage)
	 * 
	 * @param zip zipFile 
	 * @param entry entry to extract
	 * @param charset specific charset
	 * @param validation
	 * @return Neptune model
	 */
	private ChouettePTNetworkTypeType read(ZipFile zip , ZipEntry entry, Charset charset, boolean validation) 
	{
		String content = null;
		String inputName = entry.getName();
		InputStream input = null;
		try 
		{
			StringBuilder buffer = new StringBuilder();
			logger.debug("READ zipped file "+inputName);
		    input = zip.getInputStream(entry);
			BufferedReader reader = new BufferedReader(new InputStreamReader(input,charset));
			String line = reader.readLine();
			while (line != null)
			{
				buffer.append(line);
				line = reader.readLine();
			}
			reader.close();
			content = buffer.toString();
		}
		catch(Exception e) 
		{
			String msg = e.getMessage();
			LoggingManager.log(logger, msg, Level.ERROR);
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
					LoggingManager.log(logger, "fail to close entry", Level.WARN,e);
				}
			}
		}
		
		ChouettePTNetworkTypeType chouettePTNetworkType = parseXML(inputName, content, validation, true);
		return chouettePTNetworkType;
	}
	/**
	 * convert string data to Neptune model
	 * 
	 * @param contentName source name for logging purpose
	 * @param content string content to parse
	 * @return Neptune model
	 */

	private ChouettePTNetworkTypeType parseXML(String contentName, String content, boolean validation, boolean isZipEntry) 
	{
		ChouettePTNetworkTypeType chouettePTNetworkType = null;

		try 
		{
			logger.debug("UNMARSHALING content of "+contentName);
			Unmarshaller anUnmarshaller = new Unmarshaller(ChouettePTNetwork.class);
			anUnmarshaller.setIgnoreExtraElements(false);
			anUnmarshaller.setValidation(validation);
			chouettePTNetworkType = (ChouettePTNetworkTypeType)anUnmarshaller.unmarshal(new StringReader(content));
			logger.debug("END OF UNMARSHALING content of "+contentName);
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
			throw new ExchangeRuntimeException(ExchangeExceptionCode.INVALID_NEPTUNE_FILE,ex, contentName);
		}
		catch (MarshalException e) 
		{
			if ((e instanceof MarshalException) && (e.getCause() != null) && (e.getCause() instanceof SAXException)) 
			{
				File file = null;
				try {
					if (isZipEntry) {
						file = new File(contentName);
						java.io.FileWriter fw = new java.io.FileWriter(file);
						fw.write(content);
						fw.flush();
						fw.close();
					}
					test_xml(contentName);
					if (file !=null)
						file.delete();
				}
				catch (SAXParseException e1) 
				{
					if (file !=null)
						file.delete();
					String msg1 = e1.getMessage() + " AT LINE " +e1.getLineNumber()+ " COLUMN "+ e1.getColumnNumber();
					logger.error("SAXParseException "+msg1);
					LoggingManager.log(logger, msg1, Level.ERROR);
					throw new ExchangeRuntimeException(ExchangeExceptionCode.INVALID_XML_FILE,e1, contentName);
				}
				catch (Exception e1)
				{
					if (file !=null)
						file.delete();
					String msg1 = e1.getMessage();
					logger.error("Exception "+msg1);
					LoggingManager.log(logger, msg1, Level.ERROR);
					throw new ExchangeRuntimeException(ExchangeExceptionCode.INVALID_XML_FILE,e1, contentName);
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

	/**
	 * check and return specific charset
	 * <br> if default Neptune charset found : retunr null
	 * <br> if unknown charset found : throw ExchangeRuntimeException
	 * 
	 * @param contentName name for log purpose
	 * @param contentXml xml data to check
	 * @return
	 */
	private Charset checkCharset(String contentName,String contentXml)
	{
		int length = 200;
		if (length > contentXml.length()) length = contentXml.length();
		String subContent = contentXml.substring(0,length);
		int startIndex = subContent.indexOf("encoding=");
		if (startIndex == -1) 
		{
			LoggingManager.log(logger, "missing encoding for "+contentName, Level.ERROR);
			throw new ExchangeRuntimeException(ExchangeExceptionCode.INVALID_ENCODING, contentName);
		}
		startIndex += 10;
		int endIndex = subContent.indexOf('"',startIndex);
		if (endIndex <= 0)
		{
			LoggingManager.log(logger, "empty encoding for "+contentName, Level.ERROR);
			throw new ExchangeRuntimeException(ExchangeExceptionCode.INVALID_ENCODING, contentName);
		}
        String charsetName = subContent.substring(startIndex, endIndex);
        if (charsetName.equals(NEPTUNE_CHARACTER_SET)) return null; // no reload needed
        try
        {
	        Charset charset = Charset.forName(charsetName);
	        return charset;
        }
        catch (Exception e) 
        {
			LoggingManager.log(logger, "invalid encoding for "+contentName+" : "+charsetName, Level.ERROR);
			throw new ExchangeRuntimeException(ExchangeExceptionCode.INVALID_ENCODING, contentName);
		}
		
	}

	/**
	 * Check basic XML syntax 
	 * 
	 * @param contentName origin name for logging purpose
	 * @throws ParserConfigurationException invalid syntax
	 * @throws Exception check fails
	 */
	private void test_xml(String contentName) throws ParserConfigurationException, Exception 
	{
		logger.debug("Check xml from " + contentName);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		builder.parse(contentName);
		logger.debug("XML content of " + contentName +" is OK ");
	}
}
