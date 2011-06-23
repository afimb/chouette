package fr.certu.chouette.jdbc.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import fr.certu.chouette.jdbc.exception.JdbcDaoException;
import fr.certu.chouette.model.neptune.Period;
import fr.certu.chouette.model.neptune.Timetable;

/**
 * 
 * @author mamadou keira
 * 
 */

public class TimetableJdbcDao extends AbstractJdbcDao<Timetable> 
{
	@Override
	protected void populateStatement(PreparedStatement ps, Timetable timetable)
	throws SQLException 
	{
		ps.setString(1, timetable.getObjectId());
		ps.setInt(2, timetable.getObjectVersion());
		Timestamp timestamp = null;
		if(timetable.getCreationTime() != null)
			timestamp = new Timestamp(timetable.getCreationTime().getTime());
		ps.setTimestamp(3, timestamp);
		ps.setString(4, timetable.getCreatorId());
		ps.setString(5, timetable.getComment());
		ps.setString(6, timetable.getVersion());
		ps.setObject(7, (Integer)timetable.getIntDayTypes());
	}
	
	/* (non-Javadoc)
	 * @see fr.certu.chouette.jdbc.dao.AbstractJdbcDao#populateAttributeStatement(java.lang.String, java.sql.PreparedStatement, java.lang.Object)
	 */
	@Override
	protected void populateAttributeStatement(String attributeKey,PreparedStatement ps, Object attribute) throws SQLException 
	{
        if (attributeKey.equals("period"))
        {
        	JdbcPeriod jperiod = (JdbcPeriod) attribute;
        	
        	ps.setLong(1,jperiod.timetableId);
        	ps.setDate(2,jperiod.period.getStartDate());
        	ps.setDate(3,jperiod.period.getEndDate());
        	ps.setInt(4,jperiod.position);
        	return;
			
        }
        if (attributeKey.equals("date"))
        {
        	JdbcDate jdate = (JdbcDate) attribute;
        	
        	ps.setLong(1,jdate.timetableId);
        	ps.setDate(2,jdate.date);
        	ps.setInt(3,jdate.position);
        	return;
        }
		
		super.populateAttributeStatement(attributeKey, ps, attribute);
		
	}
	



	@Override
	protected Collection<? extends Object> getAttributeValues(
			String attributeKey, Timetable item) throws JdbcDaoException 
			{
        if (attributeKey.equals("period"))
        {
        	Collection<JdbcPeriod> periods = new ArrayList<TimetableJdbcDao.JdbcPeriod>();
        	int position = 1;
        	for (Period period : item.getPeriods()) 
        	{
				JdbcPeriod jperiod = new JdbcPeriod();
				jperiod.timetableId=item.getId();
				jperiod.period = period;
				jperiod.position = position++;
			}
        	return periods;
        }
        if (attributeKey.equals("date"))
        {
        	Collection<JdbcDate> dates = new ArrayList<TimetableJdbcDao.JdbcDate>();
        	int position = 1;
        	for (Date date : item.getCalendarDays()) 
        	{
        		JdbcDate jdate = new JdbcDate();
        		jdate.timetableId=item.getId();
        		jdate.date = date;
        		jdate.position = position++;
			}
        	return dates;
        }
		
		return super.getAttributeValues(attributeKey, item);
	}


	class JdbcPeriod 
	{
		public Long timetableId;
		public Period period;
		public int position;
	}

	class JdbcDate
	{
		public Long timetableId;
		public Date date;
		public int position;
	}
	
}
