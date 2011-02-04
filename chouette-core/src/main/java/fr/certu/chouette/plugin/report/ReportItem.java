/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.plugin.report;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import lombok.Getter;
import lombok.Setter;

/**
 * @author michel
 *
 */
public abstract class ReportItem extends Report
{
	@Getter @Setter private String messageKey;
	@Getter @Setter private List<String> messageArgs;

	public void addMessageArgs(String... args)
	{
		if (messageArgs == null) messageArgs= new ArrayList<String>();
		for (String arg : args) 
		{
			messageArgs.add(arg);
		}

	}	

	/**
	 * @param locale
	 * @return
	 */
	public final String getLocalizedMessage(Locale locale)
	{
		String format = "";
		String message = "";
		try
		{
			ResourceBundle bundle = ResourceBundle.getBundle(this.getClass().getName(),locale);
			format = bundle.getString(getMessageKey());
			message = MessageFormat.format(format,messageArgs.toArray());
		}
		catch (MissingResourceException e1)
		{
			try
			{
				ResourceBundle bundle = ResourceBundle.getBundle(this.getClass().getName());
				format = bundle.getString(getMessageKey());
				message = MessageFormat.format(format,messageArgs.toArray());
			}
			catch (MissingResourceException e2)
			{
				message = getMessageKey(); 
				if (messageArgs != null) message += " : "+Arrays.toString(messageArgs.toArray());
			}
		}


		return message;
	}



}
