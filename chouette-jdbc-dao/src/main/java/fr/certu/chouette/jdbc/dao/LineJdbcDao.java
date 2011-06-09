package fr.certu.chouette.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.Line;

/**
 * 
 * @author mamadou keira
 * 
 */

@SuppressWarnings("unchecked")
public class LineJdbcDao extends AbstractJdbcDao<Line> 
{
	@Override
	public List<Line> getAll() 
	{
		String sql = sqlSelectAll;
		List<Line> lines = getJdbcTemplate().query(sql,
				new BeanPropertyRowMapper(Line.class));

		return lines;
	}

	@Override
	public void saveOrUpdateAll(final List<Line> lines)
	{			
		final List<Line> insertables = new ArrayList<Line>();
		final List<Line> updatables = new ArrayList<Line>();
		
		dispatchObjects(lines, insertables, updatables);
		if(!insertables.isEmpty())
			toBatchUpdate(sqlInsert, insertables);
		if(!updatables.isEmpty())
			toBatchUpdate(sqlUpdate, updatables);
	}

	@Override
	public Line getByObjectId(String objectId) 
	{
		String sql = sqlSelectByObjectId;		 
		Line line = (Line)getJdbcTemplate().queryForObject(sql, 
				new Object[] {objectId}, 
				new BeanPropertyRowMapper(Line.class));
		return line;
	}
	
	@Override
	protected void setPreparedStatement(PreparedStatement ps, Line line)
			throws SQLException {
		ps.setLong(1, line.getPtNetworkId());
		ps.setLong(2, line.getCompanyId());
		ps.setString(3, line.getObjectId());
		ps.setInt(4, line.getObjectVersion());
		ps.setTimestamp(5, new Timestamp(line.getCreationTime().getTime()));
		ps.setString(6, line.getCreatorId());
		ps.setString(7, line.getName());
		ps.setString(8, line.getNumber());
		ps.setString(9, line.getPublishedName());
		ps.setString(10, line.getTransportModeName().toString());
		ps.setString(11, line.getRegistrationNumber());
		ps.setString(12, line.getComment());
		ps.setLong(13, line.getGroupOfLineId());
		ps.setBoolean(14, line.getMobilityRestrictedSuitable());
	}
	
	@Override
	public Line get(Long id) {
		return null;
	}

	@Override
	public void save(Line object) {

	}

	@Override
	public void remove(Long id) {

	}

	@Override
	public void removeAll(Collection<Line> objects) {


	}

	@Override
	public int removeAll(Filter clause) {

		return 0;
	}

	@Override
	public void update(Line object) {

	}


	@Override
	public List<Line> select(Filter clause) {

		return null;
	}

	@Override
	public boolean exists(Long id) {

		return false;
	}

	@Override
	public boolean exists(String objectId) {

		return false;
	}
}
