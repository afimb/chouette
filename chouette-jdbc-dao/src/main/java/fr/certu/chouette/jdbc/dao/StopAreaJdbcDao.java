package fr.certu.chouette.jdbc.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.type.Address;
import fr.certu.chouette.model.neptune.type.ProjectedPoint;

/**
 * 
 * @author mamadou keira
 * 
 */

@SuppressWarnings("unchecked")
public class StopAreaJdbcDao extends AbstractJdbcDao<StopArea> 
{
	@Override
	public List<StopArea> getAll() 
	{
		String sql = sqlSelectAll;
		List<StopArea> stopAreas = getJdbcTemplate().query(sql,
				new BeanPropertyRowMapper(StopArea.class));

		return stopAreas;
	}

	
	@Override
	protected void populateStatement(PreparedStatement ps, StopArea stopArea)
	throws SQLException {
		StopArea parent = stopArea.getParentStopArea();
		Long parentId = null;
		if(parent != null)
			parentId = parent.getId();
		ps.setLong(1, parentId);
		ps.setString(2, stopArea.getObjectId());
		ps.setInt(3, stopArea.getObjectVersion());
		Timestamp timestamp = null;
		if(stopArea.getCreationTime() != null)
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
		ps.setInt(11, stopArea.getFareCode());
		
		AreaCentroid areaCentroid = stopArea.getAreaCentroid();
		BigDecimal longitude = null, 
				   latitude = null ,
					x = null, y = null;
		
		String longLatType = null , 
			   projectionType = null,
				countryCode = null, 
				streetName = null;
		
		if(areaCentroid != null)
		{
			longitude = areaCentroid.getLongitude();
			latitude = areaCentroid.getLatitude();
			if(areaCentroid.getLongLatType() != null)
				longLatType = areaCentroid.getLongLatType().value();
			
			ProjectedPoint projectedPoint = areaCentroid.getProjectedPoint();
			if(projectedPoint != null)
			{
				x = projectedPoint.getX();
				y = projectedPoint.getY();
				projectionType = projectedPoint.getProjectionType();
			}
			
			Address address = areaCentroid.getAddress();
			if(address != null)
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
}
