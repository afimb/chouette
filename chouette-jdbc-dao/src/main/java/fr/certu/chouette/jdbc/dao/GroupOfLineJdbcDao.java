package fr.certu.chouette.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import fr.certu.chouette.model.neptune.GroupOfLine;

/**
 * 
 * @author mamadou keira
 * 
 */

@SuppressWarnings("unchecked")
public class GroupOfLineJdbcDao extends AbstractJdbcDao<GroupOfLine> 
{
   private static final Logger logger = Logger.getLogger(GroupOfLineJdbcDao.class);
   
   public Logger getLogger()
   {
      return logger;
   }

	@Override
	public List<GroupOfLine> getAll() 
	{
		String sql = sqlSelectAll;
		List<GroupOfLine> groupOfLines = getJdbcTemplate().query(sql,
				new BeanPropertyRowMapper(GroupOfLine.class));

		return groupOfLines;
	}

	@Override
	protected void populateStatement(PreparedStatement ps, GroupOfLine groupOfLine)
	throws SQLException {
		ps.setString(1, groupOfLine.getObjectId());
		ps.setInt(2, groupOfLine.getObjectVersion());
		Timestamp timestamp = null;
		if(groupOfLine.getCreationTime() != null)
			timestamp = new Timestamp(groupOfLine.getCreationTime().getTime());
		ps.setTimestamp(3, timestamp);
		ps.setString(4, groupOfLine.getCreatorId());
		ps.setString(5, groupOfLine.getName());
		ps.setString(6, groupOfLine.getComment());
	}
}
