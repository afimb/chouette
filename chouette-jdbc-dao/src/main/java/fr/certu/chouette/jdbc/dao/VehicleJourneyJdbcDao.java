package fr.certu.chouette.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;

/**
 * 
 * @author mamadou keira
 * 
 */

public class VehicleJourneyJdbcDao extends AbstractJdbcDao<VehicleJourney> 
{
   private static final Logger logger = Logger.getLogger(VehicleJourneyJdbcDao.class);
   
   public Logger getLogger()
   {
      return logger;
   }

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

		setId(ps,13,vehicleJourney.getRoute());
		setId(ps,14,vehicleJourney.getJourneyPattern());
		setId(ps,15,vehicleJourney.getTimeSlot());
		setId(ps,16,vehicleJourney.getCompany());
		

	}


	/* (non-Javadoc)
	 * @see fr.certu.chouette.jdbc.dao.AbstractJdbcDao#populateAttributeStatement(java.lang.String, java.sql.PreparedStatement, java.lang.Object)
	 */
	@Override
	protected void populateAttributeStatement(String attributeKey,PreparedStatement ps, Object attribute) throws SQLException 
	{
		if (attributeKey.equals("vjAtStop"))
		{
			JdbcVehicleJourneyAtStop jvAtStop = (JdbcVehicleJourneyAtStop) attribute;
			VehicleJourneyAtStop vAtStop = jvAtStop.vjas;
			ps.setLong(1,jvAtStop.vehicleJourneyId);
			setId(ps,2,vAtStop.getStopPoint());
			ps.setString(3, vAtStop.getConnectingServiceId());

			String bordingPossibility = null;
			if(vAtStop.getBoardingAlightingPossibility() != null)
				bordingPossibility  = vAtStop.getBoardingAlightingPossibility().value();
			ps.setString(4, bordingPossibility);
			Time arrivaltime = null,
			departuretime = null,
			waitingtime = null,
			elapseduration = null,
			headwayfrequency = null;

			if(vAtStop.getArrivalTime() != null)
				arrivaltime = vAtStop.getArrivalTime();

			if(vAtStop.getDepartureTime() != null)
				departuretime = vAtStop.getDepartureTime();

			if(vAtStop.getWaitingTime() != null)
				waitingtime = vAtStop.getWaitingTime();

			if(vAtStop.getElapseDuration() != null)
				elapseduration = vAtStop.getElapseDuration();

			if(vAtStop.getHeadwayFrequency() != null)
				headwayfrequency = vAtStop.getHeadwayFrequency();

			ps.setTime(5, arrivaltime);
			ps.setTime(6, departuretime);
			ps.setTime(7, waitingtime);
			ps.setTime(8, elapseduration);
			ps.setTime(9, headwayfrequency);
			ps.setBoolean(10, vAtStop.isDeparture());
			ps.setBoolean(11, vAtStop.isArrival());
			ps.setLong(12, vAtStop.getOrder());

			return;

		}

		if (attributeKey.equals("timetableVj"))
		{
			JdbcTimetableVehicleJourney jtJourney = (JdbcTimetableVehicleJourney) attribute;
			ps.setLong(1,jtJourney.timetableid);
			ps.setLong(2,jtJourney.vehiclejourneyid);
			
			return;
		}

		super.populateAttributeStatement(attributeKey, ps, attribute);

	}


	@Override
	protected Collection<? extends Object> getAttributeValues(String attributeKey, VehicleJourney item) 
	{
		if (attributeKey.equals("vjAtStop"))
		{
         Collection<JdbcVehicleJourneyAtStop> vjass = new ArrayList<JdbcVehicleJourneyAtStop>();
         // prepare vjas if vehiclejourneyref missing
         for (VehicleJourneyAtStop vjas : item.getVehicleJourneyAtStops())
         {
            JdbcVehicleJourneyAtStop jvjas = new JdbcVehicleJourneyAtStop();
            jvjas.vehicleJourneyId = item.getId();
            jvjas.vjas = vjas;
            vjass.add(jvjas);
         }
			return vjass;
		}

		if (attributeKey.equals("timetableVj"))
		{
			Collection<JdbcTimetableVehicleJourney> timeJourneys = new ArrayList<JdbcTimetableVehicleJourney>();
			for (Timetable timetable : item.getTimetables())
			{
				JdbcTimetableVehicleJourney object = new JdbcTimetableVehicleJourney();
				object.timetableid = timetable.getId();
				object.vehiclejourneyid = item.getId(); 
				timeJourneys.add(object);
				
			}
			return timeJourneys;
		}

		return super.getAttributeValues(attributeKey, item);
	}

	class JdbcTimetableVehicleJourney {
		Long timetableid,
		vehiclejourneyid;
	}
	
	class JdbcVehicleJourneyAtStop
	{
	   Long vehicleJourneyId;
	   VehicleJourneyAtStop vjas;
	}
}
