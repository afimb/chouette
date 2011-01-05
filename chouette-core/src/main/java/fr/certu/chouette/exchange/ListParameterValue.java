/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.exchange;

import java.util.Calendar;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class ListParameterValue extends ParameterValue 
{

	@Getter @Setter private List<Long> integerList;
	@Getter @Setter private List<Boolean> booleanList;
	@Getter @Setter private List<Calendar> dateList;
	@Getter @Setter private List<String> stringList;
	@Getter @Setter private List<String> filenameList;
	@Getter @Setter private List<String> filepathList;

	public ListParameterValue(String name) 
	{
		super(name);
	}
}
