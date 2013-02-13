package fr.certu.chouette.exchange.xml.neptune.exporter;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;

import chouette.schema.ChouettePTNetworkTypeType;
import chouette.schema.ChouetteRemoveLineTypeType;
import fr.certu.chouette.exchange.xml.neptune.exception.ExchangeExceptionCode;
import fr.certu.chouette.exchange.xml.neptune.exception.ExchangeRuntimeException;

public class NeptuneFileWriter 
{
	
	private static final String CHARSET = "ISO-8859-1"; 
	
	public NeptuneFileWriter()
	{
	}
		
	public void write(ChouettePTNetworkTypeType chouette, File file) 
	{
		FileOutputStream   fileOutputStream   = null;
		try
		{
		fileOutputStream   = new FileOutputStream(file);
        write(chouette,fileOutputStream);
		
		}
		catch(IOException e) 
		{
			throw new ExchangeRuntimeException(ExchangeExceptionCode.ERR_XML_WRITE,  e);
		}
		finally 
		{
			if (fileOutputStream != null) 
			{
				try 
				{
					fileOutputStream.close();
				}
				catch(IOException e) 
				{
					throw new ExchangeRuntimeException(ExchangeExceptionCode.ERR_XML_WRITE,  e);
				}
			}
		}
	}
	
	public void write(ChouettePTNetworkTypeType chouette, OutputStream fileOutputStream) 
	{
		OutputStreamWriter outputStreamWriter = null;
		try 
		{
			outputStreamWriter = new OutputStreamWriter(fileOutputStream, CHARSET);
			Marshaller marshaller = new Marshaller(outputStreamWriter);
			marshaller.setEncoding(CHARSET);
			marshaller.setRootElement("ChouettePTNetwork");
			marshaller.setSuppressNamespaces(true);
			marshaller.setValidation(false);
			marshaller.marshal(chouette);
			outputStreamWriter.close();
		}
		catch(IOException e) 
		{
			throw new ExchangeRuntimeException(ExchangeExceptionCode.ERR_XML_WRITE,  e);
		}
		catch(MarshalException e) 
		{
			throw new ExchangeRuntimeException(ExchangeExceptionCode.ERR_XML_FORMAT,  e);
		}
		catch(org.exolab.castor.xml.ValidationException e) {
			throw new ExchangeRuntimeException(ExchangeExceptionCode.ERR_XML_FORMAT,  e);
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
					throw new ExchangeRuntimeException(ExchangeExceptionCode.ERR_XML_WRITE,  e);
				}
			}
		}
	}
	
	public void write(ChouetteRemoveLineTypeType chouette, File file) 
	{
		FileOutputStream   fileOutputStream   = null;
		OutputStreamWriter outputStreamWriter = null;
		try 
		{
			fileOutputStream   = new FileOutputStream(file);
			outputStreamWriter = new OutputStreamWriter(fileOutputStream, CHARSET);
			Marshaller aMarshaller = new Marshaller(outputStreamWriter);
			aMarshaller.setEncoding(CHARSET);
			aMarshaller.setRootElement("ChouetteRemoveLine");
			aMarshaller.setSuppressNamespaces(true);
			aMarshaller.setValidation(false);
			aMarshaller.marshal(chouette);
			outputStreamWriter.close();
			fileOutputStream.close();
		}
		catch(IOException e) 
		{
			throw new ExchangeRuntimeException(ExchangeExceptionCode.ERR_XML_WRITE,  e);
		}
		catch(MarshalException e) 
		{
			throw new ExchangeRuntimeException(ExchangeExceptionCode.ERR_XML_FORMAT,  e);
		}
		catch(org.exolab.castor.xml.ValidationException e) 
		{
			throw new ExchangeRuntimeException(ExchangeExceptionCode.ERR_XML_FORMAT,  e);
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
					throw new ExchangeRuntimeException(ExchangeExceptionCode.ERR_XML_WRITE,  e);
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
					throw new ExchangeRuntimeException(ExchangeExceptionCode.ERR_XML_WRITE,  e);
				}
			}
		}
	}
}
