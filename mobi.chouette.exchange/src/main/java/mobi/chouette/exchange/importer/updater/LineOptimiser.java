package mobi.chouette.exchange.importer.updater;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import mobi.chouette.dao.AccessLinkDAO;
import mobi.chouette.dao.AccessPointDAO;
import mobi.chouette.dao.CompanyDAO;
import mobi.chouette.dao.ConnectionLinkDAO;
import mobi.chouette.dao.GroupOfLineDAO;
import mobi.chouette.dao.JourneyPatternDAO;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.dao.NetworkDAO;
import mobi.chouette.dao.RouteDAO;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.dao.StopPointDAO;
import mobi.chouette.dao.TimebandDAO;
import mobi.chouette.dao.TimetableDAO;
import mobi.chouette.dao.VehicleJourneyDAO;
import mobi.chouette.exchange.ChouetteIdObjectUtil;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.Company;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timeband;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.util.Referential;

@Stateless
public class LineOptimiser {

	@EJB
	private ConnectionLinkDAO connectionLinkDAO;

	@EJB
	private AccessLinkDAO accessLinkDAO;

	@EJB
	private AccessPointDAO accessPointDAO;

	@EJB
	private StopAreaDAO stopAreaDAO;

	@EJB
	private TimetableDAO timetableDAO;

	@EJB
	private NetworkDAO ptNetworkDAO;

	@EJB
	private CompanyDAO companyDAO;

	@EJB
	private GroupOfLineDAO groupOfLineDAO;

	@EJB
	private LineDAO lineDAO;

	@EJB
	private RouteDAO routeDAO;

	@EJB
	private JourneyPatternDAO journeyPatternDAO;

	@EJB
	private VehicleJourneyDAO vehicleJourneyDAO;

	@EJB
	private StopPointDAO stopPointDAO;

	@EJB
	private TimebandDAO timebandDAO;

	public void initialize(Referential cache, Referential referential) {

		// Monitor monitor = MonitorFactory.start("LineOptimiser");
		initializeStopArea(cache, referential.getStopAreas().values());

		initializeConnectionLink(cache, referential.getConnectionLinks().values());
		initializeAccessLink(cache, referential.getAccessLinks().values());
		initializeAccessPoint(cache, referential.getAccessPoints().values());

		initializeTimetable(cache, referential.getTimetables().values());
		initializePTNetwork(cache, referential.getPtNetworks().values());
		initializeCompany(cache, referential.getCompanies().values());
		initializeGroupOfLine(cache, referential.getGroupOfLines().values());

		initializeLine(cache, referential.getLines().values());
		initializeRoute(cache, referential.getRoutes().values());
		initializeStopPoint(cache, referential.getStopPoints().values());
		initializeJourneyPattern(cache, referential.getJourneyPatterns().values());
		initializeVehicleJourney(cache, referential.getVehicleJourneys().values());

		initializeTimeband(cache, referential.getTimebands().values());
		// monitor.stop();
	}

	private void initializeStopArea(Referential cache, Collection<StopArea> list) {
		if (list != null && !list.isEmpty()) {
			Map<String, List<String>> chouetteIdsByCodeSpace = UpdaterUtils.getChouetteIdsByCodeSpace(list);
			List<StopArea> objects = new ArrayList<StopArea>();

			objects.addAll((List<StopArea>) stopAreaDAO.findByChouetteId(chouetteIdsByCodeSpace));

			for (StopArea object : objects) {
				cache.getStopAreas().put(object.getChouetteId(), object);
			}

			// TODO check if stoparea really exists
			for (StopArea item : list) {
				StopArea object = cache.getStopAreas().get(item.getChouetteId());
				if (object == null) {
					object = ChouetteIdObjectUtil.getStopArea(cache, item.getChouetteId());
				}
			}
		}
	}

	private void initializeConnectionLink(Referential cache, Collection<ConnectionLink> list) {
		if (list != null && !list.isEmpty()) {
			Map<String, List<String>> chouetteIdsByCodeSpace = UpdaterUtils.getChouetteIdsByCodeSpace(list);
			List<ConnectionLink> objects = new ArrayList<ConnectionLink>();

			objects.addAll((List<ConnectionLink>) connectionLinkDAO.findByChouetteId(chouetteIdsByCodeSpace));

			for (ConnectionLink object : objects) {
				cache.getConnectionLinks().put(object.getChouetteId(), object);
			}

			for (ConnectionLink item : list) {
				ConnectionLink object = cache.getConnectionLinks().get(item.getChouetteId());
				if (object == null) {
					object = ChouetteIdObjectUtil.getConnectionLink(cache, item.getChouetteId());
				}
			}
		}
	}

	private void initializeAccessLink(Referential cache, Collection<AccessLink> list) {
		if (list != null && !list.isEmpty()) {

			Map<String, List<String>> chouetteIdsByCodeSpace = UpdaterUtils.getChouetteIdsByCodeSpace(list);
			List<AccessLink> objects = new ArrayList<AccessLink>();
			objects.addAll((List<AccessLink>) accessLinkDAO.findByChouetteId(chouetteIdsByCodeSpace));

			for (AccessLink object : objects) {
				cache.getAccessLinks().put(object.getChouetteId(), object);
			}

			for (AccessLink item : list) {
				AccessLink object = cache.getAccessLinks().get(item.getChouetteId());
				if (object == null) {
					object = ChouetteIdObjectUtil.getAccessLink(cache, item.getChouetteId());
				}
			}
		}
	}

	private void initializeAccessPoint(Referential cache, Collection<AccessPoint> list) {
		if (list != null && !list.isEmpty()) {

			Map<String, List<String>> chouetteIdsByCodeSpace = UpdaterUtils.getChouetteIdsByCodeSpace(list);
			List<AccessPoint> objects = new ArrayList<AccessPoint>();

			objects.addAll((List<AccessPoint>) accessPointDAO.findByChouetteId(chouetteIdsByCodeSpace));

			for (AccessPoint object : objects) {
				cache.getAccessPoints().put(object.getChouetteId(), object);
			}

			for (AccessPoint item : list) {
				AccessPoint object = cache.getAccessPoints().get(item.getChouetteId());
				if (object == null) {
					object = ChouetteIdObjectUtil.getAccessPoint(cache, item.getChouetteId());
				}
			}
		}
	}

	private void initializeTimetable(Referential cache, Collection<Timetable> list) {
		if (list != null && !list.isEmpty()) {

			Map<String, List<String>> chouetteIdsByCodeSpace = UpdaterUtils.getChouetteIdsByCodeSpace(list);
			List<Timetable> objects = new ArrayList<Timetable>();

			objects.addAll((List<Timetable>) timetableDAO.findByChouetteId(chouetteIdsByCodeSpace));

			for (Timetable object : objects) {
				cache.getTimetables().put(object.getChouetteId(), object);
			}

			for (Timetable item : list) {
				Timetable object = cache.getTimetables().get(item.getChouetteId());
				if (object == null) {
					object = ChouetteIdObjectUtil.getTimetable(cache, item.getChouetteId());
				}
			}
		}
	}

	private void initializePTNetwork(Referential cache, Collection<Network> list) {
		if (list != null && !list.isEmpty()) {

			Map<String, List<String>> chouetteIdsByCodeSpace = UpdaterUtils.getChouetteIdsByCodeSpace(list);
			List<Network> objects = new ArrayList<Network>();
            objects.addAll((List<Network>) ptNetworkDAO.findByChouetteId(chouetteIdsByCodeSpace));


			for (Network object : objects) {
				cache.getPtNetworks().put(object.getChouetteId(), object);
			}

			for (Network item : list) {
				Network object = cache.getPtNetworks().get(item.getChouetteId());
				if (object == null) {
					object = ChouetteIdObjectUtil.getPTNetwork(cache, item.getChouetteId());
				}
			}
		}
	}

	private void initializeCompany(Referential cache, Collection<Company> list) {
		if (list != null && !list.isEmpty()) {

			Map<String, List<String>> chouetteIdsByCodeSpace = UpdaterUtils.getChouetteIdsByCodeSpace(list);
			List<Company> objects = new ArrayList<Company>();

			objects.addAll((List<Company>) companyDAO.findByChouetteId(chouetteIdsByCodeSpace));
			

			for (Company object : objects) {
				cache.getCompanies().put(object.getChouetteId(), object);
			}

			for (Company item : list) {
				Company object = cache.getCompanies().get(item.getChouetteId());
				if (object == null) {
					object = ChouetteIdObjectUtil.getCompany(cache, item.getChouetteId());
				}
			}
		}
	}

	private void initializeGroupOfLine(Referential cache, Collection<GroupOfLine> list) {
		if (list != null && !list.isEmpty()) {

			Map<String, List<String>> chouetteIdsByCodeSpace = UpdaterUtils.getChouetteIdsByCodeSpace(list);
			List<GroupOfLine> objects = new ArrayList<GroupOfLine>();

			objects.addAll((List<GroupOfLine>) groupOfLineDAO.findByChouetteId(chouetteIdsByCodeSpace));


			for (GroupOfLine object : objects) {
				cache.getGroupOfLines().put(object.getChouetteId(), object);
			}

			for (GroupOfLine item : list) {
				GroupOfLine object = cache.getGroupOfLines().get(item.getChouetteId());
				if (object == null) {
					object = ChouetteIdObjectUtil.getGroupOfLine(cache, item.getChouetteId());
				}
			}
		}
	}

	private void initializeLine(Referential cache, Collection<Line> list) {
		if (list != null && !list.isEmpty()) {

			Map<String, List<String>> chouetteIdsByCodeSpace = UpdaterUtils.getChouetteIdsByCodeSpace(list);
			List<Line> objects = new ArrayList<Line>();

			objects.addAll((List<Line>) lineDAO.findByChouetteId(chouetteIdsByCodeSpace));

			for (Line object : objects) {
				cache.getLines().put(object.getChouetteId(), object);
			}

			for (Line item : list) {
				Line object = cache.getLines().get(item.getChouetteId());
				if (object == null) {
					object = ChouetteIdObjectUtil.getLine(cache, item.getChouetteId());
				}
			}
		}
	}

	private void initializeRoute(Referential cache, Collection<Route> list) {
		if (list != null && !list.isEmpty()) {

			Map<String, List<String>> chouetteIdsByCodeSpace = UpdaterUtils.getChouetteIdsByCodeSpace(list);
			List<Route> objects = new ArrayList<Route>();

			objects.addAll((List<Route>) routeDAO.findByChouetteId(chouetteIdsByCodeSpace));

			for (Route object : objects) {
				cache.getRoutes().put(object.getChouetteId(), object);
			}

			for (Route item : list) {
				Route object = cache.getRoutes().get(item.getChouetteId());
				if (object == null) {
					object = ChouetteIdObjectUtil.getRoute(cache, item.getChouetteId());
				}
			}
		}
	}

	private void initializeStopPoint(Referential cache, Collection<StopPoint> list) {
		if (list != null && !list.isEmpty()) {

			Map<String, List<String>> chouetteIdsByCodeSpace = UpdaterUtils.getChouetteIdsByCodeSpace(list);
			List<StopPoint> objects = new ArrayList<StopPoint>();

			objects.addAll((List<StopPoint>) stopPointDAO.findByChouetteId(chouetteIdsByCodeSpace));


			for (StopPoint object : objects) {
				cache.getStopPoints().put(object.getChouetteId(), object);
			}

			for (StopPoint item : list) {
				StopPoint object = cache.getStopPoints().get(item.getChouetteId());
				if (object == null) {
					object = ChouetteIdObjectUtil.getStopPoint(cache, item.getChouetteId());
				}
			}
		}
	}

	private void initializeJourneyPattern(Referential cache, Collection<JourneyPattern> list) {
		if (list != null && !list.isEmpty()) {

			Map<String, List<String>> chouetteIdsByCodeSpace = UpdaterUtils.getChouetteIdsByCodeSpace(list);
			List<JourneyPattern> objects = new ArrayList<JourneyPattern>();

			objects.addAll((List<JourneyPattern>) journeyPatternDAO.findByChouetteId(chouetteIdsByCodeSpace));


			for (JourneyPattern object : objects) {
				cache.getJourneyPatterns().put(object.getChouetteId(), object);
			}

			for (JourneyPattern item : list) {
				JourneyPattern object = cache.getJourneyPatterns().get(item.getChouetteId());
				if (object == null) {
					object = ChouetteIdObjectUtil.getJourneyPattern(cache, item.getChouetteId());
				}
			}
		}
	}

	private void initializeVehicleJourney(Referential cache, Collection<VehicleJourney> list) {
		if (list != null && !list.isEmpty()) {

			Map<String, List<String>> chouetteIdsByCodeSpace = UpdaterUtils.getChouetteIdsByCodeSpace(list);
			List<VehicleJourney> objects = new ArrayList<VehicleJourney>();

			objects.addAll((List<VehicleJourney>) vehicleJourneyDAO.findByChouetteId(chouetteIdsByCodeSpace));


			for (VehicleJourney object : objects) {
				cache.getVehicleJourneys().put(object.getChouetteId(), object);
			}

			for (VehicleJourney item : list) {
				VehicleJourney object = cache.getVehicleJourneys().get(item.getChouetteId());
				if (object == null) {
					object = ChouetteIdObjectUtil.getVehicleJourney(cache, item.getChouetteId());
				}
			}
		}
	}

	private void initializeTimeband(Referential cache, Collection<Timeband> list) {
		if (list != null && !list.isEmpty()) {

			Map<String, List<String>> chouetteIdsByCodeSpace = UpdaterUtils.getChouetteIdsByCodeSpace(list);
			List<Timeband> objects = new ArrayList<Timeband>();

			objects.addAll((List<Timeband>) timebandDAO.findByChouetteId(chouetteIdsByCodeSpace));

			for (Timeband object : objects) {
				cache.getTimebands().put(object.getChouetteId(), object);
			}

			for (Timeband item : list) {
				Timeband object = cache.getTimebands().get(item.getChouetteId());
				if (object == null) {
					object = ChouetteIdObjectUtil.getTimeband(cache, item.getChouetteId());
				}
			}
		}
	}
}
