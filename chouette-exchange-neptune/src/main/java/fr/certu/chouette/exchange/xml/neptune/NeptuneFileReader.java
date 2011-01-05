package fr.certu.chouette.exchange.xml.neptune;


import chouette.schema.ChouettePTNetwork;
import chouette.schema.ChouettePTNetworkTypeType;
import chouette.schema.ChouetteRemoveLineTypeType;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.validation.commun.LoggingManager;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.commun.ValidationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class NeptuneFileReader 
{
	
	private static final Logger              logger              = Logger.getLogger(NeptuneFileReader.class);
	private static final String              JEU_CARACTERES      = "ISO-8859-1"; 
	
	public NeptuneFileReader() 
	{
	}
	
	public ChouettePTNetworkTypeType read(String fileName) 
	{
		ValidationException validationException = new ValidationException();
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
			validationException.add(TypeInvalidite.FILE_NOT_FOUND, msg);
			throw validationException;
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
		catch (org.exolab.castor.xml.ValidationException e) 
		{
			logger.debug("ValidationException "+e.getMessage());
			do 
			{
				String msg = e.getMessage();
				LoggingManager.log(logger, msg, Level.ERROR);
				validationException.add(TypeInvalidite.INVALID_XML_FILE, msg);
				e = e.getNext();
			} 
			while (e != null);
			throw validationException;
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
					validationException.add(TypeInvalidite.INVALID_XML_FILE, msg1);
					throw validationException;
				}
				catch (Exception e1)
				{
					String msg1 = e1.getMessage();
					logger.error("Exception "+msg1);
					LoggingManager.log(logger, msg1, Level.ERROR);
					validationException.add(TypeInvalidite.INVALID_CHOUETTE_FILE, msg1);
					throw validationException;
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
			validationException.add(TypeInvalidite.INVALID_CHOUETTE_FILE, mesg);
			throw validationException;
		}
		return chouettePTNetworkType;
	}
	
	public void ecrire(ChouettePTNetworkTypeType chouette, File file) 
	{
		FileOutputStream   fileOutputStream   = null;
		OutputStreamWriter outputStreamWriter = null;
		try 
		{
			fileOutputStream   = new FileOutputStream(file);
			outputStreamWriter = new OutputStreamWriter(fileOutputStream, JEU_CARACTERES);
			Marshaller marshaller = new Marshaller(outputStreamWriter);
			marshaller.setEncoding(JEU_CARACTERES);
			marshaller.setRootElement("ChouettePTNetwork");
			marshaller.setSuppressNamespaces(true);
			marshaller.setValidation(false);
			marshaller.marshal(chouette);
			outputStreamWriter.close();
			fileOutputStream.close();
		}
		catch(IOException e) 
		{
			throw new ServiceException(CodeIncident.ERR_XML_ECRITURE,  e);
		}
		catch(MarshalException e) 
		{
			throw new ServiceException(CodeIncident.ERR_XML_FORMAT, e);
		}
		catch(org.exolab.castor.xml.ValidationException e) {
			throw new ServiceException(CodeIncident.ERR_XML_FORMAT, e);
		}
		finally 
		{
			if (outputStreamWriter != null) 
			{
				try 
				{
					outputStreamWriter.close();
				}
				catch(IOException e) 
				{
					throw new ServiceException(CodeIncident.ERR_XML_ECRITURE,  e);
				}
			}
			if (fileOutputStream != null) 
			{
				try 
				{
					fileOutputStream.close();
				}
				catch(IOException e) 
				{
					throw new ServiceException(CodeIncident.ERR_XML_ECRITURE,  e);
				}
			}
		}
	}
	
	public void ecrire(ChouetteRemoveLineTypeType chouette, File file) 
	{
		FileOutputStream   fileOutputStream   = null;
		OutputStreamWriter outputStreamWriter = null;
		try 
		{
			fileOutputStream   = new FileOutputStream(file);
			outputStreamWriter = new OutputStreamWriter(fileOutputStream, JEU_CARACTERES);
			Marshaller aMarshaller = new Marshaller(outputStreamWriter);
			aMarshaller.setEncoding(JEU_CARACTERES);
			aMarshaller.setRootElement("ChouetteRemoveLine");
			aMarshaller.setSuppressNamespaces(true);
			aMarshaller.setValidation(false);
			aMarshaller.marshal(chouette);
			outputStreamWriter.close();
			fileOutputStream.close();
		}
		catch(IOException e) 
		{
			throw new ServiceException(CodeIncident.ERR_XML_ECRITURE,  e);
		}
		catch(MarshalException e) 
		{
			throw new ServiceException(CodeIncident.ERR_XML_FORMAT, e);
		}
		catch(org.exolab.castor.xml.ValidationException e) 
		{
			throw new ServiceException(CodeIncident.ERR_XML_FORMAT, e);
		} 
		finally 
		{
			if (outputStreamWriter != null) 
			{
				try 
				{
					outputStreamWriter.close();
				}
				catch(IOException e) 
				{
					throw new ServiceException(CodeIncident.ERR_XML_ECRITURE,  e);
				}
			}
			if (fileOutputStream != null) 
			{
				try 
				{
					fileOutputStream.close();
				}
				catch(IOException e) 
				{
					throw new ServiceException(CodeIncident.ERR_XML_ECRITURE,  e);
				}
			}
		}
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
