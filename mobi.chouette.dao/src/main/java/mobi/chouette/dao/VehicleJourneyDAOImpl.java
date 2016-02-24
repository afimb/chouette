package mobi.chouette.dao;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.VehicleJourney;

import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.jboss.jca.adapters.jdbc.WrappedConnection;
import org.postgresql.PGConnection;

@Stateless
@Log4j
public class VehicleJourneyDAOImpl extends GenericDAOImpl<VehicleJourney> implements VehicleJourneyDAO{

	public VehicleJourneyDAOImpl() {
		super(VehicleJourney.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@Override
	public void deleteChildren(final List<String> vehicleJourneyObjectIds) {

		Session session = em.unwrap(Session.class);

		session.doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {

				final String SQL = "DELETE FROM vehicle_journey_at_stops WHERE vehicle_journey_id IN ("
						+ "SELECT id FROM vehicle_journeys WHERE objectid IN ( %s )"
						+ ")";

				// delete
				int size = vehicleJourneyObjectIds.size();
				if (size > 0) {
					StringBuffer buffer = new StringBuffer();
					for (int i = 0; i < size; i++) {

						buffer.append('\'');
						buffer.append(vehicleJourneyObjectIds.get(i));
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
			}
		});
	}

	@Override
	public void copy(final String data) {

		Session session = em.unwrap(Session.class);

		session.doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				// Monitor monitor = MonitorFactory.start("COPY");
				try {

					StringReader from = new StringReader(data);
					PGConnection pgConnection = (PGConnection) ((WrappedConnection) connection)
							.getUnderlyingConnection();
					org.postgresql.copy.CopyManager manager = pgConnection
							.getCopyAPI();
					manager.copyIn(
							"COPY vehicle_journey_at_stops("
									+ "vehicle_journey_id, stop_point_id, "
									+ "arrival_time, departure_time)"
									// + "arrival_time, departure_time, "
									// + "elapse_duration, headway_frequency)"
									+ " FROM STDIN WITH DELIMITER '|'", from);

				} catch (IOException e) {
					log.error(e);
				}
				// log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
			}
		});
	}

}
