package fr.certu.chouette.jdbc.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.certu.chouette.jdbc.exception.JdbcDaoException;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.Facility;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.type.Address;
import fr.certu.chouette.model.neptune.type.FacilityLocation;
import fr.certu.chouette.model.neptune.type.ProjectedPoint;
import fr.certu.chouette.model.neptune.type.facility.FacilityFeature;

/**
 * 
 * @author mamadou keira
 * 
 */

public class FacilityJdbcDao extends AbstractJdbcDao<Facility> 
{
	@Override
	protected void populateStatement(PreparedStatement ps, Facility facility)
	throws SQLException {
		ps.setString(1, facility.getObjectId());
		ps.setInt(2, facility.getObjectVersion());

		Timestamp timestamp = null;
		if(facility.getCreationTime() != null)
			timestamp = new Timestamp(facility.getCreationTime().getTime());
		ps.setTimestamp(3, timestamp);
		ps.setString(4, facility.getCreatorId());
		ps.setString(5, facility.getName());
		ps.setString(6, facility.getComment());
		
		Long stopAreaId = null,
			 stopPointId = null,
			 cLinkId = null,
			 lineId  = null;
		
		StopArea stopArea = facility.getStopArea();
		StopPoint stopPoint = facility.getStopPoint();
		ConnectionLink cLink = facility.getConnectionLink();
		Line line = facility.getLine();
		
		if(stopArea != null)
			stopAreaId = stopArea.getId();
		if(stopPoint != null)
			stopPointId = stopPoint.getId();
		if(cLink != null)
			cLinkId = cLink.getId();
		if(line != null)
			lineId = line.getId();
		
		ps.setLong(7, stopAreaId);
		ps.setLong(8, lineId);
		ps.setLong(9, cLinkId);
		ps.setLong(10, stopPointId);
		ps.setString(11, facility.getDescription());
		ps.setBoolean(12, facility.getFreeAccess());
		
		BigDecimal longitude = null,
			   	   latitude = null,
			   	   x = null,
			   	   y = null;
		
		String countrycode = null,
			   streetname = null,
			   longlattype = null,
			   projectionType = null,
			   containedIn = null;
		
		FacilityLocation facilityLocation = facility.getFacilityLocation();
		
		if(facilityLocation != null)
		{
			longitude = facilityLocation.getLongitude();
			latitude = facilityLocation.getLatitude();
			if(facilityLocation.getLongLatType() != null)
				longlattype = facilityLocation.getLongLatType().value();
			
			Address address = facilityLocation.getAddress();
			
			if(address != null)
			{
				countrycode = address.getCountryCode();
				streetname = address.getStreetName();
			}
			
			ProjectedPoint projectedPoint = facilityLocation.getProjectedPoint();
			if(projectedPoint != null)
			{
				x = projectedPoint.getX();
				y = projectedPoint.getY();
				projectionType = projectedPoint.getProjectionType();
			}
			
			containedIn = facilityLocation.getContainedIn();
		}
		
		ps.setBigDecimal(13, longitude);
		ps.setBigDecimal(14, latitude);
		ps.setString(15, longlattype);
		ps.setString(16, countrycode);
		ps.setString(17, streetname);
		ps.setBigDecimal(18, x);
		ps.setBigDecimal(19, y);
		ps.setString(20, projectionType);
		ps.setString(21, containedIn);
	}
	
	
	@Override
	protected Collection<? extends Object> getAttributeValues(String attributeKey, Facility item) 
	throws JdbcDaoException 
	{
		if (attributeKey.equals("feature"))
		{
			List<JdbcFeature> jfeatures = new ArrayList<JdbcFeature>();

			for (FacilityFeature feature : item.getFacilityFeatures()) 
			{
				JdbcFeature jfeature = new JdbcFeature();
				jfeature.facilityId=item.getId();
				jfeature.choiceCode = feature.getChoiceCode();
				jfeatures.add(jfeature);
			}
			return jfeatures;
		}
		return super.getAttributeValues(attributeKey, item);
	}

	@Override
	protected void populateAttributeStatement(String attributeKey,PreparedStatement ps, Object attribute) 
	throws SQLException 
	{
		if (attributeKey.equals("feature"))
		{
			JdbcFeature jfeature = (JdbcFeature) attribute;
			ps.setLong(1,jfeature.facilityId);
			ps.setInt(2,jfeature.choiceCode);
			return;
		}		
		super.populateAttributeStatement(attributeKey, ps, attribute);
	}

	class JdbcFeature
	{
		Long facilityId;
		int choiceCode;
	}
}
