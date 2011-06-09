package fr.certu.chouette.jdbc.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
		final List<PTNetwork> insertables = new ArrayList<PTNetwork>();
		final List<PTNetwork> updatables = new ArrayList<PTNetwork>();

		dispatchObjects(ptNetworks, insertables, updatables);
		if(!insertables.isEmpty())
			toBatchUpdate(sqlInsert, insertables);
		if(!updatables.isEmpty())
			toBatchUpdate(sqlUpdate, updatables);
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
	protected void setPreparedStatement(PreparedStatement ps, PTNetwork network)
	throws SQLException {
		ps.setString(1, network.getObjectId());
		ps.setInt(2, network.getObjectVersion());
		Timestamp timestamp = null;
		if(network.getCreationTime() != null)
			timestamp = new Timestamp(network.getCreationTime().getTime());
		ps.setTimestamp(5, timestamp);
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
