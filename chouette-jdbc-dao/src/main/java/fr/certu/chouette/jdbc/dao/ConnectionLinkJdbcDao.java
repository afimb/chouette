package fr.certu.chouette.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import fr.certu.chouette.model.neptune.ConnectionLink;

/**
 * 
 * @author mamadou keira
 * 
 */

@SuppressWarnings("unchecked")
public class ConnectionLinkJdbcDao extends AbstractJdbcDao<ConnectionLink> 
{
   private static final Logger logger = Logger.getLogger(ConnectionLinkJdbcDao.class);
   
   public Logger getLogger()
   {
      return logger;
   }
	@Override
	public List<ConnectionLink> getAll() 
	{
		String sql = sqlSelectAll;
		List<ConnectionLink> connectionLinks = getJdbcTemplate().query(sql,
				new BeanPropertyRowMapper(ConnectionLink.class));

		return connectionLinks;
	}

	@Override
	protected void populateStatement(PreparedStatement ps, ConnectionLink connectionLink)
	throws SQLException {
		ps.setString(1, connectionLink.getObjectId());
		ps.setInt(2, connectionLink.getObjectVersion());

		Timestamp timestamp = null;
		if(connectionLink.getCreationTime() != null)
			timestamp = new Timestamp(connectionLink.getCreationTime().getTime());
		ps.setTimestamp(3, timestamp);
		ps.setString(4, connectionLink.getCreatorId());
		ps.setString(5, connectionLink.getName());
		ps.setString(6, connectionLink.getComment());
		ps.setBigDecimal(7, connectionLink.getLinkDistance());
		setId(ps,8,connectionLink.getStartOfLink());
		setId(ps,9,connectionLink.getEndOfLink());
		ps.setBoolean(10, connectionLink.isLiftAvailable());
		ps.setBoolean(11,connectionLink.isMobilityRestrictedSuitable());
		ps.setBoolean(12,connectionLink.isStairsAvailable());
		Time defaultduration = null,
		 	 frequenttravellerduration = null,
		 	 occasionaltravellerduration = null,
		 	 mobilityrestrictedtravellerduration = null;
	
	if(connectionLink.getDefaultDuration() != null)
		defaultduration = new Time(connectionLink.getDefaultDuration().getTime());
	
	if(connectionLink.getFrequentTravellerDuration() != null)
		frequenttravellerduration = new Time(connectionLink.getFrequentTravellerDuration().getTime());
	
	if(connectionLink.getOccasionalTravellerDuration() != null)
		occasionaltravellerduration = new Time(connectionLink.getOccasionalTravellerDuration().getTime());
	
	if(connectionLink.getMobilityRestrictedTravellerDuration() != null)
		mobilityrestrictedtravellerduration = new Time(connectionLink.getMobilityRestrictedTravellerDuration().getTime());
	
	ps.setTime(13, defaultduration);
	ps.setTime(14, frequenttravellerduration);
	ps.setTime(15, occasionaltravellerduration);
	ps.setTime(16, mobilityrestrictedtravellerduration);
	String linkType = null;
	if (connectionLink.getLinkType() != null)
		linkType = connectionLink.getLinkType().value();
	ps.setString(17, linkType);
	ps.setObject(18, (Integer)connectionLink.getIntUserNeeds());
	}
}
