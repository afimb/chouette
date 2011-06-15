package fr.certu.chouette.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;

/**
 * 
 * @author mamadou keira
 * 
 */

@SuppressWarnings("unchecked")
public class StopPointJdbcDao extends AbstractJdbcDao<StopPoint> 
{
	@Override
	public List<StopPoint> getAll() 
	{
		String sql = sqlSelectAll;
		List<StopPoint> stopPoints = getJdbcTemplate().query(sql,
				new BeanPropertyRowMapper(StopPoint.class));

		return stopPoints;
	}

	@Override
	public StopPoint getByObjectId(String objectId) 
	{
		String sql = sqlSelectByObjectId;		 
		StopPoint stopPoint = (StopPoint)getJdbcTemplate().queryForObject(sql, 
				new Object[] {objectId}, 
				new BeanPropertyRowMapper(StopPoint.class));
		return stopPoint;
	}

	@Override
	protected void populateStatement(PreparedStatement ps, StopPoint stopPoint)
	throws SQLException {
		ps.setString(1, stopPoint.getObjectId());
		ps.setInt(2, stopPoint.getObjectVersion());

		Timestamp timestamp = null;
		if(stopPoint.getCreationTime() != null)
			timestamp = new Timestamp(stopPoint.getCreationTime().getTime());
		ps.setTimestamp(3, timestamp);
		ps.setString(4, stopPoint.getCreatorId());
		StopArea stopArea = stopPoint.getContainedInStopArea();
		Long stopAreaId = null; 
		if(stopArea != null)
			stopAreaId = stopArea.getId();
		ps.setLong(5, stopAreaId);
		ps.setInt(6, stopPoint.getPosition());
	}
}
