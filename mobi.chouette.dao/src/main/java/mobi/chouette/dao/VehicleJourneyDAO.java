package mobi.chouette.dao;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.VehicleJourney;

import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

@Stateless
@Log4j
public class VehicleJourneyDAO extends GenericDAOImpl<VehicleJourney> {

	public VehicleJourneyDAO() {
		super(VehicleJourney.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	public void update(final Collection<VehicleJourney> values,
			final byte[] data) {

		Session session = em.unwrap(Session.class);

		session.doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {

				final String SQL = "DELETE FROM vehicle_journey_at_stops h JOIN vehicle_journeys c ON h.vehicle_journey_id =  c.id WHERE c.objectid IN ( ? )";

				// delete
				final List<String> list = new ArrayList<String>();
				for (VehicleJourney vehicleJourney : values) {
					list.add(vehicleJourney.getObjectId());
				}

				PreparedStatement statement = connection.prepareStatement(SQL);
				statement.setArray(1,
						connection.createArrayOf("string", list.toArray()));
				
				int count = statement.executeUpdate();

				// insert
				ByteArrayInputStream in = new ByteArrayInputStream(data);
				BufferedReader from = new BufferedReader(new InputStreamReader(
						in));

				CopyManager manager = new CopyManager(
						(BaseConnection) connection);
				try {
					manager.copyIn(
							"COPY vehicle_journey_at_stops("
									+ "vehicle_journey_id, stop_point_id, "
									+ "connecting_service_id, boarding_alighting_possibility,"
									+ "arrival_time, departure_time, waiting_time, "
									+ "elapse_duration, headway_frequency)"
									+ " FROM STDIN WITH DELIMITER '|'", from);
				} catch (IOException e) {
					log.debug(e);
					throw new SQLException(e);
				}
			}
		});

	}

}
