package fr.certu.chouette.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import fr.certu.chouette.dao.IJdbcDaoTemplate;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.PeerId;

/**
 * 
 * @author mamadou keira
 *
 * @param <T>
 */
@SuppressWarnings("unchecked")
public abstract class AbstractJdbcDao<T extends NeptuneIdentifiedObject>
extends JdbcDaoSupport implements IJdbcDaoTemplate<T> 
{
	@Getter @Setter protected String sqlSelectAll;
	@Getter @Setter protected String sqlSelectByObjectId;
	@Getter @Setter protected String sqlSelectByObjectIdWithInClause;
	@Getter @Setter protected String sqlInsert;
	@Getter @Setter protected String sqlUpdate;
	@Getter @Setter protected String sqlDelete;

	/**
	 * Transform an array to sql IN clause
	 * @param myArray
	 * @return sql IN 
	 */
	protected static String arrayToSQLIn(String[] myArray) 
	{
		String result = "";
		for (int i=0;i<myArray.length; i++) 
		{
			if (i>0) result = result + ",";
			result = result + "'"+myArray[i]+"'";
		}
		return result; 
	}

	/**
	 * Execute the sql statement with IN clause
	 * @param objectids
	 * @return list of {@link PeerId}
	 */
	@SuppressWarnings({ "rawtypes" })
	public List<PeerId> get(List<String> objectids)
	{
		String[] myArray = objectids.toArray(new String[objectids.size()]);
		String sql = sqlSelectByObjectIdWithInClause.replaceAll("_OBJECTIDS_", arrayToSQLIn(myArray));

		List<PeerId> peerIds = new ArrayList<PeerId>();

		List<Map> rows = getJdbcTemplate().queryForList(sql);
		for (Map row : rows) 
		{
			PeerId peerId = new PeerId();
			peerId.setId((Long)(row.get("id")));
			peerId.setObjectid((String)row.get("objectid"));
			peerIds.add(peerId);
		}
		return peerIds;
	}

	/**
	 * An abstract method wich should be implemented in sub classes <br />
	 * It set the {@link PreparedStatement} by the {@link NeptuneIdentifiedObject}
	 * @param ps
	 * @param type
	 * @throws SQLException
	 */
	protected abstract void populateStatement(PreparedStatement ps, T type) throws SQLException;


	@Override
	public void saveOrUpdateAll(final List<T> objects)
	{			
		final List<T> insertables = new ArrayList<T>();
		final List<T> updatables = new ArrayList<T>();

		dispatchObjects(objects, insertables, updatables);
		if(!insertables.isEmpty())
			toBatchInsert(sqlInsert, insertables);
		if(!updatables.isEmpty())
		{
			if(sqlDelete != null)
			{
				toBatchDelete(sqlDelete, updatables);
				toBatchInsert(sqlInsert, updatables);
			}else
				toBatchUpdate(sqlUpdate, updatables);		
		}
	}

	/**
	 * This method allowed to dispatch a list of complete objects to two list in parameter
	 * @param list the complete list of objects
	 * @param insertables the real insertable objects
	 * @param updatables the real updatable or deletable objects
	 */
	protected void dispatchObjects(List<T> list,List<T> insertables, List<T> updatables)
	{
		Map<String,T> map = new HashMap<String, T>();
		for (T type : list) 
		{
			map.put(type.getObjectId(), type);
		}

		List<String> objectids = T.extractObjectIds(list);
		List<PeerId> peerIds = get(objectids);

		for (PeerId peerId : peerIds) 
		{
			T type = map.remove(peerId.getObjectid());
			type.setId(peerId.getId());
			updatables.add(type);
		}
		insertables.addAll(map.values());
	}

	/**
	 * Issue multiple update statements on a single PreparedStatement, using batchupdate method from Spring  
	 * @param sql request
	 * @param list of {@link NeptuneIdentifiedObject}
	 * @return an array of the number of rows affected by each statement
	 */
	protected int[] toBatchUpdate(String sql, final List<T> list)
	{
		List<String> objectids = T.extractObjectIds(list);
		String[] myArray = objectids.toArray(new String[objectids.size()]);
		String sqlUpdate = sql.replaceAll("_OBJECTIDS_", arrayToSQLIn(myArray));
		
		int[] rows = getJdbcTemplate().batchUpdate(sqlUpdate, new BatchPreparedStatementSetter() 
		{			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException 
			{
				T type = list.get(i);
				if(type != null)
					populateStatement(ps, type);	
			}
			@Override
			public int getBatchSize() 
			{
				return list.size();
			}
		});
		return rows;
	}

	/**
	 * Issue multiple update statements on a single PreparedStatement, using batchupdate method from Spring  
	 * @param sql request
	 * @param list of {@link NeptuneIdentifiedObject}
	 * @return an array of the number of rows affected by each statement
	 */
	protected int[] toBatchInsert(String sql, final List<T> list)
	{
		int[] rows = getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() 
		{			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException 
			{
				T type = list.get(i);
				if(type != null)
					populateStatement(ps, type);	
			}
			@Override
			public int getBatchSize() 
			{
				return list.size();
			}
		});	
		Map<String,T> map = new HashMap<String, T>();
		for (T type : list) 
		{
			map.put(type.getObjectId(), type);
		}

		List<String> objectids = T.extractObjectIds(list);
		List<PeerId> peerIds = get(objectids);

		for (PeerId peerId : peerIds) 
		{
			T type = map.remove(peerId.getObjectid());
			type.setId(peerId.getId());
		}
		return rows;
	}

	/**
	 * Execute a batch (multiple SQL delete) on a single JDBC Statement
	 * @param sql
	 * @param list of {@link NeptuneIdentifiedObject}
	 */
	protected int[] toBatchDelete(String sql, List<T> list)
	{
		List<String> objectids = T.extractObjectIds(list);
		String[] myArray = objectids.toArray(new String[objectids.size()]);
		String sqlDelete = sql.replaceAll("_OBJECTIDS_", arrayToSQLIn(myArray));

		int [] rows = getJdbcTemplate().batchUpdate(new String[]{sqlDelete});
		
		return rows;
	}

	@Override
	public List<T> getAll() {
		return null;
	}

	@Override
	public T get(Long id) {
		return null;
	}

	@Override
	public void save(T object) {

	}

	@Override
	public void remove(Long id) {

	}

	@Override
	public void removeAll(Collection<T> objects) {


	}

	@Override
	public int removeAll(Filter clause) {

		return 0;
	}

	@Override
	public void update(T object) {

	}


	@Override
	public List<T> select(Filter clause) {

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
