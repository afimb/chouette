package fr.certu.chouette.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

import fr.certu.chouette.model.neptune.StopArea;

/**
 * 
 * @author mamadou keira
 * 
 */

@SuppressWarnings("unchecked")
public class StopAreaJdbcDao extends AbstractJdbcDao<StopArea> 
{
	@Override
	public List<StopArea> getAll() 
	{
		String sql = sqlSelectAll;
		List<StopArea> stopAreas = getJdbcTemplate().query(sql,
				new BeanPropertyRowMapper(StopArea.class));

		return stopAreas;
	}

	@Override
	public StopArea getByObjectId(String objectId) 
	{
		String sql = sqlSelectByObjectId;		 
		StopArea stopArea = (StopArea)getJdbcTemplate().queryForObject(sql, 
				new Object[] {objectId}, 
				new BeanPropertyRowMapper(StopArea.class));
		return stopArea;
	}

	@Override
	protected void populateStatement(PreparedStatement ps, StopArea stopArea)
	throws SQLException {
		
	}
}
