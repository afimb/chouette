package fr.certu.chouette.jdbc.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.Facility;
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
	private static final Logger logger = Logger.getLogger(FacilityJdbcDao.class);
   
   public Logger getLogger()
   {
      return logger;
   }

	@Override
	protected void populateStatement(PreparedStatement ps, Facility facility)
	throws SQLException {
		logger.debug("inserting "+facility.toString("",0));
		ps.setString(1, facility.getObjectId());
		ps.setInt(2, facility.getObjectVersion());

		Timestamp timestamp = null;
		if(facility.getCreationTime() != null)
			timestamp = new Timestamp(facility.getCreationTime().getTime());
		ps.setTimestamp(3, timestamp);
		ps.setString(4, facility.getCreatorId());
		ps.setString(5, facility.getName());
		ps.setString(6, facility.getComment());

		setId(ps,7,facility.getStopArea());
		setId(ps,8,facility.getLine());
		setId(ps,9,facility.getConnectionLink());
		setId(ps,10,facility.getStopPoint());

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
	
	{
		if (attributeKey.equals("feature"))
		{
			List<JdbcFeature> jfeatures = new ArrayList<JdbcFeature>();
			if (item.getFacilityFeatures() != null)
			{
				for (FacilityFeature feature : item.getFacilityFeatures()) 
				{
					JdbcFeature jfeature = new JdbcFeature();
					jfeature.facilityId=item.getId();
					jfeature.choiceCode = feature.getChoiceCode();
					jfeatures.add(jfeature);
				}
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
