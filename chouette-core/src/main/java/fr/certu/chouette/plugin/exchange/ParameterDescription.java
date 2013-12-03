/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.plugin.exchange;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import lombok.Getter;
import lombok.Setter;

public class ParameterDescription 
{
	public enum TYPE 
	{
		INTEGER,
		BOOLEAN,
		DATE,
		STRING,
		FILENAME,
		FILEPATH,
		OBJECT
		
	};
	
	@Getter private final String name;
	@Getter private final TYPE type;
	@Getter @Setter private boolean collection;
	@Getter @Setter private boolean mandatory;
	@Getter @Setter private String defaultValue ;
	@Getter @Setter private List<String> allowedExtensions ;
	@Setter private String bundleName;
	
	public ParameterDescription(String name, TYPE type, boolean collection,
			boolean mandatory) 
	{
		this.name = name;
		this.type = type;
		this.collection = collection;
		this.mandatory = mandatory;
	} 
	
	public ParameterDescription(String name, TYPE type, boolean collection,
			String defaultValue) 
	{
		this.name = name;
		this.type = type;
		this.collection = collection;
		this.mandatory = false;
		this.defaultValue = defaultValue;
	} 
	public String getDescription()
	{
		return getDescription(Locale.getDefault());
	}

	public String getDescription(Locale locale) 
	{
		if (bundleName == null) return name;
		try
		{
			ResourceBundle bundle = ResourceBundle.getBundle(bundleName,locale);
			return bundle.getString(name);
		}
		catch (MissingResourceException e1)
		{
			try
			{
				ResourceBundle bundle = ResourceBundle.getBundle(bundleName);
				return bundle.getString(name);
			}
			catch (MissingResourceException e2)
			{
				return name;
			}
		}

	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() 
	{
		return toString(Locale.getDefault());
	}



	public String toString(Locale locale) 
	{
		String s = "parameter : name = "+name;
		s+= "\n          description = "+getDescription(locale);
		s+= "\n          type = "+type;
		s+= "\n          collection = "+collection;
		s+= "\n          mandatory = "+mandatory;
		if (defaultValue != null)
		s+= "\n          defaultValue = "+defaultValue;
		if (allowedExtensions != null)
		{
			s+= "\n          allowedExtensions = "+Arrays.toString(allowedExtensions.toArray());
		}
		
		return s;
	}
	
	
}
