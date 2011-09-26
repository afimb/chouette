package fr.certu.chouette.jdbc.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

import fr.certu.chouette.model.neptune.AreaCentroid;
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
	/**
	 * first SQL purge request : remove every physical stops without StopPoint
	 */
	@Getter @Setter private String sqlPurge1; 
   /**
    * second SQL purge request : remove every commercial stops without child
    */
	@Getter @Setter private String sqlPurge2; 
   /**
    * third SQL purge request : remove every stopplace stops without child
    */
	@Getter @Setter private String sqlPurge3; 
   /**
    * fourth SQL purge request : remove every restriction constraints without dependency 
    */
	@Getter @Setter private String sqlPurge4; 
	
	/* (non-Javadoc)
	 * @see fr.certu.chouette.jdbc.dao.AbstractJdbcDao#getAll()
	 */
	@Override
	public List<StopArea> getAll() 
	{
		String sql = sqlSelectAll;
		List<StopArea> stopAreas = getJdbcTemplate().query(sql,
				new BeanPropertyRowMapper(StopArea.class));

		return stopAreas;
	}

	
	/* (non-Javadoc)
	 * @see fr.certu.chouette.jdbc.dao.AbstractJdbcDao#populateStatement(java.sql.PreparedStatement, fr.certu.chouette.model.neptune.NeptuneIdentifiedObject)
	 */
	@Override
	protected void populateStatement(PreparedStatement ps, StopArea stopArea)
	throws SQLException {
		setId(ps,1,stopArea.getParentStopArea());
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
		if (stopArea.getFareCode() != null)
		   ps.setInt(11, stopArea.getFareCode());
		else
			ps.setNull(11, Types.INTEGER);
		
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


	/* (non-Javadoc)
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
	
	
	
}
