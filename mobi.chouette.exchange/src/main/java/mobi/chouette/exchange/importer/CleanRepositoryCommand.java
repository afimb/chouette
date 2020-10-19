package mobi.chouette.exchange.importer;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.*;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import mobi.chouette.model.FootNoteAlternativeText;

@Log4j
@Stateless(name = CleanRepositoryCommand.COMMAND)
public class CleanRepositoryCommand implements Command {

	public static final String COMMAND = "CleanRepositoryCommand";

	@EJB
	private CompanyDAO companyDAO;

	@EJB
	private GroupOfLineDAO groupOfLineDAO;

	@EJB
	private JourneyFrequencyDAO journeyFrequencyDAO;

	@EJB
	private JourneyPatternDAO journeyPatternDAO;

	@EJB
	private LineDAO lineDAO;

	@EJB
	private NetworkDAO networkDAO;

	@EJB
	private RouteDAO routeDAO;

	@EJB
	private RouteSectionDAO routeSectionDAO;

	@EJB
	private StopPointDAO stopPointDAO;

	@EJB
	private ScheduledStopPointDAO scheduledStopPointDAO;

	@EJB
	private TimetableDAO timetableDAO;

	@EJB
	private TimebandDAO timebandDAO;

	@EJB
	private VehicleJourneyDAO vehicleJourneyDAO;

	@EJB
	private VehicleJourneyAtStopDAO vehicleJourneyAtStopDAO;

	@EJB
	private DatedServiceJourneyDAO datedServiceJourneyDAO;


	@EJB
	private DestinationDisplayDAO destinationDisplayDAO;

	@EJB
	private FootnoteDAO footnoteDAO;

	@EJB
	private FootnoteAlternativeTextDAO footNoteAlternativeTextDAO;

	@EJB
	private BrandingDAO brandingDAO;

	@EJB
	private InterchangeDAO interchangeDAO;

	@EJB
	private RoutePointDAO routePointDAO;

	@EJB
	private ContactStructureDAO contactStructureDAO;

	@EJB
	private BookingArrangementDAO bookingArrangementDAO;

	@EJB
	private FlexibleServicePropertiesDAO flexibleServicePropertiesDAO;

	@EJB
	private ReferentialDAO referentialDAO;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean execute(Context context) throws Exception {

		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		try {

			companyDAO.truncate();

			groupOfLineDAO.truncate();
			journeyFrequencyDAO.truncate();
			journeyPatternDAO.truncate();
			lineDAO.truncate();
			networkDAO.truncate();
			routeDAO.truncate();
			routeSectionDAO.truncate();
			footNoteAlternativeTextDAO.truncate();
			footnoteDAO.truncate();
			brandingDAO.truncate();
			stopPointDAO.truncate();
			scheduledStopPointDAO.truncate();
			timetableDAO.truncate();
			timebandDAO.truncate();
			vehicleJourneyDAO.truncate();
			vehicleJourneyAtStopDAO.truncate();
			datedServiceJourneyDAO.truncate();
			destinationDisplayDAO.truncate();
			interchangeDAO.truncate();
			routePointDAO.truncate();
			flexibleServicePropertiesDAO.truncate();
			bookingArrangementDAO.truncate();
			contactStructureDAO.truncate();
			referentialDAO.setLastUpdateTimestamp(LocalDateTime.now());

			result = SUCCESS;
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
		log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange/" + COMMAND;
				result = (Command) context.lookup(name);
			} catch (NamingException e) {
				// try another way on test context
				String name = "java:module/" + COMMAND;
				try {
					result = (Command) context.lookup(name);
				} catch (NamingException e1) {
					log.error(e);
				}
			}
			return result;
		}
	}

	static {
		CommandFactory.factories.put(CleanRepositoryCommand.class.getName(), new DefaultCommandFactory());
	}
}
