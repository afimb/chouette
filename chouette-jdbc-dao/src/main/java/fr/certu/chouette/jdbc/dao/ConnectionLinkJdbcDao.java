package fr.certu.chouette.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

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
		Long departureId = null, arrivalId = null;
		if(connectionLink.getStartOfLink() != null)
			departureId = connectionLink.getStartOfLink().getId();
		ps.setLong(8, departureId);
		
		if(connectionLink.getEndOfLink() != null)
			arrivalId = connectionLink.getEndOfLink().getId();
		ps.setLong(9, arrivalId);
		ps.setBoolean(10, connectionLink.isLiftAvailable());
		ps.setBoolean(11,connectionLink.isMobilityRestrictedSuitable());
		ps.setBoolean(12,connectionLink.isStairsAvailable());
		Time time = null;
		//TODO Complete implementation
		
	}
}
