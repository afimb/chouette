package fr.certu.chouette.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.Line;
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
	public void saveOrUpdateAll(final List<StopArea> stopAreas)
	{			
		final List<StopArea> insertables = new ArrayList<StopArea>();
		final List<StopArea> updatables = new ArrayList<StopArea>();

		dispatchObjects(stopAreas, insertables, updatables);
		if(!insertables.isEmpty())
			toBatchUpdate(sqlInsert, insertables);
		if(!updatables.isEmpty())
			toBatchUpdate(sqlUpdate, updatables);
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
	protected void setPreparedStatement(PreparedStatement ps, StopArea stopArea)
	throws SQLException {

	}

	@Override
	public StopArea get(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(StopArea object) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(Long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeAll(Collection<StopArea> objects) {
		// TODO Auto-generated method stub

	}

	@Override
	public int removeAll(Filter clause) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void update(StopArea object) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<StopArea> select(Filter clause) {
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
