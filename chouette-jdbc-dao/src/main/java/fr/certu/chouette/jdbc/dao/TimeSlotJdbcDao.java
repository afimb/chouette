package fr.certu.chouette.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.TimeSlot;

/**
 * 
 * @author mamadou keira
 * 
 */

public class TimeSlotJdbcDao extends AbstractJdbcDao<TimeSlot> 
{
   private static final Logger logger = Logger.getLogger(TimeSlotJdbcDao.class);
   
   public Logger getLogger()
   {
      return logger;
   }

	@Override
	protected void populateStatement(PreparedStatement ps, TimeSlot timeSlot)
	throws SQLException 
	{
		ps.setString(1, timeSlot.getObjectId());
		ps.setInt(2, timeSlot.getObjectVersion());
		Timestamp timestamp = null;
		if(timeSlot.getCreationTime() != null)
			timestamp = new Timestamp(timeSlot.getCreationTime().getTime());
		ps.setTimestamp(3, timestamp);
		ps.setString(4, timeSlot.getCreatorId());
		ps.setString(5, timeSlot.getName());
		Time beginningslottime = null,
				  endslottime = null, 
				  firstdeparturetimeinslot = null, 
				  lastdeparturetimeinslot = null;
		
		if(timeSlot.getBeginningSlotTime() != null)
			beginningslottime = timeSlot.getBeginningSlotTime();
		
		if(timeSlot.getEndSlotTime() != null)
			endslottime = timeSlot.getEndSlotTime();
		
		if(timeSlot.getFirstDepartureTimeInSlot() != null)
			firstdeparturetimeinslot = timeSlot.getFirstDepartureTimeInSlot();
		
		if(timeSlot.getLastDepartureTimeInSlot() != null)
			lastdeparturetimeinslot = timeSlot.getLastDepartureTimeInSlot();
		
		ps.setTime(6, beginningslottime);
		ps.setTime(7, endslottime);
		ps.setTime(8, firstdeparturetimeinslot);
		ps.setTime(9, lastdeparturetimeinslot);
	}
}
