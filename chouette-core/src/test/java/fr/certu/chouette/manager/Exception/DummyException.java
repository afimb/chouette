/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.manager.exception;

import fr.certu.chouette.common.ChouetteException;

/**
 * @author michel
 *
 */
@SuppressWarnings("serial")
public class DummyException extends ChouetteException 
{

	/**
	 * 
	 */
	public DummyException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DummyException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public DummyException(String... args) {
		super(args);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public DummyException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 * @param args
	 */
	public DummyException(Throwable cause, String... args) {
		super(cause, args);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public DummyException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.common.ChouetteException#getPrefix()
	 */
	@Override
	public String getPrefix() 
	{
		// TODO Auto-generated method stub
		return "DUM";
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.common.ChouetteException#getCode()
	 */
	@Override
	public String getCode() 
	{
		// TODO Auto-generated method stub
		return "DUMMY";
	}

}
