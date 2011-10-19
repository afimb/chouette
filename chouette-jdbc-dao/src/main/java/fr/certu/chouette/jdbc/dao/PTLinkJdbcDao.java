package fr.certu.chouette.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.PTLink;

/**
 * 
 * @author mamadou keira
 * 
 */

public class PTLinkJdbcDao extends AbstractJdbcDao<PTLink> 
{
   private static final Logger logger = Logger.getLogger(PTLinkJdbcDao.class);
   
   public Logger getLogger()
   {
      return logger;
   }

	@Override
	protected void populateStatement(PreparedStatement ps, PTLink ptLink)
	throws SQLException {
		ps.setString(1, ptLink.getObjectId());
		ps.setInt(2, ptLink.getObjectVersion());

		Timestamp timestamp = null;
		if(ptLink.getCreationTime() != null)
			timestamp = new Timestamp(ptLink.getCreationTime().getTime());
		ps.setTimestamp(3, timestamp);
		ps.setString(4, ptLink.getCreatorId());
		ps.setString(5, ptLink.getName());
		ps.setString(6, ptLink.getComment());
		ps.setBigDecimal(7, ptLink.getLinkDistance());
		setId(ps,8,ptLink.getStartOfLink());
		setId(ps,9,ptLink.getEndOfLink());
		setId(ps,10,ptLink.getRoute());
		
	}
}
