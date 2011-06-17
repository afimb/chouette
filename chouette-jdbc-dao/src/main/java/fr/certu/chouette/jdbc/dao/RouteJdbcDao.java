package fr.certu.chouette.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

import fr.certu.chouette.model.neptune.Route;

/**
 * 
 * @author mamadou keira
 * 
 */

@SuppressWarnings("unchecked")
public class RouteJdbcDao extends AbstractJdbcDao<Route> 
{
	@Override
	public List<Route> getAll() 
	{
		String sql = sqlSelectAll;
		List<Route> routes = getJdbcTemplate().query(sql,
				new BeanPropertyRowMapper(Route.class));

		return routes;
	}

	@Override
	protected void populateStatement(PreparedStatement ps, Route route)
	throws SQLException {
		ps.setString(1, route.getObjectId());
		ps.setInt(2, route.getObjectVersion());
		Timestamp timestamp = null;
		if(route.getCreationTime() != null)
			timestamp = new Timestamp(route.getCreationTime().getTime());
		ps.setTimestamp(3, timestamp);
		ps.setString(4, route.getCreatorId());
		ps.setString(5, route.getName());
		ps.setLong(6, route.getOppositeRouteId());
		Long lineId = null;
		if(route.getLine() != null)
			lineId = route.getLine().getId();
		ps.setLong(7, lineId);
		ps.setString(8, route.getPublishedName());
		ps.setString(9, route.getNumber());
		ps.setString(10, route.getDirection().value());
		ps.setString(11, route.getComment());
		ps.setString(12, route.getWayBack());

	}
}
