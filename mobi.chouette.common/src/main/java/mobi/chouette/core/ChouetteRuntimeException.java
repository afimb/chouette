/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package mobi.chouette.core;


/**
 * Base Class for all Exception of the Chouette project when a catch is not
 * required
 * <p/>
 * This class provider basic mechanisms for internationalized error messages
 */
@SuppressWarnings("serial")
public abstract class ChouetteRuntimeException extends RuntimeException {
	public ChouetteRuntimeException(String message) {
		super(message);
	}

	public ChouetteRuntimeException(String message, Throwable cause) {
		super(message,cause);
	}

	/**
	 * Return the error message prefix
	 * <p/>
	 * every Chouette module has a specific prefix for its exceptions; this
	 * prefix should be of 3 or 4 character length
	 * 
	 * @return the prefix
	 */
	public abstract String getPrefix();

	/**
	 * Return the Exception Code
	 * <p/>
	 * every Chouette module have a specific codification for its exceptions;
	 * this code should be implemented as and enum, but the implementation of
	 * getCode has to return a String used as entry key in messages properties
	 * file
	 * 
	 * @return the code
	 */
	public abstract String getCode();






}
