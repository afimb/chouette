package fr.certu.chouette.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.InterruptibleBatchPreparedStatementSetter;
import org.springframework.jdbc.core.ParameterDisposer;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.Assert;

import fr.certu.chouette.dao.IJdbcDaoTemplate;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.PeerId;

/**
 * 
 * @author mamadou keira
 *
 * @param <T>
 */
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
	protected abstract void setPreparedStatement(PreparedStatement ps, T type) throws SQLException;

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
		int[] rows = getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() 
		{			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException 
			{
				T type = list.get(i);
				if(type != null)
					setPreparedStatement(ps, type);	
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
		
		final BatchPreparedStatementSetter pss = new BatchPreparedStatementSetter() 
		{			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException 
			{
				T type = list.get(i);
				if(type != null)
					setPreparedStatement(ps, type);	
			}
			@Override
			public int getBatchSize() 
			{
				return list.size();
			}
		};
		
		final KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
		
		int[] rows =  (int[]) getJdbcTemplate().execute(new ReturnKeysPreparedStatementCreator(sql), new PreparedStatementCallback() 
		 {
			public Object doInPreparedStatement(PreparedStatement ps) throws SQLException {
				try {
					int batchSize = pss.getBatchSize();
					InterruptibleBatchPreparedStatementSetter ipss =
							(pss instanceof InterruptibleBatchPreparedStatementSetter ?
							(InterruptibleBatchPreparedStatementSetter) pss : null);
					if (JdbcUtils.supportsBatchUpdates(ps.getConnection())) {
						for (int i = 0; i < batchSize; i++) {
							pss.setValues(ps, i);
							if (ipss != null && ipss.isBatchExhausted(i)) {
								break;
							}
							ps.addBatch();
						}
						int[] rowsAffected = ps.executeBatch();
						List generatedKeys = generatedKeyHolder.getKeyList();
						generatedKeys.clear();
						ResultSet keys = ps.getGeneratedKeys();
						if (keys != null) {
							try {
								RowMapper rowMapper = new ColumnMapRowMapper();
								RowMapperResultSetExtractor rse = new RowMapperResultSetExtractor(rowMapper, 1);
								generatedKeys.addAll((List) rse.extractData(keys));
							}
							finally {
								JdbcUtils.closeResultSet(keys);
							}
						}
						if (logger.isDebugEnabled()) {
							logger.debug("SQL update affected " + rowsAffected + " rows and returned " + generatedKeys.size() + " keys");
						}
						return rowsAffected;
					}
					else {
						List rowsAffected = new ArrayList();
						for (int i = 0; i < batchSize; i++) {
							pss.setValues(ps, i);
							if (ipss != null && ipss.isBatchExhausted(i)) {
								break;
							}
							rowsAffected.add(new Integer(ps.executeUpdate()));
						}
						int[] rowsAffectedArray = new int[rowsAffected.size()];
						for (int i = 0; i < rowsAffectedArray.length; i++) {
							rowsAffectedArray[i] = ((Integer) rowsAffected.get(i)).intValue();
						}
						return rowsAffectedArray;
					}
				}
				finally {
					if (pss instanceof ParameterDisposer) {
						((ParameterDisposer) pss).cleanupParameters();
					}
				}
			}
		});
		
		// TODO dépouillement à tester
		List keys = generatedKeyHolder.getKeyList();
		for (int i = 0; i < rows.length; i++) 
		{
			if (rows[i] ==  1)
			{
				Long key = (Long) keys.get(i);
				list.get(i).setId(key);
			}
		}
		return rows;
	}

	
	
	/**
	 * Simple adapter for PreparedStatementCreator, allowing to use a plain SQL statement.
	 */
	private static class ReturnKeysPreparedStatementCreator implements PreparedStatementCreator, SqlProvider {

		private final String sql;

		public ReturnKeysPreparedStatementCreator(String sql) 
		{
			Assert.notNull(sql, "SQL must not be null");
			this.sql = sql;
		}

		public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
			return con.prepareStatement(this.sql,PreparedStatement.RETURN_GENERATED_KEYS);
		}

		public String getSql() {
			return this.sql;
		}
	}

}
