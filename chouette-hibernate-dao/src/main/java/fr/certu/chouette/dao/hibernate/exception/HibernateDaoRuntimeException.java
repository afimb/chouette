/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.dao.hibernate.exception;

import fr.certu.chouette.common.ChouetteRuntimeException;

@SuppressWarnings("serial")
public class HibernateDaoRuntimeException extends ChouetteRuntimeException 
{
	private static final String PREFIX = "HBT";
	
	private HibernateDaoExceptionCode code;

	public HibernateDaoRuntimeException(HibernateDaoExceptionCode code, String... args) 
	{
		super( args);
		this.code = code;
	}
	public HibernateDaoRuntimeException(HibernateDaoExceptionCode code, Throwable cause, String... args) 
	{
		super( cause, args);
		this.code = code;
	}

	public HibernateDaoExceptionCode getExceptionCode() 
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
