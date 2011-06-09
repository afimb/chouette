package fr.certu.chouette.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

import fr.certu.chouette.filter.Filter;
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
	public void saveOrUpdateAll(final List<Route> routes)
	{			
		final List<Route> insertables = new ArrayList<Route>();
		final List<Route> updatables = new ArrayList<Route>();

		dispatchObjects(routes, insertables, updatables);
		if(!insertables.isEmpty())
			toBatchUpdate(sqlInsert, insertables);
		if(!updatables.isEmpty())
			toBatchUpdate(sqlUpdate, updatables);
	}

	@Override
	public Route getByObjectId(String objectId) 
	{
		String sql = sqlSelectByObjectId;		 
		Route route = (Route)getJdbcTemplate().queryForObject(sql, 
				new Object[] {objectId}, 
				new BeanPropertyRowMapper(Route.class));
		return route;
	}

	@Override
	protected void setPreparedStatement(PreparedStatement ps, Route route)
	throws SQLException {

	}

	@Override
	public Route get(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(Route object) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(Long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeAll(Collection<Route> objects) {
		// TODO Auto-generated method stub

	}

	@Override
	public int removeAll(Filter clause) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void update(Route object) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Route> select(Filter clause) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists(Long id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean exists(String objectId) {
		// TODO Auto-generated method stub
		return false;
	}

}
