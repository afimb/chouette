/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.common;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Base Class for all Exception of the Chouette project when a catch is required
 * <p/>
 * This class provider basic mechanisms for internationalized error messages
 */
@SuppressWarnings("serial")
public abstract class ChouetteException extends Exception
{
    /**
     * Return the error message prefix
     * <p/>
     * every Chouette module has a specific prefix for its exceptions; 
     * this prefix should be of 3 or 4 character length 
     * @return the prefix
     */
    public abstract String getPrefix();
	
	/**
	 * Return the Exception Code
	 * <p/>
	 * every Chouette module have a specific codification for its exceptions; 
     * this code should be implemented as and enum, but the implementation of 
     * getCode has to return a String used as entry key in messages properties file
	 * @return the code
	 */
	public abstract String getCode();

	/**
	 * the list of arguments used to populate the error message
	 */
	private String[] messageArgs;
	/**
	 * produce a chouette exception
	 * <p/> 
	 * to be use by inherited Exceptions for an Exception without arguments
	 */
	public ChouetteException() 
	{
		super();
		messageArgs = new String[0];
	}

	/**
	 * produce a chouette exception without code 
	 * <p/> 
	 * to be use by inherited Exceptions for an Exception with one argument 
	 * @param message the argument of the message
	 */
	public ChouetteException(String message) 
	{
		super();
		messageArgs = new String[]{message};
	}

	/**
	 * produce a chouette exception when catching another exception
	 * <p/> 
	 * to be use by inherited Exceptions for an Exception without arguments
	 * @param cause the exception origin
	 */
	public ChouetteException(Throwable cause) 
	{
		super(cause);
		messageArgs = new String[0];
	}

	/**
	 * produce a chouette exception
	 * <p/> 
	 * to be use by inherited Exceptions for an Exception with several arguments
	 * @param args the message arguments
	 */
	public ChouetteException(String... args)
	{
		super();
		messageArgs = args;
	}

	/**
	 * produce a chouette exception when catching another exception
	 * <p/> 
	 * to be use by inherited Exceptions for an Exception with one argument
	 * @param message the message argument
	 * @param cause the exception origin
	 */
	public ChouetteException(String message, Throwable cause)
	{
		super(cause);
		messageArgs = new String[]{message};
	}

	/**
	 * produce a chouette exception when catching another exception
	 * <p/> 
	 * to be use by inherited Exceptions for an Exception with several arguments
	 * @param cause the exception origin
	 * @param args the message arguments
	 */
	public ChouetteException(Throwable cause,String... args)
	{
		super(cause);
		messageArgs = args;
	}

	/* (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public final String getMessage()
	{
		Locale locale = new Locale(Locale.ENGLISH.getLanguage(), Locale.UK.getCountry());
		return getLocalizedMessage(locale);
	}
		

	/* (non-Javadoc)
	 * @see java.lang.Throwable#getLocalizedMessage()
	 */
	@Override
	public final String getLocalizedMessage() 
	{
		return getLocalizedMessage(Locale.getDefault());
	}

	/**
	 * return message for the given locale linguistic type
	 * <p/>
	 * populate message using Code for selection in internationalization properties file
	 * with arguments given from construction
	 * 
	 * @param locale the linguistic and regional selection
	 * @return the builded message
	 */
	public final String getLocalizedMessage(Locale locale)
	{
		try
		{
			String format = "";
			String message = "";
			try
			{
				ResourceBundle bundle = ResourceBundle.getBundle(this.getClass().getName(),locale);
				format = bundle.getString(getCode());
				message = MessageFormat.format(format,(Object[])messageArgs);
			}
			catch (MissingResourceException e1)
			{
				try
				{
					ResourceBundle bundle = ResourceBundle.getBundle(this.getClass().getName());
					format = bundle.getString(getCode());
					message = MessageFormat.format(format,(Object[])messageArgs);
				}
				catch (MissingResourceException e2)
				{
					message = Arrays.toString(messageArgs);
				}
			}

			if (getCause() != null)
			{
				format = getFormat("cause",locale);
				message += "\n"+MessageFormat.format(format,getCause().getLocalizedMessage());
			}
			
			format = getFormat("message",locale);
			return MessageFormat.format(format,getPrefix(),getCode(),message);
		}
		catch (RuntimeException ex)
		{
			throw ex;
			//return this.getClass().getName() + ":"+ code.name()+" "+detail.name();
		}
	}

	/**
	 * get the generic format which combine prefix, code and specific message for final output
	 * <p/>
	 * if the entry is missing for the asked locale, it will be search with default locale before failure
	 * 
	 * @param key the entry key in message properties
	 * @param locale the required locale
	 * @return the format
	 */
	private String getFormat(String key,Locale locale) 
	{
		try
		{
			ResourceBundle localBundle = ResourceBundle.getBundle(ChouetteException.class.getName(),locale);
			return localBundle.getString(key);
		}
		catch (MissingResourceException e)
		{
			ResourceBundle localBundle = ResourceBundle.getBundle(ChouetteException.class.getName());
			return localBundle.getString(key);
		}
	}
}
