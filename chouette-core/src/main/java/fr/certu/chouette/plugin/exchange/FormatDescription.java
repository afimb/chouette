/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.plugin.exchange;

import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 */

public class FormatDescription
{
	private String bundleName = null;
	@Getter @Setter private String name;
	@Getter private List<ParameterDescription> parameterDescriptions;

	public FormatDescription(String bundleName)
	{
		this.bundleName = bundleName;
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



	/**
	 * @param parameterDescriptions the parameterDescriptions to set
	 */
	public void setParameterDescriptions(
			List<ParameterDescription> parameterDescriptions) 
	{
		this.parameterDescriptions = parameterDescriptions;
		for (ParameterDescription parameterDescription : this.parameterDescriptions)
		{
			parameterDescription.setBundleName(bundleName);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() 
	{
		// TODO Auto-generated method stub
		return name.hashCode();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj instanceof FormatDescription)
		{
			FormatDescription fobj = (FormatDescription) obj;
			return name.equals(fobj.getName());
		}
		return super.equals(obj);
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
		String s = "FormatDescription : name = "+name+" , description = "+getDescription(locale);
		for (ParameterDescription param : parameterDescriptions) 
		{
			s+= "\n   "+param.toString(locale);
		}
		return s;
	}
}
