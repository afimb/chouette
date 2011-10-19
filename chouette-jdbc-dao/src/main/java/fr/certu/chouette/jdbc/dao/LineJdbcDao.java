package fr.certu.chouette.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import fr.certu.chouette.model.neptune.GroupOfLine;
import fr.certu.chouette.model.neptune.Line;

/**
 * 
 * @author mamadou keira
 * 
 */

@SuppressWarnings("unchecked")
public class LineJdbcDao extends AbstractJdbcDao<Line> 
{
   private static final Logger logger = Logger.getLogger(LineJdbcDao.class);
   
   public Logger getLogger()
   {
      return logger;
   }

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
      Boolean mobilityRS = false;
      if(line.getMobilityRestrictedSuitable() != null)
         mobilityRS = true;		
      ps.setBoolean(13, mobilityRS);
      ps.setLong(14, line.getUserNeedsAsLong());
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.jdbc.dao.AbstractJdbcDao#populateAttributeStatement(java.lang.String, java.sql.PreparedStatement, java.lang.Object)
    */
   @Override
   protected void populateAttributeStatement(String attributeKey,PreparedStatement ps, Object attribute) throws SQLException 
   {

      if (attributeKey.equals("groupOfLines"))
      {
         JdbcLineGroupOfLine group = (JdbcLineGroupOfLine) attribute;
         ps.setLong(1,group.lineId);
         ps.setLong(2,group.groupOfLineId);

         return;
      }

      super.populateAttributeStatement(attributeKey, ps, attribute);

   }


   @Override
   protected Collection<? extends Object> getAttributeValues(String attributeKey, Line item) 
   {

      if (attributeKey.equals("groupOfLines"))
      {
         Collection<JdbcLineGroupOfLine> groups = new ArrayList<JdbcLineGroupOfLine>();
         if (item.getGroupOfLines() != null)
         {
            for (GroupOfLine group : item.getGroupOfLines())
            {
               JdbcLineGroupOfLine object = new JdbcLineGroupOfLine();
               object.groupOfLineId = group.getId();
               object.lineId = item.getId(); 
               groups.add(object);

            }
         }
         return groups;
      }

      return super.getAttributeValues(attributeKey, item);
   }


   class JdbcLineGroupOfLine 
   {
      Long lineId,
      groupOfLineId;
   }

}
