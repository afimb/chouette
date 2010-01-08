package fr.certu.chouette.struts.converter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import chouette.schema.types.DayTypeType;
import fr.certu.chouette.modele.TableauMarche;

public class JourTypeTMConverter 
{	
	public static List<DayTypeType> getProperties( TableauMarche tm)
	{
		if ( tm==null) return new ArrayList<DayTypeType>();
		
		List<DayTypeType> properties = new ArrayList<DayTypeType>();
		for (DayTypeType dayType : tm.getDayTypes()) {
			properties.add( dayType);
		}
		return properties;
	}
	
	public static void setDayTypes( TableauMarche tm, List<DayTypeType> properties)
	{
		if ( properties==null || tm==null) throw new IllegalArgumentException();
		
		Set<DayTypeType> dayTypes = new HashSet<DayTypeType>();
		for (DayTypeType property : properties) {
			dayTypes.add( property);
		}
		tm.setDayTypes( dayTypes);
	}
	
}
