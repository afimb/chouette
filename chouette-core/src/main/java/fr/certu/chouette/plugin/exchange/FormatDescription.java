/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.plugin.exchange;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 */

public class FormatDescription
{
	@Getter @Setter private String name;
    @Getter @Setter private List<ParameterDescription> parameterDescriptions;
    

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() 
	{
		String s = "FormatDescription : name = "+name;
		for (ParameterDescription param : parameterDescriptions) 
		{
			s+= "\n   "+param;
		}
		return s;
	}
}
