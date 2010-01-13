package fr.certu.chouette.service.validation.amivif;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Timetable extends TridentObject {
	
	private String					version;												// 0..1
	private List<Period>			periods				= new ArrayList<Period>();			// 0..w
	private List<Date>				calendarDays		= new ArrayList<Date>();			// 0..w
	private List<DayType>			dayTypes			= new ArrayList<DayType>();			// 0..w
	private List<String>			stopPointIds		= new ArrayList<String>();			// 0..w
	private List<StopPoint>			stopPoints			= new ArrayList<StopPoint>();		// 0..w
	private List<String>			vehicleJourneyIds	= new ArrayList<String>();			// 0..w
	private List<VehicleJourney>	vehicleJourneys		= new ArrayList<VehicleJourney>();	// 0..w
	private String					comment;												// 0..1
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getVersion() {
		return version;
	}
	
	public void setPeriods(List<Period> periods) {
		this.periods = periods;
	}
	
	public List<Period> getPeriods() {
		return periods;
	}
	
	public void addPeriod(Period period) {
		periods.add(period);
	}
	
	public void removePeriod(Period period) {
		periods.remove(period);
	}
	
	public void removePeriod(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getPeriodsCount()))
			throw new IndexOutOfBoundsException();
		periods.remove(i);
	}
	
	public Period getPeriod(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getPeriodsCount()))
			throw new IndexOutOfBoundsException();
		return (Period)periods.get(i);
	}
	
	public int getPeriodsCount() {
		if (periods == null)
			return 0;
		return periods.size();
	}
	
	public void setCalendarDays(List<Date> calendarDays) {
		this.calendarDays = calendarDays;
	}
	
	public List<Date> getCalendarDays() {
		return calendarDays;
	}
	
	public void addCalendarDay(Date calendarDay) {
		calendarDays.add(calendarDay);
	}
	
	public void removeCalendarDay(Date calendarDay) {
		calendarDays.remove(calendarDay);
	}
	
	public void removeCalendarDay(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getCalendarDaysCount()))
			throw new IndexOutOfBoundsException();
		calendarDays.remove(i);
	}
	
	public Date getCalendarDay(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getCalendarDaysCount()))
			throw new IndexOutOfBoundsException();
		return (Date)calendarDays.get(i);
	}
	
	public int getCalendarDaysCount() {
		if (calendarDays == null)
			return 0;
		return calendarDays.size();
	}
	
	public void setDayTypes(List<DayType> dayTypes) {
		this.dayTypes = dayTypes;
	}
	
	public List<DayType> getDayTypes() {
		return dayTypes;
	}
	
	public void addDayType(DayType dayType) {
		dayTypes.add(dayType);
	}
	
	public void removeDayType(DayType dayType) {
		dayTypes.remove(dayType);
	}
	
	public void removeDayType(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getDayTypesCount()))
			throw new IndexOutOfBoundsException();
		dayTypes.remove(i);
	}
	
	public DayType getDayType(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getDayTypesCount()))
			throw new IndexOutOfBoundsException();
		return (DayType)dayTypes.get(i);
	}
	
	public int getDayTypesCount() {
		if (dayTypes == null)
			return 0;
		return dayTypes.size();
	}
	
	public void setStopPointIds(List<String> stopPointIds) {
		this.stopPointIds = stopPointIds;
	}
	
	public List<String> getStopPointIds() {
		return stopPointIds;
	}
	
	public void addStopPointId(String stopPointId) {
		stopPointIds.add(stopPointId);
	}
	
	public void removeStopPointId(String stopPointId) {
		stopPointIds.remove(stopPointId);
	}
	
	public void removeStopPointId(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getStopPointIdsCount()))
			throw new IndexOutOfBoundsException();
		stopPointIds.remove(i);
	}
	
	public String getStopPointId(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getStopPointIdsCount()))
			throw new IndexOutOfBoundsException();
		return (String)stopPointIds.get(i);
	}
	
	public int getStopPointIdsCount() {
		if (stopPointIds == null)
			return 0;
		return stopPointIds.size();
	}
	
	public void setStopPoints(List<StopPoint> stopPoints) {
		this.stopPoints = stopPoints;
	}
	
	public List<StopPoint> getStopPoints() {
		return stopPoints;
	}
	
	public void addStopPoint(StopPoint stopPoint) {
		stopPoints.add(stopPoint);
	}
	
	public void removeStopPoint(StopPoint stopPoint) {
		stopPoints.remove(stopPoint);
	}
	
	public void removeStopPoint(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getStopPointsCount()))
			throw new IndexOutOfBoundsException();
		stopPoints.remove(i);
	}
	
	public StopPoint getStopPoint(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getStopPointsCount()))
			throw new IndexOutOfBoundsException();
		return (StopPoint)stopPoints.get(i);
	}
	
	public int getStopPointsCount() {
		if (stopPoints == null)
			return 0;
		return stopPoints.size();
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setVehicleJourneyIds(List<String> vehicleJourneyIds) {
		this.vehicleJourneyIds = vehicleJourneyIds;
	}
	
	public List<String> getVehicleJourneyIds() {
		return vehicleJourneyIds;
	}
	
	public void addVehicleJourneyId(String vehicleJourneyId) {
		vehicleJourneyIds.add(vehicleJourneyId);
	}
	
	public void removeVehicleJourneyId(String vehicleJourneyId) {
		vehicleJourneyIds.remove(vehicleJourneyId);
	}
	
	public void removeVehicleJourneyId(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getVehicleJourneyIdsCount()))
			throw new IndexOutOfBoundsException();
		vehicleJourneyIds.remove(i);
	}
	
	public String getVehicleJourneyId(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getVehicleJourneyIdsCount()))
			throw new IndexOutOfBoundsException();
		return (String)vehicleJourneyIds.get(i);
	}
	
	public int getVehicleJourneyIdsCount() {
		if (vehicleJourneyIds == null)
			return 0;
		return vehicleJourneyIds.size();
	}
	
	public void setVehicleJourneys(List<VehicleJourney> vehicleJourneys) {
		this.vehicleJourneys = vehicleJourneys;
	}
	
	public List<VehicleJourney> getVehicleJourneys() {
		return vehicleJourneys;
	}
	
	public void addVehicleJourney(VehicleJourney vehicleJourney) {
		vehicleJourneys.add(vehicleJourney);
	}
	
	public void removeVehicleJourney(VehicleJourney vehicleJourney) {
		vehicleJourneys.remove(vehicleJourney);
	}
	
	public void removeVehicleJourney(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getVehicleJourneysCount()))
			throw new IndexOutOfBoundsException();
		vehicleJourneys.remove(i);
	}
	
	public VehicleJourney getVehicleJourney(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getVehicleJourneysCount()))
			throw new IndexOutOfBoundsException();
		return (VehicleJourney)vehicleJourneys.get(i);
	}
	
	public int getVehicleJourneysCount() {
		if (vehicleJourneys == null)
			return 0;
		return vehicleJourneys.size();
	}
	
	public class Period {
		
		private Date	startOfPeriod;	// 1
		private Date	endOfPeriod;	// 1
		
		public void setStartOfPeriod(Date startOfPeriod) {
			this.startOfPeriod = startOfPeriod;
		}
		
		public Date getStartOfPeriod() {
			return startOfPeriod;
		}
		
		public void setEndOfPeriod(Date endOfPeriod) {
			this.endOfPeriod = endOfPeriod;
		}
		
		public Date getEndOfPeriod() {
			return endOfPeriod;
		}
	}
	
	public enum DayType {
        WeekDay,
        WeekEnd,
        Monday,
        Tuesday,
        Wednesday,
        Thursday,
        Friday,
        Saturday,
        Sunday,
        SchoolHolliday,
        PublicHolliday,
        MarketDay
	}
}
