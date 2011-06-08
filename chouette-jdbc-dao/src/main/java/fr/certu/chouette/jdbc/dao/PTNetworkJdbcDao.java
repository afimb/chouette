package fr.certu.chouette.jdbc.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.PTNetwork;

/**
 * 
 * @author mamadou keira
 * 
 */

public class PTNetworkJdbcDao extends AbstractJdbcDao<PTNetwork> 
{

	@Override
	public void saveOrUpdateAll(final List<PTNetwork> ptNetworks)
	{
		String sql = sqlInsert;
		getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() 
		{			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException 
			{
				PTNetwork network = ptNetworks.get(i);
				ps.setLong(1, network.getId());
				ps.setString(2, network.getObjectId());
				ps.setInt(3, network.getObjectVersion());
				ps.setDate(4, (Date) network.getCreationTime());
				ps.setString(5, network.getCreatorId());
				ps.setDate(6, (Date) network.getVersionDate());
				ps.setString(7, network.getDescription());
				ps.setString(8, network.getName());
				ps.setString(9, network.getRegistrationNumber());
				ps.setString(10, network.getSourceName());
				ps.setString(11, network.getSourceIdentifier());
				ps.setString(12, network.getComment());				
			}

			@Override
			public int getBatchSize() 
			{
				return ptNetworks.size();
			}
		});
	}
	
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
	public List<PTNetwork> getAll() {
		return null;
	}

	@Override
	public PTNetwork get(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(PTNetwork object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(Long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeAll(Collection<PTNetwork> objects) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int removeAll(Filter clause) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void update(PTNetwork object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<PTNetwork> select(Filter clause) {
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
