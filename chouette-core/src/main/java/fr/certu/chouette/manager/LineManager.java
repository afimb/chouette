/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.core.CoreException;
import fr.certu.chouette.core.CoreExceptionCode;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.AccessLink;
import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.Facility;
import fr.certu.chouette.model.neptune.GroupOfLine;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.TimeSlot;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.type.ImportedItems;
import fr.certu.chouette.model.user.User;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.validation.ValidationParameters;
import fr.certu.chouette.plugin.validation.ValidationReport;

/**
 * 
 */
@SuppressWarnings("unchecked")
public class LineManager extends AbstractNeptuneManager<Line> 
{
	private static final Logger logger = Logger.getLogger(LineManager.class);

	public LineManager() 
	{
		super(Line.class,Line.LINE_KEY);
	}

	@Override
	protected Report propagateValidation(User user, List<Line> beans,ValidationParameters parameters, boolean propagate)
	throws ChouetteException {
		Report globalReport = new ValidationReport();

		List<PTNetwork> networks = new ArrayList<PTNetwork>();
		List<Company> companies = new ArrayList<Company>();
		List<Route> routes = new ArrayList<Route>();

		List<JourneyPattern> journeyPatterns = new ArrayList<JourneyPattern>();
		List<ConnectionLink> connectionLinks = new ArrayList<ConnectionLink>();
		List<PTLink> ptLinks = new ArrayList<PTLink>();
		List<StopArea> stopAreas = new ArrayList<StopArea>();
		List<StopPoint> stopPoints = new ArrayList<StopPoint>();
		List<Timetable> timetables = new ArrayList<Timetable>();
		List<VehicleJourney> vehicleJourneys = new ArrayList<VehicleJourney>();
		List<TimeSlot> timeSlots = new ArrayList<TimeSlot>();
		List<AccessLink> accessLinks = new ArrayList<AccessLink>();
		List<AccessPoint> accessPoints = new ArrayList<AccessPoint>();
		List<Facility> facilities = new ArrayList<Facility>();
		List<GroupOfLine> groupOfLines = new ArrayList<GroupOfLine>();

		// if collection of all sub items is provided
		if (beans.get(0).getImportedItems() != null) {
			propagate = false;
			for (Line line : beans) {
				ImportedItems item = line.getImportedItems();

				networks.add(item.getPtNetwork());
				companies.addAll(item.getCompanies());
				routes.addAll(item.getRoutes());

				journeyPatterns.addAll(item.getJourneyPatterns());
				connectionLinks.addAll(item.getConnectionLinks());
				ptLinks.addAll(item.getPtLinks());
				stopAreas.addAll(item.getStopAreas());
				stopPoints.addAll(item.getStopPoints());
				timetables.addAll(item.getTimetables());
				vehicleJourneys.addAll(item.getVehicleJourneys());
				timeSlots.addAll(item.getTimeSlots());
				accessLinks.addAll(item.getAccessLinks());
				accessPoints.addAll(item.getAccessPoints());
				facilities.addAll(item.getFacilities());
				groupOfLines.addAll(item.getGroupOfLines());

			}
		} else {
			// else aggregate dependent objects for validation
			for (Line line : beans) {
				if (line.getPtNetwork() != null)
					networks.add(line.getPtNetwork());
				if (line.getCompany() != null)
					companies.add(line.getCompany());
				if (line.getRoutes() != null) {
					routes.addAll(line.getRoutes());
				}
			}
		}

		// propagate validation on networks
		if (networks.size() > 0) {
			AbstractNeptuneManager<PTNetwork> manager = (AbstractNeptuneManager<PTNetwork>) getManager(PTNetwork.class);
			Report report = validateReport(user, manager, networks, parameters, propagate);
			if (report != null) {
				globalReport.addAll(report.getItems());
				globalReport.updateStatus(report.getStatus());
			}
		}

		// propagate validation on companies
		if (companies.size() > 0) {
			AbstractNeptuneManager<Company> manager = (AbstractNeptuneManager<Company>) getManager(Company.class);
			Report report = validateReport(user, manager, companies, parameters, propagate);
			if (report != null) {
				globalReport.addAll(report.getItems());
				globalReport.updateStatus(report.getStatus());
			}
		}

		// propagate validation on routes
		if (routes.size() > 0) {
			AbstractNeptuneManager<Route> manager = (AbstractNeptuneManager<Route>) getManager(Route.class);
			Report report = validateReport(user, manager, routes, parameters, propagate);
			if (report != null) {
				globalReport.addAll(report.getItems());
				globalReport.updateStatus(report.getStatus());
			}
		}

		// propagate validation on journeyPatterns
		if (!journeyPatterns.isEmpty()) {
			AbstractNeptuneManager<JourneyPattern> manager = (AbstractNeptuneManager<JourneyPattern>) getManager(JourneyPattern.class);
			Report report = validateReport(user, manager, journeyPatterns, parameters, propagate);
			if (report != null) {
				globalReport.addAll(report.getItems());
				globalReport.updateStatus(report.getStatus());
			}
		}

		// propagate validation on connectionLinks
		if (!connectionLinks.isEmpty()) {
			AbstractNeptuneManager<ConnectionLink> manager = (AbstractNeptuneManager<ConnectionLink>) getManager(ConnectionLink.class);
			Report report = validateReport(user, manager, connectionLinks, parameters, propagate);
			if (report != null) {
				globalReport.addAll(report.getItems());
				globalReport.updateStatus(report.getStatus());
			}
		}
		// propagate validation on ptLinks
		if (!ptLinks.isEmpty()) {
			AbstractNeptuneManager<PTLink> manager = (AbstractNeptuneManager<PTLink>) getManager(PTLink.class);
			Report report = validateReport(user, manager, ptLinks, parameters, propagate);
			if (report != null) {
				globalReport.addAll(report.getItems());
				globalReport.updateStatus(report.getStatus());
			}
		}

		// propagate validation on stopAreas
		if (!stopAreas.isEmpty()) {
			AbstractNeptuneManager<StopArea> manager = (AbstractNeptuneManager<StopArea>) getManager(StopArea.class);
			Report report = validateReport(user, manager, stopAreas, parameters, propagate);
			if (report != null) {
				globalReport.addAll(report.getItems());
				globalReport.updateStatus(report.getStatus());
			}
		}

		// propagate validation on stopPoints
		if (!stopPoints.isEmpty()) {
			AbstractNeptuneManager<StopPoint> manager = (AbstractNeptuneManager<StopPoint>) getManager(StopPoint.class);
			Report report = validateReport(user, manager, stopPoints, parameters, propagate);
			if (report != null) {
				globalReport.addAll(report.getItems());
				globalReport.updateStatus(report.getStatus());
			}	
		}

		// propagate validation on timetables
		if (!timetables.isEmpty()) {
			AbstractNeptuneManager<Timetable> manager = (AbstractNeptuneManager<Timetable>) getManager(Timetable.class);
			Report report = validateReport(user, manager, timetables, parameters, propagate);
			if (report != null) {
				globalReport.addAll(report.getItems());
				globalReport.updateStatus(report.getStatus());
			}	
		}

		// propagate validation on vehicleJourneys
		if(!vehicleJourneys.isEmpty()){
			AbstractNeptuneManager<VehicleJourney> manager = (AbstractNeptuneManager<VehicleJourney>) getManager(VehicleJourney.class);
			Report report = validateReport(user, manager, vehicleJourneys, parameters, propagate);
			if (report != null) {
				globalReport.addAll(report.getItems());
				globalReport.updateStatus(report.getStatus());
			}	
		}

		// propagate validation on timeSlots
		if(!timeSlots.isEmpty()){
			AbstractNeptuneManager<TimeSlot> manager = (AbstractNeptuneManager<TimeSlot>) getManager(TimeSlot.class);
			Report report = validateReport(user, manager, timeSlots, parameters, propagate);
			if (report != null) {
				globalReport.addAll(report.getItems());
				globalReport.updateStatus(report.getStatus());
			}	
		}

		// propagate validation on accessLinks
		if(!accessLinks.isEmpty()){
			AbstractNeptuneManager<AccessLink> manager = (AbstractNeptuneManager<AccessLink>) getManager(AccessLink.class);
			Report report = validateReport(user, manager, accessLinks, parameters, propagate);
			if (report != null) {
				globalReport.addAll(report.getItems());
				globalReport.updateStatus(report.getStatus());
			}	
		}

		// propagate validation on accessPoints
		if(!accessPoints.isEmpty()){
			AbstractNeptuneManager<AccessPoint> manager = (AbstractNeptuneManager<AccessPoint>) getManager(AccessPoint.class);
			Report report = validateReport(user, manager, accessPoints, parameters, propagate);
			if (report != null) {
				globalReport.addAll(report.getItems());
				globalReport.updateStatus(report.getStatus());
			}	
		}


		// propagate validation on facilities
		if(!facilities.isEmpty()){
			AbstractNeptuneManager<Facility> manager = (AbstractNeptuneManager<Facility>) getManager(Facility.class);
			Report report = validateReport(user, manager, facilities, parameters, propagate);
			if (report != null) {
				globalReport.addAll(report.getItems());
				globalReport.updateStatus(report.getStatus());
			}	
		}

		// propagate validation on groupOfLines
		if(!groupOfLines.isEmpty()){
			AbstractNeptuneManager<GroupOfLine> manager = (AbstractNeptuneManager<GroupOfLine>) getManager(GroupOfLine.class);
			Report report = validateReport(user, manager, groupOfLines, parameters, propagate);
			if (report != null) {
				globalReport.addAll(report.getItems());
				globalReport.updateStatus(report.getStatus());
			}	
		}

		return globalReport;
	}

	/**
	 * Used in propagate validation process
	 * @param <T>
	 * @param user
	 * @param manager
	 * @param list
	 * @param parameters
	 * @param propagate
	 * @return
	 * @throws ChouetteException
	 */
	private <T extends NeptuneIdentifiedObject> Report validateReport(User user,AbstractNeptuneManager<T> manager,List<T> list,
			ValidationParameters parameters,boolean propagate) throws ChouetteException{
		Report report = null;
		if (!list.isEmpty()) {
			if (manager.canValidate()) {
				report = manager.validate(user, list, parameters, propagate);
			} else if (propagate) {
				report = manager.propagateValidation(user, list, parameters,propagate);
			}
		}
		return report;
	}


	@Override
	protected Logger getLogger() 
	{
		return logger;
	}

	@Override
	public void completeObject(User user, Line line) throws ChouetteException 
	{
	   line.complete();
	}
	@Transactional
	@Override
	public void saveAll(User user, List<Line> lines, boolean propagate,boolean fast) throws ChouetteException
	{
		logger.debug("start saving line collection");
		if(propagate)
		{
			INeptuneManager<Route> routeManager = (INeptuneManager<Route>) getManager(Route.class);
			INeptuneManager<Company> companyManager = (INeptuneManager<Company>) getManager(Company.class);
			INeptuneManager<PTNetwork> networkManager = (INeptuneManager<PTNetwork>) getManager(PTNetwork.class);
			INeptuneManager<GroupOfLine> groupOfLineManager = (INeptuneManager<GroupOfLine>) getManager(GroupOfLine.class);
			INeptuneManager<Facility> facilityManager = (INeptuneManager<Facility>) getManager(Facility.class);

			List<PTNetwork> networks = new ArrayList<PTNetwork>();
			List<Company> companies = new ArrayList<Company>();
			List<GroupOfLine> groupOfLines = new ArrayList<GroupOfLine>();
			List<Route> routes = new ArrayList<Route>();
			List<Facility> facilities = new ArrayList<Facility>();

			for (Line line : lines) 
			{
				addIfMissingInCollection(companies,line.getCompany());
				mergeCollection(groupOfLines,line.getGroupOfLines());
				addIfMissingInCollection(networks, line.getPtNetwork());
				mergeCollection(routes,line.getRoutes());	
				mergeCollection(facilities,line.getFacilities());
			}
			if(!companies.isEmpty())
				companyManager.saveAll(user,companies,propagate,fast);
			if(!groupOfLines.isEmpty())
				groupOfLineManager.saveAll(user,groupOfLines,propagate,fast);
			if(!networks.isEmpty())
				networkManager.saveAll(user,networks,propagate,fast);

			super.saveAll(user, lines,propagate,fast);
			
			if(!routes.isEmpty())
				routeManager.saveAll(user, routes,propagate,fast);
			if(!facilities.isEmpty())
				facilityManager.saveAll(user, facilities, propagate,fast);
		}
		else 
		{
			super.saveAll(user, lines,propagate,fast);	
		}
		logger.debug("end saving line collection");
	}

	@Transactional
	@Override
	public int removeAll(User user, Filter filter) throws ChouetteException 
	{
		if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");
		if (filter.getType().equals(Filter.Type.EQUALS))
		{
			// INeptuneManager<Route> routeManager = (INeptuneManager<Route>) getManager(Route.class);
			INeptuneManager<Facility> facilityManager = (INeptuneManager<Facility>) getManager(Facility.class);
			// INeptuneManager<RestrictionConstraint> restrictionConstraintManager = (INeptuneManager<RestrictionConstraint>) getManager(RestrictionConstraint.class);
	        Filter dependentFilter = Filter.getNewEqualsFilter("line."+filter.getAttribute(), filter.getFirstValue());
	        // routeManager.removeAll(user, dependentFilter);
	        facilityManager.removeAll(user, dependentFilter);
	        // restrictionConstraintManager.removeAll(user, dependentFilter);
		}
		else
		{
			throw new CoreException(CoreExceptionCode.DELETE_IMPOSSIBLE,"unvalid filter");
		}
		int ret =  getDao().removeAll(filter);
		logger.debug(""+ret+" lines deleted");
		return ret;
		
	}
	
	
}
