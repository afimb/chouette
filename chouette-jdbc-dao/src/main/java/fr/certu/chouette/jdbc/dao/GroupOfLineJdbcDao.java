package fr.certu.chouette.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.GroupOfLine;
import fr.certu.chouette.model.neptune.Line;

/**
 * 
 * @author mamadou keira
 * 
 */

@SuppressWarnings("unchecked")
public class GroupOfLineJdbcDao extends AbstractJdbcDao<GroupOfLine> 
{
	@Override
	public List<GroupOfLine> getAll() 
	{
		String sql = sqlSelectAll;
		List<GroupOfLine> groupOfLines = getJdbcTemplate().query(sql,
				new BeanPropertyRowMapper(GroupOfLine.class));

		return groupOfLines;
	}

	@Override
	public void saveOrUpdateAll(final List<GroupOfLine> groupOfLines)
	{			
		final List<GroupOfLine> insertables = new ArrayList<GroupOfLine>();
		final List<GroupOfLine> updatables = new ArrayList<GroupOfLine>();

		dispatchObjects(groupOfLines, insertables, updatables);
		if(!insertables.isEmpty())
			toBatchUpdate(sqlInsert, insertables);
		if(!updatables.isEmpty())
			toBatchUpdate(sqlUpdate, updatables);
	}

	@Override
	public GroupOfLine getByObjectId(String objectId) 
	{
		String sql = sqlSelectByObjectId;		 
		GroupOfLine groupOfLine = (GroupOfLine)getJdbcTemplate().queryForObject(sql, 
				new Object[] {objectId}, 
				new BeanPropertyRowMapper(GroupOfLine.class));
		return groupOfLine;
	}

	@Override
	protected void setPreparedStatement(PreparedStatement ps, GroupOfLine groupOfLine)
	throws SQLException {
		ps.setString(1, groupOfLine.getObjectId());
		ps.setInt(2, groupOfLine.getObjectVersion());
		Timestamp timestamp = new Timestamp(groupOfLine.getCreationTime().getTime());
		ps.setTimestamp(3, timestamp);
		ps.setString(4, groupOfLine.getCreatorId());
		ps.setString(5, groupOfLine.getName());
		ps.setString(6, groupOfLine.getComment());
	}

	@Override
	public GroupOfLine get(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(GroupOfLine object) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(Long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeAll(Collection<GroupOfLine> objects) {
		// TODO Auto-generated method stub

	}

	@Override
	public int removeAll(Filter clause) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void update(GroupOfLine object) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<GroupOfLine> select(Filter clause) {
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
