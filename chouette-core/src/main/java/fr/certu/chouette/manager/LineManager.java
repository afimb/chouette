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

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.filter.DetailLevelEnum;
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
import fr.certu.chouette.model.neptune.RestrictionConstraint;
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
public class LineManager extends AbstractNeptuneManager<Line> {

	public LineManager() {
		super(Line.class);
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
	public void remove(User user, Line line,boolean propagate) throws ChouetteException{
		INeptuneManager<Route> routeManager = (INeptuneManager<Route>) getManager(Route.class);
		Filter filter = Filter.getNewEqualsFilter("line.id", line.getId());
		DetailLevelEnum level = DetailLevelEnum.ATTRIBUTE;
		INeptuneManager<Facility> facilityManager = (INeptuneManager<Facility>) getManager(Facility.class);
		INeptuneManager<RestrictionConstraint> constraintManager = 
			(INeptuneManager<RestrictionConstraint>) getManager(RestrictionConstraint.class);
		List<Route> routes = routeManager.getAll(user, filter, level);
		if(routes != null && !routes.isEmpty())
			routeManager.removeAll(user, routes,propagate);
		Facility facility = facilityManager.get(user, filter, level);
		if(facility != null)
			facilityManager.remove(user, facility,propagate);
		List<RestrictionConstraint> constraints = constraintManager.getAll(user, filter, level);
		if(constraints != null && !constraints.isEmpty())
			constraintManager.removeAll(user, constraints,propagate);
		super.remove(user, line,propagate);
	}

	@Override
	protected Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}
}
