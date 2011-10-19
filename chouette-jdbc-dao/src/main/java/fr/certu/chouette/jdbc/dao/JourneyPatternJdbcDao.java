package fr.certu.chouette.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.StopPoint;

/**
 * 
 * @author mamadou keira
 * 
 */
public class JourneyPatternJdbcDao extends AbstractJdbcDao<JourneyPattern> 
{
   private static final Logger logger = Logger.getLogger(JourneyPatternJdbcDao.class);
   
   public Logger getLogger()
   {
      return logger;
   }

	@Override
	protected void populateStatement(PreparedStatement ps, JourneyPattern journeyPattern)
	throws SQLException {
		ps.setString(1, journeyPattern.getObjectId());
		ps.setInt(2, journeyPattern.getObjectVersion());
		Timestamp timestamp = null;
		if(journeyPattern.getCreationTime() != null)
			timestamp = new Timestamp(journeyPattern.getCreationTime().getTime());
		ps.setTimestamp(3, timestamp);
		ps.setString(4, journeyPattern.getCreatorId());
		ps.setString(5, journeyPattern.getName());
		ps.setString(6, journeyPattern.getComment());
		ps.setString(7, journeyPattern.getRegistrationNumber());
		ps.setString(8, journeyPattern.getPublishedName());
		setId(ps,9,journeyPattern.getRoute());
	}

	@Override
	protected Collection<? extends Object> getAttributeValues(String attributeKey, JourneyPattern item) 
	{
		if (attributeKey.equals("stoppoint"))
		{
			List<JdbcStoppoint> jpoints = new ArrayList<JourneyPatternJdbcDao.JdbcStoppoint>();
			if (item.getStopPoints() != null)
			{
				for (StopPoint point : item.getStopPoints()) 
				{
					JdbcStoppoint jpoint = new JdbcStoppoint();
					jpoint.journeypatternId=item.getId();
					jpoint.stopPointId = point.getId();
					jpoints.add(jpoint);
				}
			}
			return jpoints;
		}
		return super.getAttributeValues(attributeKey, item);
	}

	@Override
	protected void populateAttributeStatement(String attributeKey,PreparedStatement ps, Object attribute) 
	throws SQLException 
	{
		if (attributeKey.equals("stoppoint"))
		{
			JdbcStoppoint jpoint = (JdbcStoppoint) attribute;
			ps.setLong(1,jpoint.journeypatternId);
			ps.setLong(2,jpoint.stopPointId);
			return;
		}		
		super.populateAttributeStatement(attributeKey, ps, attribute);
	}

	class JdbcStoppoint
	{
		Long journeypatternId;
		Long stopPointId;
	}
}
