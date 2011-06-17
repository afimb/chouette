package fr.certu.chouette.jdbc.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.type.Address;
import fr.certu.chouette.model.neptune.type.ProjectedPoint;

/**
 * 
 * @author mamadou keira
 * 
 */

@SuppressWarnings("unchecked")
public class AccessPointJdbcDao extends AbstractJdbcDao<AccessPoint> 
{
	@Override
	public List<AccessPoint> getAll() 
	{
		String sql = sqlSelectAll;
		List<AccessPoint> accessPoints = getJdbcTemplate().query(sql,
				new BeanPropertyRowMapper(AccessPoint.class));

		return accessPoints;
	}

	@Override
	protected void populateStatement(PreparedStatement ps, AccessPoint accessPoint)
	throws SQLException {
		ps.setString(1, accessPoint.getObjectId());
		ps.setInt(2, accessPoint.getObjectVersion());
		Timestamp timestamp = null;
		if(accessPoint.getCreationTime() != null)
			timestamp = new Timestamp(accessPoint.getCreationTime().getTime());
		ps.setTimestamp(3, timestamp);
		ps.setString(4, accessPoint.getCreatorId());
		ps.setString(5, accessPoint.getName());
		ps.setString(6, accessPoint.getComment());
		String countryCode = null , streetName = null;
		Address address = accessPoint.getAddress(); 
		if(address != null)
		{
			countryCode = address.getCountryCode();
			streetName = address.getStreetName();
		}
		ps.setString(7, countryCode );
		ps.setString(8, streetName );
		ps.setBigDecimal(9, accessPoint.getLongitude());
		ps.setBigDecimal(10, accessPoint.getLatitude());
		BigDecimal x = null, y = null;
		String projectionType = null;

		ProjectedPoint projectedPoint = accessPoint.getProjectedPoint();
		if(projectedPoint != null)
		{
			x = projectedPoint.getX();
			y= projectedPoint.getY();
			projectionType = projectedPoint.getProjectionType();
		}
		ps.setBigDecimal(11, x);
		ps.setBigDecimal(12, y);
		ps.setString(13, projectionType);
		ps.setString(14, accessPoint.getContainedIn());
		Timestamp openningTime = null , closingTime = null;
		if(accessPoint.getOpenningTime() != null)
			openningTime = new Timestamp(accessPoint.getOpenningTime().getTime());
		ps.setTimestamp(15, openningTime);
		if(accessPoint.getClosingTime() != null)
			closingTime = new Timestamp(accessPoint.getClosingTime().getTime());
		ps.setTimestamp(16, closingTime);
		ps.setString(17, accessPoint.getType());
		ps.setBoolean(18, accessPoint.isLiftAvailable());
		ps.setBoolean(19,accessPoint.isMobilityRestrictedSuitable());
		ps.setBoolean(20,accessPoint.isStairsAvailable());
	}
}
