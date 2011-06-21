package fr.certu.chouette.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopPoint;

/**
 * 
 * @author mamadou keira
 * 
 */

public class PTLinkJdbcDao extends AbstractJdbcDao<PTLink> 
{
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
		Long startId = null, endId = null, routeId = null;
		
		StopPoint start = ptLink.getStartOfLink();
		StopPoint end = ptLink.getEndOfLink();
		Route route = ptLink.getRoute();
		
		if(start != null)
			startId = start.getId();
		if(end != null)
			endId = end.getId();
		if(route != null)
			routeId = route.getId();
		
		ps.setLong(8, startId);
		ps.setLong(9, endId);
		ps.setLong(10, routeId);
		
	}
}
