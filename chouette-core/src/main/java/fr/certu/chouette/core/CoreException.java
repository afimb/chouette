/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.core;

import fr.certu.chouette.common.ChouetteException;

@SuppressWarnings("serial")
public class CoreException extends ChouetteException 
{
	private static final String PREFIX = "COR";
	private CoreExceptionCode code;

	public CoreException(CoreExceptionCode code, String... args) 
	{
		super( args);
		this.code = code;
	}
	
	public CoreException(CoreExceptionCode code, Throwable cause, String... args) 
	{
		super( cause, args);
		this.code = code;
	}

	public CoreExceptionCode getExceptionCode() 
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
