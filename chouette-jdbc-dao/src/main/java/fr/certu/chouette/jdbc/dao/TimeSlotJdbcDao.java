package fr.certu.chouette.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import fr.certu.chouette.model.neptune.TimeSlot;

/**
 * 
 * @author mamadou keira
 * 
 */

public class TimeSlotJdbcDao extends AbstractJdbcDao<TimeSlot> 
{
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
		Timestamp beginningslottime = null,
				  endslottime = null, 
				  firstdeparturetimeinslot = null, 
				  lastdeparturetimeinslot = null;
		
		if(timeSlot.getBeginningSlotTime() != null)
			beginningslottime = new Timestamp(timeSlot.getBeginningSlotTime().getTime());
		
		if(timeSlot.getEndSlotTime() != null)
			endslottime = new Timestamp(timeSlot.getEndSlotTime().getTime());
		
		if(timeSlot.getFirstDepartureTimeInSlot() != null)
			firstdeparturetimeinslot = new Timestamp(timeSlot.getFirstDepartureTimeInSlot().getTime());
		
		if(timeSlot.getLastDepartureTimeInSlot() != null)
			lastdeparturetimeinslot = new Timestamp(timeSlot.getLastDepartureTimeInSlot().getTime());
		
		ps.setTimestamp(6, beginningslottime);
		ps.setTimestamp(7, endslottime);
		ps.setTimestamp(8, firstdeparturetimeinslot);
		ps.setTimestamp(9, lastdeparturetimeinslot);
	}
}
