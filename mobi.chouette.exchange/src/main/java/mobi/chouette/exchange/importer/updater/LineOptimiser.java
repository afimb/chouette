package mobi.chouette.exchange.importer.updater;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.ChouetteId;
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
import mobi.chouette.exchange.ChouetteIdObjectUtil;
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

//		Monitor monitor = MonitorFactory.start("LineOptimiser");
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
//		monitor.stop();
	}

	@SuppressWarnings("unchecked")
	private void initializeStopArea(Referential cache, Collection<StopArea> list) {
		if (list != null && !list.isEmpty()) {
			Collection<ChouetteId> chouetteIds = UpdaterUtils.getChouetteIds(list);
			Map<String,List<ChouetteId>> chouetteIdsByCodeSpace = UpdaterUtils.getChouetteIdsByCodeSpace(chouetteIds);
			List<StopArea> objects = new ArrayList<StopArea>();
			
			for (Entry<String, List<ChouetteId>> entry : chouetteIdsByCodeSpace.entrySet())
			{
			    objects.addAll((List<StopArea>) stopAreaDAO.findByChouetteId(entry.getKey(), entry.getValue()));
			}
			
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

	@SuppressWarnings("unchecked")
	private void initializeConnectionLink(Referential cache, Collection<ConnectionLink> list) {
		if (list != null && !list.isEmpty()) {
			Collection<ChouetteId> chouetteIds = UpdaterUtils.getChouetteIds(list);
			Map<String,List<ChouetteId>> chouetteIdsByCodeSpace = UpdaterUtils.getChouetteIdsByCodeSpace(chouetteIds);
			List<ConnectionLink> objects = new ArrayList<ConnectionLink>();
			
			for (Entry<String, List<ChouetteId>> entry : chouetteIdsByCodeSpace.entrySet())
			{
			    objects.addAll((List<ConnectionLink>) connectionLinkDAO.findByChouetteId(entry.getKey(), entry.getValue()));
			}
			
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

	@SuppressWarnings("unchecked")
	private void initializeAccessLink(Referential cache, Collection<AccessLink> list) {
		if (list != null && !list.isEmpty()) {
			Collection<ChouetteId> chouetteIds = UpdaterUtils.getChouetteIds(list);
			
			Map<String,List<ChouetteId>> chouetteIdsByCodeSpace = UpdaterUtils.getChouetteIdsByCodeSpace(chouetteIds);
			List<AccessLink> objects = new ArrayList<AccessLink>();
			
			for (Entry<String, List<ChouetteId>> entry : chouetteIdsByCodeSpace.entrySet())
			{
			    objects.addAll((List<AccessLink>) accessLinkDAO.findByChouetteId(entry.getKey(), entry.getValue()));
			}
			
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

	@SuppressWarnings("unchecked")
	private void initializeAccessPoint(Referential cache, Collection<AccessPoint> list) {
		if (list != null && !list.isEmpty()) {
			Collection<ChouetteId> chouetteIds = UpdaterUtils.getChouetteIds(list);
			
			Map<String,List<ChouetteId>> chouetteIdsByCodeSpace = UpdaterUtils.getChouetteIdsByCodeSpace(chouetteIds);
			List<AccessPoint> objects = new ArrayList<AccessPoint>();
			
			for (Entry<String, List<ChouetteId>> entry : chouetteIdsByCodeSpace.entrySet())
			{
			    objects.addAll((List<AccessPoint>) accessPointDAO.findByChouetteId(entry.getKey(), entry.getValue()));
			}
			
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

	@SuppressWarnings("unchecked")
	private void initializeTimetable(Referential cache, Collection<Timetable> list) {
		if (list != null && !list.isEmpty()) {
			Collection<ChouetteId> chouetteIds = UpdaterUtils.getChouetteIds(list);
			
			Map<String,List<ChouetteId>> chouetteIdsByCodeSpace = UpdaterUtils.getChouetteIdsByCodeSpace(chouetteIds);
			List<Timetable> objects = new ArrayList<Timetable>();
			
			for (Entry<String, List<ChouetteId>> entry : chouetteIdsByCodeSpace.entrySet())
			{
			    objects.addAll((List<Timetable>) timetableDAO.findByChouetteId(entry.getKey(), entry.getValue()));
			}
			
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

	@SuppressWarnings("unchecked")
	private void initializePTNetwork(Referential cache, Collection<Network> list) {
		if (list != null && !list.isEmpty()) {
			Collection<ChouetteId> chouetteIds = UpdaterUtils.getChouetteIds(list);
			
			Map<String,List<ChouetteId>> chouetteIdsByCodeSpace = UpdaterUtils.getChouetteIdsByCodeSpace(chouetteIds);
			List<Network> objects = new ArrayList<Network>();
			
			for (Entry<String, List<ChouetteId>> entry : chouetteIdsByCodeSpace.entrySet())
			{
			    objects.addAll((List<Network>) ptNetworkDAO.findByChouetteId(entry.getKey(), entry.getValue()));
			}
			
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

	@SuppressWarnings("unchecked")
	private void initializeCompany(Referential cache, Collection<Company> list) {
		if (list != null && !list.isEmpty()) {
			Collection<ChouetteId> chouetteIds = UpdaterUtils.getChouetteIds(list);
			
			Map<String,List<ChouetteId>> chouetteIdsByCodeSpace = UpdaterUtils.getChouetteIdsByCodeSpace(chouetteIds);
			List<Company> objects = new ArrayList<Company>();
			
			for (Entry<String, List<ChouetteId>> entry : chouetteIdsByCodeSpace.entrySet())
			{
			    objects.addAll((List<Company>) companyDAO.findByChouetteId(entry.getKey(), entry.getValue()));
			}
			
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

	@SuppressWarnings("unchecked")
	private void initializeGroupOfLine(Referential cache, Collection<GroupOfLine> list) {
		if (list != null && !list.isEmpty()) {
			Collection<ChouetteId> chouetteIds = UpdaterUtils.getChouetteIds(list);
			
			Map<String,List<ChouetteId>> chouetteIdsByCodeSpace = UpdaterUtils.getChouetteIdsByCodeSpace(chouetteIds);
			List<GroupOfLine> objects = new ArrayList<GroupOfLine>();
			
			for (Entry<String, List<ChouetteId>> entry : chouetteIdsByCodeSpace.entrySet())
			{
			    objects.addAll((List<GroupOfLine>) groupOfLineDAO.findByChouetteId(entry.getKey(), entry.getValue()));
			}
			
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

	@SuppressWarnings("unchecked")
	private void initializeLine(Referential cache, Collection<Line> list) {
		if (list != null && !list.isEmpty()) {
			Collection<ChouetteId> chouetteIds = UpdaterUtils.getChouetteIds(list);
			
			Map<String,List<ChouetteId>> chouetteIdsByCodeSpace = UpdaterUtils.getChouetteIdsByCodeSpace(chouetteIds);
			List<Line> objects = new ArrayList<Line>();
			
			for (Entry<String, List<ChouetteId>> entry : chouetteIdsByCodeSpace.entrySet())
			{
			    objects.addAll((List<Line>) lineDAO.findByChouetteId(entry.getKey(), entry.getValue()));
			}
			
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

	@SuppressWarnings("unchecked")
	private void initializeRoute(Referential cache, Collection<Route> list) {
		if (list != null && !list.isEmpty()) {
			Collection<ChouetteId> chouetteIds = UpdaterUtils.getChouetteIds(list);
			
			Map<String,List<ChouetteId>> chouetteIdsByCodeSpace = UpdaterUtils.getChouetteIdsByCodeSpace(chouetteIds);
			List<Route> objects = new ArrayList<Route>();
			
			for (Entry<String, List<ChouetteId>> entry : chouetteIdsByCodeSpace.entrySet())
			{
			    objects.addAll((List<Route>) routeDAO.findByChouetteId(entry.getKey(), entry.getValue()));
			}
			
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

	@SuppressWarnings("unchecked")
	private void initializeStopPoint(Referential cache, Collection<StopPoint> list) {
		if (list != null && !list.isEmpty()) {
			Collection<ChouetteId> chouetteIds = UpdaterUtils.getChouetteIds(list);
			
			Map<String,List<ChouetteId>> chouetteIdsByCodeSpace = UpdaterUtils.getChouetteIdsByCodeSpace(chouetteIds);
			List<StopPoint> objects = new ArrayList<StopPoint>();
			
			for (Entry<String, List<ChouetteId>> entry : chouetteIdsByCodeSpace.entrySet())
			{
			    objects.addAll((List<StopPoint>) stopPointDAO.findByChouetteId(entry.getKey(), entry.getValue()));
			}
			
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

	@SuppressWarnings("unchecked")
	private void initializeJourneyPattern(Referential cache, Collection<JourneyPattern> list) {
		if (list != null && !list.isEmpty()) {
			Collection<ChouetteId> chouetteIds = UpdaterUtils.getChouetteIds(list);
			
			Map<String,List<ChouetteId>> chouetteIdsByCodeSpace = UpdaterUtils.getChouetteIdsByCodeSpace(chouetteIds);
			List<JourneyPattern> objects = new ArrayList<JourneyPattern>();
			
			for (Entry<String, List<ChouetteId>> entry : chouetteIdsByCodeSpace.entrySet())
			{
			    objects.addAll((List<JourneyPattern>) journeyPatternDAO.findByChouetteId(entry.getKey(), entry.getValue()));
			}
			
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

	@SuppressWarnings("unchecked")
	private void initializeVehicleJourney(Referential cache, Collection<VehicleJourney> list) {
		if (list != null && !list.isEmpty()) {
			Collection<ChouetteId> chouetteIds = UpdaterUtils.getChouetteIds(list);
			
			Map<String,List<ChouetteId>> chouetteIdsByCodeSpace = UpdaterUtils.getChouetteIdsByCodeSpace(chouetteIds);
			List<VehicleJourney> objects = new ArrayList<VehicleJourney>();
			
			for (Entry<String, List<ChouetteId>> entry : chouetteIdsByCodeSpace.entrySet())
			{
			    objects.addAll((List<VehicleJourney>) vehicleJourneyDAO.findByChouetteId(entry.getKey(), entry.getValue()));
			}
			
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

	@SuppressWarnings("unchecked")
	private void initializeTimeband(Referential cache, Collection<Timeband> list) {
		if (list != null && !list.isEmpty()) {
			Collection<ChouetteId> chouetteIds = UpdaterUtils.getChouetteIds(list);
			
			Map<String,List<ChouetteId>> chouetteIdsByCodeSpace = UpdaterUtils.getChouetteIdsByCodeSpace(chouetteIds);
			List<Timeband> objects = new ArrayList<Timeband>();
			
			for (Entry<String, List<ChouetteId>> entry : chouetteIdsByCodeSpace.entrySet())
			{
			    objects.addAll((List<Timeband>) timebandDAO.findByChouetteId(entry.getKey(), entry.getValue()));
			}
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
