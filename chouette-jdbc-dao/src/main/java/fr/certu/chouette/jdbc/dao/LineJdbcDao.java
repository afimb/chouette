package fr.certu.chouette.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.StopArea;

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
   protected void populateStatement(PreparedStatement ps, Line line)
   throws SQLException {
      setId(ps,1,line.getPtNetwork());
      setId(ps,2,line.getCompany());
      ps.setString(3, line.getObjectId());
      ps.setInt(4, line.getObjectVersion());
      Timestamp timestamp = null;
      if(line.getCreationTime() != null)
         timestamp = new Timestamp(line.getCreationTime().getTime());
      ps.setTimestamp(5, timestamp);
      ps.setString(6, line.getCreatorId());
      ps.setString(7, line.getName());
      ps.setString(8, line.getNumber());
      ps.setString(9, line.getPublishedName());
      ps.setString(10, line.getTransportModeName().toString());
      ps.setString(11, line.getRegistrationNumber());
      ps.setString(12, line.getComment());
      setId(ps,13,line.getGroupOfLine());
      Boolean mobilityRS = false;
      if(line.getMobilityRestrictedSuitable() != null)
         mobilityRS = true;		
      ps.setBoolean(14, mobilityRS);
      ps.setLong(15, line.getUserNeedsAsLong());
   }


}
