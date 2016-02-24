package mobi.chouette.exchange.importer.updater;

import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import mobi.chouette.common.CollectionUtil;
import mobi.chouette.common.Context;
import mobi.chouette.common.Pair;
import mobi.chouette.dao.RouteSectionDAO;
import mobi.chouette.dao.StopPointDAO;
import mobi.chouette.dao.VehicleJourneyDAO;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.RouteSection;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Stateless(name = JourneyPatternUpdater.BEAN_NAME)
public class JourneyPatternUpdater implements Updater<JourneyPattern> {

	public static final String BEAN_NAME = "JourneyPatternUpdater";

	@EJB 
	private StopPointDAO stopPointDAO;

	@EJB 
	private VehicleJourneyDAO vehicleJourneyDAO;
	
	@EJB 
	private RouteSectionDAO routeSectionDAO;

	@EJB(beanName = VehicleJourneyUpdater.BEAN_NAME)
	private Updater<VehicleJourney> vehicleJourneyUpdater;
	
	@EJB(beanName = RouteSectionUpdater.BEAN_NAME)
	private Updater<RouteSection> routeSectionUpdater;

	@Override
	public void update(Context context, JourneyPattern oldValue, JourneyPattern newValue) throws Exception {

		if (newValue.isSaved()) {
			return;
		}
		newValue.setSaved(true);

		Referential cache = (Referential) context.get(CACHE);

		if (newValue.getObjectId() != null && !newValue.getObjectId().equals(oldValue.getObjectId())) {
			oldValue.setObjectId(newValue.getObjectId());
		}
		if (newValue.getObjectVersion() != null && !newValue.getObjectVersion().equals(oldValue.getObjectVersion())) {
			oldValue.setObjectVersion(newValue.getObjectVersion());
		}
		if (newValue.getCreationTime() != null && !newValue.getCreationTime().equals(oldValue.getCreationTime())) {
			oldValue.setCreationTime(newValue.getCreationTime());
		}
		if (newValue.getCreatorId() != null && !newValue.getCreatorId().equals(oldValue.getCreatorId())) {
			oldValue.setCreatorId(newValue.getCreatorId());
		}
		if (newValue.getName() != null && !newValue.getName().equals(oldValue.getName())) {
			oldValue.setName(newValue.getName());
		}
		if (newValue.getComment() != null && !newValue.getComment().equals(oldValue.getComment())) {
			oldValue.setComment(newValue.getComment());
		}
		if (newValue.getRegistrationNumber() != null
				&& !newValue.getRegistrationNumber().equals(oldValue.getRegistrationNumber())) {
			oldValue.setRegistrationNumber(newValue.getRegistrationNumber());
		}
		if (newValue.getPublishedName() != null && !newValue.getPublishedName().equals(oldValue.getPublishedName())) {
			oldValue.setPublishedName(newValue.getPublishedName());
		}

		if (newValue.getSectionStatus() != null && !newValue.getSectionStatus().equals(oldValue.getSectionStatus())) {
			oldValue.setSectionStatus(newValue.getSectionStatus());
		}
		
		// RouteSections
		if (!newValue.getRouteSections().equals(oldValue.getRouteSections()))
		{
			// List<RouteSection> sections = routeSectionDAO.findByObjectId(UpdaterUtils.getObjectIds(newValue.getRouteSections()));
//            for (RouteSection object : sections) {
//				cache.getRouteSections().put(object.getObjectId(), object);
//			}
			oldValue.getRouteSections().clear();
			for (RouteSection item : newValue.getRouteSections()) {
				RouteSection section = cache.getRouteSections().get(item.getObjectId());
				if (section == null)
				{
					section = ObjectFactory.getRouteSection(cache,
							item.getObjectId());
				}
				oldValue.getRouteSections().add(section);
			}
		}
		Collection<Pair<RouteSection, RouteSection>> modifiedRouteSection = CollectionUtil
				.intersection(oldValue.getRouteSections(),
						newValue.getRouteSections(),
						NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<RouteSection, RouteSection> pair : modifiedRouteSection) {
			routeSectionUpdater.update(context, pair.getLeft(),
					pair.getRight());
		}
		

		// StopPoint
		Collection<StopPoint> addedStopPoint = CollectionUtil.substract(newValue.getStopPoints(),
				oldValue.getStopPoints(), NeptuneIdentifiedObjectComparator.INSTANCE);

		List<StopPoint> stopPoints = null;
		for (StopPoint item : addedStopPoint) {

			StopPoint stopPoint = cache.getStopPoints().get(item.getObjectId());
			if (stopPoint == null) {
				if (stopPoints == null) {
					stopPoints = stopPointDAO.findByObjectId(UpdaterUtils.getObjectIds(addedStopPoint));
					for (StopPoint object : stopPoints) {
						cache.getStopPoints().put(object.getObjectId(), object);
					}
				}
				stopPoint = cache.getStopPoints().get(item.getObjectId());
			}

			if (stopPoint != null) {
				oldValue.addStopPoint(stopPoint);
			}
		}

		Collection<StopPoint> removedStopPoint = CollectionUtil.substract(oldValue.getStopPoints(),
				newValue.getStopPoints(), NeptuneIdentifiedObjectComparator.INSTANCE);
		for (StopPoint stopPoint : removedStopPoint) {
			oldValue.removeStopPoint(stopPoint);
		}

		// ArrivalStopPoint
		if (newValue.getArrivalStopPoint() == null) {
			oldValue.setArrivalStopPoint(null);
		} else if (!newValue.getArrivalStopPoint().equals(oldValue.getArrivalStopPoint())) {

			String objectId = newValue.getArrivalStopPoint().getObjectId();
			StopPoint stopPoint = cache.getStopPoints().get(objectId);
			if (stopPoint == null) {
				stopPoint = stopPointDAO.findByObjectId(objectId);
				if (stopPoint != null) {
					cache.getStopPoints().put(objectId, stopPoint);
				}
			}

			if (stopPoint != null) {
				oldValue.setArrivalStopPoint(stopPoint);
			}
		}

		// DepartureStopPoint
		if (newValue.getDepartureStopPoint() == null) {
			oldValue.setDepartureStopPoint(null);
		} else if (!newValue.getDepartureStopPoint().equals(oldValue.getDepartureStopPoint())) {

			String objectId = newValue.getDepartureStopPoint().getObjectId();
			StopPoint stopPoint = cache.getStopPoints().get(objectId);
			if (stopPoint == null) {
				stopPoint = stopPointDAO.findByObjectId(objectId);
				if (stopPoint != null) {
					cache.getStopPoints().put(objectId, stopPoint);
				}
			}

			if (stopPoint != null) {
				oldValue.setDepartureStopPoint(stopPoint);
			}
		}

		// VehicleJourney
		Collection<VehicleJourney> addedVehicleJourney = CollectionUtil.substract(newValue.getVehicleJourneys(),
				oldValue.getVehicleJourneys(), NeptuneIdentifiedObjectComparator.INSTANCE);

		List<VehicleJourney> vehicleJourneys = null;
		for (VehicleJourney item : addedVehicleJourney) {

			VehicleJourney vehicleJourney = cache.getVehicleJourneys().get(item.getObjectId());
			if (vehicleJourney == null) {
				if (vehicleJourneys == null) {
					vehicleJourneys = vehicleJourneyDAO.findByObjectId(UpdaterUtils.getObjectIds(addedVehicleJourney));
					for (VehicleJourney object : vehicleJourneys) {
						cache.getVehicleJourneys().put(object.getObjectId(), object);
					}
				}
				vehicleJourney = cache.getVehicleJourneys().get(item.getObjectId());
			}

			if (vehicleJourney == null) {
				vehicleJourney = ObjectFactory.getVehicleJourney(cache, item.getObjectId());
			}
			vehicleJourney.setJourneyPattern(oldValue);
		}

		Collection<Pair<VehicleJourney, VehicleJourney>> modifiedVehicleJourney = CollectionUtil.intersection(
				oldValue.getVehicleJourneys(), newValue.getVehicleJourneys(),
				NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<VehicleJourney, VehicleJourney> pair : modifiedVehicleJourney) {
			vehicleJourneyUpdater.update(context, pair.getLeft(), pair.getRight());
		}

		// Collection<VehicleJourney> removedVehicleJourney = CollectionUtils
		// .substract(oldValue.getVehicleJourneys(),
		// newValue.getVehicleJourneys(),
		// NeptuneIdentifiedObjectComparator.INSTANCE);
		// for (VehicleJourney vehicleJourney : removedVehicleJourney) {
		// vehicleJourney.setJourneyPattern(null);
		// vehicleJourneyDAO.delete(vehicleJourney);
		// }
	}

}
