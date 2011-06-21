package fr.certu.chouette.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

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
		ps.setInt(7, timetable.getIntDayTypes());
	}
}
