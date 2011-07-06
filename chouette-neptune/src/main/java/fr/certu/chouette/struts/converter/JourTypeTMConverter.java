package fr.certu.chouette.struts.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;

public class JourTypeTMConverter 
{	
	public static List<DayTypeEnum> getProperties( Timetable tm)
	{
		if ( tm==null) return new ArrayList<DayTypeEnum>();
		
		List<DayTypeEnum> properties = new ArrayList<DayTypeEnum>();
		for (DayTypeEnum dayType : tm.getDayTypes()) {
			properties.add( dayType);
		}
		return properties;
	}
	
	public static void setDayTypes( Timetable tm, List<DayTypeEnum> properties)
	{
		if ( properties==null || tm==null) throw new IllegalArgumentException();
		
		Set<DayTypeEnum> dayTypes = new HashSet<DayTypeEnum>();
		for (DayTypeEnum property : properties) {
			dayTypes.add( property);
		}
		tm.setDayTypes( Arrays.asList(dayTypes.toArray(new DayTypeEnum[0])));
	}
}
