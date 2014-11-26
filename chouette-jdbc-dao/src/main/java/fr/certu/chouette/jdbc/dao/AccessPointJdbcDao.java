package fr.certu.chouette.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import fr.certu.chouette.model.neptune.AccessPoint;

/**
 * 
 * @author mamadou keira
 * 
 */

public class AccessPointJdbcDao extends AbstractJdbcDao<AccessPoint>
{
   private static final Logger logger = Logger
         .getLogger(AccessPointJdbcDao.class);

   public Logger getLogger()
   {
      return logger;
   }

   @Override
   public List<AccessPoint> getAll()
   {
      String sql = sqlSelectAll;
      List<AccessPoint> accessPoints = getJdbcTemplate().query(sql,
            new BeanPropertyRowMapper<AccessPoint>(AccessPoint.class));

      return accessPoints;
   }

   @Override
   protected void populateStatement(PreparedStatement ps,
         AccessPoint accessPoint) throws SQLException
   {
      ps.setString(1, accessPoint.getObjectId());
      ps.setInt(2, accessPoint.getObjectVersion());
      Timestamp timestamp = null;
      if (accessPoint.getCreationTime() != null)
         timestamp = new Timestamp(accessPoint.getCreationTime().getTime());
      ps.setTimestamp(3, timestamp);
      ps.setString(4, accessPoint.getCreatorId());
      ps.setString(5, accessPoint.getName());
      ps.setString(6, accessPoint.getComment());
      ps.setString(7, accessPoint.getCountryCode());
      ps.setString(8, accessPoint.getStreetName());
      ps.setBigDecimal(9, accessPoint.getLongitude());
      ps.setBigDecimal(10, accessPoint.getLatitude());
      String longLatType = null, type = null;
      if (accessPoint.getLongLatType() != null)
         longLatType = accessPoint.getLongLatType().name();
      ps.setString(11, longLatType);

      ps.setString(12, accessPoint.getContainedInStopArea());
      setId(ps, 13, accessPoint.getContainedIn(), true, "stop_area_id");
      Time openningTime = null, closingTime = null;
      if (accessPoint.getOpeningTime() != null)
         openningTime = accessPoint.getOpeningTime();
      ps.setTime(14, openningTime);
      if (accessPoint.getClosingTime() != null)
         closingTime = accessPoint.getClosingTime();
      ps.setTime(15, closingTime);
      if (accessPoint.getType() != null)
      {
         type = accessPoint.getType().name();
      }
      ps.setString(16, type);
      ps.setBoolean(17, accessPoint.isLiftAvailable());
      ps.setBoolean(18, accessPoint.isMobilityRestrictedSuitable());
      ps.setBoolean(19, accessPoint.isStairsAvailable());
      ps.setString(20, accessPoint.getZipCode());
      ps.setString(21, accessPoint.getCityName());
   }
}
