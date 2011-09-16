package fr.certu.chouette.model.neptune;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;

public class Timetable extends NeptuneIdentifiedObject {
	private static final long serialVersionUID = -1598554061982685113L;
	@Getter @Setter private String comment;
	@Getter @Setter private String version;
	private List<DayTypeEnum> dayTypes;    //Never be persisted
	@Getter @Setter private Integer intDayTypes;
	@Getter @Setter private List<Date> calendarDays = new ArrayList<Date>();
	@Getter @Setter private List<Period> periods  = new ArrayList<Period>();
	@Getter @Setter private List<String> vehicleJourneyIds;
	@Getter @Setter private List<VehicleJourney> vehicleJourneys;

	public void addDayType(DayTypeEnum dayType)
	{
		if (dayTypes == null) dayTypes = getDayTypes();
		if (dayType != null && !dayTypes.contains(dayType))
		{
			dayTypes.add(dayType);
			refreshIntDaytypes();
		}
	}

	public void removeDayType(DayTypeEnum dayType)
	{
		if (dayTypes == null) dayTypes = getDayTypes();
		if (dayType != null)
		{
			if (dayTypes.remove(dayType))
				refreshIntDaytypes();
		}
	}

	public void addCalendarDay(Date calendarDay)
	{
		if (calendarDays == null) calendarDays = new ArrayList<Date>();
		if (calendarDay != null && !calendarDays.contains(calendarDay))
		{
			calendarDays.add(calendarDay);
		}
	}

	public void removeCalendarDay(Date calendarDay)
	{
		if (calendarDays == null) calendarDays = new ArrayList<Date>();
		if (calendarDay != null)
		{
			calendarDays.remove(calendarDay);
		}
	}

	public void addPeriod(Period period)
	{
		if (periods == null) periods = new ArrayList<Period>();
		if (period != null && !periods.contains(period)) periods.add(period);

	}

	public void removePeriod(Period period)
	{
		if (periods == null) periods = new ArrayList<Period>();
		if (period != null)
		{
			periods.remove(period);
		}
	}

	public void removePeriod(int rank)
	{
		if (periods == null) periods = new ArrayList<Period>();
		if (rank >= 0 && rank < periods.size())
		{
			periods.remove(rank);
		}
	}

	public void addVehicleJourneyId(String vehicleJourneyId)
	{
		if (vehicleJourneyIds == null) vehicleJourneyIds = new ArrayList<String>();
		vehicleJourneyIds.add(vehicleJourneyId);
	}

	public void addVehicleJourney(VehicleJourney vehicleJourney)
	{
		if (vehicleJourneys == null) vehicleJourneys = new ArrayList<VehicleJourney>();
		if (vehicleJourney != null && !vehicleJourneys.contains(vehicleJourney))
		{
			vehicleJourneys.add(vehicleJourney);
		}
	}

	public void removeVehicleJourney(VehicleJourney vehicleJourney)
	{
		if (vehicleJourneys == null) vehicleJourneys = new ArrayList<VehicleJourney>();
		if (vehicleJourney != null && vehicleJourneys.contains(vehicleJourney))
		{
			vehicleJourneys.remove(vehicleJourney);
		}
	}



	@Override
	public String toString(String indent,int level)
	{
		StringBuilder sb = new StringBuilder(super.toString(indent,level));
		sb.append("\n").append(indent).append("  comment = ").append(comment);
		sb.append("\n").append(indent).append("  version = ").append(version);
		if (dayTypes != null)
		{
			sb.append("\n").append(indent).append(CHILD_ARROW).append("dayTypes");
			for (DayTypeEnum dayType : getDayTypes())
			{
				sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(dayType);
			}
		}
		if (calendarDays != null)
		{
			sb.append("\n").append(indent).append(CHILD_ARROW).append("calendarDays");
			for (Date calendarDay : getCalendarDays())
			{
				sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(formatDate(calendarDay));
			}
		}
		if (periods != null)
		{
			sb.append("\n").append(indent).append(CHILD_ARROW).append("periods");
			for (Period period : getPeriods())
			{
				sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(period);
			}
		}
		if (vehicleJourneyIds != null)
		{
			sb.append("\n").append(indent).append(CHILD_ARROW).append("vehicleJourneyIds");
			for (String vehicleJourneyId : getVehicleJourneyIds())
			{
				sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(vehicleJourneyId);
			}
		}
		if (level > 0)
		{
			int childLevel = level -1;
			String childIndent = indent + CHILD_INDENT;
			childIndent = indent + CHILD_LIST_INDENT;
			if (vehicleJourneys != null)
			{
				sb.append("\n").append(indent).append(CHILD_ARROW).append("routes");
				for (VehicleJourney vehicleJourney : getVehicleJourneys())
				{
					sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(vehicleJourney.toString(childIndent,childLevel));
				}
			}
		}

		return sb.toString();
	}


	private static String formatDate(Date date){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if(date != null){
			return dateFormat.format(date);
		}
		else{
			return null;
		}
	}

	public List<DayTypeEnum> getDayTypes() 
	{
		if (dayTypes == null) 
		{
			dayTypes = new ArrayList<DayTypeEnum>();
		}
		else
		{
			dayTypes.clear();
		}
		if (intDayTypes == null) intDayTypes = 0;;	

		DayTypeEnum[] dayTypeEnum = DayTypeEnum.values();
		for (DayTypeEnum dayType : dayTypeEnum) 
		{
			int filtreJourType = (int) Math.pow(2, dayType.ordinal());
			if (filtreJourType == (intDayTypes.intValue() & filtreJourType))
			{
				dayTypes.add(dayType);
			}
		}	
		return this.dayTypes;
	}

	public void setDayTypes(List<DayTypeEnum> dayTypes)
	{
		this.dayTypes = dayTypes;
		//CASTOREVO
		refreshIntDaytypes();
	}

	/**
	 * 
	 */
	private void refreshIntDaytypes() {
		intDayTypes = 0;
		if (this.dayTypes == null) new ArrayList<DayTypeEnum>();

		for (DayTypeEnum dayType : this.dayTypes) 
		{
			intDayTypes += (int)Math.pow(2, dayType.ordinal());
		}
	}
}
