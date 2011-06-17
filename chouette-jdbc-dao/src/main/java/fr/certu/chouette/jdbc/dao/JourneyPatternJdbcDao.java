package fr.certu.chouette.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Route;

/**
 * 
 * @author mamadou keira
 * 
 */
public class JourneyPatternJdbcDao extends AbstractJdbcDao<JourneyPattern> 
{
	@Override
	protected void populateStatement(PreparedStatement ps, JourneyPattern journeyPattern)
	throws SQLException {
		ps.setString(1, journeyPattern.getObjectId());
		ps.setInt(2, journeyPattern.getObjectVersion());
		Timestamp timestamp = null;
		if(journeyPattern.getCreationTime() != null)
			timestamp = new Timestamp(journeyPattern.getCreationTime().getTime());
		ps.setTimestamp(3, timestamp);
		ps.setString(4, journeyPattern.getCreatorId());
		ps.setString(5, journeyPattern.getName());
		ps.setString(6, journeyPattern.getComment());
		ps.setString(7, journeyPattern.getRegistrationNumber());
		ps.setString(8, journeyPattern.getPublishedName());
		Route route = journeyPattern.getRoute();
		Long routeId = null;
		if(route != null)
			routeId = route.getId();
		ps.setLong(9, routeId);
	}
}
