package fr.certu.chouette.jdbc.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.type.Address;
import fr.certu.chouette.model.neptune.type.ProjectedPoint;

/**
 * manage mass storage for StopAreas
 * 
 */

@SuppressWarnings("unchecked")
public class StopAreaJdbcDao extends AbstractJdbcDao<StopArea>
{
   private static final Logger logger = Logger.getLogger(StopAreaJdbcDao.class);
   
   public Logger getLogger()
   {
      return logger;
   }

   /**
    * first SQL purge request : remove every physical stops without StopPoint
    */
   @Getter
   @Setter
   private String              sqlPurge1;
   /**
    * second SQL purge request : remove every commercial stops without child
    */
   @Getter
   @Setter
   private String              sqlPurge2;
   /**
    * third SQL purge request : remove every stopplace stops without child
    */
   @Getter
   @Setter
   private String              sqlPurge3;
   /**
    * fourth SQL purge request : remove every restriction constraints without
    * dependency
    */
   @Getter
   @Setter
   private String              sqlPurge4;

   /*
    * (non-Javadoc)
    * 
    * @see fr.certu.chouette.jdbc.dao.AbstractJdbcDao#getAll()
    */
   @Override
   public List<StopArea> getAll()
   {
      String sql = sqlSelectAll;
      List<StopArea> stopAreas = getJdbcTemplate().query(sql, new BeanPropertyRowMapper(StopArea.class));

      return stopAreas;
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.jdbc.dao.AbstractJdbcDao#populateStatement(java.sql.
    * PreparedStatement,
    * fr.certu.chouette.model.neptune.NeptuneIdentifiedObject)
    */
   @SuppressWarnings("deprecation")
   @Override
   protected void populateStatement(PreparedStatement ps, StopArea stopArea) throws SQLException
   {
      if (stopArea.getParentId() == null)
      {
         ps.setNull(1, Types.BIGINT);
      }
      else
      {
         ps.setLong(1, stopArea.getParentId());
      }
      ps.setString(2, stopArea.getObjectId());
      ps.setInt(3, stopArea.getObjectVersion());
      Timestamp timestamp = null;
      if (stopArea.getCreationTime() != null)
         timestamp = new Timestamp(stopArea.getCreationTime().getTime());
      ps.setTimestamp(4, timestamp);
      ps.setString(5, stopArea.getCreatorId());
      ps.setString(6, stopArea.getName());
      ps.setString(7, stopArea.getComment());
      String areaType = null;
      if (stopArea.getAreaType() != null)
         areaType = stopArea.getAreaType().value();
      ps.setString(8, areaType);
      ps.setString(9, stopArea.getRegistrationNumber());
      ps.setString(10, stopArea.getNearestTopicName());
      if (stopArea.getFareCode() != null)
         ps.setInt(11, stopArea.getFareCode());
      else
         ps.setNull(11, Types.INTEGER);

      AreaCentroid areaCentroid = stopArea.getAreaCentroid();
      BigDecimal longitude = null, latitude = null, x = null, y = null;

      String longLatType = null, projectionType = null, countryCode = null, streetName = null;

      if (areaCentroid != null)
      {
         longitude = areaCentroid.getLongitude();
         latitude = areaCentroid.getLatitude();
         if (areaCentroid.getLongLatType() != null)
            longLatType = areaCentroid.getLongLatType().value();

         ProjectedPoint projectedPoint = areaCentroid.getProjectedPoint();
         if (projectedPoint != null)
         {
            x = projectedPoint.getX();
            y = projectedPoint.getY();
            projectionType = projectedPoint.getProjectionType();
         }

         Address address = areaCentroid.getAddress();
         if (address != null)
         {
            countryCode = address.getCountryCode();
            streetName = address.getStreetName();
         }
      }
      ps.setBigDecimal(12, longitude);
      ps.setBigDecimal(13, latitude);
      ps.setString(14, longLatType);
      ps.setBigDecimal(15, x);
      ps.setBigDecimal(16, y);
      ps.setString(17, projectionType);
      ps.setString(18, countryCode);
      ps.setString(19, streetName);

   }

   /*
    * (non-Javadoc)
    * 
    * @see fr.certu.chouette.jdbc.dao.AbstractJdbcDao#purge()
    */
   @Override
   public synchronized int purge()
   {
      int count = 0;
      setSqlPurge(sqlPurge1);
      count += super.purge();
      setSqlPurge(sqlPurge2);
      count += super.purge();
      setSqlPurge(sqlPurge3);
      count += super.purge();
      setSqlPurge(sqlPurge4);
      count += super.purge();
      return count;
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.jdbc.dao.AbstractJdbcDao#populateAttributeStatement(
    * java.lang.String, java.sql.PreparedStatement, java.lang.Object)
    */
   @Override
   protected void populateAttributeStatement(String attributeKey, PreparedStatement ps, Object attribute)
         throws SQLException
   {

      if (attributeKey.equals("stopAreaStopArea"))
      {
         JdbcParentChild child = (JdbcParentChild) attribute;
         logger.debug("save relation parentId = " + child.parentId + ", childId = " + child.childId);
         ps.setLong(1, child.parentId);
         ps.setLong(2, child.childId);

         return;
      }
      if (attributeKey.equals("routingconstraints"))
      {
         JdbcRoutingConstraint routing = (JdbcRoutingConstraint) attribute;
         logger.debug("save relation line = " + routing.lineId + ", stopArea = " + routing.stopAreaId);
         ps.setLong(1, routing.lineId);
         ps.setLong(2, routing.stopAreaId);

         return;
      }

      super.populateAttributeStatement(attributeKey, ps, attribute);

   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.jdbc.dao.AbstractJdbcDao#getAttributeValues(java.lang
    * .String, fr.certu.chouette.model.neptune.NeptuneIdentifiedObject)
    */
   @Override
   protected Collection<? extends Object> getAttributeValues(String attributeKey, StopArea item)
   {
      if (attributeKey.equals("stopAreaStopArea"))
      {
         Collection<JdbcParentChild> parentChilds = new ArrayList<JdbcParentChild>();
         if (item.getParents() != null)
         {
            for (StopArea parent : item.getParents())
            {
               JdbcParentChild object = new JdbcParentChild();
               object.parentId = parent.getId();
               object.childId = item.getId();
               logger.debug("prepare relation parentId = " + object.parentId + ", childId = " + object.childId);
               parentChilds.add(object);

            }
         }
         return parentChilds;
      }
      if (attributeKey.equals("routingconstraints"))
      {
         Collection<JdbcRoutingConstraint> routingConstraints = new ArrayList<JdbcRoutingConstraint>();
         if (item.getRoutingConstraintLines() != null)
         {
            for (Line routing : item.getRoutingConstraintLines())
            {
               JdbcRoutingConstraint object = new JdbcRoutingConstraint();
               object.lineId = routing.getId();
               object.stopAreaId = item.getId();
               logger.debug("prepare relation line = " + object.lineId + ", stopArea = " + object.stopAreaId);
               routingConstraints.add(object);

            }
         }
         return routingConstraints;
      }

      return super.getAttributeValues(attributeKey, item);
   }

   class JdbcParentChild
   {
      Long parentId;
      Long childId;
   }

   class JdbcRoutingConstraint
   {
      Long lineId;
      Long stopAreaId;
   }

}
