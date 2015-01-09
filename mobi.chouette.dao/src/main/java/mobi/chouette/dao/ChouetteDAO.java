package mobi.chouette.dao;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import lombok.Getter;

@Stateless
@Getter
public class ChouetteDAO {

	@EJB
	private AccessLinkDAO accessLinkDAO;

	@EJB
	private AccessPointDAO accessPointDAO;

	@EJB
	private CompanyDAO companyDAO;

	@EJB
	private ConnectionLinkDAO connectionLinkDAO;

	@EJB
	private GroupOfLineDAO groupOfLineDAO;

	@EJB
	private JourneyPatternDAO journeyPatternDAO;

	@EJB
	private LineDAO lineDAO;

	@EJB
	private PTNetworkDAO PTNetworkDAO;

	@EJB
	private RouteDAO routeDAO;

	@EJB
	private StopAreaDAO stopAreaDAO;

	@EJB
	private StopPointDAO stopPointDAO;

	@EJB
	private TimetableDAO timetableDAO;

	@EJB
	private TimeSlotDAO timeSlotDAO;

	@EJB
	private VehicleJourneyDAO vehicleJourneyDAO;

}
