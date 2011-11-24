package fr.certu.chouette.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import fr.certu.chouette.dao.IDaoTemplate;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.jdbc.exception.JdbcDaoException;
import fr.certu.chouette.jdbc.exception.JdbcDaoExceptionCode;
import fr.certu.chouette.jdbc.exception.JdbcDaoRuntimeException;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.NeptuneObject;
import fr.certu.chouette.model.neptune.PeerId;

/**
 * 
 * @author mamadou keira
 * 
 * @param <T>
 */
@SuppressWarnings("unchecked")
public abstract class AbstractJdbcDao<T extends NeptuneIdentifiedObject> extends JdbcDaoSupport implements
      IDaoTemplate<T>
{
   @Getter
   @Setter
   protected String                           sqlSelectAll;
   @Getter
   @Setter
   protected String                           sqlSelectByObjectId;
   @Getter
   @Setter
   protected String                           sqlSelectByObjectIdWithInClause;
   @Getter
   @Setter
   protected String                           sqlInsert;
   @Getter
   @Setter
   protected String                           sqlUpdate;
   @Getter
   @Setter
   protected String                           sqlDelete;
   @Getter
   @Setter
   protected Map<String, Map<String, String>> collectionAttributes;
   @Getter
   @Setter
   protected String                           sqlPurge;

   protected abstract Logger getLogger();

   /**
    * Transform an array to sql IN clause
    * 
    * @param myArray
    * @return sql IN
    */
   protected static String arrayToSQLIn(String[] myArray)
   {
      String result = "";
      for (int i = 0; i < myArray.length; i++)
      {
         if (i > 0)
            result = result + ",";
         result = result + "'" + myArray[i] + "'";
      }
      return result;
   }

   /**
    * Transform an array to sql IN clause
    * 
    * @param myArray
    * @return sql IN
    */
   protected static String arrayToSQLIn(Long[] myArray)
   {
      String result = "";
      for (int i = 0; i < myArray.length; i++)
      {
         if (i > 0)
            result = result + ",";
         result = result + myArray[i];
      }
      return result;
   }

   /**
    * Execute the sql statement with IN clause
    * 
    * @param objectids
    * @return list of {@link PeerId}
    * @throws JdbcDaoException
    */
   @SuppressWarnings({ "rawtypes" })
   public List<PeerId> get(List<String> objectids)
   {
      if (sqlSelectByObjectIdWithInClause == null)
         throw new JdbcDaoRuntimeException(JdbcDaoExceptionCode.NO_SQL_REQUEST_AVALAIBLE,
               "implements sqlSelectByObjectIdWithInClause request statement in xml file :"
                     + objectids.get(0).split(":")[1] + "JdbcDaoConext.xml");

      String[] myArray = objectids.toArray(new String[objectids.size()]);
      String sql = sqlSelectByObjectIdWithInClause.replaceAll("_OBJECTIDS_", arrayToSQLIn(myArray));

      List<PeerId> peerIds = new ArrayList<PeerId>();

      List<Map> rows = getJdbcTemplate().queryForList(sql);
      for (Map row : rows)
      {
         PeerId peerId = new PeerId();
         peerId.setId((Long) (row.get("id")));
         peerId.setObjectid((String) row.get("objectid"));
         peerIds.add(peerId);
      }
      return peerIds;
   }

   /**
    * An abstract method which should be implemented in sub classes <br />
    * It populate the {@link PreparedStatement} by the
    * {@link NeptuneIdentifiedObject}
    * 
    * @param ps
    * @param type
    * @throws SQLException
    */
   protected abstract void populateStatement(PreparedStatement ps, T type) throws SQLException;

   @Override
   public void saveOrUpdateAll(final List<T> objects)
   {
      if (objects.isEmpty())
      {
         getLogger().debug("no object to save");
         return;
      }
      final List<T> insertables = new ArrayList<T>();
      final List<T> updatables = new ArrayList<T>();

      dispatchObjects(objects, insertables, updatables);
      if (!insertables.isEmpty())
         toBatchInsert(sqlInsert, insertables);
      if (!updatables.isEmpty())
      {
         if (sqlUpdate != null)
            toBatchUpdate(sqlUpdate, updatables);

         else if (sqlDelete != null)
         {
            toBatchDelete(sqlDelete, updatables);
            toBatchInsert(sqlInsert, updatables);
         }
         else
         {
            throw new JdbcDaoRuntimeException(JdbcDaoExceptionCode.NO_SQL_REQUEST_AVALAIBLE,
                  "implements sqlUpdate AND/OR sqlDelete request statement in xml file :"
                        + objects.get(0).getClass().getName() + "JdbcDaoConext.xml");
         }

      }
      afterSaveOrUpdateAllProcessing(objects);
   }

   /**
    * may be overrided for specific post processing actions
    * 
    * @param objects
    */
   protected void afterSaveOrUpdateAllProcessing(List<T> objects)
   {
      // nothing to do
   }

   /**
    * This method allowed to dispatch a list of complete objects to two list in
    * parameter
    * 
    * @param list
    *           the complete list of objects
    * @param insertables
    *           the real insertable objects
    * @param updatables
    *           the real updatable or deletable objects
    * @throws JdbcDaoException
    */
   protected void dispatchObjects(List<T> list, List<T> insertables, List<T> updatables)
   {
      Map<String, T> map = new HashMap<String, T>();
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
    * Issue multiple update statements on a single PreparedStatement, using
    * batchupdate method from Spring
    * 
    * @param sql
    *           request
    * @param list
    *           of {@link NeptuneIdentifiedObject}
    * @return an array of the number of rows affected by each statement
    * @throws JdbcDaoException
    */
   protected int[] toBatchUpdate(String sql, final List<T> list)
   {
      if (sql == null)
         throw new JdbcDaoRuntimeException(JdbcDaoExceptionCode.NO_SQL_REQUEST_AVALAIBLE,
               "implements sqlUpdate request statement in xml file :" + list.get(0).getClass().getName()
                     + "JdbcDaoConext.xml");

      int[] rows = getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter()
      {
         @Override
         public void setValues(PreparedStatement ps, int i) throws SQLException
         {
            T type = list.get(i);
            if (type != null)
               populateStatement(ps, type);
            int rank = ps.getParameterMetaData().getParameterCount();
            ps.setString(rank, type.getObjectId());
         }

         @Override
         public int getBatchSize()
         {
            return list.size();
         }
      });

      // remove from secondary tables for multiple occurence attributes
      toBatchDeleteCollectionAttributes(list);
      // insert in secondary tables for multiple occurence attributes
      toBatchInsertCollectionAttributes(list);

      return rows;
   }

   /**
    * @param list
    * @throws JdbcDaoException
    */
   private void toBatchDeleteCollectionAttributes(List<T> list)
   {
      if (collectionAttributes != null && !collectionAttributes.isEmpty())
      {
         for (String attributeKey : collectionAttributes.keySet())
         {
            toBatchDeleteCollectionAttribute(list, attributeKey, collectionAttributes.get(attributeKey));
         }
      }

   }

   /**
    * @param list
    * @param attributeKey
    * @param map
    * @throws JdbcDaoException
    */
   private void toBatchDeleteCollectionAttribute(List<T> list, String attributeKey, Map<String, String> map)
   {
      String sql = map.get("sqlDelete");
      if (sql == null)
         throw new JdbcDaoRuntimeException(JdbcDaoExceptionCode.NO_SQL_SUBREQUEST_AVALAIBLE,
               "implements sqlDelete request statement for " + attributeKey + " in xml file :"
                     + list.get(0).getClass().getName() + "JdbcDaoConext.xml");

      List<Long> ids = T.extractIds(list);
      Long[] myArray = ids.toArray(new Long[ids.size()]);
      String sqlDelete = sql.replaceAll("_IDS_", arrayToSQLIn(myArray));

      getJdbcTemplate().batchUpdate(new String[] { sqlDelete });

   }

   /**
    * Issue multiple update statements on a single PreparedStatement, using
    * batchupdate method from Spring
    * 
    * @param sql
    *           request
    * @param list
    *           of {@link NeptuneIdentifiedObject}
    * @return an array of the number of rows affected by each statement
    * @throws JdbcDaoException
    */
   protected int[] toBatchInsert(String sql, final List<T> list)
   {
      if (sql == null)
         throw new JdbcDaoRuntimeException(JdbcDaoExceptionCode.NO_SQL_REQUEST_AVALAIBLE,
               "implements sqlInsert request statement in xml file :" + list.get(0).getClass().getName()
                     + "JdbcDaoConext.xml");

      int[] rows = getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter()
      {
         @Override
         public void setValues(PreparedStatement ps, int i) throws SQLException
         {
            T type = list.get(i);
            if (type != null)
               populateStatement(ps, type);
         }

         @Override
         public int getBatchSize()
         {
            return list.size();
         }
      });
      Map<String, T> map = new HashMap<String, T>();
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

      // insert in secondary tables for multiple occurrence attributes
      toBatchInsertCollectionAttributes(list);

      return rows;
   }

   /**
    * launch insert for each attribute describe in collectionAttributes
    * 
    * @param list
    * @throws JdbcDaoException
    */
   private void toBatchInsertCollectionAttributes(List<T> list)
   {
      if (collectionAttributes != null && !collectionAttributes.isEmpty())
      {
         for (String attributeKey : collectionAttributes.keySet())
         {
            toBatchInsertCollectionAttribute(list, attributeKey, collectionAttributes.get(attributeKey));
         }
      }

   }

   /**
    * launch insert for one attribute describe in collectionAttributes
    * 
    * @param list
    * @param attributeKey
    * @param map
    * @throws JdbcDaoException
    */
   private void toBatchInsertCollectionAttribute(List<T> list, final String attributeKey, Map<String, String> map)
   {
      String sql = map.get("sqlInsert");
      if (sql == null)
         throw new JdbcDaoRuntimeException(JdbcDaoExceptionCode.NO_SQL_SUBREQUEST_AVALAIBLE,
               "implements sqlInsert request statement for " + attributeKey + " in xml file :"
                     + list.get(0).getClass().getName() + "JdbcDaoConext.xml");

      final List<Object> attributes = new ArrayList<Object>();
      for (T item : list)
      {
         attributes.addAll(getAttributeValues(attributeKey, item));
      }
      getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter()
      {
         @Override
         public void setValues(PreparedStatement ps, int i) throws SQLException
         {
            Object attribute = attributes.get(i);
            if (attribute != null)
               populateAttributeStatement(attributeKey, ps, attribute);
         }

         @Override
         public int getBatchSize()
         {

            return attributes.size();
         }
      });

   }

   /**
    * extract values of attributes for populateAttributeStatement
    * 
    * @param attributeKey
    * @param item
    * @return
    * @throws JdbcDaoException
    */
   protected Collection<? extends Object> getAttributeValues(String attributeKey, T item)
   {
      throw new JdbcDaoRuntimeException(JdbcDaoExceptionCode.NO_SQL_REQUEST_AVALAIBLE,
            "getAttributeValues is not implemented for " + this.getClass().getName());
   }

   /**
    * populate statement for one attribute value produced by getAttributeValues
    * 
    * @param attributeKey
    * @param ps
    * @param attribute
    * @throws SQLException
    */
   protected void populateAttributeStatement(String attributeKey, PreparedStatement ps, Object attribute)
         throws SQLException
   {
      throw new SQLException("populateAttributeStatement is not implemented for " + this.getClass().getName());
   }

   /**
    * Execute a batch (multiple SQL delete) on a single JDBC Statement
    * 
    * @param sql
    * @param list
    *           of {@link NeptuneIdentifiedObject}
    * @throws JdbcDaoException
    */
   protected int[] toBatchDelete(String sql, List<T> list)
   {
      if (sql == null)
         throw new JdbcDaoRuntimeException(JdbcDaoExceptionCode.NO_SQL_REQUEST_AVALAIBLE,
               "implements sqlDelete request statement in xml file :" + list.get(0).getClass().getName()
                     + "JdbcDaoConext.xml");

      List<String> objectids = T.extractObjectIds(list);
      String[] myArray = objectids.toArray(new String[objectids.size()]);
      String sqlDelete = sql.replaceAll("_OBJECTIDS_", arrayToSQLIn(myArray));

      int[] rows = getJdbcTemplate().batchUpdate(new String[] { sqlDelete });

      return rows;
   }

   /*
    * (non-Javadoc)
    * 
    * @see fr.certu.chouette.dao.IDaoTemplate#getAll()
    */
   @Override
   public List<T> getAll()
   {
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see fr.certu.chouette.dao.IDaoTemplate#removeAll(java.util.List)
    */
   @Override
   public void removeAll(List<T> objects)
   {
      toBatchDelete(sqlDelete, objects);

   }

   /**
    * add object database id in prepared statement
    * 
    * @param ps
    *           prepared statement
    * @param pos
    *           position in statement
    * @param object
    *           object
    * @throws SQLException
    */
   protected void setId(PreparedStatement ps, int pos, NeptuneObject object) throws SQLException
   {
      if (object == null)
         ps.setNull(pos, Types.BIGINT);
      else if (object.getId() == null)
      {
         getLogger().error("refer object has not id set " + object.toString("", 0));
         throw new NullPointerException("refer object has not id set");
      }
      else
         ps.setLong(pos, object.getId());
   }

   /*
    * (non-Javadoc)
    * 
    * @see fr.certu.chouette.dao.IDaoTemplate#purge()
    */
   @Override
   public int purge()
   {
      if (sqlPurge == null)
      {
         return 0;
      }
      return getJdbcTemplate().update(sqlPurge);
   }

   /*
    * (non-Javadoc)
    * 
    * @see fr.certu.chouette.dao.IDaoTemplate#get(java.lang.Long)
    */
   @Override
   public T get(Long id)
   {
      throw new JdbcDaoRuntimeException(JdbcDaoExceptionCode.NOT_YET_IMPLEMENTED, "get");
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.dao.IDaoTemplate#save(fr.certu.chouette.model.neptune
    * .NeptuneObject)
    */
   @Override
   public void save(T object)
   {
      throw new JdbcDaoRuntimeException(JdbcDaoExceptionCode.NOT_YET_IMPLEMENTED, "save");
   }

   /*
    * (non-Javadoc)
    * 
    * @see fr.certu.chouette.dao.IDaoTemplate#remove(java.lang.Long)
    */
   @Override
   public void remove(Long id)
   {
      throw new JdbcDaoRuntimeException(JdbcDaoExceptionCode.NOT_YET_IMPLEMENTED, "remove");

   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.dao.IDaoTemplate#removeAll(fr.certu.chouette.filter.
    * Filter)
    */
   @Override
   public int removeAll(Filter clause)
   {
      throw new JdbcDaoRuntimeException(JdbcDaoExceptionCode.NOT_YET_IMPLEMENTED, "removeAll");
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.dao.IDaoTemplate#update(fr.certu.chouette.model.neptune
    * .NeptuneObject)
    */
   @Override
   public void update(T object)
   {
      throw new JdbcDaoRuntimeException(JdbcDaoExceptionCode.NOT_YET_IMPLEMENTED, "update");

   }

   /*
    * (non-Javadoc)
    * 
    * @see fr.certu.chouette.dao.IDaoTemplate#getByObjectId(java.lang.String)
    */
   @Override
   public T getByObjectId(String objectId)
   {
      throw new JdbcDaoRuntimeException(JdbcDaoExceptionCode.NOT_YET_IMPLEMENTED, "getByObjectId");
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.dao.IDaoTemplate#select(fr.certu.chouette.filter.Filter)
    */
   @Override
   public List<T> select(Filter clause)
   {
      throw new JdbcDaoRuntimeException(JdbcDaoExceptionCode.NOT_YET_IMPLEMENTED, "select");
   }

   /*
    * (non-Javadoc)
    * 
    * @see fr.certu.chouette.dao.IDaoTemplate#exists(java.lang.Long)
    */
   @Override
   public boolean exists(Long id)
   {
      throw new JdbcDaoRuntimeException(JdbcDaoExceptionCode.NOT_YET_IMPLEMENTED, "exists");
   }

   /*
    * (non-Javadoc)
    * 
    * @see fr.certu.chouette.dao.IDaoTemplate#exists(java.lang.String)
    */
   @Override
   public boolean exists(String objectId)
   {
      throw new JdbcDaoRuntimeException(JdbcDaoExceptionCode.NOT_YET_IMPLEMENTED, "exists");
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.dao.IDaoTemplate#count(fr.certu.chouette.filter.Filter)
    */
   @Override
   public long count(Filter clause)
   {
      throw new JdbcDaoRuntimeException(JdbcDaoExceptionCode.NOT_YET_IMPLEMENTED, "count");
   }

   /*
    * (non-Javadoc)
    * 
    * @see fr.certu.chouette.dao.IDaoTemplate#detach(java.util.List)
    */
   @Override
   public void detach(List<T> beans)
   {

   }

}
