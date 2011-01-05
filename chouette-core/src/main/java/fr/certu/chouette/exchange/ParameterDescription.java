/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.exchange;

import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
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
		FILEPATH
		
	};
	
	@Getter private final String name;
	@Getter private final TYPE type;
	@Getter @Setter private boolean collection;
	@Getter @Setter private boolean mandatory;
	@Getter @Setter private String defaultValue ;
	@Getter @Setter private List<String> allowedExtensions ;
	
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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() 
	{
		String s = "parameter : name = "+name;
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
