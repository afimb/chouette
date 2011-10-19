package fr.certu.chouette.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import fr.certu.chouette.model.neptune.AccessLink;

/**
 * 
 * @author mamadou keira
 * 
 */

@SuppressWarnings("unchecked")
public class AccessLinkJdbcDao extends AbstractJdbcDao<AccessLink> 
{
   private static final Logger logger = Logger.getLogger(AccessLinkJdbcDao.class);
   
   public Logger getLogger()
   {
      return logger;
   }
   
	@Override
	public List<AccessLink> getAll() 
	{
		String sql = sqlSelectAll;
		List<AccessLink> accessLinks = getJdbcTemplate().query(sql,
				new BeanPropertyRowMapper(AccessLink.class));

		return accessLinks;
	}

	@Override
	protected void populateStatement(PreparedStatement ps, AccessLink accessLink)
	throws SQLException {
		ps.setString(1, accessLink.getObjectId());
		ps.setInt(2, accessLink.getObjectVersion());

		Timestamp timestamp = null;
		if(accessLink.getCreationTime() != null)
			timestamp = new Timestamp(accessLink.getCreationTime().getTime());
		ps.setTimestamp(3, timestamp);
		ps.setString(4, accessLink.getCreatorId());
		ps.setString(5, accessLink.getName());
		ps.setString(6, accessLink.getComment());
		ps.setBigDecimal(7, accessLink.getLinkDistance());
		ps.setBoolean(8, accessLink.isLiftAvailable());
		ps.setBoolean(9,accessLink.isMobilityRestrictedSuitable());
		ps.setBoolean(10,accessLink.isStairsAvailable());
		
		Time defaultduration = null,
			 frequenttravellerduration = null,
			 occasionaltravellerduration = null,
			 mobilityrestrictedtravellerduration = null;
		
		if(accessLink.getDefaultDuration() != null)
			defaultduration = new Time(accessLink.getDefaultDuration().getTime());
		
		if(accessLink.getFrequentTravellerDuration() != null)
			frequenttravellerduration = new Time(accessLink.getFrequentTravellerDuration().getTime());
		
		if(accessLink.getOccasionalTravellerDuration() != null)
			occasionaltravellerduration = new Time(accessLink.getOccasionalTravellerDuration().getTime());
		
		if(accessLink.getMobilityRestrictedTravellerDuration() != null)
			mobilityrestrictedtravellerduration = new Time(accessLink.getMobilityRestrictedTravellerDuration().getTime());
		
		ps.setTime(11, defaultduration);
		ps.setTime(12, frequenttravellerduration);
		ps.setTime(13, occasionaltravellerduration);
		ps.setTime(14, mobilityrestrictedtravellerduration);
		String linkType = null;
		if (accessLink.getLinkType() != null)
			linkType = accessLink.getLinkType().value();
		ps.setString(15, linkType);
		ps.setObject(16, (Integer)accessLink.getIntUserNeeds());
		
		String linkOrientation = null;
		if(accessLink.getLinkOrientation() != null)
			linkOrientation = accessLink.getLinkOrientation().value();
		ps.setString(17, linkOrientation);
		
		setId(ps,18,accessLink.getAccessPoint());
		setId(ps,19,accessLink.getStopArea());

	}


}
