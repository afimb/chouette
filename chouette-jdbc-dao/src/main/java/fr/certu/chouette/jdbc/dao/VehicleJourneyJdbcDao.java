package fr.certu.chouette.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.TimeSlot;
import fr.certu.chouette.model.neptune.VehicleJourney;

/**
 * 
 * @author mamadou keira
 * 
 */

public class VehicleJourneyJdbcDao extends AbstractJdbcDao<VehicleJourney> 
{
	@Override
	protected void populateStatement(PreparedStatement ps, VehicleJourney vehicleJourney)
	throws SQLException {
		ps.setString(1, vehicleJourney.getObjectId());
		ps.setInt(2, vehicleJourney.getObjectVersion());
		Timestamp timestamp = null;
		if(vehicleJourney.getCreationTime() != null)
			timestamp = new Timestamp(vehicleJourney.getCreationTime().getTime());
		ps.setTimestamp(3, timestamp);
		ps.setString(4, vehicleJourney.getCreatorId());
		ps.setString(5, vehicleJourney.getComment());
		String statusValue = null,
			   transportMode = null;

		if(vehicleJourney.getServiceStatusValue() != null)
			statusValue = vehicleJourney.getServiceStatusValue().value();

		if(vehicleJourney.getTransportMode() != null)
			transportMode = vehicleJourney.getTransportMode().value();

		ps.setString(6, statusValue );
		ps.setString(7, transportMode);
		ps.setString(8, vehicleJourney.getPublishedJourneyName());
		ps.setString(9, vehicleJourney.getPublishedJourneyIdentifier());
		ps.setString(10, vehicleJourney.getFacility());
		ps.setString(11, vehicleJourney.getVehicleTypeIdentifier());
		ps.setLong(12, vehicleJourney.getNumber());

		Long routeId = null,
			 jPatternId = null,
			 timeSlotId = null,
			 companyId = null;

		Route route = vehicleJourney.getRoute();
		JourneyPattern jPattern = vehicleJourney.getJourneyPattern();
		TimeSlot timeSlot = vehicleJourney.getTimeSlot();
		Company company = vehicleJourney.getCompany();

		if(route != null)
			routeId = route.getId();
		if(jPattern != null)
			jPatternId = jPattern.getId();
		if(timeSlot != null)
			timeSlotId = timeSlot.getId();
		if(company != null)
			companyId = company.getId();

		ps.setLong(13, routeId);
		ps.setLong(14, jPatternId);
		ps.setObject(15, (Long)timeSlotId);
		ps.setLong(16, companyId);
	}
}
