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
import mobi.chouette.model.util.ObjectFactory;
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

	private void initializeStopArea(Referential cache, Collection<StopArea> list) {
		if (list != null && !list.isEmpty()) {
			Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
			Map<String,List<String>> objectIdsByCodeSpace = UpdaterUtils.getObjectIdsByCodeSpace(objectIds);
			List<StopArea> objects = new ArrayList<StopArea>();
			
			for (Entry<String, List<String>> entry : objectIdsByCodeSpace.entrySet())
			{
			    objects.addAll(stopAreaDAO.findByChouetteId(entry.getKey(), entry.getValue()));
			}
			
			for (StopArea object : objects) {
				cache.getStopAreas().put(object.getChouetteId().getObjectId(), object);
			}

			// TODO check if stoparea really exists
			for (StopArea item : list) {
				StopArea object = cache.getStopAreas().get(item.getChouetteId().getObjectId());
				if (object == null) {
					object = ObjectFactory.getStopArea(cache, item.getChouetteId().getObjectId());
				}
			}
		}
	}

	private void initializeConnectionLink(Referential cache, Collection<ConnectionLink> list) {
		if (list != null && !list.isEmpty()) {
			Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
			Map<String,List<String>> objectIdsByCodeSpace = UpdaterUtils.getObjectIdsByCodeSpace(objectIds);
			List<ConnectionLink> objects = new ArrayList<ConnectionLink>();
			
			for (Entry<String, List<String>> entry : objectIdsByCodeSpace.entrySet())
			{
			    objects.addAll(connectionLinkDAO.findByChouetteId(entry.getKey(), entry.getValue()));
			}
			
			for (ConnectionLink object : objects) {
				cache.getConnectionLinks().put(object.getChouetteId().getObjectId(), object);
			}

			for (ConnectionLink item : list) {
				ConnectionLink object = cache.getConnectionLinks().get(item.getChouetteId().getObjectId());
				if (object == null) {
					object = ObjectFactory.getConnectionLink(cache, item.getChouetteId().getObjectId());
				}
			}
		}
	}

	private void initializeAccessLink(Referential cache, Collection<AccessLink> list) {
		if (list != null && !list.isEmpty()) {
			Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
			
			Map<String,List<String>> objectIdsByCodeSpace = UpdaterUtils.getObjectIdsByCodeSpace(objectIds);
			List<AccessLink> objects = new ArrayList<AccessLink>();
			
			for (Entry<String, List<String>> entry : objectIdsByCodeSpace.entrySet())
			{
			    objects.addAll(accessLinkDAO.findByChouetteId(entry.getKey(), entry.getValue()));
			}
			
			for (AccessLink object : objects) {
				cache.getAccessLinks().put(object.getChouetteId().getObjectId(), object);
			}

			for (AccessLink item : list) {
				AccessLink object = cache.getAccessLinks().get(item.getChouetteId().getObjectId());
				if (object == null) {
					object = ObjectFactory.getAccessLink(cache, item.getChouetteId().getObjectId());
				}
			}
		}
	}

	private void initializeAccessPoint(Referential cache, Collection<AccessPoint> list) {
		if (list != null && !list.isEmpty()) {
			Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
			
			Map<String,List<String>> objectIdsByCodeSpace = UpdaterUtils.getObjectIdsByCodeSpace(objectIds);
			List<AccessPoint> objects = new ArrayList<AccessPoint>();
			
			for (Entry<String, List<String>> entry : objectIdsByCodeSpace.entrySet())
			{
			    objects.addAll(accessPointDAO.findByChouetteId(entry.getKey(), entry.getValue()));
			}
			
			for (AccessPoint object : objects) {
				cache.getAccessPoints().put(object.getChouetteId().getObjectId(), object);
			}

			for (AccessPoint item : list) {
				AccessPoint object = cache.getAccessPoints().get(item.getChouetteId().getObjectId());
				if (object == null) {
					object = ObjectFactory.getAccessPoint(cache, item.getChouetteId().getObjectId());
				}
			}
		}
	}

	private void initializeTimetable(Referential cache, Collection<Timetable> list) {
		if (list != null && !list.isEmpty()) {
			Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
			
			Map<String,List<String>> objectIdsByCodeSpace = UpdaterUtils.getObjectIdsByCodeSpace(objectIds);
			List<Timetable> objects = new ArrayList<Timetable>();
			
			for (Entry<String, List<String>> entry : objectIdsByCodeSpace.entrySet())
			{
			    objects.addAll(timetableDAO.findByChouetteId(entry.getKey(), entry.getValue()));
			}
			
			for (Timetable object : objects) {
				cache.getTimetables().put(object.getChouetteId().getObjectId(), object);
			}

			for (Timetable item : list) {
				Timetable object = cache.getTimetables().get(item.getChouetteId().getObjectId());
				if (object == null) {
					object = ObjectFactory.getTimetable(cache, item.getChouetteId().getObjectId());
				}
			}
		}
	}

	private void initializePTNetwork(Referential cache, Collection<Network> list) {
		if (list != null && !list.isEmpty()) {
			Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
			
			Map<String,List<String>> objectIdsByCodeSpace = UpdaterUtils.getObjectIdsByCodeSpace(objectIds);
			List<Network> objects = new ArrayList<Network>();
			
			for (Entry<String, List<String>> entry : objectIdsByCodeSpace.entrySet())
			{
			    objects.addAll(ptNetworkDAO.findByChouetteId(entry.getKey(), entry.getValue()));
			}
			
			for (Network object : objects) {
				cache.getPtNetworks().put(object.getChouetteId().getObjectId(), object);
			}

			for (Network item : list) {
				Network object = cache.getPtNetworks().get(item.getChouetteId().getObjectId());
				if (object == null) {
					object = ObjectFactory.getPTNetwork(cache, item.getChouetteId().getObjectId());
				}
			}
		}
	}

	private void initializeCompany(Referential cache, Collection<Company> list) {
		if (list != null && !list.isEmpty()) {
			Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
			
			Map<String,List<String>> objectIdsByCodeSpace = UpdaterUtils.getObjectIdsByCodeSpace(objectIds);
			List<Company> objects = new ArrayList<Company>();
			
			for (Entry<String, List<String>> entry : objectIdsByCodeSpace.entrySet())
			{
			    objects.addAll(companyDAO.findByChouetteId(entry.getKey(), entry.getValue()));
			}
			
			for (Company object : objects) {
				cache.getCompanies().put(object.getChouetteId().getObjectId(), object);
			}

			for (Company item : list) {
				Company object = cache.getCompanies().get(item.getChouetteId().getObjectId());
				if (object == null) {
					object = ObjectFactory.getCompany(cache, item.getChouetteId().getObjectId());
				}
			}
		}
	}

	private void initializeGroupOfLine(Referential cache, Collection<GroupOfLine> list) {
		if (list != null && !list.isEmpty()) {
			Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
			
			Map<String,List<String>> objectIdsByCodeSpace = UpdaterUtils.getObjectIdsByCodeSpace(objectIds);
			List<GroupOfLine> objects = new ArrayList<GroupOfLine>();
			
			for (Entry<String, List<String>> entry : objectIdsByCodeSpace.entrySet())
			{
			    objects.addAll(groupOfLineDAO.findByChouetteId(entry.getKey(), entry.getValue()));
			}
			
			for (GroupOfLine object : objects) {
				cache.getGroupOfLines().put(object.getChouetteId().getObjectId(), object);
			}

			for (GroupOfLine item : list) {
				GroupOfLine object = cache.getGroupOfLines().get(item.getChouetteId().getObjectId());
				if (object == null) {
					object = ObjectFactory.getGroupOfLine(cache, item.getChouetteId().getObjectId());
				}
			}
		}
	}

	private void initializeLine(Referential cache, Collection<Line> list) {
		if (list != null && !list.isEmpty()) {
			Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
			
			Map<String,List<String>> objectIdsByCodeSpace = UpdaterUtils.getObjectIdsByCodeSpace(objectIds);
			List<Line> objects = new ArrayList<Line>();
			
			for (Entry<String, List<String>> entry : objectIdsByCodeSpace.entrySet())
			{
			    objects.addAll(lineDAO.findByChouetteId(entry.getKey(), entry.getValue()));
			}
			
			for (Line object : objects) {
				cache.getLines().put(object.getChouetteId().getObjectId(), object);
			}

			for (Line item : list) {
				Line object = cache.getLines().get(item.getChouetteId().getObjectId());
				if (object == null) {
					object = ObjectFactory.getLine(cache, item.getChouetteId().getObjectId());
				}
			}
		}
	}

	private void initializeRoute(Referential cache, Collection<Route> list) {
		if (list != null && !list.isEmpty()) {
			Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
			
			Map<String,List<String>> objectIdsByCodeSpace = UpdaterUtils.getObjectIdsByCodeSpace(objectIds);
			List<Route> objects = new ArrayList<Route>();
			
			for (Entry<String, List<String>> entry : objectIdsByCodeSpace.entrySet())
			{
			    objects.addAll(routeDAO.findByChouetteId(entry.getKey(), entry.getValue()));
			}
			
			for (Route object : objects) {
				cache.getRoutes().put(object.getChouetteId().getObjectId(), object);
			}

			for (Route item : list) {
				Route object = cache.getRoutes().get(item.getChouetteId().getObjectId());
				if (object == null) {
					object = ObjectFactory.getRoute(cache, item.getChouetteId().getObjectId());
				}
			}
		}
	}

	private void initializeStopPoint(Referential cache, Collection<StopPoint> list) {
		if (list != null && !list.isEmpty()) {
			Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
			
			Map<String,List<String>> objectIdsByCodeSpace = UpdaterUtils.getObjectIdsByCodeSpace(objectIds);
			List<StopPoint> objects = new ArrayList<StopPoint>();
			
			for (Entry<String, List<String>> entry : objectIdsByCodeSpace.entrySet())
			{
			    objects.addAll(stopPointDAO.findByChouetteId(entry.getKey(), entry.getValue()));
			}
			
			for (StopPoint object : objects) {
				cache.getStopPoints().put(object.getChouetteId().getObjectId(), object);
			}

			for (StopPoint item : list) {
				StopPoint object = cache.getStopPoints().get(item.getChouetteId().getObjectId());
				if (object == null) {
					object = ObjectFactory.getStopPoint(cache, item.getChouetteId().getObjectId());
				}
			}
		}
	}

	private void initializeJourneyPattern(Referential cache, Collection<JourneyPattern> list) {
		if (list != null && !list.isEmpty()) {
			Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
			
			Map<String,List<String>> objectIdsByCodeSpace = UpdaterUtils.getObjectIdsByCodeSpace(objectIds);
			List<JourneyPattern> objects = new ArrayList<JourneyPattern>();
			
			for (Entry<String, List<String>> entry : objectIdsByCodeSpace.entrySet())
			{
			    objects.addAll(journeyPatternDAO.findByChouetteId(entry.getKey(), entry.getValue()));
			}
			
			for (JourneyPattern object : objects) {
				cache.getJourneyPatterns().put(object.getChouetteId().getObjectId(), object);
			}

			for (JourneyPattern item : list) {
				JourneyPattern object = cache.getJourneyPatterns().get(item.getChouetteId().getObjectId());
				if (object == null) {
					object = ObjectFactory.getJourneyPattern(cache, item.getChouetteId().getObjectId());
				}
			}
		}
	}

	private void initializeVehicleJourney(Referential cache, Collection<VehicleJourney> list) {
		if (list != null && !list.isEmpty()) {
			Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
			
			Map<String,List<String>> objectIdsByCodeSpace = UpdaterUtils.getObjectIdsByCodeSpace(objectIds);
			List<VehicleJourney> objects = new ArrayList<VehicleJourney>();
			
			for (Entry<String, List<String>> entry : objectIdsByCodeSpace.entrySet())
			{
			    objects.addAll(vehicleJourneyDAO.findByChouetteId(entry.getKey(), entry.getValue()));
			}
			
			for (VehicleJourney object : objects) {
				cache.getVehicleJourneys().put(object.getChouetteId().getObjectId(), object);
			}

			for (VehicleJourney item : list) {
				VehicleJourney object = cache.getVehicleJourneys().get(item.getChouetteId().getObjectId());
				if (object == null) {
					object = ObjectFactory.getVehicleJourney(cache, item.getChouetteId().getObjectId());
				}
			}
		}
	}

	private void initializeTimeband(Referential cache, Collection<Timeband> list) {
		if (list != null && !list.isEmpty()) {
			Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
			
			Map<String,List<String>> objectIdsByCodeSpace = UpdaterUtils.getObjectIdsByCodeSpace(objectIds);
			List<Timeband> objects = new ArrayList<Timeband>();
			
			for (Entry<String, List<String>> entry : objectIdsByCodeSpace.entrySet())
			{
			    objects.addAll(timebandDAO.findByChouetteId(entry.getKey(), entry.getValue()));
			}
			for (Timeband object : objects) {
				cache.getTimebands().put(object.getChouetteId().getObjectId(), object);
			}

			for (Timeband item : list) {
				Timeband object = cache.getTimebands().get(item.getChouetteId().getObjectId());
				if (object == null) {
					object = ObjectFactory.getTimeband(cache, item.getChouetteId().getObjectId());
				}
			}
		}
	}
}
