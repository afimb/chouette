package fr.certu.chouette.jdbc.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.GroupOfLine;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTNetwork;

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
		String sql = sqlInsert;
		getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() 
		{			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException 
			{
				Line line = lines.get(i);

				ps.setLong(1, line.getId());
				PTNetwork ptNetwork = line.getPtNetwork();
				if(ptNetwork != null)
					ps.setLong(2, ptNetwork.getId());
				Company company = line.getCompany();
				if(company != null)
					ps.setLong(3, company.getId());
				ps.setString(4, line.getObjectId());
				ps.setInt(5, line.getObjectVersion());
				ps.setDate(6, (Date) line.getCreationTime());
				ps.setString(7, line.getCreatorId());
				ps.setString(8, line.getName());
				ps.setString(9, line.getNumber());
				ps.setString(10, line.getPublishedName());
				ps.setString(11, line.getTransportModeName().toString());
				ps.setString(12, line.getRegistrationNumber());
				ps.setString(13, line.getComment());
				GroupOfLine groupOfline = line.getGroupOfLine();
				if(groupOfline != null)
					ps.setLong(14, groupOfline.getId());
				ps.setBoolean(15, line.getMobilityRestrictedSuitable());
			}

			@Override
			public int getBatchSize() 
			{
				return lines.size();
			}
		});
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
