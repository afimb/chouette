/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.jdbc.exception;

import fr.certu.chouette.common.ChouetteException;

@SuppressWarnings("serial")
public class JdbcDaoException extends ChouetteException 
{
	private static final String PREFIX = "JDBC";
	private JdbcDaoExceptionCode code;

	public JdbcDaoException(JdbcDaoExceptionCode code, String... args) 
	{
		super( args);
		this.code = code;
	}
	public JdbcDaoException(JdbcDaoExceptionCode code, Throwable cause, String... args) 
	{
		super( cause, args);
		this.code = code;
	}

	public JdbcDaoExceptionCode getExceptionCode() 
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
