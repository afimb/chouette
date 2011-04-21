package fr.certu.chouette.model.neptune;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.filter.DetailLevelEnum;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;

public class Timetable extends NeptuneIdentifiedObject {
	private static final long serialVersionUID = -1598554061982685113L;
	@Getter @Setter private String comment;
	@Getter @Setter private String version;
	@Getter @Setter private List<DayTypeEnum> dayTypes;
	@Getter @Setter private List<Date> calendarDays;
	@Getter @Setter private List<Period> periods;
	@Getter @Setter private List<String> vehicleJourneyIds;
	@Getter @Setter private List<VehicleJourney> vehicleJourneys;

	public void addDayType(DayTypeEnum dayType)
	{
		if (dayTypes == null) dayTypes = new ArrayList<DayTypeEnum>();
		dayTypes.add(dayType);
	}
	
	public void addCalendarDay(Date calendarDay)
	{
		if (calendarDays == null) calendarDays = new ArrayList<Date>();
		calendarDays.add(calendarDay);
	}
	
	public void addPeriod(Period period)
	{
		if (periods == null) periods = new ArrayList<Period>();
		periods.add(period);
	}
	
	public void addVehicleJourneyId(String vehicleJourneyId)
	{
		if (vehicleJourneyIds == null) vehicleJourneyIds = new ArrayList<String>();
		vehicleJourneyIds.add(vehicleJourneyId);
	}

	public void addVehicleJourney(VehicleJourney vehicleJourney)
	{
		if (vehicleJourneys == null) vehicleJourneys = new ArrayList<VehicleJourney>();
		vehicleJourneys.add(vehicleJourney);
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.model.neptune.NeptuneBean#expand(fr.certu.chouette.manager.NeptuneBeanManager.DETAIL_LEVEL)
	 */
	@Override
	public void expand(DetailLevelEnum level)
	{
		// to avoid circular call check if level is already set according to this level
		if (getLevel().ordinal() >= level.ordinal()) return;
		super.expand(level);
		switch (level)
		{
		case ATTRIBUTE : 
			vehicleJourneys = null;
			break;
		case NARROW_DEPENDENCIES : 
		case STRUCTURAL_DEPENDENCIES : 
		case ALL_DEPENDENCIES :
			if (getVehicleJourneys() != null)
			{
				for (VehicleJourney vehicleJourney : getVehicleJourneys())
				{
					vehicleJourney.expand(DetailLevelEnum.ATTRIBUTE);
				}
			}
			break;
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
}
