package mobi.chouette.dao;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.model.VehicleJourney;

import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.jboss.jca.adapters.jdbc.WrappedConnection;
import org.jboss.jca.adapters.jdbc.WrappedPreparedStatement;
import org.postgresql.PGConnection;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

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

	public void delete(final List<VehicleJourney> values) {

		Session session = em.unwrap(Session.class);

		session.doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {

				Monitor monitor = MonitorFactory.start("DELETE");

				final String SQL = "DELETE FROM vehicle_journey_at_stops WHERE id IN ("
						+ "SELECT h.id FROM vehicle_journey_at_stops h JOIN vehicle_journeys c ON h.vehicle_journey_id = c.id WHERE c.objectid IN ( %s )"
						+ ")";

				// delete
				int size = values.size();
				if (size > 0) {
					StringBuffer buffer = new StringBuffer();
					for (int i = 0; i < size; i++) {
						VehicleJourney vehicleJourney = values.get(i);

						buffer.append('\'');
						buffer.append(vehicleJourney.getObjectId());
						buffer.append('\'');
						if (i != size - 1) {
							buffer.append(',');
						}
					}

					Statement statement = connection.createStatement();
					String sql = String.format(SQL, buffer.toString());
					// System.out.println("execute SQL : " + sql);
					int count = statement.executeUpdate(sql);
					log.info("[DSU] delete " + count + " objects.");
				}
				log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
			}
		});
	}

	public void copy(final String data) {

		Session session = em.unwrap(Session.class);

		session.doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				Monitor monitor = MonitorFactory.start("COPY");
				try {

					StringReader from = new StringReader(data);
					PGConnection pgConnection = (PGConnection) ((WrappedConnection) connection)
							.getUnderlyingConnection();
					org.postgresql.copy.CopyManager manager = pgConnection
							.getCopyAPI();
					manager.copyIn(
							"COPY vehicle_journey_at_stops("
									+ "vehicle_journey_id, stop_point_id, "
									+ "connecting_service_id, boarding_alighting_possibility,"
									+ "arrival_time, departure_time, waiting_time, "
									+ "elapse_duration, headway_frequency)"
									+ " FROM STDIN WITH DELIMITER '|'", from);

				} catch (IOException e) {
					log.error(e);
				}
				log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
			}
		});
	}

}
