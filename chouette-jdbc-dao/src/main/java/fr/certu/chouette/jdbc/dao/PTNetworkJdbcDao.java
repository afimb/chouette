package fr.certu.chouette.jdbc.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

import fr.certu.chouette.model.neptune.PTNetwork;

/**
 * 
 * @author mamadou keira
 * 
 */

public class PTNetworkJdbcDao extends AbstractJdbcDao<PTNetwork> 
{
	@Override
	public PTNetwork getByObjectId(String objectId) 
	{
		String sql = sqlSelectByObjectId;		 
		PTNetwork network = (PTNetwork)getJdbcTemplate().queryForObject(sql, 
				new Object[] {objectId}, 
				new BeanPropertyRowMapper(PTNetwork.class));
		return network;
	}

	@Override
	protected void populateStatement(PreparedStatement ps, PTNetwork network)
	throws SQLException {
		ps.setString(1, network.getObjectId());
		ps.setInt(2, network.getObjectVersion());
		Timestamp timestamp = null;
		if(network.getCreationTime() != null)
			timestamp = new Timestamp(network.getCreationTime().getTime());
		ps.setTimestamp(3, timestamp);
		ps.setString(4, network.getCreatorId());
		Date date = null;
		if(network.getVersionDate() != null)
			date = new Date(network.getVersionDate().getTime());
		ps.setDate(5,date);
		ps.setString(6, network.getDescription());
		ps.setString(7, network.getName());
		ps.setString(8, network.getRegistrationNumber());
		ps.setString(9, network.getSourceName());
		ps.setString(10, network.getSourceIdentifier());
		ps.setString(11, network.getComment());
	}
}
