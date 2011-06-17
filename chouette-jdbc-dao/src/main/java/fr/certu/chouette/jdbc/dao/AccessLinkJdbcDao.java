package fr.certu.chouette.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

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
		Long departureId = null, arrivalId = null;
		if(accessLink.getStopArea() != null)
			departureId = accessLink.getStopArea().getId();
		ps.setLong(8, departureId);
		
		if(accessLink.getAccessPoint() != null)
			arrivalId = accessLink.getAccessPoint().getId();
		ps.setLong(9, arrivalId);
		ps.setBoolean(10, accessLink.isLiftAvailable());
		ps.setBoolean(11,accessLink.isMobilityRestrictedSuitable());
		ps.setBoolean(12,accessLink.isStairsAvailable());
		Time time = null;
		//TODO Complete implementation
		
	}
}
