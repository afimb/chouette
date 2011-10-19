package fr.certu.chouette.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import fr.certu.chouette.model.neptune.StopPoint;

/**
 * 
 * @author mamadou keira
 * 
 */

@SuppressWarnings("unchecked")
public class StopPointJdbcDao extends AbstractJdbcDao<StopPoint> 
{
   private static final Logger logger = Logger.getLogger(StopPointJdbcDao.class);
   
   public Logger getLogger()
   {
      return logger;
   }

	@Override
	public List<StopPoint> getAll() 
	{
		String sql = sqlSelectAll;
		List<StopPoint> stopPoints = getJdbcTemplate().query(sql,
				new BeanPropertyRowMapper(StopPoint.class));

		return stopPoints;
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
		
		setId(ps,5,stopPoint.getContainedInStopArea());
		ps.setInt(6, stopPoint.getPosition());
		setId(ps,7,stopPoint.getRoute());
	}
}
