package fr.certu.chouette.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.Facility;
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

		String containedIn = facility.getContainedIn(); // TODO should be containedInStopArea.getId()
		
		ps.setBigDecimal(13, facility.getLongitude());
		ps.setBigDecimal(14, facility.getLatitude());
		String longlattype = null;
		if(facility.getLongLatType() != null)
			longlattype = facility.getLongLatType().value();
		ps.setString(15, longlattype);
		ps.setString(16, facility.getCountryCode());
		ps.setString(17, facility.getStreetName());
		ps.setString(18, containedIn);
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
