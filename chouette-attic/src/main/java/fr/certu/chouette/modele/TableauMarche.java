package fr.certu.chouette.modele;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.exolab.castor.types.Date;

import chouette.schema.Period;
import chouette.schema.Timetable;
import chouette.schema.types.DayTypeType;

public class TableauMarche extends BaseObjet 
{
	private Timetable timetable;
	private List<Periode> periodes;
	private List<java.util.Date> dates;
	private Integer intDayTypes;

	public TableauMarche() {
		super();
		
		timetable = new Timetable();
		periodes = new ArrayList<Periode>();
		dates = new ArrayList<java.util.Date>();
	}
	
	public Integer getIntDayTypes() {
		return intDayTypes;
	}

	private void setIntDayTypes(Integer intDayTypes) 
	{
		this.intDayTypes = intDayTypes;
	}
	
	public Set<DayTypeType> getDayTypes() 
	{
		Set<DayTypeType> dayTypeSet = new HashSet<DayTypeType>();
		if (intDayTypes == null) return dayTypeSet;
		
		//CASTOREVO
		DayTypeType[] dayTypes = DayTypeType.values();
		for (DayTypeType dayTypeType : dayTypes) 
		{
			int filtreJourType = (int) Math.pow(2, dayTypeType.ordinal());
			if (filtreJourType == (intDayTypes.intValue() & filtreJourType))
			{
				dayTypeSet.add(dayTypeType);
			}
		}
		
		return dayTypeSet;
	}
	
	public void setDayTypes(Set<DayTypeType> dayTypes)
	{
		//CASTOREVO
		intDayTypes = 0;
		if (dayTypes == null) return;
			
		for (DayTypeType dayType : dayTypes) 
		{
			intDayTypes += (int)Math.pow(2, dayType.ordinal());
		}
	}

	public int getTotalDates()
	{
		return dates.size();
	}
	
	public List<java.util.Date> getDates() {
		return dates;
	}

	private void setDates(List<java.util.Date> dates) {
		this.dates = dates;
		if ( dates==null)
			this.dates = new ArrayList<java.util.Date>();
	}
	
	public void ajoutDate( java.util.Date date)
	{
		dates.add( date);
	}
	
	public void retraitDate( java.util.Date date)
	{
		dates.remove( date);
	}

	public int getTotalPeriodes()
	{
		return periodes.size();
	}
	
	public List<Periode> getPeriodes()
	{
		return periodes;
	}
	
	private void setPeriodes( List<Periode> periodes)
	{
		this.periodes = periodes;
		if ( periodes==null)
			this.periodes = new ArrayList<Periode>();
	}
	
	public void ajoutPeriode( Periode periode)
	{
		periodes.add( periode);
		
		timetable.addPeriod( periode.getPeriod());
	}
	
	public void retraitPeriode( Periode periode)
	{
		periodes.remove( periode);
		
		timetable.removePeriod( periode.getPeriod());
	}
	

	public Timetable getTimetable() {
		
		if ( timetable!=null)
		{
			Date[] castorDates = new Date[ dates.size()];
			for ( int i=0; i<dates.size(); i++) {
				castorDates[ i] = new Date( dates.get( i));
			}
			timetable.setCalendarDay( castorDates);
			
			Period[] castorPeriodes = new Period[ periodes.size()];
			for (int i = 0; i < castorPeriodes.length; i++) {
				castorPeriodes[ i] = periodes.get( i).getPeriod();
			}
			timetable.setPeriod( castorPeriodes);
			
			timetable.setDayType( new ArrayList<DayTypeType>( getDayTypes()));
		}
		return timetable;
	}

	public void setTimetable(final Timetable timetable) {
		this.timetable = timetable;
		
		if ( timetable!=null)
		{
			if ( dates==null) dates = new ArrayList<java.util.Date>();
				
			dates.clear();
			int totalDates = timetable.getCalendarDayCount();
			for (int i = 0; i < totalDates; i++) {
				dates.add( timetable.getCalendarDay( i).toDate());
			}
			
			if ( periodes==null) periodes = new ArrayList<Periode>();
			
			periodes.clear();
			int totalPeriodes = timetable.getPeriodCount();
			for (int i = 0; i < totalPeriodes; i++) {
				Periode periode = new Periode();
				periode.setDebut( timetable.getPeriod( i).getStartOfPeriod().toDate());
				periode.setFin( timetable.getPeriod( i).getEndOfPeriod().toDate());
				periodes.add( periode);
			}
			
			if ( timetable.getDayType()!=null)
				setDayTypes( new HashSet<DayTypeType>( Arrays.asList( timetable.getDayType())));
			else
				setDayTypes( null);
		}
	}

	public void addVehicleJourneyId(String vVehicleJourneyId) throws IndexOutOfBoundsException {
		timetable.addVehicleJourneyId(vVehicleJourneyId);
	}

	public String getVehicleJourneyId(int index) throws IndexOutOfBoundsException {
		return timetable.getVehicleJourneyId(index);
	}

	public int getVehicleJourneyIdCount() {
		return timetable.getVehicleJourneyIdCount();
	}

	public void addCalendarDay(Date vCalendarDay) throws IndexOutOfBoundsException {
		timetable.addCalendarDay(vCalendarDay);
	}

	private List<Date> getCalendarDays() 
	{
		if ( timetable.getCalendarDay()==null)
		{
			return new ArrayList<Date>();
		}
		return new ArrayList<Date>( Arrays.asList( timetable.getCalendarDay()));
	}

	private Date getCalendarDay(int index) throws IndexOutOfBoundsException {
		return timetable.getCalendarDay(index);
	}

	private int getCalendarDayCount() {
		return timetable.getCalendarDayCount();
	}

	private void setCalendarDays(final List<Date> calendarDays) 
	{
		if ( calendarDays==null)
		{
			timetable.setCalendarDay( new Date[ 0]);
		}
		else
		{
			timetable.setCalendarDay( ( Date[])calendarDays.toArray( new Date[ 0]));
		}
	}
	

	public String getComment() {
		return timetable.getComment();
	}

	public java.util.Date getCreationTime() {
		return timetable.getCreationTime();
	}

	public String getCreatorId() {
		return timetable.getCreatorId();
	}

	public String getObjectId() {
		return timetable.getObjectId();
	}

	public int getObjectVersion() {
            setObjectVersion((int)timetable.getObjectVersion());
		return (int)timetable.getObjectVersion();
	}

	public String getVersion() {
		return timetable.getVersion();
	}

	public void setComment(String comment) {
		timetable.setComment(comment);
	}

	public void setCreationTime(java.util.Date creationTime) {
		timetable.setCreationTime(creationTime);
	}

	public void setCreatorId(String creatorId) {
		timetable.setCreatorId(creatorId);
	}

	public void setObjectId(String objectId) {
		timetable.setObjectId(objectId);
	}

	public void setObjectVersion(int objectVersion) {
            if (objectVersion >= 1)
		timetable.setObjectVersion(objectVersion);
            else
                timetable.setObjectVersion(1);
	}

	public void setVersion(String version) {
		timetable.setVersion(version);
	}

	public boolean isTimetableInPeriod(java.util.Date startDate, java.util.Date endDate){
		if(startDate == null && endDate == null){
			return true;
		}
		
		java.util.Date startOfTimetable = null;
		java.util.Date endOfTimetable = null;
		
		for( java.util.Date date : this.dates){
			if(startOfTimetable == null || date.before(startOfTimetable)){
				startOfTimetable = date;
			}
			else if(endOfTimetable == null || date.after(endOfTimetable)){
				endOfTimetable = date;
			}
		}
		
		for(Periode period  : this.periodes){
			if(startOfTimetable == null || period.getDebut().before(startOfTimetable)){
				startOfTimetable = period.getDebut();
			}
			if(endOfTimetable == null || period.getFin().after(endOfTimetable)){
				endOfTimetable = period.getFin();
			}
		}
		
		if(startOfTimetable == null || endOfTimetable == null){
			return false;
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(startOfTimetable);
		cal.add(Calendar.DATE, -1);
		startOfTimetable = cal.getTime();
		
		cal.setTime(endOfTimetable);
		cal.add(Calendar.DATE, 1);
		endOfTimetable = cal.getTime();
			
		return (startDate == null || (startDate.before(endOfTimetable)))
				&& (endDate == null || (endDate.after(startOfTimetable)));
	}
}
