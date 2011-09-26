/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */package fr.certu.chouette.exchange.csv.exception;

import fr.certu.chouette.common.ChouetteRuntimeException;

@SuppressWarnings("serial")
public class ExchangeRuntimeException extends ChouetteRuntimeException 
{
	private static final String PREFIX = "NPT";
	private ExchangeExceptionCode code;

	public ExchangeRuntimeException(ExchangeExceptionCode code, String... args) 
	{
		super( args);
		this.code = code;
	}
	public ExchangeRuntimeException(ExchangeExceptionCode code, Throwable cause, String... args) 
	{
		super( cause, args);
		this.code = code;
	}

	public ExchangeExceptionCode getExceptionCode() 
	{
		return code;
	}
	
	public String getCode()
	{
		return code.name();
	}
	@Override
	public String getPrefix() 
	{
		return PREFIX;
	}

}
