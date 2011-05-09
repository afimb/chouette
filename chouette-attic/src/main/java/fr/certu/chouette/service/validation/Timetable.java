package fr.certu.chouette.service.validation;

import java.util.ArrayList;
import java.util.List;

import org.exolab.castor.types.Date;

public class Timetable {
	
	private ChouettePTNetwork 		chouettePTNetwork;
	private List<Date> 				calendarDays 		= new ArrayList<Date>();
	private String 					comment;
	private java.util.Date 			creationTime;
	private String 					creatorId;
	private List<DayType> 			dayTypes 			= new ArrayList<DayType>();
	private String 					objectId;
	private boolean 				hasObjectVersion 	= false;
	private int 					objectVersion;
	private List<Period> 			periods 			= new ArrayList<Period>();
	private String 					version;
	private String[] 				vehicleJourneyIds;
	private List<VehicleJourney> 	vehicleJourneys 	= new ArrayList<VehicleJourney>();
	
	public void setChouettePTNetwork(ChouettePTNetwork chouettePTNetwork) {
		this.chouettePTNetwork = chouettePTNetwork;
	}
	
	public ChouettePTNetwork getChouettePTNetwork() {
		return chouettePTNetwork;
	}
	
	public void addCalendarDay(Date calendarDay) throws IndexOutOfBoundsException {
		calendarDays.add(calendarDay);
	}
	
	public void addCalendarDay(int index, Date calendarDay) throws IndexOutOfBoundsException {
		calendarDays.add(index, calendarDay);
	}
	
	public void removeCalendarDay(int index) throws IndexOutOfBoundsException {
		calendarDays.remove(index);
	}
	
	public void removeCalendarDay(Date calendarDay) {
		calendarDays.remove(calendarDay);
	}
	
	public void clearCalendarDays() {
		calendarDays.clear();
	}
	
	public void setCalendarDays(List<Date> calendarDays) {
		this.calendarDays = calendarDays;
	}
	
	public List<Date> getCalendarDays() {
		return calendarDays;
	}
	
	public Date[] getCalendarDayAsTable() {
		int size = calendarDays.size();
		Date[] mArray = new Date[size];
		for (int index = 0; index < size; index++)
			mArray[index] = (Date)calendarDays.get(index);
		return mArray;
	}
	
	public Date getCalendarDay(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > calendarDays.size()))
            throw new IndexOutOfBoundsException();
		return (Date) calendarDays.get(index);
	}
	
	public int getCalendarDayCount() {
        return calendarDays.size();
    }
	
	public void setCalendarDays(ArrayList<Date> calendarDays) {
        this.calendarDays = calendarDays;
    }
	
    public void setCalendarDays(Date[] arrayOfCalendarDays) {
    	calendarDays.clear();
        for (int i = 0; i < arrayOfCalendarDays.length; i++)
            calendarDays.add(arrayOfCalendarDays[i]);
    }
    
    public void setCalendarDay(int index, Date calendarDay) throws IndexOutOfBoundsException {
    	if ((index < 0) || (index > calendarDays.size()))
    		throw new IndexOutOfBoundsException();
    	calendarDays.set(index, calendarDay);
    }
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setCreationTime(java.util.Date creationTime) {
		this.creationTime = creationTime;
	}
	
	public java.util.Date getCreationTime() {
		return creationTime;
	}
	
	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}
	
	public String getCreatorId() {
		return creatorId;
	}
	
	public void addDayType(DayType dayType) throws IndexOutOfBoundsException {
		dayTypes.add(dayType);
	}
	
	public void addDayType(int index, DayType dayType) throws IndexOutOfBoundsException {
		dayTypes.add(index, dayType);
	}
	
	public void removeDayType(int index) throws IndexOutOfBoundsException {
		dayTypes.remove(index);
	}
	
	public void removeDayType(DayType dayType) {
		dayTypes.remove(dayType);
	}
	
	public void clearDayTypes() {
		dayTypes.clear();
	}
	
	public void setDayTypes(List<DayType> dayTypes) {
		this.dayTypes = dayTypes;
	}
	
	public List<DayType> getDayTypes() {
		return dayTypes;
	}
	
	public DayType[] getDayTypeAsTable() {
		int size = dayTypes.size();
		DayType[] mArray = new DayType[size];
		for (int index = 0; index < size; index++)
			mArray[index] = (DayType)dayTypes.get(index);
		return mArray;
	}
	
	public DayType getDayType(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > dayTypes.size()))
            throw new IndexOutOfBoundsException();
		return (DayType) dayTypes.get(index);
	}
	
	public int getDayTypeCount() {
        return dayTypes.size();
    }
	
	public void setDayTypes(ArrayList<DayType> dayTypes) {
        this.dayTypes = dayTypes;
    }
	
    public void setDayTypes(DayType[] arrayOfDayTypes) {
    	dayTypes.clear();
        for (int i = 0; i < arrayOfDayTypes.length; i++)
            dayTypes.add(arrayOfDayTypes[i]);
    }
    
    public void setDayType(int index, DayType dayType) throws IndexOutOfBoundsException {
    	if ((index < 0) || (index > dayTypes.size()))
    		throw new IndexOutOfBoundsException();
    	dayTypes.set(index, dayType);
    }
	
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	
	public String getObjectId() {
		return objectId;
	}
	
	public void setObjectVersion(int objectVersion) {
		if (objectVersion >= 1) {
			hasObjectVersion = true;
		this.objectVersion = objectVersion;
                }
                else {
			hasObjectVersion = false;
		this.objectVersion = 1;
                }
	}
	
	public int getObjectVersion() {
		return objectVersion;
	}
	
	public boolean hasObjectVersion() {
		return hasObjectVersion;
	}
	
	public void addPeriod(Period period) throws IndexOutOfBoundsException {
		periods.add(period);
	}
	
	public void addPeriod(int index, Period period) throws IndexOutOfBoundsException {
		periods.add(index, period);
	}
	
	public void removePeriod(int index) throws IndexOutOfBoundsException {
		periods.remove(index);
	}
	
	public void removePeriod(Period period) {
		periods.remove(period);
	}
	
	public void clearPeriods() {
		periods.clear();
	}
	
	public void setPeriods(List<Period> periods) {
		this.periods = periods;
	}
	
	public List<Period> getPeriods() {
		return periods;
	}
	
	public Period[] getPeriodAsTable() {
		int size = periods.size();
		Period[] mArray = new Period[size];
		for (int index = 0; index < size; index++)
			mArray[index] = (Period)periods.get(index);
		return mArray;
	}
	
	public Period getPeriod(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > periods.size()))
            throw new IndexOutOfBoundsException();
		return (Period) periods.get(index);
	}
	
	public int getPeriodCount() {
        return periods.size();
    }
	
	public void setPeriods(ArrayList<Period> periods) {
        this.periods = periods;
    }
	
    public void setPeriods(Period[] arrayOfPeriods) {
    	periods.clear();
        for (int i = 0; i < arrayOfPeriods.length; i++)
            periods.add(arrayOfPeriods[i]);
    }
    
    public void setPeriod(int index, Period period) throws IndexOutOfBoundsException {
    	if ((index < 0) || (index > periods.size()))
    		throw new IndexOutOfBoundsException();
    	periods.set(index, period);
    }
	
	public void addVehicleJourney(VehicleJourney vehicleJourney) throws IndexOutOfBoundsException {
		vehicleJourneys.add(vehicleJourney);
	}
	
	public void addVehicleJourney(int index, VehicleJourney vehicleJourney) throws IndexOutOfBoundsException {
		vehicleJourneys.add(index, vehicleJourney);
	}
	
	public void removeVehicleJourney(int index) throws IndexOutOfBoundsException {
		vehicleJourneys.remove(index);
	}
	
	public void removeVehicleJourney(VehicleJourney vehicleJourney) {
		vehicleJourneys.remove(vehicleJourney);
	}
	
	public void clearVehicleJourneys() {
		vehicleJourneys.clear();
	}
	
	public void setVehicleJourneys(List<VehicleJourney> vehicleJourneys) {
		this.vehicleJourneys = vehicleJourneys;
	}
	
	public List<VehicleJourney> getVehicleJourneys() {
		return vehicleJourneys;
	}
	
	public VehicleJourney[] getVehicleJourneyAsTable() {
		int size = vehicleJourneys.size();
		VehicleJourney[] mArray = new VehicleJourney[size];
		for (int index = 0; index < size; index++)
			mArray[index] = (VehicleJourney)vehicleJourneys.get(index);
		return mArray;
	}
	
	public VehicleJourney getVehicleJourney(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > vehicleJourneys.size()))
            throw new IndexOutOfBoundsException();
		return (VehicleJourney) vehicleJourneys.get(index);
	}
	
	public int getVehicleJourneyCount() {
        return vehicleJourneys.size();
    }
	
	public void setVehicleJourneys(ArrayList<VehicleJourney> vehicleJourneys) {
        this.vehicleJourneys = vehicleJourneys;
    }
	
    public void setVehicleJourneys(VehicleJourney[] arrayOfVehicleJourneys) {
    	vehicleJourneys.clear();
        for (int i = 0; i < arrayOfVehicleJourneys.length; i++)
            vehicleJourneys.add(arrayOfVehicleJourneys[i]);
    }
    
    public void setVehicleJourney(int index, VehicleJourney vehicleJourney) throws IndexOutOfBoundsException {
    	if ((index < 0) || (index > vehicleJourneys.size()))
    		throw new IndexOutOfBoundsException();
    	vehicleJourneys.set(index, vehicleJourney);
    }
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getVersion() {
		return version;
	}
	
	public void setVehicleJourneyIds(String[] vehicleJourneyIds) {
		this.vehicleJourneyIds = vehicleJourneyIds; 
	}
	
	public String[] getVehicleJourneyIds() {
		return vehicleJourneyIds;
	}
	
	public String toString() {
		StringBuffer stb = new StringBuffer();
		stb.append("<Timetable>\n");
		if (version != null)
			stb.append("<Version>"+version+"</Version>\n");
		for (int i = 0; i < periods.size(); i++)
			stb.append(periods.get(i).toString());
		for (int i = 0; i < periods.size(); i++)
			stb.append("<CalendarDay>"+calendarDays.get(i).toString()+"</CalendarDay>\n");
		for (int i = 0; i < dayTypes.size(); i++)
			stb.append("<DayType>"+dayTypes.get(i).toString()+"</DayType>\n");
		for (int i = 0; i < vehicleJourneyIds.length; i++)
			stb.append("<VehicleJourney>"+vehicleJourneyIds[i]+"</VehicleJourney>\n");
		if (comment != null)
			stb.append("<Comment>"+comment+"</Comment>\n");			
		stb.append("<ObjectId>"+objectId+"</ObjectId>\n");
		if (hasObjectVersion)
			stb.append("<ObjectVersion>"+objectVersion+"</ObjectVersion>\n");		
		if (creationTime != null)
			stb.append("<CreationTime>"+creationTime.toString()+"</CreationTime>\n");
		if (creatorId != null)
			stb.append("<CreatorId>"+creatorId.toString()+"</CreatorId>\n");		
		stb.append("</Timetable>\n");
		return stb.toString();
	}

	public String toString(int indent, int indentSize) {
		StringBuffer stb = new StringBuffer();
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<Timetable>\n");
		if (version != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<Version>"+version+"</Version>\n");
		}
		for (int i = 0; i < periods.size(); i++)
			stb.append(periods.get(i).toString(indent+1, indentSize));
		for (int i = 0; i < periods.size(); i++) {
			for (int j = 0; j < indent+1; j++)
				for (int k = 0; k < indentSize; k++)
					stb.append(" ");
			stb.append("<CalendarDay>"+calendarDays.get(i).toString()+"</CalendarDay>\n");
		}
		for (int i = 0; i < dayTypes.size(); i++) {
			for (int j = 0; j < indent+1; j++)
				for (int k = 0; k < indentSize; k++)
					stb.append(" ");
			stb.append("<DayType>"+dayTypes.get(i).toString()+"</DayType>\n");
		}
		for (int i = 0; i < vehicleJourneyIds.length; i++) {
			for (int j = 0; j < indent+1; j++)
				for (int k = 0; k < indentSize; k++)
					stb.append(" ");
			stb.append("<VehicleJourney>"+vehicleJourneyIds[i]+"</VehicleJourney>\n");
		}
		if (comment != null) {
			for (int j = 0; j < indent+1; j++)
				for (int k = 0; k < indentSize; k++)
					stb.append(" ");
			stb.append("<Comment>"+comment+"</Comment>\n");			
		}
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<ObjectId>"+objectId+"</ObjectId>\n");
		if (hasObjectVersion) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<ObjectVersion>"+objectVersion+"</ObjectVersion>\n");		
		}
		if (creationTime != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<CreationTime>"+creationTime.toString()+"</CreationTime>\n");
		}
		if (creatorId != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<CreatorId>"+creatorId.toString()+"</CreatorId>\n");		
		}
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("</Timetable>\n");
		return stb.toString();
	}
}
